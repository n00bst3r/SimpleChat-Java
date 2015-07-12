package de.chatclient.components;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.chatclient.commands.CommandProvider;
import de.chatclient.gui.ClientGUI;



public class ClientThread extends Thread {

	public static int id = 1;
	public final Object client_lock = new Object();
	public boolean alive = true;
	private static Random r = new Random();
	private static int sequenceCounter = r.nextInt(2000);
	public static Socket server = null;
	public boolean stopped = false;
	public Scanner in;
	public PrintWriter out;
	public boolean active;
	private boolean firstRun = true;
	private JSONObject req;
	private JSONObject res;
	private CommandProvider commands = CommandProvider.getInstance();

	public void run() {
		id++;
		this.setName("Client mit der ID :" + id);
		ClientGUI.getInstance().getStatus()
				.setText(this.getName() + " connected");
		System.out.println("Client startet mit Thread: " + this.getName()
				+ " Thread-Status: " + this.getState());
		System.out.println("Run Methode startet...");
		try {
			synchronized (this.client_lock) {
				System.out.println("Client-Socket für " + this.getName()
						+ " wird angelegt");
				server = new Socket(ClientGUI.getInstance().getHostField()
						.getText(), Integer.parseInt(ClientGUI.getInstance()
						.getPortField().getText()));
				System.out.println("Scanner für " + this.getName()
						+ " wird angelegt...");
				this.in = new Scanner(server.getInputStream());
				System.out.println("PrintWriter out für " + this.getName()
						+ " wird angelegt...");
				this.out = new PrintWriter(server.getOutputStream(), true);
				// synchronized(this.client_lock){
				this.req = commands.giveRequestOne("register", sequenceCounter);
				out.println(req.toJSONString());
				sequenceCounter++;
				// }

			}

		} catch (IOException ex) {
			System.err
					.println("Fehler bei Serverinitialisierung. Hier der Stacktrace:");
			ex.printStackTrace();
		} catch (NoSuchElementException ex) {
			System.err.println("Stream unterbrochen. Server geschlossen?");
			ClientGUI.getInstance().getStatus().setText("Disconnected");
			ClientGUI.getInstance().getStatus()
					.setBackground(ClientGUI.getInstance().disconnected);
			return;
		}

		System.out.println("Verbindung über Port: "
				+ ClientGUI.getInstance().getPortField().getText() + " Host: "
				+ ClientGUI.getInstance().getHostField().getText());
		while (!this.isStopped()) { // Solange this.isStopped false:
			/*
			 * HIER WIRD DER RESPONSE VERARBEITET!!!!
			 */
			String s = in.nextLine();
			System.out.println("Input Stream(Response vom Server): " + s);
			JSONParser parser = new JSONParser();
			Object obj;
			try {
				obj = parser.parse(s);
				this.res = (JSONObject) obj;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (res != null) {
				String status = (String) res.get("statuscode");
				Long longseq = new Long((Long) res.get("sequence"));
				String resText = (String) res.get("response");

				if (resText.equals("initLogin")) {
					// TODO Dialogfenster öffnen und Namen aufnehmen, setzen
					// im Client und per JSON an den Server schicken. Und in
					// die Liste eintragen im Server.
					ClientGUI.getInstance().showNameDialogue();
					String name = ClientGUI.getInstance().getNickName();
					this.sequenceCounter++;
					this.req = commands.giveRequestTwo("login",
							sequenceCounter, name);
					System.out.println("Login Request an den Server: "
							+ req.toString());
					out.println(req);

				}

				if (resText.equals("loginAdmission")) {
					String msg = (String) res.get("msg");
					ClientGUI.getInstance().showMessage("Server", msg);
					this.sequenceCounter++;
					this.req = commands.giveRequestOne("who", sequenceCounter);
					System.out.println("Fordere Teilnehmerliste von Server: "
							+ req.toString());
					out.println(req);
				}

				if (resText.equals("msgSent")) {
					String msg = (String) this.res.get("msg");
					System.out.println("Msg ist: " + msg);
					String author = (String) this.res.get("author");
					System.out.println("Author ist: " + author);
					ClientGUI.getInstance().showMessage(author, msg);

				}
				if (resText.equals("msgSent2Client")) {
					String msg = (String) this.res.get("msg");
					System.out.println("Msg ist: " + msg);
					String author = (String) this.res.get("author");
					String target = (String) this.res.get("target");
					System.out.println("Author ist: " + author);
					author = "[" + author + " --->" + target + "] ";
					ClientGUI.getInstance().showMessage(author, msg);
				}

				if (resText.equals("sendWho")) {
					synchronized (this.client_lock) {
						System.out.println("In WhoIs Case...");
						ClientGUI.getInstance().getListModel().clear();
						ClientGUI.getInstance().getListModel()
								.add(0, "Alle Teilnehmer");
						ClientGUI.getInstance().getListModel().add(1, "");
						JSONArray params = (JSONArray) res.get("params");

						for (int j = 0; j < params.size(); j++) {
							int index = 2;
							ClientGUI.getInstance().getListModel()
									.add(index, params.get(j).toString());
							index++;
						}
						System.out
								.println("Bin am Ende des Locks für WhoIs...");
					}
				}

				if (resText.equals("memberLeft")) {
					String msg = (String) this.res.get("msg");
					String author = (String) this.res.get("author");
					ClientGUI.getInstance().showMessage(author, msg);
					this.sequenceCounter++;
					this.req = commands.giveRequestOne("who", sequenceCounter);

				}
				if(resText.equals("server full")){
					String author = "Server";
					String msg = (String) this.res.get("response");
					ClientGUI.getInstance().showMessage(author, msg);
					return;
				}
				if (resText.equals("actualTime")) {
					String time = (String) this.res.get("time");
					String author = "Server";
					String alert = "Das aktuelle Datum und Uhrzeit: ";
					ClientGUI.getInstance().showMessage(author, alert);
					ClientGUI.getInstance().showMessage(author, time);
				}
				if (resText.equals("pahtRes")) {
					synchronized (this.client_lock) {
						JSONArray params = (JSONArray) res.get("files");
						ArrayList<String> files = new ArrayList<String>(params.size());
						for (int j = 0; j < params.size(); j++) {
							files.add(params.get(j).toString());
						}
						String author = "Server";
						ClientGUI.getInstance().showMessage(author, "**********************************");
						ClientGUI.getInstance().showMessage(author, "Y O U R  D I R E C T O R Y  : " );
						ClientGUI.getInstance().showMessage(author, "**********************************");
						for (String fs : files) {
							ClientGUI.getInstance().showMessage(author, fs);
						}
						
						
					}
				}
				if (resText.equals("logoutDone")) {
					return;
				}
			}
			System.out.println("in der while schleife...");

		}

		this.interrupt();
		System.out.println("Thread unterbrochen: " + this.isInterrupted());
	}

	public boolean isStopped() {
		synchronized (this.client_lock) {
			return stopped;
		}

	}

	public synchronized void sendMsgReq2All(String msg) {
		this.sequenceCounter++;
		JSONObject req = commands.giveRequestThree("send",
				this.sequenceCounter, msg, ClientGUI.getInstance()
						.getNickName());
		System.out.println("Sende Msg-JSON Request an Server: " + req);
		out.println(req);
	}

	public synchronized void sendMsg2Client(String msg, String target) {
		this.sequenceCounter++;
		JSONObject req = commands.giveRequestFour("msg<client>",
				this.sequenceCounter, msg, ClientGUI.getInstance()
						.getNickName(), target);
		out.println(req);
	}

	public synchronized void logOut() {
		this.sequenceCounter++;
		JSONObject req = commands.giveRequestTwo("logout", sequenceCounter,
				ClientGUI.getInstance().getNickName());
		out.println(req);
	}

	public synchronized void sendTimeRequest() {
		this.sequenceCounter++;
		JSONObject req = commands.giveRequestOne("time", sequenceCounter++);
		System.out.println("Sende time request an Server: "
				+ req.toJSONString());
		out.println(req);
	}

	public synchronized void sendPathRequest(String path) {
		this.sequenceCounter++;
		JSONObject req = commands.giveRequestTwo("isPath",
				this.sequenceCounter, path);
		out.println(req);
	}

	public void setStopped(boolean stopped) {
		synchronized (this.client_lock) {
			this.stopped = stopped;
			notifyAll();
		}

	}

	public Object getClient_lock() {
		return client_lock;
	}

}

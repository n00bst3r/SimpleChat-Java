package de.chatserver.components;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.chatserver.gui.ServerGUI;
import de.chatserver.handling.ClientHandler;

public class ServerThread extends Thread {
	public ServerSocket server;
	private static int threadNo = 1;
	public static boolean active = true;
	public static Object lock = new Object();
	private Socket client;
	public Scanner in;
	public PrintWriter out;
	private final int limit = 5;
	private int con = 1;
	public ArrayList<Socket> listWithoutName = new ArrayList<Socket>();
	public HashMap<String, Socket> keyList = new HashMap<String, Socket>();
	public ArrayList<String> names = new ArrayList<String>();

	public void run() {
		this.setName("ServerThread Nummer:" + ServerThread.threadNo);
		System.out.println(this.getName() + " ist gestartet...");
		System.out.println("Thread Status: " + this.getState());
		ServerThread.threadNo++;
		System.out.println("Beginn Run Methode: ");
		try {

			System.out.println("Serversocket wird angelegt...");
			synchronized (ServerThread.lock) {
				server = new ServerSocket(ServerGUI.getInstance()
						.getPortNumber());
				System.out.println("Serversocket läuft über Port: "
						+ server.getLocalPort());
				server.setReuseAddress(true);

			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		while (true) {

			try {

				System.out.println("Nun bin ich vor Server accept...");
				try {
					
						this.client = server.accept();
						ServerGUI.getInstance().id++;
					
					
				} catch (SocketException ex) {
					client.close();
					return;
				}
				System.out.println("Nun bin ich nach dem  Server accept...");
				System.out.println(this.getState());
				synchronized (ServerThread.lock) {
					new ClientHandler(client).start();
					System.out.println("Neuer Client von "
							+ server.getInetAddress() + " auf Client's Port  "
							+ client.getPort());
					if (ServerGUI.getInstance().id <= 5) {
						listWithoutName.add(this.client);
						System.out.println("Client no."
								+ ServerGUI.getInstance().id + "on Port"
								+ client.getPort()
								+ "added to the Clientpool of MailBox Server.");

					} else {
						// TODO Client mit Benachrichtung abweisen.
						Scanner in = new Scanner(this.client.getInputStream());
						out = new PrintWriter(this.client.getOutputStream(), true);
						System.out.println("Lehne Anfrage ab...");
						String s = in.nextLine();
						System.out.println("Input Stream(Request von Client): "
								+ s.toString());
						JSONParser parser = new JSONParser();
						Object obj;
						try {
							obj = parser.parse(s.toString());
							JSONObject req = (JSONObject) obj;
							Long longseq = new Long((Long) req.get("sequence"));
							Integer seq = longseq.intValue();
							JSONObject res = new JSONObject();
							res.put("statuscode", ServerGUI.STATUS_SERVICE_UNAVAILABLE);
							res.put("sequence", seq);
							res.put("response","server_full");
							out.println(res);
							client.close();
							
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							client.close();
							
							ServerGUI.getInstance().id--;
							e.printStackTrace();
						}
						
					}
				}// hier muss sync
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}

	}

	public static boolean isActive() {
		return active;
	}

	public static void setActive(boolean active) {
		synchronized (ServerThread.lock) {
			ServerThread.active = active;
		}
	}

	public ServerSocket getServer() {
		return server;
	}



	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public static int getThreadNo() {
		return threadNo;
	}
	/**
	 * Liefert ein Update darüber, wer alles akutell im Raum ist.(an alle Chatmember)
	 */
	public synchronized void updateMembers(){
		System.out.println("UPDATE getNAmes: "+ this.names);
		ArrayList<String> names = this.names;
		Iterator it = names.iterator();
		JSONObject res = new JSONObject();
		Random r = new Random();
		int sequenceCounter = r.nextInt(2000);
		Long longseq =  new Long(sequenceCounter);
		JSONArray params = new JSONArray();
		for (String n : names){
			params.add(n);
		}
		res.put("sequence", longseq);
		res.put("statuscode", ServerGUI.STATUS_CREATED);
		res.put("response", "sendWho");
		res.put("params", params);
		for (Socket s : listWithoutName){
			try {
				PrintWriter p = new PrintWriter(s.getOutputStream(), true);
				p.println(res);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Sendet eine Message an alle Chatteilnehmer.
	 */
	public synchronized void sendMsg2All(JSONObject req, String msg){
		JSONObject res = new JSONObject();
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		String author = (String)req.get("author");
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_OK);
		res.put("response", "msgSent");
		res.put("msg", msg);
		res.put("author", author);
		System.out.println("Message wird bestätigt: "+res.toJSONString());
		for (Socket s : listWithoutName){
			try {
				PrintWriter p = new PrintWriter(s.getOutputStream(), true);
				p.println(res);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public synchronized void sendMsg2Client(JSONObject req){
		String msg = (String)req.get("msg");
		String target = (String) req.get("target");
		String author = (String)req.get("author");
		JSONObject res = new JSONObject();
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_OK);
		res.put("response", "msgSent2Client");
		res.put("msg", msg);
		res.put("author", author);
		res.put("target",target);
		ArrayList<Socket>tmp = new ArrayList<Socket>();
		tmp.add(keyList.get(author));
		tmp.add(keyList.get(target));
		for (Socket s : tmp){
			try {
				PrintWriter p = new PrintWriter(s.getOutputStream(), true);
				p.println(res);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public synchronized void logout(JSONObject req){
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		JSONObject res = new JSONObject();
		String name = (String) req.get("params");
		System.out.println("Logout von Client mit Name: "+name);
		Socket t = this.keyList.get(name);
		ServerGUI.getInstance().setClientInactive(name);
		this.keyList.remove(name);
		System.out.println("In KeyList: "+keyList.toString());
		this.names.remove(name);
		this.listWithoutName.remove(t);
		ServerGUI.getInstance().id--;
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_OK);
		res.put("response", "memberLeft");
		res.put("author", "Server");
		res.put("msg", name+" hat den Chat verlassen.");
		for (Socket s : listWithoutName){
			try {
				PrintWriter p = new PrintWriter(s.getOutputStream(), true);
				p.println(res);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}	
		
	}
	/**
	 * Methode, die die Verbindung vom Spamsockets vom Server trennt.
	 * @param s
	 */
	public synchronized void removeSpamConnection(Socket s){
		this.listWithoutName.remove(s);
		if(ServerGUI.getInstance().id > 0){
			ServerGUI.getInstance().id--;
		}
		
		System.out.println("ID ist nun nach Remove: "+ServerGUI.getInstance().id);
		JSONObject res = new JSONObject();
		res.put("statuscode", ServerGUI.STATUS_UNAUTHORIZED);
		res.put("response", "Hau ab, du Spammer!");
		try {
			PrintWriter p = new PrintWriter(s.getOutputStream(), true);
			p.println(res);
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		
	}

	/**
	 * Liefert dem Client einen JSON mit allen Chatteilnehmern.
	 * @param req
	 * @return <b>JSONObject</b> res
	 */
	public synchronized JSONObject sendWho(JSONObject req){
		System.out.println("SendWho Liste getNAmes: "+ this.names);
		ArrayList<String> names = this.names;
		Iterator it = names.iterator();
		JSONObject res = new JSONObject();
		Long longseq = new Long((Long)req.get("sequence"));
		Integer seq = longseq.intValue();
		JSONArray params = new JSONArray();
		for (String n : names){
			params.add(n);
		}
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_CREATED);
		res.put("response", "sendWho");
		res.put("params", params);
		return res;
	}

}
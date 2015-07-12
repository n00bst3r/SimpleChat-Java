package de.chatserver.handling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.chatserver.components.ServerThread;
import de.chatserver.gui.ServerGUI;

/**
 * Klasse, die das Verhalten des Servers auf Clientanfragen handled.
 * 
 * @author Daniel/Phil
 * 
 */
public class ClientHandler extends Thread {

	private Socket toClient;
	private BufferedReader in;
	private PrintWriter out;
	private String name;

	public ClientHandler(Socket s) {
		this.toClient = s;

	}

	public void run() {
		try {

			Scanner in = new Scanner(toClient.getInputStream());
			out = new PrintWriter(toClient.getOutputStream(), true);
			// JSON Aufnehmen und handlen.
			while (true) {
				try {
					String s = in.nextLine();
					System.out.println("Input Stream(Request von Client): "
							+ s.toString());
					JSONParser parser = new JSONParser();
					Object obj = parser.parse(s.toString());
					JSONObject req = (JSONObject) obj;

					String command = (String) req.get("command");
					if (command.equals("register")) {
						// handlen
						out.println(welcome(req));
					}
					else if (command.equals("login")) {

						// out.println(admitLogin(req));

						JSONArray paramStrings = (JSONArray) req.get("params");
						String name = (String) paramStrings.get(0);
						// TODO Überprüfen, ob Name schon vorhanden.
						System.out
								.println("Client Nick wird zum Mapping übergeben: "
										+ name);

						ServerGUI.getInstance().setClientActive(name, toClient);
						ServerGUI.getInstance().serverThread.names.add(name);

						System.out.println("Führe update aus...");
						ServerGUI.getInstance().serverThread.updateMembers();
						out.println(admitLogin(req));

					}
					else if (command.equals("time")) {
						// handlen
						System.out.println("Verarbeite time-Anfrage...");
						out.println(timeResponse(req));
					}

					else if (command.equals("who")) {

						// handlen
						// TODO Alle Namen in einen JSON packen als
						// Parameter und dem Client übergeben.
						synchronized (ServerThread.lock) {
							System.out
									.println("Verarbeite den WhoIs Request...");
							out.println(ServerGUI.getInstance().serverThread
									.sendWho(req));
						}

					}
					
					else if (command.equals("isPath")) {
						// handlen
						out.println(sendPathResonse(req));

					}
					else if (command.equals("send")) {

						String msg = (String) req.get("msg");
						ServerGUI.getInstance().serverThread.sendMsg2All(req,
								msg);

					}
					else if (command.equals("msg<client>")) {
						ServerGUI.getInstance().serverThread
								.sendMsg2Client(req);

					}
					else if (command.equals("logout")) {
						synchronized (ServerThread.class) {
							System.out
									.println("ClientHandler: Gehe in den LogOut Zweig...");
							out.println(logOutRes(req));
							ServerGUI.getInstance().serverThread.logout(req);
							toClient.close();

							ServerGUI.getInstance().serverThread
									.updateMembers();
							return;
						}

					}
					else{
						
						ServerGUI.getInstance().serverThread.removeSpamConnection(toClient);
					}

				} catch (NoSuchElementException ex) {
					System.err
							.println("Kein Inputstream mehr vorhanden. Clientverbindung unterbrochen. Server eventuell voll.");
					
					return;
				} catch (ParseException ex) {
					System.err
							.println("Fehler beim Parsen des Request im ClientHandler. ");
					System.err.println();
				
					ServerGUI.getInstance().serverThread.removeSpamConnection(toClient);
					
					return;
				}
				catch(ClassCastException ex){
					ServerGUI.getInstance().serverThread.removeSpamConnection(toClient);
					return;
				}

			}// Hier endet While.

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * RESPONSE METHODEN:
	 */
	public synchronized static JSONObject timeResponse(JSONObject req) {
		JSONObject res = new JSONObject();
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		Calendar cal = Calendar.getInstance();
		Date time = cal.getTime();
		String timeString = new SimpleDateFormat().format(time) + " Uhr";
		System.out.println("Time-String: " + timeString);
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_OK);
		res.put("response", "actualTime");
		res.put("time", timeString);
		return res;
	}

	/**
	 * Liefert den Response nach Registrierungsrequest.
	 * 
	 * @param req
	 * @return
	 */
	public synchronized static JSONObject welcome(JSONObject req) {
		JSONObject res = new JSONObject();
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_CREATED);
		res.put("response", "initLogin");
		System.out.println("Sende Welcome Response an Client: "
				+ res.toJSONString());
		return res; // Nur zum Test:

	}

	/**
	 * Liefert den Response nach Anmeldung am Server.
	 * 
	 * @param req
	 * @return
	 */
	public synchronized static JSONObject admitLogin(JSONObject req) {
		JSONObject res = new JSONObject();
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_OK);
		res.put("response", "loginAdmission");
		res.put("msg", "Willkommen im Chat!");
		System.out.println("Sende Login Bestätigung an Client: "
				+ res.toJSONString());
		return res;
	}

	public synchronized static JSONObject logOutRes(JSONObject req) {
		JSONObject res = new JSONObject();
		String name = (String) req.get("name");
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_OK);
		res.put("response", "logoutDone");
		return res;
	}

	public synchronized static JSONObject sendPathResonse(JSONObject req) {
		JSONObject res = new JSONObject();
		String path = (String) req.get("params").toString();
		String pathConverted = path.replace("\\", " ");
		String pathConverted2 = pathConverted.replace("  ", "/");
		System.out.println("Converted Path2: " + pathConverted2);
		String con3 = pathConverted2.replace("[", "");
		String con4 = con3.replace("\"", "");
		String con5 = con4.replace("]", "");
		String con6 = con5.replace(" ", "");
		System.out.println("Con6: " + con6);
		File f = new File(con6);
		File[] listOfFiles = f.listFiles();
		JSONArray params = new JSONArray();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				params.add("File: " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				params.add("Folder: " + listOfFiles[i].getName());
			}
			
		}
		Long longseq = new Long((Long) req.get("sequence"));
		Integer seq = longseq.intValue();
		res.put("sequence", seq);
		res.put("statuscode", ServerGUI.STATUS_OK);
		res.put("response", "pahtRes");
		res.put("files", params);
		System.out.println(res.toJSONString());
		return res;

	}
}
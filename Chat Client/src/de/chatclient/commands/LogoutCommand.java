package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LogoutCommand implements ICommandTwo {


	/**
	 * Überladen excecute Methode, die noch einen zusätzlichen String
	 * für den Nickname entgegennimmt, der augeloggt werden soll.
	 * @param seq
	 * @param name
	 * @return
	 */
	public JSONObject execute(int seq, String name){
		JSONObject req = new JSONObject();
		req.put("sequence", seq);
		req.put("command", "logout");
		req.put("params", name);
		return req;
	}

}

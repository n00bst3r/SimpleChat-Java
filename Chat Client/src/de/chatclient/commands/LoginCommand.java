package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LoginCommand implements ICommand, ICommandTwo {

	public JSONObject execute(int seq) {
		System.err.println("LoginCommand: Bitte nur überladene excecute-Methode benutzen.");
		return null;
	}

	/**
	 * Überladene execute-Methode, die als zusätzlichen Parameter den Chatnamen
	 * entgegennimmt.
	 * 
	 * @param seq
	 * @param name
	 * @return
	 */
	public JSONObject execute(int seq, String name) {
		JSONObject req = new JSONObject();
		JSONArray params = new JSONArray();
		params.add(name);
		req.put("sequence", seq);
		req.put("command", "login");
		req.put("params", params);
		return req;
	}

}

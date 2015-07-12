package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class LoginCommand implements ICommand, ICommandTwo {

	public JSONObject execute(int seq) {
		System.err.println("LoginCommand: Bitte nur �berladene excecute-Methode benutzen.");
		return null;
	}

	/**
	 * �berladene execute-Methode, die als zus�tzlichen Parameter den Chatnamen
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

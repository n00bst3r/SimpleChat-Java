package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * Klasse für das Register-JSON. 
 * @author Daniel
 *
 */
public class RegisterCommand implements ICommand {

	public synchronized JSONObject execute(int seq) {
		JSONObject req = new JSONObject();
		JSONArray params = new JSONArray();
		req.put("sequence", seq);
		req.put("command", "register");
		req.put("params", params);
		System.out.println("Erzeugter Request: "+ req);
		return req;
	}

}
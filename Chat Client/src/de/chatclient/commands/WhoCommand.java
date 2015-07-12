package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WhoCommand implements ICommand{

	public JSONObject execute(int seq) {
		JSONObject req = new JSONObject();
		JSONArray params = new JSONArray();
		req.put("sequence", seq);
		req.put("command", "who");
		req.put("params", params);
		return req;
	}

}
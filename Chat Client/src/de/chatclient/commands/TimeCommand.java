package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Klasse für das Timestamp JSON. 
 * @author Daniel
 *
 */
public class TimeCommand implements ICommand {

	public JSONObject execute(int seq) {
		JSONObject req = new JSONObject();
		JSONArray params = new JSONArray();
		req.put("sequence", seq);
		req.put("command", "time");
		req.put("params", params);
		return req;
	}

}
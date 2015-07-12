package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Klasse f�r dem isPath Request.
 * 
 * @author Daniel
 * 
 */
public class PathCommand implements ICommandTwo {


	/**
	 * �berladene execute, die als zus�tzlichen Parameter einen Pfad entgegennimmt.
	 * @param seq
	 * @param path
	 * @return
	 */
	public JSONObject execute(int seq, String path) {
		JSONObject req = new JSONObject();
		JSONArray params = new JSONArray();
		params.add(path);
		req.put("sequence", seq);
		req.put("command", "isPath");
		req.put("params", params);
		return req;
	}

}
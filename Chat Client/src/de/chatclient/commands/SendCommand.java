package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Klasse f�r den Send-Request. Dies ist der Request f�r alle Messages, die an alle Clients gehen sollen.
 * @author Daniel
 *
 */
public class SendCommand implements ICommandThree {

	
	/**
	 * �berladene execute, die noch einen Message String entgegennimmt. 
	 * @param seq
	 * @param msg
	 * @return
	 */
	public JSONObject execute(int seq, String msg, String author){
		JSONObject req = new JSONObject();
		JSONArray params = new JSONArray();
		req.put("sequence", seq);
		req.put("command", "send");
		req.put("msg", msg);
		req.put("author", author);
		return req;
		
	}

}

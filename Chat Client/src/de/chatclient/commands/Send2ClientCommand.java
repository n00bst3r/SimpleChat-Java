package de.chatclient.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Klasse, die den JSON Request für eine Message an einen bestimmten User erstellt. 
 * @author Daniel
 *
 */
public class Send2ClientCommand implements ICommandFour {

	/**
	 * Überladen exectute, die einen String für die Message und 
	 * einen String für den Clientnamen entgegennimmt. 
	 * @param seq
	 * @param msg
	 * @param clientName
	 * @return
	 */
	public JSONObject execute(int seq, String msg, String author, String target){
		 JSONObject req = new JSONObject();
		 req.put("sequence", seq);
		 req.put("command", "msg<client>");
		 req.put("msg", msg);
		 req.put("author", author);
		 req.put("target",target);
		 return req;
		
	}

}

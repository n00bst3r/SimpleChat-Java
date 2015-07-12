package de.chatclient.commands;

import org.json.simple.JSONObject;
/**
 * 
 * @author Daniel
 *
 */
public interface ICommandFour {
	
	public JSONObject execute(int seq, String msg, String clientName, String target);

}

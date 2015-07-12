package de.chatclient.commands;

import org.json.simple.JSONObject;
/**
 * Interface für alle Commands mit drei Parametern. 
 * @author Daniel
 *
 */
public interface ICommandThree {
	
	
	public JSONObject execute(int seq, String msg, String clientName);

}
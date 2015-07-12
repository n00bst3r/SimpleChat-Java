package de.chatclient.commands;

import org.json.simple.JSONObject;
/**
 * Klasse für alle Commands mit zwei Parametern
 * @author Daniel
 *
 */
public interface ICommandTwo {

	
	/**
	 * Execute Methode mit 2 Parametern.
	 * @param seq
	 * @param para
	 * @return
	 */
	public JSONObject execute(int seq, String para);

}
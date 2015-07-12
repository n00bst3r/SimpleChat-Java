package de.chatclient.commands;

import org.json.simple.JSONObject;
/**
 * Command-Interface mit grundlegenden Konstanten und Methoden. 
 * @author Daniel
 *
 */
public interface ICommand {
	
	
	/**
	 * Default-Execute-Methode, die als Parameter einen Sequence Wert entgegennimmt und
	 * einen Request-JSON zurückgibt. 
	 * @param seq
	 * @return <b>JSONObject</b>
	 */
	public JSONObject execute(int seq);

}

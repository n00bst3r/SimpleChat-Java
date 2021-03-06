package de.chatclient.commands;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class CommandProvider {
	
	private static CommandProvider instance;
	
	
	public HashMap<String,ICommand> oneParaList = new HashMap<String,ICommand>(); //Liste f�r alle Commands mit einem Parameter
	{
		oneParaList.put("register", new RegisterCommand());
		oneParaList.put("time", new TimeCommand());
		oneParaList.put("who", new WhoCommand());
	}
	public HashMap<String,ICommandTwo> twoParaList = new HashMap<String,ICommandTwo>(); //Liste f�r alle Commands mit zwei Parametern
	{
		twoParaList.put("login", new LoginCommand());
		twoParaList.put("logout", new LogoutCommand());
		twoParaList.put("isPath", new PathCommand());

	}
	public HashMap<String,ICommandThree> threeParaList = new HashMap<String,ICommandThree>(); //Liste f�r alle Commands mit drei Parametern
	{
		
		threeParaList.put("send", new SendCommand());
	}
	
	public HashMap<String,ICommandFour> fourParaList = new HashMap<String,ICommandFour>();
	{
		fourParaList.put("msg<client>", new Send2ClientCommand());
	}
	
	private CommandProvider(){
		
	}
	
	public static CommandProvider getInstance(){
		if(instance == null)return new CommandProvider();
		return instance;
	}
	
	/**
	 * Sucht nach allen Commands mit einem Parameter.
	 * @param command
	 * @param seq
	 * @return
	 */
	public synchronized  JSONObject giveRequestOne(String command, int seq){
		ICommand request = oneParaList.get(command);
		return request.execute(seq);
	}
	/**
	 * Sucht nach allen Commmands mit zwei Parametern.
	 * @param command
	 * @param seq
	 * @param para
	 * @return
	 */
	public synchronized  JSONObject giveRequestTwo(String command, int seq, String para){
		ICommandTwo request = twoParaList.get(command);
		return request.execute(seq, para);
	}
	/**
	 * Sucht nach allen Commands mit drei Parametern. 
	 * @param command
	 * @param seq
	 * @param msg
	 * @param clientName
	 * @return
	 */
	public synchronized  JSONObject giveRequestThree(String command, int seq,String msg, String clientName){
		ICommandThree request = threeParaList.get(command);
		return request.execute(seq, msg, clientName);
	}
	/**
	 * Methode, die vier Parameter entgegen nimmt und einen bestimmten Client anschreibt. 
	 * @param command
	 * @param seq
	 * @param msg
	 * @param author
	 * @param target
	 * @return
	 */
	public synchronized  JSONObject giveRequestFour(String command, int seq,String msg, String author, String target){
		ICommandFour request = fourParaList.get(command);
		return request.execute(seq, msg, author, target);
	}
}

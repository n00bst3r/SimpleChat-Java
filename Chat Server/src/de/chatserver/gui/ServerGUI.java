package de.chatserver.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.LabelAlignment;
import de.chatserver.components.ServerThread;


/**
 * 
 * @author Daniel und Phil aka Philly Blunt
 *
 */
public class ServerGUI {
	
	private static ServerGUI instance; // Instanz für den Singleton Server
	
	public static String STATUS_OK = "200 - OK";
	public static String STATUS_CREATED = "204 - NO CONTENT";
	public static String STATUS_BAD_REQUEST = "400 - BAD REQUEST";
	public static String STATUS_UNAUTHORIZED = "401 - UNAUTHORIZED";
	public static String STATUS_TOO_MANY_REQUESTS = "429 - TOO MANY REQUESTS";
	public static String STATUS_UNIMPLEMENTED = "501 - NOT IMPLEMENTED";
	public static String STATUS_SERVICE_UNAVAILABLE = "503 - SERVICE UNAVAILABLE";
	
	public boolean busy1 = false;
	public boolean busy2 = false;
	public boolean busy3 = false;
	public boolean busy4 = false;
	public boolean busy5 = false;
	
	//GUI Scheiß
	public ServerThread serverThread;
	public Color connected = new Color(0, 139, 69);
	private  JFrame mainFrame;
	private  JPanel backgroundPanel;
	private JPanel mainViewPanel;
	
	public static final Font HEADERFONT = new Font("Calibri", Font.BOLD, 50);

	
	//Logik
	public int portNumber;
	public int id = 0;

	
	//MainView Elemente:
	private Color disconnected = new Color(178,34,34);
	private JLabel ueberschrift = new JLabel("Porteinstellung: ");
	private JLabel portnrLabel = new JLabel("Portnummer: ");
	private JTextField portField = new JTextField();
	{
		portField.setEditable(false);
		portField.setHorizontalAlignment(JTextField.CENTER);
		portField.setBackground(Color.lightGray);
	}
	
	JButton change = new JButton("Port ändern");
	{
		change.setActionCommand("change");
		change.addActionListener(new ServerButtonsController());
	}
	JButton save = new JButton("Port festlegen");
	{
		save.setEnabled(false);
		save.setBackground(Color.LIGHT_GRAY);
		save.setActionCommand("save");
		save.addActionListener(new ServerButtonsController());
	}
	
	//Client1: 
	
	JTextField client1 = new JTextField("Disconnected");
	{
		client1.setEditable(false);
		client1.setForeground(Color.WHITE);
		client1.setBackground(this.disconnected);
		client1.setHorizontalAlignment(JTextField.CENTER);
	}
	// Client2: 
	
	JTextField client2 = new JTextField("Disconnected");
	{
		client2.setEditable(false);
		client2.setForeground(Color.WHITE);
		client2.setBackground(this.disconnected);
		client2.setHorizontalAlignment(JTextField.CENTER);
	}
	
	//Client3: 
	
	JTextField client3 = new JTextField("Disconnected");
	{
		client3.setEditable(false);
		client3.setForeground(Color.WHITE);
		client3.setBackground(this.disconnected);
		client3.setHorizontalAlignment(JTextField.CENTER);
	}
	
	//Client4: 
	
	JTextField client4 = new JTextField("Disconnected");
	{
		client4.setEditable(false);
		client4.setForeground(Color.WHITE);
		client4.setBackground(this.disconnected);
		client4.setHorizontalAlignment(JTextField.CENTER);
	}
	
	//Client5: 
	
	JTextField client5 = new JTextField("Disconnected");
	{
		client5.setEditable(false);
		client5.setForeground(Color.WHITE);
		client5.setBackground(this.disconnected);
		client5.setHorizontalAlignment(JTextField.CENTER);
	}
	//ServerControll Line1:
	JTextField serverStatus = new JTextField("stopped");
	{
		serverStatus.setEditable(false);
		serverStatus.setForeground(Color.WHITE);
		serverStatus.setBackground(this.disconnected);
		serverStatus.setHorizontalAlignment(JTextField.CENTER);
	}
	//ServerControll Line2:
	
	JButton start = new JButton("Starte den Server");
	{
		start.setActionCommand("start");
		start.addActionListener(new ServerButtonsController());
	}
	JButton stop = new JButton("Stoppe den Server");
	{
		stop.setActionCommand("stop");
		stop.setEnabled(false);
		stop.setBackground(Color.LIGHT_GRAY);
		stop.addActionListener(new ServerButtonsController());
	}
	
	
	/**
	 * Privater Konstruktor aufgrund des Singleton Patterns. 
	 */
	private ServerGUI(){
		this.mainFrame = new JFrame("Mailbox Server");
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setPreferredSize(new Dimension(1280, 768));
		this.mainFrame.setPreferredSize(new Dimension(1280, 768));
		this.mainFrame.setMinimumSize(new Dimension(1280, 768));
		this.mainFrame.setLocationRelativeTo(null);
		init();
		this.mainFrame.setVisible(true);
		
	}
	/**
	 * Initiiert die grafische Oberfläche. 
	 */
	private void init(){
		BorderLayout layoutBackground = new BorderLayout();
		this.backgroundPanel = new JPanel(layoutBackground);
		this.backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.backgroundPanel.setBackground(Color.GRAY);
		this.mainFrame.add(this.backgroundPanel);
		
		/*
		 * Main-View: 		 
		 */
		this.mainViewPanel = new JPanel();
		this.backgroundPanel.add(BorderLayout.CENTER, this.mainViewPanel);
		DesignGridLayout layout = new DesignGridLayout(mainViewPanel);
		layout.labelAlignment(LabelAlignment.RIGHT);
		layout.row(Component.LEFT_ALIGNMENT);
		
		layout.row().left().add(this.ueberschrift);
		layout.row().center().fill().add(new JSeparator());
		layout.emptyRow();
		layout.row().grid(this.portnrLabel, 2).add(this.portField).add(this.change).add(this.save);
		layout.emptyRow();
		layout.row().left().add(new JLabel("Client-Verbindungen: "));
		layout.row().center().fill().add(new JSeparator());
		layout.emptyRow();
		layout.row().grid(new JLabel("Verbindungsstatus Client 1: "), 2).add(this.client1);
		layout.row().grid(new JLabel("Verbindungsstatus Client 2: "), 2).add(this.client2);
		layout.row().grid(new JLabel("Verbindungsstatus Client 3: "), 2).add(this.client3);
		layout.row().grid(new JLabel("Verbindungsstatus Client 4: "), 2).add(this.client4);
		layout.row().grid(new JLabel("Verbindungsstatus Client 5: "), 2).add(this.client5);
		layout.emptyRow();
		layout.row().left().add(new JLabel("Server-Funktionen: "));
		layout.row().center().fill().add(new JSeparator());
		layout.emptyRow();
		layout.row().grid(new JLabel("Server-Status: "), 2).add(this.serverStatus);
		layout.emptyRow();
		layout.row().center().add(this.start).add(this.stop);
	}
	/**
	 * Gibt das Singleton Serverobjekt zurück. 
	 * @return <b>Server</b>
	 */
	public static ServerGUI getInstance(){
		synchronized(ServerThread.lock){
		if(ServerGUI.instance == null) ServerGUI.instance = new ServerGUI();
		return ServerGUI.instance;
		}
	}
	/**
	 * Verwaltet die Anfragen der Clients und nimmt sie bei freier Kapazität in den Clientpool auf. 
	 * Jeder Client wird einem Clienthandler übergeben. Dieser Clienthandler handled dann alle
	 * Anfragen des Clients.
	 * 
	 * @throws IOException
	 */
	public void waitForClients() throws IOException{
			serverThread = new ServerThread();
			serverThread.start(); 
			}
	/**
	 * Skaliert ein Bild auf eine bestimmte Größe. 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage resize(BufferedImage image, int width, int height) {
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
	}
	/*
	 * Handler Methoden: 
	 */
	public synchronized static void sendMsg2All(String msg){
		//TODO Methode, die JSON mit Nachricht an alle Clients übergibt. Namen noch hinzufügen!! (Also des Absenders)
//		Socket s;
//		PrintWriter p;
//		for (ListIterator list = clientList.listIterator(); list.hasNext();) {
//			s = (Socket) list.next();
//			p = new PrintWriter(s.getOutputStream(), true);
//			p.println(name + ": " + message);
//		}
	}
	
	
	
	public static void killThread(){
		synchronized(ServerThread.lock){
			ServerThread.setActive(false);
			try {
				ServerGUI.getInstance().serverThread.server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("killThread ausgeführt. --> Server geschlossen.");
		   //Notify???l
			ServerThread.lock.notify();
		}

	}
	
	public synchronized static void sendMsg2Client(String msg, String name){
		//TODO Methode, die JSON mit Nachricht an bestimmten Client übergibt. 
	}
	
	public synchronized static void sendTime(){
		//TODO schickt Zeit zu Client, von dem der Request kommt. 
		
	}
	
	
	/**
	 * Setzt den Client auf aktiv im Server und mappt den Namen des Clients.
	 * @param name
	 * @param client
	 */
	public synchronized void setClientActive(String name, Socket client){
		if (!ServerGUI.getInstance().busy1){
			ServerGUI.getInstance().busy1 = true;
			ServerGUI.getInstance().client1.setText(name+" connected");
			ServerGUI.getInstance().client1.setBackground(ServerGUI.getInstance().connected);
			ServerGUI.getInstance().serverThread.keyList.put(name, client);
		}else if(!ServerGUI.getInstance().busy2){
			ServerGUI.getInstance().busy2 = true;
			ServerGUI.getInstance().client2.setText(name+" connected");
			ServerGUI.getInstance().client2.setBackground(ServerGUI.getInstance().connected);
			ServerGUI.getInstance().serverThread.keyList.put(name, client);
		}else if(!ServerGUI.getInstance().busy3){
			ServerGUI.getInstance().busy3 = true;
			ServerGUI.getInstance().client3.setText(name+" connected");
			ServerGUI.getInstance().client3.setBackground(ServerGUI.getInstance().connected);
			ServerGUI.getInstance().serverThread.keyList.put(name, client);
		}else if(!ServerGUI.getInstance().busy4){
			ServerGUI.getInstance().busy4 = true;
			ServerGUI.getInstance().client4.setText(name+" connected");
			ServerGUI.getInstance().client4.setBackground(ServerGUI.getInstance().connected);
			ServerGUI.getInstance().serverThread.keyList.put(name, client);
		}else if(!ServerGUI.getInstance().busy5) {
			ServerGUI.getInstance().busy5 = true;
			ServerGUI.getInstance().client5.setText(name+" connected");
			ServerGUI.getInstance().client5.setBackground(ServerGUI.getInstance().connected);
			ServerGUI.getInstance().serverThread.keyList.put(name, client);
		}else{
			System.out.println("Alle Client-Slots belegt.");
		}
	}
	
	public synchronized void setClientInactive(String name){
		if (ServerGUI.getInstance().client1.getText().equals(name+" connected")){
			ServerGUI.getInstance().busy1 = false;
			ServerGUI.getInstance().client1.setText("Disconnected");
			ServerGUI.getInstance().client1.setBackground(ServerGUI.getInstance().disconnected);
			;
		}else if(ServerGUI.getInstance().client2.getText().equals(name+" connected")){
			ServerGUI.getInstance().busy2 = false;
			ServerGUI.getInstance().client2.setText("Disconnected");
			ServerGUI.getInstance().client2.setBackground(ServerGUI.getInstance().disconnected);
			
		}else if(ServerGUI.getInstance().client3.getText().equals(name+" connected")){
			ServerGUI.getInstance().busy3 = false;
			ServerGUI.getInstance().client3.setText("Disconnected");
			ServerGUI.getInstance().client3.setBackground(ServerGUI.getInstance().disconnected);
			
		}else if(ServerGUI.getInstance().client4.getText().equals(name+" connected")){
			ServerGUI.getInstance().busy4 = false;
			ServerGUI.getInstance().client4.setText("Disconnected");
			ServerGUI.getInstance().client4.setBackground(ServerGUI.getInstance().disconnected);
			
		}else if(ServerGUI.getInstance().client5.getText().equals(name+" connected")){
			ServerGUI.getInstance().busy5 = false;
			ServerGUI.getInstance().client5.setText("Disconnected");
			ServerGUI.getInstance().client5.setBackground(ServerGUI.getInstance().disconnected);
		
		}else{
			System.out.println("Alle Client-Slots belegt.");
		}
		
	}
	
	public int getPortNumber() {
		synchronized (ServerThread.lock) {
		return portNumber;
		}
	}
	public void setPortNumber(int portNumber) {
		synchronized (ServerThread.lock) {
		this.portNumber = portNumber;
		}
	}
	public static void main(String[] args) {
		synchronized (ServerThread.lock) {
			ServerGUI.getInstance();
		}
		
	}
	
	class ServerButtonsController implements ActionListener{
		
		private String command;

		public void actionPerformed(ActionEvent e) {
			this.command = e.getActionCommand();
			
			if(command.equals("change")){
				ServerGUI.getInstance().portField.setEditable(true);
				ServerGUI.getInstance().portField.setBackground(Color.WHITE);
				ServerGUI.getInstance().change.setEnabled(false);
				ServerGUI.getInstance().save.setEnabled(true);
				ServerGUI.getInstance().save.setBackground(new JButton().getBackground());
				ServerGUI.getInstance().change.setBackground(Color.LIGHT_GRAY);
			}
			
			if(command.equals("save")){
				ServerGUI.getInstance().portNumber = Integer.parseInt(ServerGUI.getInstance().portField.getText());
				ServerGUI.getInstance().portField.setEditable(false);
				ServerGUI.getInstance().portField.setBackground(Color.LIGHT_GRAY);
				ServerGUI.getInstance().change.setEnabled(true);
				ServerGUI.getInstance().change.setBackground(new JButton().getBackground());
				ServerGUI.getInstance().save.setEnabled(false);
				ServerGUI.getInstance().save.setBackground(Color.LIGHT_GRAY);
			}
			
			if(command.equals("start")){
				if (ServerGUI.getInstance().portNumber == 0) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane
							.showMessageDialog(
									backgroundPanel,
									"Bitte geben Sie eine gültigen Portnummer ein.",
									"Keine Porteingabe",
									JOptionPane.ERROR_MESSAGE);
					return;

				}
				try{
					ServerThread.setActive(true);
					ServerGUI.getInstance().waitForClients();
					ServerGUI.getInstance().serverStatus.setBackground(connected);
					ServerGUI.getInstance().serverStatus.setText("Running");
					ServerGUI.getInstance().start.setBackground(Color.LIGHT_GRAY);
					ServerGUI.getInstance().start.setEnabled(false);
					ServerGUI.getInstance().stop.setBackground(new JButton()
							.getBackground());
					ServerGUI.getInstance().stop.setEnabled(true);
					
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
			
			if(command.equals("stop")){
				ServerGUI.getInstance().serverStatus
				.setBackground(disconnected);
				ServerGUI.getInstance().serverStatus.setText("Stopped");
				ServerGUI.getInstance().stop.setBackground(Color.LIGHT_GRAY);
				ServerGUI.getInstance().stop.setEnabled(false);
				ServerGUI.getInstance().start.setBackground(new JButton()
						.getBackground());
				ServerGUI.getInstance().start.setEnabled(true);
				ServerGUI.killThread();
			}
			
		}
		
	}

}

package de.chatclient.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.LabelAlignment;
import de.chatclient.components.ClientThread;


/**
 * Client-Klasse
 * 
 * @author Daniel und Phil aka Philly Blunt
 *
 */
public class ClientGUI {
	
	private static ClientGUI instance;
	//Klassenvariablen: 
	public static final Font HEADERFONT = new Font("Calibri", Font.BOLD, 50);
	
	//Instanzvariablen GUI:
	private ClientThread clientThread = new ClientThread();
	public Color disconnected = new Color(178, 34, 34);
	public Color connected = new Color(0, 139, 69);
	private  JFrame mainFrame;
	private String nickName;
	private  JPanel backgroundPanel;
	private JPanel mainViewPanel;
	private JPanel northPanel;
	private JPanel westView;
	private JLabel picLabel;
	public String listchoice;
	private JPanel enterPanel;
	public JButton startConnect = new JButton("Verbinde mit Server");
	{
		this.startConnect.setActionCommand("connect");
		this.startConnect.addActionListener(new ClientButtonsController());
	}
	public JTextField status = new JTextField();
	{
		this.status.setEditable(false);
		this.status.setForeground(Color.WHITE);
		this.status.setBackground(disconnected);
		this.status.setText("Disconnected");
		this.status.setHorizontalAlignment(JTextField.CENTER);
	}
	//MainView Formular:
	
	private JTextField hostField = new JTextField();
	{
		this.hostField.setEditable(false);
		this.hostField.setHorizontalAlignment(JTextField.CENTER);
		this.hostField.setBackground(Color.lightGray);
	}
	private JTextField portField = new JTextField();
	{
		this.portField.setEditable(false);
		this.portField.setHorizontalAlignment(JTextField.CENTER);
		this.portField.setBackground(Color.lightGray);
	}
	private JButton saveButton = new JButton("Verbindungseinstellungen speichern");
	{
		this.saveButton.setActionCommand("save");
		this.saveButton.addActionListener(new ClientButtonsController());
		this.saveButton.setEnabled(false);
		this.saveButton.setBackground(Color.LIGHT_GRAY);
		
	}
	private JButton editButton = new JButton("Verbindungseinstellungen ändern");
	{
		this.editButton.setActionCommand("Edit");
		this.editButton.addActionListener(new ClientButtonsController());
	}

	//Liste für die Chat-Teilnehmer:
	

	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	{
		listModel.add(0, "Alle Teilnehmer");
		listModel.add(1, "");

		
	}
	
	JList<String> list = new JList<String>( listModel );
	{
		 MouseListener mouseListener = new MouseAdapter() {
		      public void mouseClicked(MouseEvent mouseEvent) {
		    	 JList theList = (JList) mouseEvent.getSource();
		        if (mouseEvent.getClickCount() == 1) {
		          int index = theList.locationToIndex(mouseEvent.getPoint());
		          if (index >= 0) {
		            Object o = theList.getModel().getElementAt(index);
		            listchoice = o.toString();
		            System.out.println(listchoice);
		          }
		        }
		      }
		    };
		    list.addMouseListener(mouseListener);
	}

	private JScrollPane scroller =
	 			new JScrollPane(
				list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
 				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
 		
 	

 	
 	//Panel für den Chat: 
 		 public JTextArea chat = new JTextArea();{
 		 		this.chat.setEditable(false);
// 		 		this.chat.setText("ChatFläche");
 		 		this.chat.setBackground(Color.WHITE);
// 		 		chat.setPreferredSize(new Dimension(500,390));
 		 		chat.setAlignmentX(SwingConstants.LEFT);
 		 		chat.setAlignmentY(SwingConstants.TOP);
 		 		chat.setLineWrap(true);
 		 		chat.setWrapStyleWord(true);
 		 		DefaultCaret caret = (DefaultCaret) chat.getCaret(); // ←
 		 		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
 		 	}
 		 JScrollPane scroller2 =
 		 			new JScrollPane(
 				chat, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
 						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 		 {
 			 scroller2.setPreferredSize(new Dimension(500,390));
 		 }
 
 	private JPanel chatComplete = new JPanel();
 	{
 		chatComplete.setLayout(new FlowLayout(FlowLayout.CENTER));
 		scroller.setPreferredSize(new Dimension(150,500));
 		scroller2.setPreferredSize(new Dimension(600,500));
 		this.chatComplete.add(scroller);
 		this.chatComplete.add(scroller2);
 	
 	}
 	
 	private JTextField msgField = new JTextField(50);
 	private JButton send = new JButton("Absenden");
 	{
 		this.send.setActionCommand("send");
 		this.send.addActionListener(new ClientButtonsController());
 	}
 	
 	private JButton pathBtn = new JButton("IsPath");
 	{
 		this.pathBtn.setActionCommand("isPath");
 		this.pathBtn.addActionListener(new ClientButtonsController());
 	}
 	
 	private JButton timeBtn = new JButton("Uhrzeit");
 	{
 		this.timeBtn.setActionCommand("time");
 		this.timeBtn.addActionListener(new ClientButtonsController());
 	}
 	

	 /**
	  * Konstruktor der Clientklasse		
	  */
	private ClientGUI(){
		this.mainFrame = new JFrame("Mailbox Client");
		this.mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClientGUI.class.getResource("/de/chatclient/images/clientIcon.png")));
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.mainFrame.setLocationRelativeTo(null);
		init();
		this.mainFrame.setVisible(true);
	}
	
	public synchronized static ClientGUI getInstance(){
		if (ClientGUI.instance == null)ClientGUI.instance = new ClientGUI();
		return ClientGUI.instance;
	}
	/**
	 * Initialisierungsmethode für die GUI und dessen Layout.
	 */
	private void init(){
		
		BorderLayout layoutBackground = new BorderLayout();
		this.backgroundPanel = new JPanel(layoutBackground);
		this.backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.backgroundPanel.setBackground(Color.GRAY);
		JScrollPane scrPane = new JScrollPane(this.backgroundPanel);
		this.mainFrame.add(scrPane);
		this.mainFrame.add(scrPane);
		/*
		 * North-Panel:
		 */
		this.northPanel = new JPanel();
		JLabel header = new JLabel(" MailBox Client");
		header.setForeground(Color.black);
		header.setFont(HEADERFONT);
		try{
			BufferedImage myPicture = ImageIO.read(this.getClass().getResource("de/chatclient/images/clientIcon.png"));
			BufferedImage resizedImage= resize(myPicture,100,100);
			
			this.picLabel = new JLabel(new ImageIcon(resizedImage));
		} catch (IOException ex) {
			System.err.println("Image konnte nicht geladen werden.");
			ex.printStackTrace();
		}
		
		
		this.northPanel.add(picLabel);
		this.northPanel.add(header);
		this.backgroundPanel.add(BorderLayout.NORTH, this.northPanel);
		
		
		/*
		 * Main-View: 		 
		 */
		this.mainViewPanel = new JPanel();
		this.backgroundPanel.add(BorderLayout.CENTER, this.mainViewPanel);
		DesignGridLayout layout = new DesignGridLayout(mainViewPanel);
		layout.labelAlignment(LabelAlignment.RIGHT);
		layout.row(Component.LEFT_ALIGNMENT);
		
		layout.emptyRow();
		layout.row().left().add(new JLabel("Verbindungseinstellungen"));
		layout.row().center().fill().add(new JSeparator());
		layout.emptyRow();
		layout.row().grid(new JLabel("Host: "), 2).add(this.hostField).grid(new JLabel("Port: "),2).add(this.portField);
		layout.emptyRow();
		layout.row().center().add(this.editButton).add(this.saveButton);
		layout.emptyRow();
		layout.row().left().add(new JLabel("Serververbindung:"));
		layout.row().center().fill().add(new JSeparator());
		layout.emptyRow();
		layout.row().grid(new JLabel("Verbindungsstatus: "), 3).add(this.status).add(this.startConnect);
		layout.emptyRow();
		layout.row().left().add(new JLabel("Chat:"));
		layout.row().center().fill().add(new JSeparator());
		layout.emptyRow();
		
		
		//South-View:
		
		BorderLayout southLayout = new BorderLayout();
		JPanel southPanel = new JPanel(southLayout);
		this.backgroundPanel.add(BorderLayout.SOUTH,southPanel);
		southPanel.add(BorderLayout.CENTER,this.chatComplete);
		
		this.enterPanel = new JPanel();
		southPanel.add(BorderLayout.SOUTH,this.enterPanel);
		DesignGridLayout enterLayout = new DesignGridLayout(enterPanel);
		layout.labelAlignment(LabelAlignment.PLATFORM);
		layout.row(Component.LEFT_ALIGNMENT);
		
		enterLayout.emptyRow();
		enterLayout.row().center().fill().add(new JSeparator());
		enterLayout.row().center().add(this.msgField);
		enterLayout.row().center().add(this.pathBtn).add(this.send).add(this.timeBtn);
	
		
	
	}
	/**
	 * Methode für die Skalierung von Bildern. 
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
	/**
	 * Methode, die die Verbinung zum Server herstellt. 
	 */
	public void startConnection2Server(){
		clientThread.start();
		
		
	}
	
	public void stopConnection2Server() {
				
		clientThread.setStopped(true);
	}
	/**
	 * Einstiegspunkt des Client-Programms.
	 * @param args
	 */
	public static void main(String[] args) {
		ClientGUI c = ClientGUI.getInstance();
	}
	
	
	
	public DefaultListModel<String> getListModel() {
		return listModel;
	}

	public synchronized void setListModel(DefaultListModel<String> listModel) {
		this.listModel = listModel;
	}

	public JTextField getHostField() {
		return hostField;
	}

	public void setHostField(JTextField hostField) {
		this.hostField = hostField;
	}

	public JTextField getPortField() {
		return portField;
	}

	public void setPortField(JTextField portField) {
		this.portField = portField;
	}
	
	
	public JTextField getStatus() {
		return status;
	}

	public void setStatus(JTextField status) {
		this.status = status;
	}
	
	
	
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	

	public synchronized JList getList() {
		return list;
	}

	public synchronized void setList(JList list) {
		this.list = list;
	}

	public synchronized void showNameDialogue(){
		String nick = (String) JOptionPane.showInputDialog( "Bitte geben Sie ihren Nickname ein:" );
		ClientGUI.getInstance().setNickName(nick);
		ClientGUI.getInstance().getStatus().setText(nickName+" connected");
	}

	public synchronized void showMessage(String name, String msg){
		ClientGUI.getInstance().chat.setText(ClientGUI.getInstance().chat.getText()+"\n"+"   ["+name+"]   "+msg);
	}
	
	public synchronized void createNewList(String names[]){
		
		
	}
	
	



	public JScrollPane getScroller() {
		return scroller;
	}

	public void setScroller(JScrollPane scroller) {
		this.scroller = scroller;
	}





	class ClientButtonsController implements ActionListener {
		
		private String command;

		public void actionPerformed(ActionEvent e) {
			
			this.command = e.getActionCommand();
			
			if(this.command.equals("Edit")){
				editButton.setEnabled(false);
				saveButton.setEnabled(true);
				saveButton.setBackground(new JButton().getBackground());
				hostField.setBackground(Color.WHITE);
				hostField.setEditable(true);
				portField.setBackground(Color.WHITE);
				portField.setEditable(true);
				
			}
			
			if(this.command.equals("save")){
				editButton.setEnabled(true);
				saveButton.setEnabled(false);
				editButton.setBackground(new JButton().getBackground());
				saveButton.setBackground(Color.LIGHT_GRAY);
				hostField.setBackground(Color.LIGHT_GRAY);
				hostField.setEditable(false);
				portField.setBackground(Color.LIGHT_GRAY);
				portField.setEditable(false);
			}
			
			if(this.command.equals("disconnect")){
				startConnect.setActionCommand("connect");
				startConnect.setText("Verbinde mit Server");
				status.setBackground(disconnected);
				status.setText("Disconnected");
				System.out.println("CLientButtonController leitet logOut ein...");
				clientThread.logOut();
				
			}
			if(this.command.equals("connect")){
				startConnect.setActionCommand("disconnect");
				startConnect.setText("Verbindung zum Server trennen");
				status.setBackground(connected);
				status.setText("Connected");
				startConnection2Server();  
				
			}
			
			if(this.command.equals("send")){
				if(listchoice.equals("Alle Teilnehmer")){
					String msg = msgField.getText();
					System.out.println("Message aufgenommen im Comtroller: "+msg);
					clientThread.sendMsgReq2All(msg);
					msgField.setText("");
				}else{
					String msg = msgField.getText();
					System.out.println("Target ist: "+listchoice);
					clientThread.sendMsg2Client(msg, listchoice);
					msgField.setText("");
				}
				
			}
			
			if(this.command.equals("isPath")){
				// /Users/Daniel/C++
				// /Users/Daniel/workspace
				String path = msgField.getText();
				System.out.println("ClientButtonController: sende path-Request mit Pfad: "+path);
				clientThread.sendPathRequest(path);
				msgField.setText("");
			}
			
			if(this.command.equals("time")){
				System.out.println("ClientButtonController: sende time-Request...");
				clientThread.sendTimeRequest();
			}
		}
		
	}

}

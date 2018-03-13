import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Vrav extends Applet implements Runnable
{
	private static final long serialVersionUID = -4297335882692216363L;

	Socket socket;
	TextArea t1 = new TextArea();
	boolean zapisuje = false;
	Thread th;
	private static final int port = 2004;
	boolean server;
	
	ArrayList<ClientHandler> clients = new ArrayList<>();
	
	ObjectOutputStream wr; 
	ObjectInputStream rd;
	
	public void init()
	{
		add(t1); 
		//listenery();		
		Thread th = new Thread(this);
		th.start();
	}
	
	private void createConnection()
	{
		try {
		    
		    try {
		    	// first try to connect to other party, if it is already listening
			    socket = new Socket("localhost", port);
			    System.out.println("Vytvoreny socket pre odosielanie");
			    server = false;
		    } catch (ConnectException e) {
		    	server = true;
		    	// otherwise create a listening socket and wait for the other party to connect
			    System.out.println("Druha strana este nie je pripravena, cakam na spojenie...");
		    	ServerSocket srv = new ServerSocket(port);
			    socket = srv.accept();
		    	clients.add(new ClientHandler());
		    	add(new TextArea());
			    System.out.println("Vytvoreny socket pre prijimanie");
			}
		    wr = new ObjectOutputStream(socket.getOutputStream());
            rd = new ObjectInputStream(socket.getInputStream());
            sendInfoFromOtherClients();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void paint()
	{
		
	}

	public void listenery()
	{
		t1.addTextListener(new TextListener(){
			@Override
			public void textValueChanged(TextEvent arg0) {
				//TextComponent tc = (TextComponent)arg0.getSource();
			    //String messageOut = tc.getText();
				String messageOut = t1.getText();
				posliSpravu(messageOut);
			}			
		});
	}

	public void posliSpravu(String message)
	{
		byte bts[] = message.getBytes();
		    try {
			    wr.write(bts.length & 255);
			    wr.write(bts.length >> 8);
			    wr.write(bts, 0, bts.length);
			    wr.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void run() 
	{
		createConnection();

		while(prijmiSpravu());
		System.out.println("Koniec rozhovoru.");
		try {
  		  socket.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public boolean prijmiSpravu()
	{
			try {
				ArrayList<ClientHandler> cl = (ArrayList<ClientHandler>) rd.readObject();
				for(ClientHandler c : cl){
					
					add( new TextArea());
					
					//this.repaint();
				}
			} catch (Exception e) {
				
			}
			//if(!bts.equals("")) t1.setText(new String(bts));
		return true;
	}
	
	public void sendInfoFromOtherClients() throws IOException{
		   wr.writeObject(clients);
		   wr.flush();
	}
}

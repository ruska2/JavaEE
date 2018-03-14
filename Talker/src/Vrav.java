import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import msg.AddAreaMsg;

public class Vrav extends Applet implements Runnable
{
	private static final long serialVersionUID = -4297335882692216363L;

	Socket socket;
	TextArea t1 = new TextArea();
	Thread th;
	private static final int port = 2004;
	public int counter;
	ServerSocket srv;
	
	HashMap<Integer,ClientHandler> clients = new HashMap<>();
	HashMap<Integer,TextArea> textAreas = new HashMap<>();
	
	ObjectOutputStream wr; 
	ObjectInputStream rd;
	
	@Override
	public void init()
	{
		add(t1); 	
		Thread th = new Thread(this);
		th.start();
		System.out.println("thread started");
	}
	
	private void waitingForConnection() throws IOException
	{
			Socket s = srv.accept();
			counter++;
			System.out.println("client" + counter +" connected");
		    ClientHandler client = new ClientHandler(s, this, counter);
			//clients.put(counter,client);
		    TextArea clientArea = new TextArea();
		    textAreas.put(counter, clientArea);
		    System.out.println("client" + counter +" connected");
		    add(clientArea);
		    this.doLayout();
		    client.addTextArea(0);
			
			new Thread(client).start();
			
	}
	
	public void listenery()
	{
		t1.addTextListener(new TextListener(){
			@Override
			public void textValueChanged(TextEvent arg0) {
				String messageOut = t1.getText();
				//Send msg to other clients;
			}			
		});
	}


	public void run() 
	{
		try{
			  socket = new Socket("localhost", port);
			  wr = new ObjectOutputStream(socket.getOutputStream());
			  rd = new ObjectInputStream(socket.getInputStream());
			  while(prijmiSpravu());
		}catch(Exception e){
			try {
				srv = new ServerSocket(port);
				System.out.println("ServerClientCreated");
				while(true){
					waitingForConnection();
					System.out.println("waiting again client connected");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		
	}
	
	public boolean prijmiSpravu()
	{
			try {
				AddAreaMsg cl = (AddAreaMsg) rd.readObject();
				add(new TextArea());
			} catch (Exception e) {
				return false;
			}
		return true;
	}
}

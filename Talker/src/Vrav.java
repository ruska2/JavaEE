import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map.Entry;
import msg.AddAreaMsg;
import msg.Textt;

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
		listeners();
	}
	
	void listeners() {
		t1.addTextListener(new TextListener() {
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				// TODO Auto-generated method stub
				String msg = t1.getText();
				sendMsg(msg);
			}
		});
	}
	
	private void sendMsg(String msg) {
		try {
			wr.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void waitingForConnection() throws IOException
	{
			Socket s = srv.accept();
			counter++;
		    ClientHandler client = new ClientHandler(this, s, counter);
			clients.put(counter,client);
		    TextArea clientArea = new TextArea();
		    clientArea.setEditable(false);
		    textAreas.put(counter, clientArea);
		    //System.out.println("client" + counter +" connected");
		    add(clientArea);
		    this.doLayout();
		    client.addTextArea(0);
			new Thread(client).start();
			//System.out.println(clients);
			
			for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
				if(counter != c.getKey()) {
					c.getValue().addTextArea(counter);
					client.addTextArea(c.getKey());
				}
			}
			
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
			  while(getMsg());
		}catch(Exception e){
			try {
				srv = new ServerSocket(port);
				//System.out.println("ServerClientCreated");
				while(true){
					waitingForConnection();
					//System.out.println("waiting again client connected");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		
	}
	
	public boolean getMsg()
	{
			try {
				AddAreaMsg cl = (AddAreaMsg) rd.readObject();
				TextArea area = new TextArea();
				area.setEditable(false);
				add(area);
				this.doLayout();
				textAreas.put(cl.id, area);
			} catch (Exception e) {
				try {
					Textt t = (Textt) rd.readObject();
				}catch(Exception e1) {
					
				}
			}
		return true;
	}
	
	public void sendMsgForAll(int id, String text) {
		System.out.println("call");
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
			if(id != c.getKey()) {
				textAreas.get(id).setText(text);
			}
		}
	}
	
	
}

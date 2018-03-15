import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map.Entry;
import msg.AddAreaMsg;
import msg.RemoveAreaMsg;
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
			synchronized(wr) {
				wr.writeObject(msg);
				wr.flush();
				}
			
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
			
			for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
				if(counter != c.getKey()) {
					c.getValue().addTextArea(counter);
					client.addTextArea(c.getKey());
				}
			}
			
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
	{	synchronized(rd) {
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
					textAreas.get(t.id).setText(t.msg);
					textAreas.get(t.id).doLayout();
				}catch(Exception e1) {
					try {
						RemoveAreaMsg area = (RemoveAreaMsg) rd.readObject();
						
						clients.remove(area.id);
						remove(textAreas.get(area.id));
						textAreas.remove(area.id);
						this.doLayout();
						System.out.println("rempve");
					}catch(Exception e2) {
					}
				}
			}
		}
		return true;
	}
	
	public void sendMsgForAll(int id, String text) {
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
				textAreas.get(id).setText(text);
				textAreas.get(id).doLayout();
				if(id != c.getKey()) {
					c.getValue().sendMsg(new Textt(id,text));
				}
		}
	}
	
	
	public void removeClient(int id) {
		clients.remove(id);
		remove(textAreas.get(id));
		textAreas.remove(id);
		System.out.println(clients + "..." + id);
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
			c.getValue().removeTextArea(new RemoveAreaMsg(id));
		}
	}
	
	
}

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map.Entry;



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
	
	OutputStream wr; 
	InputStream rd;
	
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
			if(srv == null) {
				byte bts[] = msg.getBytes();
				try {
					synchronized(wr){
				    wr.write(bts.length & 255);
				    wr.write(bts.length >> 8);
				    wr.write(bts, 0, bts.length);
				    wr.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
					c.getValue().sendMsg("T 0 " + t1.getText());
				}
				
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
			  wr = socket.getOutputStream();
			  rd = socket.getInputStream();
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
		try{String str;
			synchronized(rd) {
				str = readAndcreateString();
			}
		if(str.length() > 0) {
			
				String[] msg = str.split(" ");
				int id;
				switch(msg[0]) {
				case "A":
					id = Integer.parseInt(msg[1]);
					TextArea area = new TextArea();
					area.setEditable(false);
					add(area);
					doLayout();
					textAreas.put(id, area);
					break;
				case "R":
					id = Integer.parseInt(msg[1]);
					remove(textAreas.get(id));
					textAreas.remove(id);
					doLayout();
					break;
				case "T":
					id = Integer.parseInt(msg[1]);
					String text = msg[2];
					textAreas.get(id).setText(text);
					break;
				default:
					return false;
				}
				
		}}catch(Exception e) {
		}
		return true;
	}
	
	public void sendMsgForAll(int id, String text) {
		String msg = "T " + id + " " + text;
		textAreas.get(id).setText(text);
		textAreas.get(id).doLayout();
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
				if(id != c.getKey()) {
					c.getValue().sendMsg(msg);
				}
		}
	}
	
	
	public void removeClient(int id) {
		clients.remove(id);
		remove(textAreas.get(id));
		textAreas.remove(id);
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
			c.getValue().removeTextArea(id);
		}
	}
	
	
	public String readAndcreateString()
	{
		try {
			int nbts = rd.read() + (rd.read() << 8);
			byte bts[] = new byte[nbts];
			int i = 0; // how many bytes did we read so far
			do {
				int j = rd.read(bts, i, bts.length - i);
				if (j > 0) i += j;
				else break;
			} while (i < bts.length);
			return new String(bts);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
}

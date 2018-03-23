/*
 * Hotove ulohy:
 * 1.
 * 4. kreslenie pre viacerych
 * 3. mazanie hystorie
 * 5. posielau sa iba zmeny bez moznostou editacie
 * */


import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;





public class Vrav extends Applet implements Runnable
{
	private static final long serialVersionUID = -4297335882692216363L;

	Socket socket;
	TextArea t1 = new TextArea();
	Thread th;
	private static final int port = 2004;
	public int counter;
	String text = new String();
	
	
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
		
	}
	
	void listeners() {
		t1.addTextListener(new TextListener() {
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				// TODO Auto-generated method stub
				if(text.length() == 0 || text.length() <= t1.getText().length()) {
					String msg = t1.getText();
					sendMsg(msg.substring(msg.length()-1, msg.length()));
				}else {
					sendMsg("");
				}
				text = t1.getText();
				
			}
		});
	}
	
	private void sendMsg(String msg) {
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
	}
	
	private void waitingForConnection() throws IOException
	{
			Selector selector = Selector.open();
	        ServerSocketChannel ssc1 = ServerSocketChannel.open();
	        ssc1.configureBlocking( false );
	        ServerSocket ss = ssc1.socket();
	        InetSocketAddress address = new InetSocketAddress( port );
	        ss.bind( address );
	        
	        
	        SelectionKey key1 = ssc1.register( selector, SelectionKey.OP_ACCEPT );
        
			while(true) {
				int num = selector.select();
				Set selectedKeys = selector.selectedKeys();
				Iterator it = selectedKeys.iterator();
				
				while(it.hasNext()) {
					SelectionKey key = (SelectionKey)it.next();
					if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
						SocketChannel sc = (SocketChannel)key.channel();
	                    ByteBuffer rb = ByteBuffer.allocate(1);
	                    sc.read(rb);
	                    rb.flip();
	                    //WRITE TO SPECIFIED CLIENT TEXT AREA
	                    //SEND AND WRITE TO ALL CLIENTS 
					}
					else if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)
	                {
						System.out.println("New client arriving.");
	                    ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
	                    SocketChannel sc = ssc.accept();	 
	                      
	                    sc.configureBlocking( false );
	                    SelectionKey newKey = sc.register( selector, SelectionKey.OP_READ );
	                    
	                    //ADD CLIENT
	                    //MAKE NEW TEXT AREA
	                    //ADD SERVER TO CLIENT WITH NEW TEXT AREA
	                }
					else {
						//REMOVE CLIENT FROM clients
						//REMOVE text area
					}
				}
				
				
				
				/*counter++;
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
				}*/
			}
			
			
	}

	public void run() 
	{
		try{
			  socket = new Socket("localhost", port);
			  wr = socket.getOutputStream();
			  rd = socket.getInputStream();
			  listeners();
			  while(getMsg());
		}catch(Exception e){
			try {
				listeners();
				waitingForConnection();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
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
					//System.out.println(msg.length);
					id = Integer.parseInt(msg[1]);
					if(msg.length > 2) {
						String text = msg[2];
						textAreas.get(id).setText(textAreas.get(id).getText() + text);
					}else {
						String t = textAreas.get(id).getText();
						textAreas.get(id).setText(t.substring(0,t.length()-1));
					}
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
		if(text.equals("")) {
			String t = textAreas.get(id).getText();
			textAreas.get(id).setText(t.substring(0,t.length()-1));
		}else {
			textAreas.get(id).setText(textAreas.get(id).getText() + text);
		}
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

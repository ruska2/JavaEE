
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
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;





public class Vrav extends Applet implements Runnable
{
	private static final long serialVersionUID = -4297335882692216363L;

	SocketChannel socket;
	TextArea t1 = new TextArea();
	Thread th;
	private static final int port = 2004;
	public int counter;
	
	Selector selector;
	LinkedList pendingChanges = new LinkedList();
	private Map pendingData = new HashMap();
	
	HashMap<Integer,TextArea> textAreas = new HashMap<>();
	HashMap<Integer,SocketChannel> clients = new HashMap<>();
	
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
				sendMsg(t1.getText());	
			}
		});
	}
	
	private void sendMsg(String msg) {
		if(socket != null){
			byte bts[] = msg.getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(bts);
			try {
				socket.write(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			sendMsgForAll(0,msg);
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
	        
	        
	        SelectionKey selectKey = ssc1.register( selector, ssc1.validOps() );
	        Iterator<SelectionKey> iter;
	        SelectionKey key;
	        
			while(true) {
				selector.select();
				iter = selector.selectedKeys().iterator();
				
				while(iter.hasNext()) {
					key = iter.next();
					iter.remove();
					if(key.isReadable()) {
						SocketChannel sc = (SocketChannel)key.channel();
	                    ByteBuffer rb = ByteBuffer.allocate(256);
	                    sc.read(rb);
	                    String x = new String(rb.array()).trim();
	                    rb.flip();
	                    
	                    //GET KEY OF SENDER
	                    int senderKey = -1;
	                    for(Entry<Integer,SocketChannel> c: clients.entrySet()) {
	    					if(sc.equals(c.getValue())){
	    						senderKey = c.getKey();
	    					}
	    				}
	                    
	                    
	                    //REMOVE CLIENT
	                    if(x.length() == 0) {
	                    	key.cancel();
	                    	removeClient(senderKey);
	                    	break;
	                    }
	                    
	                    
	                    textAreas.get(senderKey).setText(x);
	                    sendMsgForAll(senderKey,x);
	                    
					}
					else if (key.isAcceptable() && key.readyOps() == SelectionKey.OP_ACCEPT)
	                {
						System.out.println("New client arriving.");
						counter++;
	                    SocketChannel sc = ssc1.accept();	 
	                      
	                    sc.configureBlocking( false );
	                    sc.register( selector, SelectionKey.OP_READ );
	                    
	                    TextArea clientArea = new TextArea();
	    			    clientArea.setEditable(false);
	    			    textAreas.put(counter, clientArea);
	    			    clients.put(counter, sc);
	    			    add(clientArea);
	    			    this.doLayout();
	    			    
	    			    //CREATE TEXT AREA 0 IN ALL CLIENTS
	    			    byte bts[] = ("A 0").getBytes();
						ByteBuffer buffer = ByteBuffer.wrap(bts);
						try {
							sc.write(buffer);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
	    			    
	    			    // CREATE TEXT AREA IN ALL CLIENTS
	    			    for(Entry<Integer,SocketChannel> c: clients.entrySet()) {
	    					if(counter != c.getKey()) {
	    						SocketChannel s = c.getValue();
	    						bts = ("A " + counter).getBytes();
	    						buffer = ByteBuffer.wrap(bts);
	    						s.write(buffer);
	    						sleep(20);
									
	    					}
	    				}
	    			    
	    			    
	    			    for(Entry<Integer,SocketChannel> c: clients.entrySet()) {
	    					if(counter != c.getKey()) {
	    						 bts = ("A " + c.getKey()).getBytes();
	    						 buffer = ByteBuffer.wrap(bts);
	    						 sc.write(buffer);
	    						 sleep(20);
	    					}
	    				}
	                }
				}
				
			}
			
			
	}

	public void run() 
	{
		try{
			  InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", port);
			  socket = SocketChannel.open(crunchifyAddr);
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
		String str = readAndcreateString();
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
					String text = msg[2];
					textAreas.get(id).setText(text);
					break;
				default:
					return false;
				}
				
		}
		return true;
	}
	
	public void sendMsgForAll(int id, String text) {
		String msg = "T " + id + " " + text;
		for(Entry<Integer,SocketChannel> c: clients.entrySet()) {
				if(id != c.getKey()) {
					 byte bts[] = (msg).getBytes();
					 ByteBuffer buffer = ByteBuffer.wrap(bts);
						try {
							c.getValue().write(buffer);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
						
				}
		}
	}
	
	
	public void removeClient(int id) {
		clients.remove(id);
		remove(textAreas.get(id));
		textAreas.remove(id);
		for(Entry<Integer,SocketChannel> c: clients.entrySet()) {
			String msg = "R " + id;
			byte bts[] = msg.getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(bts);
			try {
				c.getValue().write(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public String readAndcreateString()
	{
		try {
			ByteBuffer rb = ByteBuffer.allocate(256);
            socket.read(rb);
            String x = new String(rb.array()).trim();
            rb.flip();
            return x;
		} catch (IOException e) {
	
		}
		return "";
	}
	
	private void sleep(int time) 
	{
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void destroy() {
		System.out.println("socket closed");
		sendMsg(Character.toString((char) 27));
		sleep(200);
	}
}

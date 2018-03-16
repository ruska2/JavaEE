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
	Button clear = new Button("Clear History");
	Button canv = new Button("Canvas");
	Frame f= new Frame("Canvas");
	Canvas c = new Canvas();
	String text = new String();
	
	
	HashMap<Integer,ClientHandler> clients = new HashMap<>();
	HashMap<Integer,TextArea> textAreas = new HashMap<>();
	
	OutputStream wr; 
	InputStream rd;
	
	@Override
	public void init()
	{
		add(t1); 
		add(clear);
		add(canv);
		Thread th = new Thread(this);
		th.start();
		c.setSize(400,400);
		f.add(c);
		
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
		
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				for(Entry<Integer,TextArea> area: textAreas.entrySet()) {
					area.getValue().setText("");
				}
			}
			
		});
		
		
		
		 //f.setLayout(null);  
		 f.setSize(400, 400);
		 
		
		canv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				f.setVisible(true);
			}
			
		});
		
		f.addWindowListener(new WindowAdapter(){
			  public void windowClosing(WindowEvent we){
			    f.setVisible(false);
			  }
			});
		
		c.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
				if(srv != null) {
					c.getGraphics().fillOval(arg0.getX(), arg0.getY(), 20, 20);
					for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
						String d = "D "  + arg0.getX() + " " + arg0.getY();
						c.getValue().sendGraphics(d);
					}
				}
				else {
					c.getGraphics().fillOval(arg0.getX(), arg0.getY(), 20, 20);
					String d = "D "  + arg0.getX() + " " + arg0.getY();
					sendCords(d);
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
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
					c.getValue().sendMsg("T 0 " + msg);
				}
				
			}
	}
	

	private void sendCords(String msg) {
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
			}
	}
	
	public void draw(String msg) {
		String[] p = msg.split(" ");
		int x = Integer.parseInt(p[1]);
		int y = Integer.parseInt(p[2]);
		try{
			c.getGraphics().fillOval(x, y, 20, 20);
		}catch(Exception e) {}
		
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
			c.getValue().sendGraphics(msg);
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
			  listeners();
			  while(getMsg());
		}catch(Exception e){
			try {
				srv = new ServerSocket(port);
				listeners();
				//System.out.println("ServerClientCreated");
				while(true){
					waitingForConnection();
					//System.out.println("waiting again client connected");
				}
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
				case "D":
					int x = Integer.parseInt(msg[1]);
					int y = Integer.parseInt(msg[2]);
					c.getGraphics().fillOval(x, y, 20, 20);
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

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Vrav extends Applet implements Runnable
{
	private static final long serialVersionUID = -4297335882692216363L;

	Socket socket;
	TextArea t1 = new TextArea();
	TextArea t2 = new TextArea();
	boolean zapisuje = false;
	Thread th;
	private static final int port = 2004;
 
	OutputStream wr; 
	InputStream rd;
	
	public void init()
	{
		add(t1); add(t2);
		t1.setEditable(false);
		listenery();		
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
		    } catch (ConnectException e) {
		    	// otherwise create a listening socket and wait for the other party to connect
			    System.out.println("Druha strana este nie je pripravena, cakam na spojenie...");
		    	ServerSocket srv = new ServerSocket(port);
			    socket = srv.accept();
			    System.out.println("Vytvoreny socket pre prijimanie");
			}
		    wr = socket.getOutputStream();
            rd = socket.getInputStream();
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
		t2.addTextListener(new TextListener(){
			@Override
			public void textValueChanged(TextEvent arg0) {
				//TextComponent tc = (TextComponent)arg0.getSource();
			    //String messageOut = tc.getText();
				String messageOut = t2.getText();
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
			int nbts = rd.read() + (rd.read() << 8);
			byte bts[] = new byte[nbts];
			int i = 0; // how many bytes did we read so far
			do {
				int j = rd.read(bts, i, bts.length - i);
				if (j > 0) i += j;
				else break;
			} while (i < bts.length);
			t1.setText(new String(bts));
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}

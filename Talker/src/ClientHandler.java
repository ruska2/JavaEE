import java.awt.TextArea;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ClientHandler implements Runnable {
	
	public ClientHandler(Vrav server, Socket socket, int id) throws IOException {
		super();
		this.server = server;
		this.socket = socket;
		this.id = id;
		out = socket.getOutputStream();
		inp = socket.getInputStream();
		new Thread(this).start();
	}

	Vrav server;
	Socket socket;
	int id;
	OutputStream out;
	InputStream inp;
	
	@Override
	public void run() {
		while(getMsg());
		server.removeClient(id);
		try {
			socket.close();
		} catch (IOException e) {
		}
	}


	public void addTextArea(int id) {
		String msg = "A "+id;
		byte bts[] = msg.getBytes();
		try {
			synchronized(out){
		    out.write(bts.length & 255);
		    out.write(bts.length >> 8);
		    out.write(bts, 0, bts.length);
		    out.flush();
			}
		} catch (IOException e) {
		}
		
	}
	
	public boolean getMsg() {
		String cl;
		synchronized(inp){
			cl = readAndcreateString();
		}
		if(cl == null) {
			return false;
		}
		if(cl != "") {
			if(cl.length() > 0 && cl.charAt(0) == 'D') {
				server.draw(cl);
			}else {
				server.sendMsgForAll(id, cl);
			}
			
		}
		return true;
	}
	
	
	public void sendMsg(String msg) {
		byte bts[] = msg.getBytes();
		try {
			synchronized(out){
		    out.write(bts.length & 255);
		    out.write(bts.length >> 8);
		    out.write(bts, 0, bts.length);
		    out.flush();
			}
		} catch (IOException e) {
		}
		
	}
	
	public void removeTextArea(int id) {
		String msg = "R "+id;
		byte bts[] = msg.getBytes();
		try {
			synchronized(out){
		    out.write(bts.length & 255);
		    out.write(bts.length >> 8);
		    out.write(bts, 0, bts.length);
		    out.flush();
			}
		} catch (IOException e) {
		}
	}
	
	public void sendGraphics(String msg) {
		byte bts[] = msg.getBytes();
		try {
			synchronized(out){
		    out.write(bts.length & 255);
		    out.write(bts.length >> 8);
		    out.write(bts, 0, bts.length);
		    out.flush();
			}
		} catch (IOException e) {
		}
	}
	
	
	public String readAndcreateString()
	{
		try {
			int nbts = inp.read() + (inp.read() << 8);
			byte bts[] = new byte[nbts];
			int i = 0; // how many bytes did we read so far
			do {
				int j = inp.read(bts, i, bts.length - i);
				if (j > 0) i += j;
				else break;
			} while (i < bts.length);
			return new String(bts);
		} catch (IOException e) {
			return null;
		}
	}
}

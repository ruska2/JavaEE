import java.awt.TextArea;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import msg.AddAreaMsg;
import msg.RemoveAreaMsg;
import msg.Textt;

public class ClientHandler implements Runnable {
	
	public ClientHandler(Vrav server, Socket socket, int id) throws IOException {
		super();
		this.server = server;
		this.socket = socket;
		this.id = id;
		out = new ObjectOutputStream(socket.getOutputStream());
		inp = new ObjectInputStream(socket.getInputStream());
		new Thread(this).start();
	}

	Vrav server;
	Socket socket;
	int id;
	ObjectOutputStream out;
	ObjectInputStream inp;
	
	
	@Override
	public void run() {
		while(getMsg());
	}


	public void addTextArea(int id) {
		try {
			out.writeObject(new AddAreaMsg(id));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean getMsg() {
		String cl;
		try {
			synchronized(inp) {
				cl = (String) inp.readObject();
			}
			server.sendMsgForAll(id,cl);
		} catch (Exception e) {
			server.removeClient(id);
			return false;
		}
		return true;
	}
	
	
	public void sendMsg(Textt text) {
		try {
			
			synchronized(out) {
				out.writeObject(text);
				out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void removeTextArea(RemoveAreaMsg area) {
		try {
			
			synchronized(out) {
				out.writeObject(area);
				out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

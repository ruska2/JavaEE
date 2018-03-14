import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import msg.AddAreaMsg;

public class ClientHandler implements Runnable {


	Socket sock;
	Vrav server;
	int id;

	
	public ClientHandler(Socket sock, Vrav server, int id) {
		this.sock = sock;
		this.server = server;
		this.id = id;
	}
	
	public ClientHandler(int id){
		this.id = id;
	}


	public void addTextArea(int id) {
		try {
			new ObjectOutputStream(sock.getOutputStream()).writeObject(new AddAreaMsg(id));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
}





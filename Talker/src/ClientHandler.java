import java.awt.TextArea;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import msg.AddAreaMsg;
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
		try {
			String cl = (String) inp.readObject();
			server.sendMsgForAll(id,cl);
		} catch (Exception e) {
		}
		return true;
	}

}

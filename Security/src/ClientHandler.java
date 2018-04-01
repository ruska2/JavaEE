import java.io.IOException;
import java.io.InputStream;
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


	public void addTextArea(byte[] bts) {
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
		byte[] cl;
		synchronized(inp){
			cl = readAndcreateBytes();
			if(!server.clientKeys.containsKey(id)) {
				server.addClientWithKey(id, cl);
				server.addAreaToClients(id);
				return true;
			}
			if(!server.clientSignkeys.containsKey(id)) {
				server.addClientWithSignKey(id, cl);
				server.clientMsgCount.put(id, 1);
				server.serverClientMsgCount.put(id, 0);
				return true;
			}
			server.sendMsgForAll(id, cl);
		}
		if(cl == null) {
			return false;
		}
		return true;
	}
	
	
	public void sendMsg(byte[] bts) {
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
	
	public void removeTextArea(byte[] bts) {
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
	
	public byte[] readAndcreateBytes()
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
			return bts;
		} catch (IOException e) {
			return null;
		}
	}
}

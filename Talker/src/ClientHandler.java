import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class ClientHandler implements Runnable,Serializable {

	Socket sock;
	Vrav server;
	
	ObjectOutputStream wr; 
	ObjectInputStream rd;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}



}

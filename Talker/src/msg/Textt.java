package msg;

import java.io.Serializable;

public class Textt implements Serializable {

	public String msg;
	public int id;
	
	public Textt(int id, String m) {
		msg = m;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return msg;
		
	}
}

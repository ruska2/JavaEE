package msg;

import java.io.Serializable;

public class Textt extends Message implements Serializable {

	String msg;
	
	public Textt(String m) {
		msg = m;
	}
	
	@Override
	public String toString() {
		return msg;
		
	}
}

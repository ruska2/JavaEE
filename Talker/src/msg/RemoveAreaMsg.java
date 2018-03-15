package msg;

import java.io.Serializable;

public class RemoveAreaMsg implements Serializable {

	public int id;
	
	public RemoveAreaMsg(int id){
		this.id = id;
	}
}

package msg;

import java.io.Serializable;

public class AddAreaMsg implements Serializable{

	int id;
	
	public AddAreaMsg(int id){
		this.id = id;
	}
}

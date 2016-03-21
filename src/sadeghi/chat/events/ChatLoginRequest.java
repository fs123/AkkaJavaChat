package sadeghi.chat.events;

import java.io.Serializable;


public class ChatLoginRequest implements Serializable {
	private static final long serialVersionUID = 1;
	
	public final String name;
	
	public ChatLoginRequest (String name) {
		this.name = name;
	}
}

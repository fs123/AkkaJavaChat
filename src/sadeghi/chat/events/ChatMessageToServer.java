package sadeghi.chat.events;

import java.io.Serializable;


public class ChatMessageToServer implements Serializable {
	private static final long serialVersionUID = 1;
	
	public final String from;
	public final String message;
	
	public ChatMessageToServer(String from, String message) {
		this.from = from;
		this.message = message;
	}
}

package sadeghi.chat.events;

import java.io.Serializable;


public class ChatMessageFromServer implements Serializable {
	private static final long serialVersionUID = 1;
	
	public final String from;
	public final String message;
	
	public ChatMessageFromServer(String from, String message) {
		this.from = from;
		this.message = message;
	}
}

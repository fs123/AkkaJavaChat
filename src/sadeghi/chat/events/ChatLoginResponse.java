package sadeghi.chat.events;

import java.io.Serializable;


public class ChatLoginResponse implements Serializable {
	private static final long serialVersionUID = 1;
	
	public final boolean successful;
	
	public ChatLoginResponse (boolean successful) {
		this.successful = successful;
	}
}

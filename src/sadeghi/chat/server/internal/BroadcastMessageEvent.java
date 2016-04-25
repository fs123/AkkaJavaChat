package sadeghi.chat.server.internal;

import java.io.Serializable;

public class BroadcastMessageEvent implements Serializable {
	private static final long serialVersionUID = 1;

	public final String from;
	public final String message;

	public BroadcastMessageEvent(String from, String message) {
		this.from = from;
		this.message = message;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + 
				"from=" + from + "," + 
				"message=" + message + "," +
				"]";
	}
}

package sadeghi.chat.server.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;

public class BroadcastMessageEvent implements Serializable {
	private static final long serialVersionUID = 1;

	public final Map<String, ActorRef> userList;
	public final String from;
	public final String message;

	public BroadcastMessageEvent(String from, String message, Map<String, ActorRef> userList) {
		this.from = from;
		this.message = message;
		this.userList = Collections.unmodifiableMap(new HashMap<>(userList));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + 
				"from=" + from + "," + 
				"message=" + message + "," + 
				"userList.size()=" + userList.size() + 
				"]";
	}
}

package sadeghi.chat.events;

import java.io.Serializable;

import akka.actor.ActorRef;


public class ChatLoginRequest implements Serializable {
	private static final long serialVersionUID = 1;
	
	public final String name;
	public final ActorRef sender;
	
	public ChatLoginRequest (String name, ActorRef sender) {
		this.name = name;
		this.sender = sender;
	}
}

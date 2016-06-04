package sadeghi.chat.server.internal;

import sadeghi.chat.events.ChatMessageFromServer;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class UserActor extends UntypedActor {

	public static Props props(String userName, ActorRef listener) {
		return Props.create(UserActor.class, userName, listener);
	}
	
	private final ActorRef listener;
	private final String userName;
	
	public UserActor(String userName, ActorRef listener) {
		this.userName = userName;
		this.listener = listener;
	}
	
	@Override
	public void onReceive(final Object message) throws Exception {
		if (message instanceof ChatMessageFromServer) {
			handleIncomingMessage((ChatMessageFromServer) message);
		} else {
			System.out.println("wtf :" + message.toString());
			unhandled(message);
		}
	}

	private void handleIncomingMessage(ChatMessageFromServer message) {
		if (message.from.equals(userName)){
			return;
		}
		listener.tell(message, getSelf());
	}

}

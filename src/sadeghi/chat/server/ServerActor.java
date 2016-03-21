package sadeghi.chat.server;

import java.util.HashMap;
import java.util.Map;

import sadeghi.chat.events.ChatLoginRequest;
import sadeghi.chat.events.ChatLoginResponse;
import sadeghi.chat.events.ChatMessageToServer;
import sadeghi.chat.server.internal.BroadcastMessageActor;
import sadeghi.chat.server.internal.BroadcastMessageEvent;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ServerActor extends UntypedActor {

	private Map<String, ActorRef> userList = new HashMap<>();
	private ActorRef broadcastMessageActor;


	@Override
	public void preStart() throws Exception {
		broadcastMessageActor = getContext().actorOf(Props.create(BroadcastMessageActor.class), "BroadcastMessageActor");
	}
	
	@Override
	public void onReceive(final Object message) throws Exception {
		System.out.println("onReceive: " + message);
		if (message instanceof ChatLoginRequest) {
			handleLoginRequest(message);
		} else if (message instanceof ChatMessageToServer) {
			handleChatMessageToServer(message);
		} else {
			System.out.println("wtf :" + message.toString());
			unhandled(message);
		}
	}

	private void handleLoginRequest(final Object message) {
		boolean ok = userList.put(((ChatLoginRequest) message).name, sender()) == null;
		getSender().tell(new ChatLoginResponse(ok), self());
	}

	private void handleChatMessageToServer(final Object message) {
		ChatMessageToServer receivedMessage = (ChatMessageToServer) message;
		if (receivedMessage.message.startsWith("/disconnect")) {
			userList.remove(receivedMessage.from);
		} else {
			BroadcastMessageEvent event = new BroadcastMessageEvent(receivedMessage.from, receivedMessage.message, userList);
			broadcastMessageActor.tell(event, self());
		}
		
	}
}
package sadeghi.chat.client;

import sadeghi.chat.events.ChatLoginRequest;
import sadeghi.chat.events.ChatLoginResponse;
import sadeghi.chat.events.ChatMessageFromServer;
import sadeghi.chat.events.ChatMessageToServer;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

public class ClientActor extends UntypedActor {
	private static String REMOTE_PATTERN = "akka.tcp://%s@%s:%d/user/serverActor";
	// selection, even the actor is restarted, this selection is still valid
	// but this requires some performance
	private final ActorSelection server;

	public ClientActor(String host, int port) {
		if (host == null || host.isEmpty()) {
			throw new IllegalArgumentException("HOST and/or PORT missing.");
		}

		String serverRemotePath = String.format(REMOTE_PATTERN, 
				getContext().system().settings().config().getString("chat.server.actor.name"), 
				host, port);
		server = getContext().actorSelection(serverRemotePath);
	}

	@Override
	public void onReceive(final Object message) throws Exception {
		if (message instanceof ChatLoginRequest) {
			server.tell(message, self());
		} else if (message instanceof ChatLoginResponse) {
			System.out.println(" $ LOGIN "+  (((ChatLoginResponse)message).successful ? "OK - HELLO" : "FAILED - SORRY") +" !");
		} else if (message instanceof ChatMessageFromServer) {
			System.out.println(" $ " + ((ChatMessageFromServer) message).from + ": " + ((ChatMessageFromServer) message).message);
		} else if (message instanceof ChatMessageToServer) {
			server.tell(message, self());
		}
	}

	@Override
	public void aroundPostStop() {
		super.aroundPostStop();
//		ChatMessage message = ChatMessage.createMessageSender(userName);
//		message.setMessage("/disconnect");
//		server.tell(message, getSelf());

	}
}

package sadeghi.chat.server.internal;

import java.util.concurrent.atomic.AtomicInteger;

import sadeghi.chat.events.ChatMessageFromServer;
import scala.Option;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

public class BroadcastMessageActor extends UntypedActor {
	
	private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger(0);
	private final int instanceCount = INSTANCE_COUNTER.getAndIncrement();
	
	@Override
	public void onReceive(final Object message) throws Exception {
		System.out.println("BroadcastMessageActor[" + instanceCount + "].onReceive(): " + message);
		if (message instanceof BroadcastMessageEvent) {
			broadcastMessage((BroadcastMessageEvent) message);
		} else {
			unhandled(message);
		}
	}

	private void broadcastMessage(BroadcastMessageEvent broadcastMessage) {
		handleFailRequestOnStart(broadcastMessage);
		ActorSelection actorSelection = context().actorSelection("/user/serverActor/user_*");
		ChatMessageFromServer sendMessage = new ChatMessageFromServer(broadcastMessage.from, broadcastMessage.message);
		actorSelection.tell(sendMessage, getSelf());
	}

	private void handleFailRequestOnStart(BroadcastMessageEvent broadcastMessage) {
		String message = broadcastMessage.message;
		if (!message.startsWith("/broadcast-fail-onstart")) {
			return;
		}
		message = message.substring("/broadcast-fail-onstart".length()).trim();
		throw new IllegalStateException(message);
	}
	
	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {
		super.preRestart(reason, message);
		System.out.println("BroadcastMessageActor[" + instanceCount + "].preRestart() says: good-by ");
	}
	
	@Override
	public void postStop() throws Exception {
		super.postStop();
		System.out.println("BroadcastMessageActor[" + instanceCount + "].postStop() says: adios amigos ");
	}
	
	@Override
	public void preStart() throws Exception {
		super.preStart();
		System.out.println("BroadcastMessageActor[" + instanceCount + "].preStart() says: back in town ");
	}
}

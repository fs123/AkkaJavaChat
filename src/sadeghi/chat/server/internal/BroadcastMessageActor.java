package sadeghi.chat.server.internal;

import java.util.Map.Entry;

import sadeghi.chat.events.ChatMessageFromServer;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;

public class BroadcastMessageActor extends UntypedActor {

	@Override
	public void onReceive(final Object message) throws Exception {
		System.out.println("onReceive: " + message);
		if (message instanceof BroadcastMessageEvent) {
			broadcastMessage((BroadcastMessageEvent) message);
		} else {
			System.out.println("wtf :" + message.toString());
			unhandled(message);
		}
	}

	private void broadcastMessage(BroadcastMessageEvent broadcastMessage) {
		ChatMessageFromServer sendMessage = new ChatMessageFromServer(broadcastMessage.from, broadcastMessage.message);
		for (Entry<String, ActorRef> entry : broadcastMessage.userList.entrySet()) {
			if (!entry.getKey().equals(broadcastMessage.from)) {
				entry.getValue().tell(sendMessage, getSelf());
			}
		}
	}

	private static SupervisorStrategy strategy = 
			new OneForOneStrategy(10, Duration.create("1 minute"), new Function<Throwable, Directive>() {
		@Override
		public Directive apply(Throwable t) {
			/*
			 *  - For this actor (AND all children!)
				1. Resume the subordinate, keeping its accumulated internal state (not visible to the outside)
				2. Restart the subordinate, clearing out its accumulated internal state
				3. Stop the subordinate permanently
				4. Escalate the failure, thereby failing itself
			 */
			if (t instanceof ArithmeticException) {
				return SupervisorStrategy.resume();
			} else if (t instanceof NullPointerException) {
				return SupervisorStrategy.restart();
			} else if (t instanceof IllegalArgumentException) {
				return SupervisorStrategy.stop();
			} else {
				return SupervisorStrategy.escalate();
			}
		}
	});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}
}

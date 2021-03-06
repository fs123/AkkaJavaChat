package sadeghi.chat.server;

import sadeghi.chat.events.ChatLoginRequest;
import sadeghi.chat.events.ChatLoginResponse;
import sadeghi.chat.events.ChatMessageToServer;
import sadeghi.chat.server.internal.BroadcastMessageActor;
import sadeghi.chat.server.internal.BroadcastMessageEvent;
import sadeghi.chat.server.internal.UserActor;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;

public class ServerActor extends UntypedActor {

	private ActorRef broadcastMessageActor;

	@Override
	public void preStart() throws Exception {
		broadcastMessageActor = getContext().actorOf(Props.create(BroadcastMessageActor.class),
				"BroadcastMessageActor");
	}

	@Override
	public void onReceive(final Object message) throws Exception {
		System.out.println("ServerActor.onReceive: " + message);
		if (message instanceof ChatLoginRequest) {
			handleLoginRequest((ChatLoginRequest)message);
		} else if (message instanceof ChatMessageToServer) {
			handleChatMessageToServer((ChatMessageToServer)message);
		} else {
			unhandled(message);
		}
	}

	private void handleLoginRequest(final ChatLoginRequest message) {
		getContext().actorOf(UserActor.props(message.name, message.listener), "user_" + message.name);
		getSender().tell(new ChatLoginResponse(true), self());
	}

	private void handleChatMessageToServer(final ChatMessageToServer message) {
		ChatMessageToServer receivedMessage = (ChatMessageToServer) message;
		BroadcastMessageEvent event = new BroadcastMessageEvent(receivedMessage.from, receivedMessage.message);
		broadcastMessageActor.tell(event, self());
	}

	/**
	 * From the docs -
	 * http://doc.akka.io/docs/akka/2.4.2/general/supervision.html: If you try
	 * to do too much at one level, it will become hard to reason about, hence
	 * the recommended way in this case is to add a level of supervision.
	 */
	private static SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("3 second"), new Function<Throwable, Directive>() {
		@Override
		public Directive apply(Throwable t) {
			/*
			 *  When a child actor fails, this actor is the supervisor and has to decide,
			 *  what its to do:
				1. Escalate the failure, thereby failing itself
				2. Stop the subordinate permanently
				3. Restart the subordinate, clearing out its accumulated internal state
					- This is carried out by creating a new instance of the underlying Actor class and 
					  replacing the failed instance with the fresh one inside the child’s ActorRef; the 
					  ability to do this is one of the reasons for encapsulating actors within special references. 
					  The new actor then resumes processing its mailbox, meaning that the restart is not visible 
					  outside of the actor itself with the notable exception that the message during which the 
					  failure occurred is not re-processed.
				4. Resume the subordinate, keeping its accumulated internal state (not visible to the outside)
			 */
			System.out.println("SupervisorStrategy: " + t.getMessage());
			if (t.getMessage().equals("escalate")) {
				return SupervisorStrategy.escalate();
			} else if (t.getMessage().equals("stop")) {
				return SupervisorStrategy.stop();
			} else if (t.getMessage().equals("restart")) {
				return SupervisorStrategy.restart();
			} else {
				return SupervisorStrategy.resume();
			}
		}
	});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}
}
package sadeghi.fun;

import static akka.japi.Util.classTag;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class SimpleRequestResponseExample {
	public static class MyRequest implements Serializable {
		private static final long serialVersionUID = 1L;
	}

	public static class MyResponse implements Serializable {
		private static final long serialVersionUID = 1L;
		final String message;
		public MyResponse(String message) {
			this.message = message;
		}
	}

	public static class MyServer extends UntypedActor {
		public void onReceive(Object message) {
			if (message instanceof MyRequest) {
				System.out.println(" -> send response");
				getSender().tell(new MyResponse("cool!"), getSelf());
			} else {
				unhandled(message);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		versionOne();
		versionTwo();
	}

	private static void versionTwo() throws Exception {
		final ActorSystem system = ActorSystem.create("request-response-example");
		final ActorRef server = system.actorOf(Props.create(MyServer.class));

		Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		Future<Object> askFeature = Patterns.ask(server, new MyRequest(), timeout);
		final MyResponse myResponse = Await.result(askFeature.mapTo(classTag(MyResponse.class)), timeout.duration());
		System.out.println("Response: " + myResponse.message);
	}

	private static void versionOne() throws Exception {
		final ActorSystem system = ActorSystem.create("request-response-example");
		final Inbox inboxPseudoActor = Inbox.create(system);

		final ActorRef server = system.actorOf(Props.create(MyServer.class));
		inboxPseudoActor.send(server, new MyRequest());
		
		Thread.sleep(1000);
		System.out.println("sleep done");
		
		final MyResponse myResponse = (MyResponse) inboxPseudoActor.receive(Duration.create(5, TimeUnit.SECONDS));
		System.out.println("Response: " + myResponse.message);
	}
}
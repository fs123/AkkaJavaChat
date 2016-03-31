package sadeghi.chat.client;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import sadeghi.chat.events.ChatLoginRequest;
import sadeghi.chat.events.ChatLoginResponse;
import sadeghi.chat.events.ChatMessageToServer;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
public class ClientApplication {
	private String serverHostName;
	private int serverPort;
	private Inbox loginActor;
	private ActorRef clientActor;
	private String userName;
	private Consumer<String> answerHandler;
	private ActorSystem clientActorSystem;

	public ClientApplication(String hostName, int port, Consumer<String> answerHandler) {
		this.serverHostName = hostName;
		this.serverPort = port;
		this.answerHandler = answerHandler;
	}

	public boolean login(String userName) {
		this.userName = userName;
		Config clientConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0" )
				.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + serverHostName))
				.withFallback(ConfigFactory.load("common"));

		clientActorSystem = ActorSystem.create("clientActorSystem", clientConfig);
		loginActor = Inbox.create(clientActorSystem);
		
		clientActor = clientActorSystem.actorOf(Props.create(ClientActor.class, serverHostName, serverPort), userName);

		answerHandler.accept("Send login request... " + serverHostName + ":" + serverPort );
		loginActor.send(clientActor, new ChatLoginRequest(userName, clientActor));

		try {
			ChatLoginResponse response = (ChatLoginResponse) loginActor.receive(Duration.create(1, TimeUnit.SECONDS));
			if(!response.successful) {
				answerHandler.accept("Ups, user already used...");
				return false;
			}
		} catch (Exception e ) {
			answerHandler.accept("Timeout: " + e.getMessage());
			return false;
		}
		
		answerHandler.accept("you are logged in, let's chat...:");
		return true;
	}
	
	public void sendMessage(String message) {
		loginActor.send(clientActor, new ChatMessageToServer(userName, message));
	}

	public void stop() {
//		clientActor.tell(PoisonPill.getInstance(), ActorRef.noSender());	
		clientActorSystem.terminate();
	}
}

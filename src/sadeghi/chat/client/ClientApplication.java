package sadeghi.chat.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import sadeghi.chat.events.ChatLoginRequest;
import sadeghi.chat.events.ChatLoginResponse;
import sadeghi.chat.events.ChatMessageToServer;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.PoisonPill;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClientApplication {
	private String serverHostName;
	private int serverPort;
	private Inbox clientInbox;
	private ActorRef clientActor;
	private String userName;
	private Consumer<String> answerHandler;

	public ClientApplication(String hostName, int port, Consumer<String> answerHandler) {
		this.serverHostName = hostName;
		this.serverPort = port;
		this.answerHandler = answerHandler;
	}

	public void login(String userName) {
		this.userName = userName;
		Config clientConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0" );
		clientConfig = clientConfig.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + serverHostName));

		ActorSystem clientActorSystem = ActorSystem.create("clientActorSystem", clientConfig.withFallback(ConfigFactory.load("common")));
		clientActor = clientActorSystem.actorOf(Props.create(ClientActor.class, serverHostName, serverPort), userName);
		clientInbox = Inbox.create(clientActorSystem);
		
		answerHandler.accept("Send login request... " + serverHostName +":"+serverPort );
		clientInbox.send(clientActor, new ChatLoginRequest(userName));

		try {
			ChatLoginResponse response = (ChatLoginResponse) clientInbox.receive(Duration.create(15, TimeUnit.SECONDS));
			if(!response.successful){
				answerHandler.accept("ups, user already used...");
				return;
			}
		} catch (TimeoutException | ClassCastException e ) {
			answerHandler.accept("Connection to server timeout. Try again later.");
			return;
		}
		
		answerHandler.accept("OK!");
		answerHandler.accept("");
	}
	
	public void sendMessage(String message) {
		clientInbox.send(clientActor, new ChatMessageToServer(userName, message));
	}

	public void stop() {
		clientActor.tell(PoisonPill.getInstance(), ActorRef.noSender());		
	}
}

package sadeghi.chat.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ServerApplication {

	private final ActorSystem actorSystem;
	private int port = 14711;
	private ActorRef serverActor;
	
	public static ServerApplication start(String host) {
		return new ServerApplication(host);
	}
	
	private ServerApplication(String host) {

		Config serverConfig = ConfigFactory.parseString("akka.remote.netty.tcp.port =" + port)
				.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + host));
		
		serverConfig = serverConfig.withFallback(ConfigFactory.load("common"));
		
		actorSystem = ActorSystem.create("ChatServerSystem", serverConfig);
		serverActor = actorSystem.actorOf(Props.create(ServerActor.class), "serverActor");
	}
	
	public void stop() {
		serverActor.tell(PoisonPill.getInstance(), ActorRef.noSender());
	}
}
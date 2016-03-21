package sadeghi.chat.server;

import java.net.UnknownHostException;

public class ServerStarter {
	public static void main(String[] args) throws UnknownHostException {
		String hostname = "localhost"; //InetAddress.getLocalHost().getHostName();
		ServerApplication.start(hostname);
	}
}

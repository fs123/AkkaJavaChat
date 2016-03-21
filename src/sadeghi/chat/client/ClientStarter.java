package sadeghi.chat.client;

import java.io.IOException;

import sadeghi.chat.util.ConsoleUtil;

public class ClientStarter {

	public static void main(String[] args) throws IOException {

//		System.out.println("Server Hostname?: ");
//		System.out.print(InetAddress.getLocalHost().getHostName());
		String hostname = "localhost"; //ConsoleUtil.readLineFromConsole();
		
//		System.out.println("Server Port?: ");
//		System.out.print("4711");
//		String port = System.console().readLine();
		
		System.out.println("User name?:");
		String userName = ConsoleUtil.readLineFromConsole();
		
		ClientApplication clientService = new ClientApplication(hostname, 14711, message -> System.out.println(message));
		clientService.login(userName);

		System.out.println("");
		System.out.println("Enter your message:");
		String message = "";
		while(!message.equals("/exit")) {
			message = ConsoleUtil.readLineFromConsole();
			clientService.sendMessage(message);
		}
		clientService.stop();
	}
}

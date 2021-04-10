package SeverSide;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private ServerSocket service;

	public Server(int port) {
		try {
			service = new ServerSocket(port);
			while (true) {// 24/7
				Socket currentClient = service.accept();
				generalService generalService = new generalService(currentClient);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new Server(4000);
	}

}

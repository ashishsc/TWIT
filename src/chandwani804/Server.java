package chandwani804;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
	private static final int DEFAULT_PORT = 55555;
	private static final Boolean DEBUG = false;
	private ServerSocket serverSocket;
	private Boolean halted;

	public Server(int port) {
		halted = false;
		try {
			serverSocket = new ServerSocket(port);
			start();
		} catch (IOException e) {
			System.out.println("Server: Cannot listen on " + port);
			halt();
		}
	}
	
	public Server() {
		this(DEFAULT_PORT);
	}

	// Indefinitely check for new client connections
	public void run() {
		try {
			while (!halted) {
				Socket clientSocket = serverSocket.accept();
				Console.setIncomingConnection(clientSocket);
			}

		} catch (IOException e) {
			if (!halted) { // Check to see if this was a real break or I closed
							// it.
				System.out.println("IOException " + e.toString());
			}
		}
	}

	public void halt() {
		halted = true;
		if (serverSocket != null)
			try {
				serverSocket.close();
				if (DEBUG) {
					System.out.println("Server halted");
				}
			} catch (IOException e) {
				System.out.println("Server socket IOException when closing "
						+ e.toString());
			}
	}
}

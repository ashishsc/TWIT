package chandwani804;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection extends Thread {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private volatile Boolean halted;
	private Boolean closeWhenFinished;
	private Boolean DEBUG = false;

	// Outgoing connection
	public Connection(String hostName, int port, boolean closeWhenFinished) {
		try {
			this.socket = new Socket(hostName, port);
			this.closeWhenFinished = closeWhenFinished;
			if (DEBUG) {
				System.out.println("Connected to " + hostName + " on port "
						+ port);
			}
			halted = false;
			getInOut();
		} catch (UnknownHostException e) {
			System.err
					.println("Couldn't connect to host when sending outgoing connection.");
			close();
		} catch (IOException e) {
			System.err
					.println("Couldn't get IO when sending outgoing connection.");
			close();
		}

	}

	// Incoming connection: Bind to the socket and get IO
	public Connection(Socket incomingSocket) {
		this.socket = incomingSocket;
		this.halted = false;
		this.closeWhenFinished = true;
		try {
			getInOut();
		} catch (IOException e) {
			System.err.println("Couldn't get incoming IO");
			close();
		}
	}

	// Gets the input and output stream from the socket and starts the run
	// thread
	private void getInOut() throws IOException {
		// halted = false;
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		start();
	}

	public void run() {
		String inputLine = null;
		while (!halted) {
			try {
				inputLine = in.readLine();
				if (inputLine == null) {
					if (DEBUG) {
						System.out.println("No input, closing");
					}
					halt();
				} else {
					if (DEBUG) {
						System.out.println(inputLine);
					}
					Console.processIncoming(inputLine);
					if (!inputLine.startsWith(Console.GET_PROF_PREFIX)) {
						halt();
					}
				}
			} catch (IOException e) {
				if (DEBUG) {
					System.out.println("IOException reading input "
							+ e.toString());
				}
				if (!halted) {
					halt();
				}
			}

		}
		close(); // Close after halt
		if (DEBUG) {
			System.out.println("Closed after exiting while loop");
		}
	}

	// Pre: connection must be successful
	public void send(String message) {
		if (DEBUG) {
			System.out.println("Sending message: " + message);
		}
		out.println(message);
		out.flush();
		if (closeWhenFinished) {
			close();
		}
	}

	public Boolean brokenConnection() {
		return out.checkError();
	}

	public void halt() {
		halted = true;
	}

	// Close connection, socket.
	private void close() {
		halted = true;
		if (out != null) {
			out.close();
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				System.out.println("Error closing connection input "
						+ e.toString());
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e2) {
				System.out.println("Error closing connection input "
						+ e2.toString());
			}
		}
	}

}

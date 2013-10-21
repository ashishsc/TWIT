// Ashish Chandwani
// INFO 341
// October 5, 2012
// Lab 1: Console
// This program provides a simple console and short list of available commands.

package chandwani804;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Console {

	// Constants
	public static final String DEFAULT_PROMPT = "command>";
	public static final Map<String, String> COMMANDS = new TreeMap<String, String>();
	public static Boolean debug = false;

	// Protocol Prefixes
	public static final String STATUS_PREFIX = "tweet ";
	public static final String GET_PROF_PREFIX = "profile request ";
	public static final String PROFILE_SEND_PREFIX = "profile ";
	public static final String FOLLOW_PREFIX = "follow ";
	public static final String UNFOLLOW_PREFIX = "follow remove ";

	public static UserProfile profile;
	public static FollowerList followers;
	public static Announcer registrar;
	public static Browser browser;
	public static OnlineList onlineList;
	public static Connection incoming;

	public static void main(String[] args) throws NumberFormatException,
			UnknownHostException, IOException {
		// Set up
		profile = new UserProfile();
		followers = new FollowerList();
		incoming = null;
		storeCommands();
		Scanner console = new Scanner(System.in);
		String prompt = DEFAULT_PROMPT;
		Boolean halted = false;
		Stack<TimedCounter> threads = new Stack<TimedCounter>();
		Server server = new Server(Integer.parseInt(profile.getPort()));

		registrar = null;
		browser = null;
		onlineList = new OnlineList();

		// Main loop
		while (!halted) {
			System.out.print(prompt + " ");
			String entryLine = console.nextLine().trim();
			// Split off the command by whitespace
			String[] entryChopped = entryLine.split(" ");

			if (entryChopped.length != 0) {
				String command = entryChopped[0];

				// HALT
				if (command.equalsIgnoreCase("halt")) {
					halted = true;
					halt(threads, console, server);
				} else if (command.equalsIgnoreCase("debug")) {
					debug = !debug;
					System.out.println("debug " + debug);
					// HELP
				} else if (command.equalsIgnoreCase("help")) {
					help();

					// DATE
				} else if (command.equalsIgnoreCase("date")) {
					System.out.println(dateTime("date"));

					// TIME
				} else if (command.equalsIgnoreCase("time")) {
					System.out.println(dateTime("time"));

					// SETPROMPT
				} else if (command.equalsIgnoreCase("setprompt")) {
					prompt = entryLine.substring(command.length()).trim();

					// COUNTER
				} else if (command.equalsIgnoreCase("counter")) {
					if (entryChopped.length < 4) {
						System.out.println("bad parameters");
					} else {
						counter(threads, entryChopped[1],
								entryChopped[2].split(":"),
								Integer.parseInt(entryChopped[3]));
					}

					// PROFILE COMMANDS
				} else if (command.equalsIgnoreCase("showProfile")) {
					profile.show();
				} else if (command.equalsIgnoreCase("setName")) {
					if (entryChopped.length < 2) {
						System.out.println("bad parameters");
					} else {
						profile.setName(removePrefix(entryLine, 1));
					}
				} else if (command.equalsIgnoreCase("setID")) {
					if (entryChopped.length < 2) {
						System.out.println("bad parameters");
					} else {
						profile.setID(entryChopped[1]);
					}
				} else if (command.equalsIgnoreCase("setMotto")) {
					if (entryChopped.length < 2) {
						System.out.println("bad parameters");
					} else {
						profile.setMotto(removePrefix(entryLine, 1));
					}

				} else if (command.equalsIgnoreCase("setURL")) {
					if (entryChopped.length < 2) {
						System.out.println("bad parameters");
					} else {
						profile.setURL(entryChopped[1]);
					}

				} else if (command.equalsIgnoreCase("showFollowers")) {
					followers.show();

					// STATUS
				} else if (command.equalsIgnoreCase("status")) {
					status(entryChopped, entryLine);

					// GET PROFILE, FOLLOW, UNFOLLOW
				} else if (command.equalsIgnoreCase("getprofile")
						|| command.equalsIgnoreCase("follow")
						|| command.equalsIgnoreCase("unfollow")) {
					if (browser == null) {
						System.out.println("Start browsing first");
					} else if (entryChopped.length != 2){
						System.out.println("bad parameters");
					} else {
						sendRequest(command, entryChopped);
					}
					// ANNOUNCE
				} else if (command.equalsIgnoreCase("announce")) {
					if (registrar == null) {
						registrar = new Announcer(profile.getServiceName(),
								profile.getName(), profile.getID(),
								Integer.parseInt(profile.getPort()));
					} else {
						System.out.println("Already online");
					}
				} else if (command.equalsIgnoreCase("browse")) {
					if (browser == null) {
						browser = new Browser(profile.getServiceName(),
								onlineList, profile.getID());
					} else {
						System.out.println("Already browsing");
					}
				} else if (command.equalsIgnoreCase("showOnline")) {
					if (browser == null) {
						System.out.println("Start browsing first");
					} else if (onlineList.isEmpty()) {
						System.out.println("No users online.");
					} else {
						System.out.println(onlineList);
					}
					// Close the connection if it is broken.
				} else if (incoming != null && incoming.brokenConnection()) {

					incoming.halt();
					incoming = null;

				} else if (command.length() == 0) {
					// reprompt
				} else {
					System.out.println("Unrecognized command: " + entryLine);
				}
			}
		}

	}

	// Send a getprofile,follow or unfollow request
	public static void sendRequest(String command, String[] entryChopped)
			throws UnknownHostException, IOException {
		int choiceIndex;
		try {
			choiceIndex = Integer.parseInt(entryChopped[1]);
		} catch (NumberFormatException e) {
			System.out.println("Not a number");
			return;
		}
		if (!isValidOnlineRequest(entryChopped, choiceIndex)) {
			System.out.println("bad parameters");

			// Send
		} else {
			String toSend = "";
			Boolean closeAfterSending = true;
			if (command.equalsIgnoreCase("getprofile")) { // getprofile
				toSend = GET_PROF_PREFIX + profile.getID();
				closeAfterSending = false;
			} else if (command.equalsIgnoreCase("follow")) { // follow
				toSend = FOLLOW_PREFIX + profile.getName() + '#'
						+ profile.getID();
			} else { // unfollow
				toSend = UNFOLLOW_PREFIX + profile.getName() + '#'
						+ profile.getID();
			}
			TwitPeer peer = onlineList.get(choiceIndex);
			Connection getProf = new Connection(peer.getHost(), peer.getPort(),
					closeAfterSending);
			getProf.send(toSend); // TODO nullpoint?
		}
	}

	// Send tweet userName#userID#messages messages messages
	public static void status(String[] entryChopped, String entryLine)
			throws UnknownHostException, IOException {
		if (entryChopped.length < 2) {
			System.out.println("bad parameters");
		} else if (browser == null) {
			System.out.println("Start browsing first");
		} else {
			String status = removePrefix(entryLine, 1);
			if (status.length() > 140) {
				status = status.substring(0, 140);
			}
			for (String peerID : followers.getIDs()) {
				// send to online list
				if (onlineList.contains(peerID)) {
					TwitPeer peer = onlineList.getByID(peerID);
					
						Connection statusCon = new Connection(peer.getHost(),
								peer.getPort(), true);
						statusCon.send(STATUS_PREFIX + profile.getName() + '#'
								+ profile.getID() + '#' + status);

				}
			}
		}
	}

	/**
	 * @param incomingSocket
	 */
	public static void setIncomingConnection(Socket incomingSocket) {
		if (incoming == null) {
			if (debug) {
				System.out.println("Server: Connection accepted from "
						+ incomingSocket.getInetAddress());
			}
			incoming = new Connection(incomingSocket);
		} else {
			System.out.println("Already active connection");
		}
	}

	// Halts all running threads
	public static void halt(Stack<TimedCounter> threads, Scanner console,
			Server server) throws IOException {

		// TimedCounters
		for (int i = 0; i < threads.size(); i++) {
			TimedCounter thread = threads.pop();
			thread.terminated = true;
			thread.interrupt();
			i--;
		}

		// Halting the server and server connections
		server.halt();
		if (incoming != null) {
			incoming.halt();
		}
		if (registrar != null) {
			registrar.close();
		}
		if (browser != null) {
			browser.close();
		}
		if (incoming != null) {
			incoming.halt();
			//.close()
		}
		console.close();
		
	}

	/**
	 * @param entryChopped
	 * @param choiceIndex
	 * @return
	 */
	public static Boolean isValidOnlineRequest(String[] entryChopped,
			int choiceIndex) {
		return entryChopped.length == 2 && choiceIndex < onlineList.size()
				&& choiceIndex >= 0;
	}

	// Print a message from the sender
	/**
	 * @param sender
	 * @param message
	 */
	public static void message(String sender, String message) {
		System.out.println(sender + ": " + message);
	}

	// Start a TimedCounter thread given: the threads stack, name of the thread,
	// an array containing the minutes and seconds and an increment for the
	// counter to go up by.
	public static void counter(Stack<TimedCounter> threads, String name,
			String[] expireTimes, int increment) {
		int minutes = Integer.parseInt(expireTimes[0]);
		int seconds = Integer.parseInt(expireTimes[1]);
		TimedCounter counter = new TimedCounter(name, minutes, seconds,
				increment);
		threads.push(counter);
		counter.start();
	}

	// Store the commands and their descriptions
	public static void storeCommands() {
		COMMANDS.put("halt", "exits the console");
		COMMANDS.put("time", "prints the current time HH:MM:SS");
		COMMANDS.put("date", "prints the current date MM/DD/YYYY");
		COMMANDS.put("setprompt",
				"setprompt <new prompt> changes the prompt string");
		COMMANDS.put("help", "lists available commands");
		COMMANDS.put(
				"counter",
				"counter <name> <intervalMinutes>:<intervalSeconds> <increment>\n"
						+ "starts a new thread that prints it's counter at the end of every interval");
		COMMANDS.put("showProfile", "Prints the user profile");
		COMMANDS.put("setName",
				"setname <username> sets the UserName to given string");
		COMMANDS.put("setID",
				"setID <UniqueID> sets the unique ID for the current profile");
		COMMANDS.put("setMotto",
				"setMotto <motto> sets the user motto to the given string");
		COMMANDS.put("setURL",
				"setURL <url> sets the URL for the current profile");
		COMMANDS.put("showFollowers", "lists all peers currently following you");
		COMMANDS.put(
				"status",
				"status <statusString> Sends a 140-character restricted string to all followers online");
		COMMANDS.put(
				"getProfile",
				"getProfile <#> requests and displays the remote peer's profile, # is from the online list");
		COMMANDS.put("follow", "follow <#> follows the specified peer's tweets");
		COMMANDS.put("unfollow",
				"unfollow <#> stops following the specified peer's tweets");
		COMMANDS.put("announce",
				"Annouces your peer and makes it available to others(go online)");
		COMMANDS.put("browse", "Begin browsing for users online");
		COMMANDS.put("showOnline", "Display a list of peers currently online");
	}

	// List available commands and functions
	public static void help() {
		for (String command : COMMANDS.keySet()) {
			System.out.println(command + " - " + COMMANDS.get(command));
		}
	}

	// Provides time given the string "time" or date given any other string.
	public static String dateTime(String option) {
		Date state = new Date();
		if (option.equalsIgnoreCase("time")) {
			DateFormat formattedTime = new SimpleDateFormat("HH:mm:ss");
			return formattedTime.format(state);
		} else {
			DateFormat formmatedDate = new SimpleDateFormat("MM/dd/yyyy");
			return formmatedDate.format(state);
		}
	}

	// Remove the given number of words from the prefix
	// PRE: ASSUME that whitespace is the delimiter
	public static String removePrefix(String raw, int words) {
		String[] white = raw.split(" ");
		int offset = 0;
		for (int i = 0; i < words; i++) {
			offset += white[i].length() + 1;
		}
		return raw.substring(offset);
	}

	// Process incoming peer's protocol requests
	public static void processIncoming(String inputLine) {
		// follow remove userName#userId
		if (inputLine.startsWith(UNFOLLOW_PREFIX)) {
			String userNameID = removePrefix(inputLine, 2);
			followers.remove(userNameID);
			message("FollowerList", userNameID.split("#")[0] + " removed");
			incoming = null;
			// FOLLOW add to the follower list
			// follow userName#userID
		} else if (inputLine.startsWith(FOLLOW_PREFIX)) {
			String userNameID = removePrefix(inputLine, 1);
			String[] parsedNameID = userNameID.split("#");
			String parsedName = parsedNameID[0];
			String parsedID = parsedNameID[1];
			followers.add(parsedName, parsedID);			
			message("FollowerList", parsedNameID[0] + " added");
			incoming = null;
			// PROFILE REQUEST send profile data with hash tag delimiters
			// profile request userID
			// respond in the form:
			// key#value,key#value,key#value

		} else if (inputLine.startsWith(GET_PROF_PREFIX)) {
			// Check if fields are null before sending.
			Map<String, String> fields = profile.getFields();
			String toSend = "profile ";
			for (String fieldType : fields.keySet()) {
				if (fields.get(fieldType) != null) {
					toSend += fieldType + "#" + fields.get(fieldType) + ",";
				}
			}
			// Remove the last comma
			toSend = toSend.substring(0, toSend.length() - 1);
			if (incoming != null && !incoming.brokenConnection()) {
				incoming.send(toSend);
				incoming = null;
			} else {
				// if (inputLine.split(" ").length == 3)
				incoming = null;
				String sillyPeerID = removePrefix(inputLine, 2);
				if (onlineList.contains(sillyPeerID)) {
					TwitPeer sillyPeer = onlineList.getByID(sillyPeerID);
					Connection profileOnBrokenCon = new Connection(
							sillyPeer.getHost(), sillyPeer.getPort(), true);
					profileOnBrokenCon.send(toSend);
				}
			}

			// PROFILE Display requested user data
			// profile key#value,key#value;
		} else if (inputLine.startsWith(PROFILE_SEND_PREFIX)) {
			incoming = null;
			System.out.println("Requested Peer Profile:");
			String data = removePrefix(inputLine, 1);
			String[] pairs = data.split(",");
			for (int i = 0; i < pairs.length; i++) {
				String[] keyValue = pairs[i].split("#");
				if (keyValue.length == 2) {
					System.out.println(keyValue[0] + ": " + keyValue[1]);
				}
			}

			// TWEET Print the message, truncate to 140 characters
			// tweet userName#userID#StatusMessageString
		} else if (inputLine.startsWith(STATUS_PREFIX)) {
			incoming = null;
			String[] nameIDMessage = removePrefix(inputLine, 1).split("#");
			if (nameIDMessage.length == 3) {
				String status = nameIDMessage[2];
				if (status.length() > 140) {
					status = status.substring(0, 140);
				}
				message(nameIDMessage[0], status);
			}
			// Error message if the peer hasn't formatted their protocol
			// correctly
		} else {
			incoming = null;
			System.out.println("Peer has malformed protocol:" + inputLine);
		}
	}

}
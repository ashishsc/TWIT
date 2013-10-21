// Manages the followers of the local peer.
// Allows unique IDs for each follower

package chandwani804;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;
import java.util.Scanner;

public class FollowerList {
	private static final String FILE_NAME = "followers.txt";
	private HashMap<String, String> followers; // ID -> username

	// Store the followers as <FollowerID -> Follower User Name>
	public FollowerList() throws FileNotFoundException {
		followers = new HashMap<String, String>();
		File file = new File(FILE_NAME);
		if (file.exists()) {
			load();
		}
	}

	private void load() throws FileNotFoundException {
		Scanner in = new Scanner(new File(FILE_NAME));
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (!line.startsWith("#")) {
				String[] nameID = line.split("#");
				followers.put(nameID[1], nameID[0]);
			}
		}
		in.close();
	}

	private void save() throws FileNotFoundException {
		PrintStream out = new PrintStream(new File(FILE_NAME));
		out.println("#Follower Data");
		out.println("#" + Console.dateTime("date") + " "
				+ Console.dateTime("time"));
		for (String followerID : followers.keySet()) {
			// Print to the file {userName#userID} every line
			out.println(followers.get(followerID) + "#" + followerID);
		}
		out.close();
	}

	public void add(String userNameID) {
		String[] delimited = userNameID.split("#"); // Name#ID -> [Name, ID]

		followers.put(delimited[1], delimited[0]);
		try {
			save();
		} catch (FileNotFoundException f) {
			System.err.println("Unable to open file. " + f);
		}

	}

	public void add(String userName, String ID) {
		
		add(userName + "#" + ID);
	}

	// Remove the client from the follower list
	public void remove(String userNameID) {
		String[] delimited = userNameID.split("#");
		followers.remove(delimited[1]);
		try {
			save();
		} catch (FileNotFoundException f) {
			System.err.println("Unable to open file. " + f);
		}
	}

	public Boolean contains(String ID) {
		return followers.keySet().contains(ID);
	}

	// Show a list of all followers and their ID's
	// If the local client does not have any followers, then a special message
	// will print
	public void show() {
		if (followers.size() == 0) {
			System.out.println("You have no followers");
		} else {
			System.out.println("#Follower Data");
			int i = 1;
			for (String followerID : followers.keySet()) {
				System.out.println("Follower " + i + ": "
						+ followers.get(followerID) + " ID: " + followerID);
				i++;
			}
		}
	}

	public Set<String> getIDs() {
		return followers.keySet();
	}

}

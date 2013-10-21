// Manages the user profile 

package chandwani804;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TreeMap;

/* The expected file format looks like this:
 #User Profile Data
 #Tue Nov 15 16:41:09 PST 2011
 ProtocolPort=34567
 UserMotto=Sunshine makes people blonde 
 UserID=12345ABC
 UserURL=http\://www.surf-righteous-waves.net
 UserName=Sandy Surfer
 DNSSDServiceName=twit
 */

public class UserProfile {
	private final String FILE_NAME = "profile.txt";
	private final String DEFAULT_PORT = "55555";
	private final String DEFAULT_SERVICE = "twit";
	private final String ASSIGNED_ID = "eeaa11094279ab90d816891506928045";
	private final char[] illegals = { '#', ',', '=', '@' };

	private TreeMap<String, String> fields;

	public UserProfile() throws FileNotFoundException {
		fields = new TreeMap<String, String>();
		fields.put("UserName", null);
		fields.put("UserID", null);
		fields.put("UserMotto", null);
		fields.put("UserURL", null);
		fields.put("DNNSDServiceName", null);
		fields.put("ProtocolPort", null);
		File file = new File(FILE_NAME);
		if (file.exists()) {
			load();
		}
		if (fields.get("UserID") == null) {
			setID(ASSIGNED_ID);
		}
		if (fields.get("DNNSDServiceName") == null) {
			setServiceName(DEFAULT_SERVICE);
		}
		if (fields.get("ProtocolPort") == null) {
			setPort(Integer.parseInt(DEFAULT_PORT));
		}
	}

	private void load() throws FileNotFoundException {
		Scanner in = new Scanner(new File(FILE_NAME));
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (!line.startsWith("#")) {
				String[] bisect = line.split("=");

				// If the field was defined;
				if (bisect.length > 1) {
					if (line.startsWith("ProtocolPort")) {
						setPort(Integer.parseInt(bisect[1]));
					} else if (line.startsWith("UserMotto")) {
						setMotto(bisect[1]);
					} else if (line.startsWith("UserID")) {
						setID(bisect[1]);
					} else if (line.startsWith("UserURL")) {
						setURL(bisect[1]);
					} else if (line.startsWith("UserName")) {
						setName(bisect[1]);
					} else if (line.startsWith("DNNSDServiceName")) {
						setServiceName(bisect[1]);
					}
				}
			}
		}
		in.close();
	}

	private void save() throws FileNotFoundException {
		PrintStream out = new PrintStream(new File(FILE_NAME));
		out.println("#User Profile Data");
		out.println("#" + Console.dateTime("date") + " "
				+ Console.dateTime("time"));
		printFields(out);
		out.close();
	}

	public TreeMap<String, String> getFields() {
		return fields;
	}

	public void show() {
		printFields(System.out);
	}

	private void printFields(PrintStream out) {
		for (String field : fields.keySet()) {
			out.print(field + "=");
			if (fields.get(field) == null) {
				out.println("");
			} else {
				out.println(fields.get(field));
			}
		}
	}

	public String getPort() {
		return fields.get("ProtocolPort");
	}

	public void setPort(int port) throws FileNotFoundException {
		fields.put("ProtocolPort", "" + port);
		save();
	}

	public String getName() {
		return fields.get("UserName");
	}

	public void setName(String name) throws FileNotFoundException {
		if (!containsIllegal(name)) {
			fields.put("UserName", name);
			save();
		}
	}

	public String getID() {
		return fields.get("UserID");
	}

	public void setID(String ID) throws FileNotFoundException {
		if (!containsIllegal(ID)) {
			fields.put("UserID", ID);
			save();
		}
	}

	public String getMotto() {
		return fields.get("UserMotto");
	}

	public void setMotto(String motto) throws FileNotFoundException {
		if (!containsIllegal(motto)) {
			fields.put("UserMotto", motto);
			save();
		}
	}

	public String getURL() {
		return fields.get("UserURL");
	}

	public void setURL(String URL) throws FileNotFoundException {
		if (!containsIllegal(URL)) {
			fields.put("UserURL", URL);
			save();
		}
	}

	// returns the service name
	public String getServiceName() {
		return fields.get("DNNSDServiceName");
	}

	// Sets the service name to the given string
	public void setServiceName(String service) throws FileNotFoundException {
		if (!containsIllegal(service)) {
			fields.put("DNNSDServiceName", service);
			save();
		}
	}

	// Returns whether or not an illegal is contained in the String.
	private Boolean containsIllegal(String test) {
		for (int i = 0; i < illegals.length; i++) {
			for (int j = 0; j < test.length(); j++) {
				if (test.charAt(j) == illegals[i]) {
					System.out.println("Illegal character");
					return true;
				}
			}
		}
		return false;
	}
}
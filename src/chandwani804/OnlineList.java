package chandwani804;

import java.util.ArrayList;

import com.apple.dnssd.TXTRecord;

public class OnlineList {

	private volatile ArrayList<TwitPeer> list;

	public OnlineList() {
		list = new ArrayList<TwitPeer>();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * @param hostName
	 * @param port
	 * @param record
	 */
	public void add(String hostName, int port,
			TXTRecord record) {
		list.add(new TwitPeer(hostName, port, record));
	}

	/**
	 * Removes and returns the TwitPeer with the given serviceName
	 * 
	 * @param serviceName
	 * @return the peer if they were in the list, null if not
	 */
	public TwitPeer remove(String serviceName) {
		for (int i = 0; i < list.size(); i++) {
			TwitPeer peer = list.get(i);
			if (list.get(i).getName().equals(serviceName)) {
				list.remove(i);
				return peer;
			}
		}
		return null;
	}

	/**
	 * @param ID
	 * @return
	 */
	public boolean contains(String ID) {
		for (int i = 0; i < list.size(); i++) {
			if (ID.equals(list.get(i).getID())) {
				return true;
			}
		}
		return false;
	}

	public boolean containsName(String name) {
		for (int i = 0; i < list.size(); i++) {
			if (name.equals(list.get(i).getName())) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		String toReturn = "";
		for (int i = 0; i < list.size(); i++) {
			toReturn += i + " " + list.get(i).getName() + " ("
					+ list.get(i).getID() + ") " + "\n";
		}
		return toReturn;
	}

	public TwitPeer getByID(String userID) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getID().equals(userID)) {
				return list.get(i);
			}
		}
		throw new IllegalArgumentException();
	}
	
	public TwitPeer getByName(String name) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getID().equals(name)) {
				return list.get(i);
			}
		}
		throw new IllegalArgumentException();
	}

	public TwitPeer get(int index) {
		return list.get(index);
	}

	public int size() {
		return list.size();
	}

}

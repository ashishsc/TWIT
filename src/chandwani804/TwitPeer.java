package chandwani804;

import com.apple.dnssd.TXTRecord;

public class TwitPeer {
	private String ID;
	private String userName;
	private int port;
	private String hostName;


	/**
	 * @param hostName
	 * @param port
	 * @param record
	 */
	public TwitPeer(String hostName, int port, TXTRecord record) {
		this.port = port;
		this.hostName = hostName;
		
		// This could take a string instead of depending on order
		this.ID = record.getValueAsString(3);
		this.userName = record.getValueAsString(2);
	}

	public String getID() {
		return this.ID;
	}

	public String getName() {
		return this.userName;
	}



	public String getHost() {
		return this.hostName;
	}
	public int getPort() {
		return this.port;
	}
}
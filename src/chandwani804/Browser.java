package chandwani804;

import com.apple.dnssd.*;

public class Browser implements BrowseListener, ResolveListener {
	// service should probably be something like "http"

	private DNSSDService browser;
	private DNSSDService resolver;
	private OnlineList onlineList;
	private String myID;

	/**
	 * @param service
	 * @param onlineList
	 */
	public Browser(String service, OnlineList onlineList, String myID) {
		String sdService = "_" + service + "._tcp";
		browser = null;
		browse(sdService);
		this.onlineList = onlineList;
		this.myID = myID;
	}

	public void browse(String sdService) {
		if (browser == null) {
			try {
				browser = DNSSD.browse(sdService, this);
				Console.message("Browser", "browse(): looking for " + sdService);
			} catch (DNSSDException e) {
				Console.message("Browser",
						"Exception on browse: " + e.toString());
			}
		}
	}

	// Part of BrowseListener interface
	public void serviceFound(DNSSDService br, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		// Console.message("Browser", instanceName);
		try {
			DNSSD.resolve(0, DNSSD.ALL_INTERFACES, serviceName, regType,
					domain, this);
		} catch (DNSSDException e) {
			Console.message("Browser", "Exception on resolve: " + e.toString());
		}
	}

	// Part of BrowseListener interface
	// Remove the lost person from the online list
	public void serviceLost(DNSSDService br, int flags, int ifIndex,
			String serviceName, String regType, String domain) {
		synchronized (onlineList) {
			if (onlineList.containsName(serviceName)) {
				TwitPeer peer = onlineList.remove(serviceName);
				Console.message("Browser", peer.getName() + " went offline");
			} else {
				Console.message("Browser", serviceName
						+ " went offline (couldn't remove)");
			}
		}
	}

	// Once the service is resolved, add someone to online list
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord record) {
		resolver.stop();
		resolver = null;
		// Add to online list
		synchronized (onlineList) {
			// Verify that the ID of the registered user is not my own.
			if (!(record.getValueAsString(3)).equalsIgnoreCase(myID)) {
				// BAD: Assumes serviceName == DisplayName from the txtrec
				String name = record.getValueAsString("DisplayName"); 
				onlineList.add(hostName, port, record);
				Console.message("Browser", name + " is online.");
			} else {
				Console.message("Browser", "You are now online");
			}
		}
	}

	// Part of BrowseListener interface
	public void operationFailed(DNSSDService service, int errorCode) {
		Console.message("Browser", "Operation Failure, errorCode: " + errorCode);
	}

	// Close browser and any open resolver
	public void close() {
		if (browser != null) {
			browser.stop();
			browser = null;
		}
		if (resolver != null) {
			resolver.stop();
			resolver = null;
		}
	}

}

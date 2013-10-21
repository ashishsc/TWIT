package chandwani804;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.TXTRecord;

public class Announcer implements RegisterListener {

	private DNSSDRegistration register;
	public Announcer(String service, String userName, String userID, int port) {

		String sdService = "_" + service + "._tcp";
		System.out.println("registering " + sdService);
		announce(userName, userID, port, sdService);
	}

	public void operationFailed(DNSSDService arg0, int arg1) {
		// TODO Auto-generated method stub
		Console.message("Announcer", "Announcer: Operation failed");

	}

	public void serviceRegistered(DNSSDRegistration reg, int flags,
			String instanceName, String regType, String domain) {
		Console.message("Announcer", "Registered service: " + regType + ":"
				+ instanceName);
	}


	public void announce(String userName, String userID, int port, String sdService) {
		if (register == null) {
			TXTRecord record = new TXTRecord();
			record.set("Version", "1.1");
			record.set("EncryptSupport", "false");
			record.set("DisplayName", userName);
			record.set("UserID", userID);
			try {
				register = DNSSD.register(0, DNSSD.ALL_INTERFACES, userName,
						sdService, null, null, port, record, this);
			} catch (DNSSDException e) {
				System.out.println("announce: " + e.toString());
			}
		}
	}

	public void close() {
		if (register != null) {
			register.stop();
		}
		register = null;
	}
}
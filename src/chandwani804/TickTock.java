// Ashish Chandwani
// INFO 341
// Extends a thread and prints every given amount of time. 

package chandwani804;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TickTock extends Thread {
	private String name; // string name
	Boolean terminated; // whether or not the thread has ended
	protected int minutes;
	protected int seconds;

	// Pre: minutes and seconds must be positive, minuts and seconds cannot be 0
	public TickTock(String name, int minutes, int seconds) {
		this.name = name;
		if (minutes < 0 || seconds < 0 || (minutes == 0 && seconds == 0)) {
			throw new IllegalArgumentException();
		}
		this.minutes = minutes;
		this.seconds = seconds;
		this.terminated = false;
	}

	public void run() {
		while (!terminated) {
			try {
				set_interval(minutes, seconds);
			} catch (InterruptedException e) {
				return;
			}
			System.out.print(speak() + "\r");
		}
	}

	public String speak() {
		Date state = new Date();
		DateFormat formattedDate = new SimpleDateFormat(
				"E MMM dd HH:mm:ss z yyyy");
		return ("Thread:" + name + " " + formattedDate.format(state));
	}

	public void set_interval(int minutes, int seconds)
			throws InterruptedException {
		sleep((minutes * 60 + seconds) * 1000);
	}

}
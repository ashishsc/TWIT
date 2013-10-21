package chandwani804;

public class TimedCounter extends TickTock {
	private int increment;
	private int counter;

	public TimedCounter(String name, int minutes, int seconds, int increment) {
		super(name, minutes, seconds);
		this.increment = increment;
		counter = 0;

	}
	
	// adds a counter that increments everytime speak is called
	public String speak() {
		counter += increment;
		return super.speak() + " - counter: " + counter;
	}

}

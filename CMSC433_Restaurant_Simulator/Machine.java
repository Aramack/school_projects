//Mark Penny 111477906
package cmsc433.p2;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;

	//YOUR CODE GOES HERE...
	private final int capacity;
	private int numInUse;
	private Object myLock;

	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		//YOUR CODE GOES HERE...
		this.capacity = capacityIn;
		this.numInUse = 0;
		this.myLock = new Object();
		Simulation.logEvent(SimulationEvent.machineStarting(this, foodIn, capacityIn));
	}

	private void incrementNumberInUse(){
		synchronized(myLock){
			numInUse++;
		}
	}

	private void decrementNumberInUse(){
		synchronized(myLock){
			numInUse--;
			myLock.notifyAll();
		}
	}

	public void shutDown(){
		Simulation.logEvent(SimulationEvent.machineEnding(this));
	}
	
	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 * 
	 * Changed return type from Object to CookanItem 
	 */

	public CookAnItem makeFood() throws InterruptedException {
		//YOUR CODE GOES HERE...
		synchronized(myLock){
			while(numInUse >= capacity){
				myLock.wait();
			}
			incrementNumberInUse();
			return new CookAnItem();
		}
	}

	private Machine getThisMachine(){
		return this;
	}
	
	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	public class CookAnItem implements Runnable {
		public boolean isDone = false;
		public Object completionNotification = new Object();
		public void run() {
			Simulation.logEvent(SimulationEvent.machineCookingFood(getThisMachine(), machineFoodType));
			synchronized(completionNotification){
				try {
					Thread.sleep(machineFoodType.cookTimeMS);
				} catch(InterruptedException e) { 

				}
				isDone = true;
				Simulation.logEvent(SimulationEvent.machineDoneFood(getThisMachine(), machineFoodType));
				decrementNumberInUse();
				completionNotification.notifyAll();
			}
			synchronized(myLock){
				myLock.notifyAll();
			}
		}
		public Food getFoodType(){
			return machineFoodType;
		}
	}


	public String toString() {
		return machineName;
	}
}
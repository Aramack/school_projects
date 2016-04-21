//Mark Penny 111477906
package cmsc433.p2;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;    
	private static Object entranceGaurd = new Object();
	private Table myTable;

	//Not using these variables
	//private static int runningCounter = 0;
	//private final int orderNum;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		//this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}

	/**
	 * Enter the cafe by trying to sit at every table.
	 * Sleeps on failure.
	 *
	 */
	private void enter(){
		while(true){
			synchronized(entranceGaurd){
				int numTables = SeatingArea.size();
				for(int itt = 0; itt < numTables; itt++){
					if(SeatingArea.get(itt).sitdown()){
						myTable = SeatingArea.get(itt);
						return;
					}
				}
				try {
					entranceGaurd.wait();
				} catch (InterruptedException e) {
					continue;
				}
			}
		}
	}
	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//Before entering the coffee shop:
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		enter();
		//After entering the coffee shop:
		Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
		int orderNumber;
		synchronized(OrderQueue.mainLock){
			orderNumber = OrderQueue.placeOrder(order);
			//After placing order (but before it has been filled):
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, orderNumber));
		}
		synchronized(Server.serverLock){
			while(!Server.checkForCompletedOrder(orderNumber)){
				try {
					Server.serverLock.wait();
				} catch (InterruptedException e) {
					continue;
				}
			}
			Server.serverLock.notifyAll();
		}

		//After receiving order:
		Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, orderNumber)); 
		//Just before about to leave the coffee shop:
		leave();
	}

	/**
	 * Leave the cafe. Wakes up all threads waiting on the entrance gaurd. 
	 */
	private void leave(){
		synchronized(entranceGaurd){
			Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
			myTable.leave();
			entranceGaurd.notifyAll();
		}
	}
}
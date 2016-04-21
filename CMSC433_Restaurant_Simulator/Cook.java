//Mark Penny 111477906
package cmsc433.p2;

import java.util.ArrayList;
import java.util.List;



/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private static int ordersCompleted = 0;
	private static int totalCustomers;
	private static Object cookLock = new Object();

	public static void setTotalCustomers(int customersExpected){
		totalCustomers = customersExpected;
	}
	private static void incrementOrdersCompleted(){
		synchronized(cookLock){
			ordersCompleted++;
		}
	}

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {
		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(true) {
				//YOUR CODE GOES HERE...
				if(ordersCompleted >= totalCustomers){
					throw new InterruptedException();
				}
				
				Order CurrentOrder;
				synchronized(OrderQueue.mainLock){
					CurrentOrder = OrderQueue.takeOrder();
					if(CurrentOrder == null){
						continue;
					}
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, CurrentOrder.getOrder(), CurrentOrder.getOrderNumber()));
				}
				List<Food> currentFood = CurrentOrder.getOrder();
				List<Machine.CookAnItem> foodThread = new ArrayList<Machine.CookAnItem>();
				for(int itt = 0; itt < currentFood.size(); itt++){
					if(currentFood.get(itt).name.equals(FoodType.burger.name)){
						foodThread.add(Kitchen.Burgers.makeFood());
					} else if(currentFood.get(itt).name.equals(FoodType.fries.name)){
						foodThread.add(Kitchen.Fries.makeFood());
					} else {
						foodThread.add(Kitchen.Coffees.makeFood());
					}
					Simulation.logEvent(SimulationEvent.cookStartedFood(this, foodThread.get(itt).getFoodType(),  CurrentOrder.getOrderNumber()));
					foodThread.get(itt).run();
				}
				
				for(Machine.CookAnItem itt: foodThread){
					synchronized(itt.completionNotification){
						while(!itt.isDone){
							itt.completionNotification.wait();							
						}
						Simulation.logEvent(SimulationEvent.cookFinishedFood(this, itt.getFoodType(), CurrentOrder.getOrderNumber()));
					}
				}

				//Order is completed; Notify all customers that an order is finished;
				Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, CurrentOrder.getOrderNumber()));
				incrementOrdersCompleted();
				synchronized(Server.serverLock){
					Server.addCompletedOrder(CurrentOrder.getOrderNumber());
					Server.serverLock.notifyAll();
				}
			}
		} catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}
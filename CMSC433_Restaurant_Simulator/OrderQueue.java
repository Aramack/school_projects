//Mark Penny 111477906
package cmsc433.p2;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class OrderQueue {
	private static Queue<Order> orderQueue;
	public static Object mainLock;
	private static int orderNumber = 0;

	public static void Initialize(){
		orderQueue = new LinkedList<Order>();
		mainLock = new Object();
	}

	public static int placeOrder(List<Food> order){
		orderNumber++;
		orderQueue.add(new Order(order, orderNumber));
		return orderNumber;

	}

	public static synchronized Order takeOrder(){
		return orderQueue.poll();

	}

}

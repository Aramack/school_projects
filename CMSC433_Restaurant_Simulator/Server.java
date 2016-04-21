//Mark Penny 111477906
package cmsc433.p2;

import java.util.ArrayList;
import java.util.List;

public class Server {
	private static List<Integer> completedOrders;
	public static Object serverLock = new Object();

	public static void initialize(){
		completedOrders = new ArrayList<Integer>();
	}

	public static void addCompletedOrder(int orderNum){
		completedOrders.add(new Integer(orderNum));
	}

	public static boolean checkForCompletedOrder(int orderNumber){
		int size = completedOrders.size();
		for(int itt = 0; itt < size; itt++){
			if(completedOrders.get(itt) == orderNumber){
				return true;
			}
		}
		return false;
	}
}

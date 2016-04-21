package cmsc433.p2;

import java.util.List;

public class Order {
	private List<Food> myOrder;
	private int myOrderNumber;
	
	public Order(List<Food> order, int orderNumber ){
		this.myOrder = order;
		this.myOrderNumber = orderNumber;
	}
	
	public List<Food> getOrder(){
		return myOrder;
	}
	
	public int getOrderNumber(){
		return myOrderNumber;
	}
}

package cmsc433.p2;

public class Kitchen {
	public static Machine Burgers;
	public static Machine Fries;
	public static Machine Coffees;
	
	public static void init(int capacity){
		Burgers = new Machine("Grill", FoodType.burger, capacity);
		Fries = new Machine("Fryer", FoodType.fries, capacity);
		Coffees = new Machine("CoffeeMaker2000", FoodType.coffee, capacity);
	}
}

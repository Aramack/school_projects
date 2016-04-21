//Mark Penny 111477906
package cmsc433.p2;

public class Table {
	private Boolean occupied;
	public Table(){
		occupied = false;
	}
	
	public boolean isOccupied(){
		return new Boolean(occupied);
	}
	
	public synchronized boolean sitdown(){
		if(occupied){
			return false;
		}else{
			occupied = true;
			return true;
		}
	}
	
	public synchronized void leave(){
		occupied = false; 
	}
	
}

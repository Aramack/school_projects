import java.util.HashSet;
import java.util.Set;

public class MySet<T> {
	private Set<T> mySet = new HashSet<T>();
	
	// returns the number of items in the set
	public int size() {
		return mySet.size();
	}
	
	// removes all items from the set
	public synchronized void clear() {
		mySet.clear();
	}
	
	// removes the object from the set
	public synchronized boolean remove(T o) {
		if(mySet.contains(o)){
			mySet.remove(o);
			return true;//Returns true is the removal is successful
		}
		return false;//returns false otherwise
	}
	
	// adds the object to the set
	public synchronized boolean add(T o){
		if(!mySet.contains(o)){
			mySet.add(o);
			return true;//Returns true if the generic object of type T was inserted.
		}
		return false;//Returns false otherwise.
	}
	
	// returns true if the set contains the object, false otherwise
	public synchronized boolean contains(T o) {
		if(mySet.contains(o)){
			return true;
		}
		return false;
	}
}

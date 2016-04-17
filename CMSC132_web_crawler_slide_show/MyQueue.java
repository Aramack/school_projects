import java.util.LinkedList;



public class MyQueue<T> {
	private LinkedList<T> myQueue = new LinkedList<T>();
	//A linked list data structure being used to represent a queue.

	//Returns the number of items in the queue
	public int size(){
		return myQueue.size();	
	}


	//Removes all items from the queue
	public synchronized void clear(){
		myQueue.clear();
	}
	//Adds the object to one end of the queue
	public synchronized void enqueue(T o) {
		myQueue.addLast(o);
		notifyAll();
	}

	//Removes an object from the end opposite of that to which things are added.
	//If the queue is empty, the thread will wait here until an item becomes available
	public synchronized T dequeue() {
		while (myQueue.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return myQueue.removeFirst();
	}
	
	public synchronized boolean isEmpty(){
		return myQueue.isEmpty();//Returns true if the queue is empty, false otherwise
	}
}

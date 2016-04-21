package cmsc433.p3;
//found on:
//http://stackoverflow.com/questions/4173873/single-lifo-executor-swingworker
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.ConcurrentLinkedDeque;

public class BlockingLifoQueue<T> implements BlockingQueue<T>{
    private final LinkedBlockingDeque<T> stack = new LinkedBlockingDeque<T>();
    //private final ConcurrentLinkedDeque<T> stack = new ConcurrentLinkedDeque<T>();
    public T remove() {
        return stack.remove();
    }
    
    public T poll() {
        return stack.poll();
    }
    
    public T element() {
        return stack.element();
    }
    
    public T peek() {
        return stack.peek();
    }

    public int size() {
        return stack.size();
    }
    
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    public Iterator<T> iterator() {
        return stack.iterator();
    }
    
    public Object[] toArray() {
        return stack.toArray();
    }
    
    public <S> S[] toArray(final S[] a) {
        return stack.toArray(a);
    }
    
    public boolean containsAll(final Collection<?> c) {
        return stack.containsAll(c);
    }
    
    public boolean addAll(final Collection<? extends T> c) {
        return stack.addAll(c);
    }
    
    public boolean removeAll(final Collection<?> c) {
        return stack.removeAll(c);
    }
    
    public boolean retainAll(final Collection<?> c) {
        return stack.removeAll(c);
    }
    
    public void clear() {
        stack.clear();
    }
    
    public boolean add(final T e) {
        return stack.offerFirst(e); //Used offerFirst instead of add.
    }
    
    public boolean offer(final T e) {
        return stack.offerFirst(e); //Used offerFirst instead of offer.
    }
    
    public void put(final T e) throws InterruptedException {
        stack.put(e);
    }
    
    public boolean offer(final T e, final long timeout, final TimeUnit unit)
    throws InterruptedException {
        return stack.offerLast(e, timeout, unit);
    }
    
    public T take() throws InterruptedException {
        return stack.take();
    }
    
    public T poll(final long timeout, final TimeUnit unit)
    throws InterruptedException {
        return stack.poll();
    }
    
    public int remainingCapacity() {
        return stack.remainingCapacity();
    }
    
    public boolean remove(final Object o) {
        return stack.remove(o);
    }
    
    public boolean contains(final Object o) {
        return stack.contains(o);
    }
    
    public int drainTo(final Collection<? super T> c) {
        return stack.drainTo(c);
    }
    
    public int drainTo(final Collection<? super T> c, final int maxElements) {
        return stack.drainTo(c, maxElements);
    }
}
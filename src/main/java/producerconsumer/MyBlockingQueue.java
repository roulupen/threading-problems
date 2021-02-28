package producerconsumer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
*  Reference : https://www.youtube.com/watch?v=UOr9kMCCa5g
* */
public class MyBlockingQueue<E> {
    private Queue<E> queue;
    private int capacity;

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public MyBlockingQueue(int capacity) {
        queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public void put(E e) {
        lock.lock();
        try {
            while (queue.size() == capacity) { // Wait the thread
                notEmpty.await();
            }
            queue.add(e);
            notFull.notifyAll();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        } finally {
            lock.lock();
        }
    }

    public E take() {
        lock.lock();
        try {
            while (queue.size() == 0) {// Thread waits here
                notFull.await();
            }
            E item = queue.remove();
            notEmpty.notifyAll();
            return item;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return null;
    }

}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
* One or more write operation on the resource but one thread to write at a time. multiple simultaneous reader.
* */
public class ReadWriteLockExample {
    static final int READER_SIZE = 10;
    static final int WRITER_SIZE = 2;

    /*
    * This is a List data structure which supports sequential/single write operation to the list,
    * but allows multiple read operation of the list using ReentrantReadWriteLock
    * */
    static class ReadWriteList<E> {
        private final List<E> list = new ArrayList<>();
        private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

        public ReadWriteList(E... initialElements) {
            list.addAll(Arrays.asList(initialElements));
        }

        public void add(E e) {
            Lock writeLock = readWriteLock.writeLock();
            writeLock.lock();
            try {
                list.add(e);
            } finally {
                writeLock.unlock();
            }
        }

        public E get(int index) {
            Lock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
                return list.get(index);
            } finally {
                readLock.unlock();
            }
        }

        public int size() {
            Lock readLock = readWriteLock.readLock();
            readLock.lock();
            try {
                return list.size();
            } finally {
                readLock.unlock();
            }
        }
    }

    /*
    * Writer thread operation class
    * */
    static class Writer implements Runnable {
        ReadWriteList<Integer> readWriteList;

        public Writer(ReadWriteList<Integer> readWriteList) {
            this.readWriteList = readWriteList;
        }

        @Override
        public void run() {
            Random random = new Random();

            // Each writer thread will add 10 elemnts to the list
            int i = 0;
            while(i++ < 10) {
                int number = random.nextInt(100);
                readWriteList.add(number);
                System.out.println(Thread.currentThread().getName() + " added :=> " + number);
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Reader implements Runnable {
        ReadWriteList<Integer> readWriteList;

        public Reader(ReadWriteList<Integer> readWriteList) {
            this.readWriteList = readWriteList;
        }

        @Override
        public void run() {
            Random random = new Random();

            // Each reader thread will read 10 elements from the list
            int i = 0;
            while(i++ < 10) {
                int randIndex = random.nextInt(readWriteList.size());
                int number = readWriteList.get(randIndex);
                System.out.println(Thread.currentThread().getName() + " read :( " + randIndex + " )=> " + number);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ReadWriteList<Integer> sharedResource = new ReadWriteList<>();

        for(int i = 0; i < WRITER_SIZE; i++) {
            new Thread(new Writer(sharedResource), "Writer_" + (i+1)).start();
        }

        for(int j = 0; j < READER_SIZE; j++) {
            new Thread(new Reader(sharedResource), "Reader_" + (j+1)).start();
        }
    }
}
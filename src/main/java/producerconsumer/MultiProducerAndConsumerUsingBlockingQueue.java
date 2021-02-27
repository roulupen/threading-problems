package producerconsumer;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MultiProducerAndConsumerUsingBlockingQueue {
    public static void main(String[] args) {

        BlockingQueue<String> bq = new ArrayBlockingQueue<>(20);
        Random rnd = new Random();

        Runnable producer = () -> {
            while (true) {
                try {
                    bq.put(Thread.currentThread().getName() + " => " + rnd.nextInt());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable consumer = () -> {
            while (true) {
                try {
                    System.out.println(Thread.currentThread().getName() + " : " + bq.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        new Thread(producer, "A").start();
        new Thread(producer, "B").start();


        new Thread(consumer, "X").start();
        new Thread(consumer, "Y").start();
        new Thread(consumer, "Z").start();

        // Collections.synchronizedMap()
    }
}

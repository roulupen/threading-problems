import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/*
* Shared Printer class prints both even and odd number by different thread.
* Initially semEven permits 0 means no one can acquire a lock on semEven,
* to make the permits to one someone has to call release on that semEven
*
* Initially semOdd is 1, means one thread can acquire lock on this semaphore and perform operations.
* Since initially semEven = 0 and semOdd = 1 means only a odd number will be printed first.
*
* Once we got access to odd semaphore and prints the odd number we need to make sure then even number prints.
* So instead of releasing odd semaphore we are releasing even semaphore which will make it semEven permits to increase by 1
*
* Initially semEven was 0 and since release was called it becomes 1 and now someone can perform acquire operation on this.
* */
class SharedPrinter {
    private Semaphore semEven = new Semaphore(0);
    private Semaphore semOdd = new Semaphore(1);

    /**
     * For Printing an even number
     */
    public void printEven(int num) {
        try {
            semEven.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " : " + num);
        semOdd.release();
    }

    public void printOdd(int num) {
        try {
            semOdd.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " : " + num);
        semEven.release();
    }
}

class PrintEvenTask implements Runnable {
    SharedPrinter printer;
    int max;

    public PrintEvenTask(SharedPrinter printer, int max) {
        this.max = max;
        this.printer = printer;
    }

    @Override
    public void run() {
        for(int i = 2; i <= max; i+=2) {
            printer.printEven(i);
        }
    }
}

class PrintOddTask implements Runnable {
    SharedPrinter printer;
    int max;

    public PrintOddTask(SharedPrinter printer, int max) {
        this.max = max;
        this.printer = printer;
    }

    @Override
    public void run() {
        for(int i = 1; i <= max; i+=2) {
            printer.printOdd(i);
        }
    }
}

public class PrintOddEventNumberUsingTwoThreadsUsingSemaphore {

    public static void main(String[] args) throws InterruptedException {
        SharedPrinter printer =  new SharedPrinter();
        ExecutorService service = Executors.newFixedThreadPool(2);

        PrintEvenTask evenTask = new PrintEvenTask(printer, 10);
        PrintOddTask oddTask = new PrintOddTask(printer, 10);

        service.submit(evenTask);
        service.submit(oddTask);

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }
}

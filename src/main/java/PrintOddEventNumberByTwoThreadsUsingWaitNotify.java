
/*
* This class just passes odd/even number to the printer class which will print the odd and even number.
* Instead of creating two separate class for Print Even or Print Odd, here in this class the constructor identifies whether the object meant
* to perform EvenTask or OddTask
* */
class TaskOddEvenPrinting implements Runnable{
    private int max;
    private Printer print;
    private boolean isEvenNumber;

    public TaskOddEvenPrinting(Printer print, int max, boolean isEvenNumber) {
        this.print = print;
        this.max = max;
        this.isEvenNumber = isEvenNumber;
    }

    @Override
    public void run() {
        int number = isEvenNumber ? 0 : 1;
        while(number <= max) {
            if(isEvenNumber)
                print.printEven(number);
            else
                print.printOdd(number);

            number += 2;
        }
    }
}

/*
* This is a shared printer which prints odd and even number using wait and notify.
*
* We have a volatile variable isEven which keeps track which function is currently performing the operation.
* If isEven = false, means odd number can print
* If isEven = true, means even number can print
*
* printEven/printOdd:
*   This method checks if the isEven is false it means printOdd is printing so it waits till isEven = true
*   Once it gets the lock then it prints the number and sets isEven = false and notify() this means it's notifying the printOdd functions to resume the operation
*   if it's waiting for the lock.
*
*   Same thing happens when the thread execution is in side printOdd function  but in reverse way.
* */
class Printer {
    private volatile boolean isEven = false;

    public synchronized void printEven(int number) {
        while (!isEven) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " : " + number);
        isEven = false;
        notify();
    }

    public synchronized void printOdd(int number) {
        while (isEven) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " : " + number);
        isEven = true;
        notify();
    }
}

public class PrintOddEventNumberByTwoThreadsUsingWaitNotify {

    public static void main(String[] args) {
        Printer print = new Printer();
        Thread t1 = new Thread(new TaskOddEvenPrinting(print, 10, false),"Odd");
        Thread t2 = new Thread(new TaskOddEvenPrinting(print, 10, true),"Even");
        t2.start();
        t1.start();
    }

}

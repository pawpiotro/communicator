/**
 * Created by Pawel on 23-Apr-16.
 */

public class Client {
    public static void main(String[] args) {
        /*Runnable[] runners = new Runnable[10];
        Thread[] threads = new Thread[10];

        for(int i=0; i<10; i++) {
            runners[i] = new MyRun(i);
        }

        for(int i=0; i<10; i++) {
            threads[i] = new Thread(runners[i]);
        }

        for(int i=0; i<10; i++) {
            threads[i].start();
            System.out.println("=================");
        }*/
        Thread thread1 = new Thread(new Receive(1));
        Thread thread2 = new Thread(new Send());

        thread1.start();
        thread2.start();
    }
}

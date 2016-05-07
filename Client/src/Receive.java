/**
 * Created by Pawel on 23-Apr-16.
 */

import java.lang.Thread;

public class Receive implements Runnable{

    private int id;

    public Receive(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while(true) {
            System.out.println("Watek "+id);
            try {
                //usypiamy wątek na 1000 milisekund
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

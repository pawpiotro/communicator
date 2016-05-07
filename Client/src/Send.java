
/**
 * Created by Pawel on 23-Apr-16.
 */

import java.util.Scanner;

import java.lang.Thread;

public class Send implements Runnable {

    private String name;

    public void run() {
        System.out.print("Enter your name\n");
        while(true) {

            Scanner scanner = new Scanner (System.in);
            name = scanner.nextLine();
            System.out.print("name="+name+"\n");
            try {
                //usypiamy wątek na 1000 milisekund
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

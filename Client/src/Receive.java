/**
 * Created by Pawel on 23-Apr-16.
 */

import java.io.BufferedReader;
import java.io.IOException;

public class Receive implements Runnable{
    private BufferedReader stream;
    public Receive(BufferedReader stream){
        this.stream = stream;
    }

    private void receivedMsg(String msg){
        int id = Integer.parseInt(msg.substring(0,4));
        Contact tmp = Client.findUser(id);
        Client.display.print(tmp.getName()+": "+msg.substring(4));
    }
    @Override
    public void run(){
            while(true) {
                try {
                    String line;
                    while((line = stream.readLine()) != null) {
                        String header = line.substring(0, 5);
                        String msg;
                        if(line.length()>5)
                            msg = line.substring(5);
                        else
                            msg = "";
                        switch (header) {
                            case "nrmsg":
                                receivedMsg(msg);
                                break;
                            case "close":
                                Client.disconnectFromServer();
                                return;
                            case "usrls":
                                Client.buildContactsList(msg);
                                Client.display.printUsers();
                                break;
                            case "chngn":
                                Client.display.print("Name already taken. Changed to " + msg);
                                Client.name = msg;
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Client.disconnectFromServer();
                } catch (NullPointerException e3) {
                    e3.printStackTrace();

                }

        }
    }
}

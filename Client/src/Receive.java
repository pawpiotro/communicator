/**
 * Created by Pawel on 23-Apr-16.
 */

import java.io.BufferedReader;
import java.io.IOException;

public class Receive implements Runnable{
    private BufferedReader stream;
    private Client client;
    public Receive(Client c){
        this.client = c;
        this.stream = c.input_stream;
    }

    private void receivedMsg(String msg){
        int id = Integer.parseInt(msg.substring(0,4));
        Contact tmp = client.findUser(id);
        String s = tmp.getName()+": "+msg.substring(4);
        if(id == Integer.parseInt(client.recipient_id)) {
            client.display.print(s);
            client.file_writer.write(s + "\n");
        } else {
            client.writeToFile(msg.substring(0,4), s);
        }
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
                                client.disconnectFromServer();
                                client.clearContactsList();
                                return;
                            case "usrls":
                                client.buildContactsList(msg);
                                break;
                            case "chngn":
                                client.display.print("Name already taken. Changed to " + msg);
                                client.name = msg;
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    client.disconnectFromServer();
                } catch (NullPointerException e3) {
                    e3.printStackTrace();

                }

        }
    }
}

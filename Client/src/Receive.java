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

    private void buildContactsList(String s){
        String[] contacts = s.split(";");
        boolean found;
        for(String tmp: contacts){
            found = false;
            String[] parts = tmp.split(":");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            for(Contact elem: Client.contacts_list)
                if(elem.getid() == id){
                    found = true;
                    break;
                }
            if(!found && (!name.equals(Client.name)))
                Client.contacts_list.add(new Contact(id,name));
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
                                Client.display.print(msg);
                                break;
                            case "close":
                                Client.display.print("Connection to server lost.");
                                Client.display.lock();
                                Client.disconnectFromServer();
                                return;
                            case "usrls":
                                buildContactsList(msg);
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
                } catch (NullPointerException e3) {
                    e3.printStackTrace();
                }

        }
    }
}

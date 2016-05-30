import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Wątek pojedynczego połączenia między klientami.
 */

public class Connection implements Runnable {

    private int client_ID;
    private Socket socket;
    private BufferedReader input_stream;
    private PrintWriter output_stream;
    public Connection(int id, Socket socket) throws IOException {
        this.client_ID = id;
        this.socket = socket;
        this.input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output_stream = new PrintWriter(socket.getOutputStream(),true);
    }
    private String receiveName() throws IOException{
        String s = input_stream.readLine();
        String header = s.substring(0,5);
        if(header.equals("login")) {
            return s.substring(5);
        }
        else
            return null;
    }
    private boolean nameAlreadyTaken(String client_name){
        for(User elem: Server.users_list)
            if(elem.getName().equals(client_name))
                return true;
        return false;
    }
    private synchronized void addUser(User usr){
        Server.users_list.add(usr);
    }
    private synchronized boolean readMsg(String client_name) throws IOException{
        String line;
        if((line = input_stream.readLine()) != null){
            String header = line.substring(0, 5);
            String msg;
            if (line.length() > 5)
                msg = line.substring(5);
            else
                msg = "elo";
            switch (header) {
                case "nrmsg":
                    System.out.println(client_name + ": " + msg);
                    break;
                case "close":
                    Server.disconnect(client_ID);
                    return false;
            }
        }
        return true;
    }
    @Override
    public void run() {
        String address = socket.getRemoteSocketAddress().toString();
        String client_name = "";
        try {
            if((client_name = receiveName()) == null){
                System.out.println("Connection from "+address+" failed");
                Server.connectionFailed(socket);
                return;
            }
            else{
                if(nameAlreadyTaken(client_name)) {
                    while (nameAlreadyTaken(client_name)) {
                        client_name = client_name + "_";
                    }
                    output_stream.println("chngn" + client_name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("New client connected from "+ address);
        User new_user = new User(client_ID, client_name, address, socket, output_stream);
        addUser(new_user);
        Server.display.printUsers();
        Server.distributeList();
        while(true) {
            try {
                if(!readMsg(client_name))
                    return;
                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

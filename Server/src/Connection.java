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
    private String client_name;
    private String address;
    private static Socket socket;
    private static BufferedReader input_stream;
    private static PrintWriter output_stream;
    public Connection(int id, Socket socket) throws IOException {
        this.client_ID = id;
        this.socket = socket;
        input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output_stream = new PrintWriter(socket.getOutputStream(),true);
        Server.outputs.add(output_stream);
        this.address = socket.getRemoteSocketAddress().toString();
    }
    private boolean receiveName() throws IOException{
        String s = input_stream.readLine();
        String header = s.substring(0,5);
        if(header.equals("login")) {
            this.client_name = s.substring(5);
            return true;
        }
        else
            return false;
    }
    private boolean nameAlreadyTaken(){
        for(User elem: Server.users_list)
            if(elem.getName().equals(client_name))
                return true;
        return false;
    }
    @Override
    public void run() {
        try {
            if(!receiveName()){
                System.out.println("Connection from "+address+" failed");
                Server.connectionFailed(socket);
                return;
            }
            else if(nameAlreadyTaken()){
                client_name = client_name+"_";
                output_stream.println("chngn"+client_name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("New client connected from "+ address);
        User new_user = new User(client_ID, client_name, address, socket);
        Server.users_list.add(new_user);
        Server.display.printUsers();
        Server.distributeList();
        while(true) {
            try {
                String line;
                while((line = input_stream.readLine()) != null) {
                    String header = line.substring(0, 5);
                    String msg;
                    if (line.length() > 5)
                        msg = line.substring(5);
                    else
                        msg = "";
                    switch (header) {
                        case "nrmsg":
                            System.out.println(client_name + ": " + msg);
                            break;
                        case "close":
                            Server.disconnect(client_ID);
                            return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

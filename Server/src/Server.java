/**
 * Klasa główna. Inicjalizacja zmiennych i struktur potrzebnych do obsługi klienta.
 * Nasłuchuje nowych połączeń od klientów i zestawia klientów. Tworzy wątki dla każdego połączenia.
 */

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {

    private final int PORT = 12412;
    //private final String HOST = "127.0.0.1";
    private ServerSocket server_socket;

    public List<User> users_list = new ArrayList<>();
    public ServerDisplay display;// = new ServerDisplay();
    private boolean serverInit(){
        try
        {
            this.server_socket = new ServerSocket(PORT);
            System.out.println("Server running");

        }
        catch(IOException e)
        {
            System.out.println("Server not initialized");
            return false;
        }
        return true;
    }

    public synchronized void distributeList(){
        String list = "usrls";
        for(User usr: users_list) {
            list = list+usr.getid()+":"+usr.getName()+";";
        }
        for(User elem: users_list){
            elem.send(list);
        }
    }
    public synchronized User findUser(int id){
        for(User tmp: users_list)
            if(tmp.getid() == id) {
                return tmp;
            }
        return null;
    }
    public static void connectionFailed(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void disconnect(int id){
        try {
            User usr = findUser(id);
            String name = usr.getName();
            if(name.equals(null))
                name = "Unknown user";
            String address = usr.getAddress();
            usr.closeSocket();
            users_list.remove(usr);
            System.out.println(name+" ("+address+") disconnected");
            display.printUsers();
            distributeList();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void displayInit(Server s){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                s.display = new ServerDisplay(s);
        }
        });
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.display = new ServerDisplay(server);
        //server.displayInit(server);
        boolean running = true;
        int client_seq = 0;
        if(server.serverInit())
            while(running) {
                try {
                    Socket tmp_socket = server.server_socket.accept();
                    Thread tmp = new Thread(new Connection(client_seq,tmp_socket,server));
                    tmp.start();
                    client_seq++;
                    server.display.printUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        else
            System.out.println("Server already started.");
    }

}

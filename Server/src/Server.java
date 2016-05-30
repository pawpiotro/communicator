/**
 * Klasa główna. Inicjalizacja zmiennych i struktur potrzebnych do obsługi klienta.
 * Nasłuchuje nowych połączeń od klientów i zestawia klientów. Tworzy wątki dla każdego połączenia.
 */

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.net.*;


public class Server {

    private static final int PORT = 12412;
    private static final String HOST = "127.0.0.1";
    private ServerSocket server_socket;

    public static List<User> users_list = new ArrayList<>();
    public static ServerDisplay display = new ServerDisplay();
    private void serverInit(){
        try
        {
            InetSocketAddress address = new InetSocketAddress(HOST, PORT);
            ServerSocket tmp_socket = new ServerSocket();
            tmp_socket.bind(address);
            this.server_socket = tmp_socket;
            System.out.println("Server running");

        }
        catch(IOException e)
        {
            System.out.println("Server not initialized");
            //exit(1);
        }

    }

    public synchronized static void distributeList(){
        String list = "usrls";
        for(User usr: users_list) {
            list = list+usr.getid()+":"+usr.getName()+";";
        }
        for(User elem: users_list){
            elem.output_stream.println(list);
        }
    }
    public synchronized static User findUser(int id){
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
    public synchronized static void disconnect(int id){
        try {
            User usr = findUser(id);
            String name = usr.getName();
            if(name.equals(null))
                name = "Unknown user";
            String address = usr.getAddress();
            usr.socket.close();
            users_list.remove(usr);
            System.out.println(name+" ("+address+") disconnected");
            display.printUsers();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        boolean running = true;
        int client_seq = 0;
        Server server = new Server();
        server.serverInit();
        display.setVisible(true);
        while(running) {
            try {
                Socket tmp_socket = server.server_socket.accept();
                Thread tmp = new Thread(new Connection(client_seq,tmp_socket));
                tmp.start();
                client_seq++;
                display.printUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

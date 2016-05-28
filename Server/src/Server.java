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
    public static List<Socket> client_sockets = new ArrayList<>();
    public static List<PrintWriter> outputs = new ArrayList<>();
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

    public static void distributeList(){
        String list = "usrls";
        for(User usr: users_list) {
            list = list+usr.getid()+":"+usr.getName()+";";
        }
        for(PrintWriter elem: outputs){
            elem.println(list);
        }
    }
    public static User findUser(int id){
        for(User tmp: users_list)
            if(tmp.getid() == id) {
                return tmp;
            }
        return null;
    }
    public static void connectionFailed(Socket socket){
        try {
            socket.close();
            client_sockets.remove(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void disconnect(int id){
        try {
            User usr = findUser(id);
            String name = usr.getName();
            if(name.equals(null))
                name = "Unknown user";
            String address = usr.getAddress();
            client_sockets.remove(usr.getSocket());
            usr.getSocket().close();
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
        /*File f = new File("./Server/src/data.txt");
        if (!f.isFile()) {
            try {
                PrintWriter writer = new PrintWriter("./Server/src/data.txt", "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e2) {
                e2.printStackTrace();
            }
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                for (String line; (line = br.readLine()) != null; ) {
                    String[] parts = line.split(";");
                    User new_user = new User(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    users_list.add(new_user);
                }
                // line is not visible here.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        while(running) {
            try {
                Socket tmp_socket =server.server_socket.accept();
                client_sockets.add(tmp_socket);
                Thread tmp = new Thread(new Connection(client_seq,tmp_socket));
                if(tmp != null)
                    tmp.start();
                client_seq++;
                display.printUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

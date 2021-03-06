import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Klasa główna. Inicjalizacja zmiennych i struktur potrzebnych do obsługi klienta.
 * Nasłuchuje nowych połączeń od klientów i tworzy dla nich wątki.
 */
public class Server {

    private final int PORT = 12412;

    private ServerSocket serverSocket;

    private List<User> usersList = new ArrayList<>();
    private ServerDisplay display;


    private boolean serverInit() {
        try {
            this.setServerSocket(new ServerSocket(PORT));
            System.out.println("Server running");

        } catch (IOException e) {
            System.out.println("Server not initialized");
            return false;
        }
        return true;
    }

    /**
     * Metoda rozsyła aktualną listę podłączony użytkowników do wszystkich klientów.
     */
    public synchronized void distributeList() {
        String list = "usrls";
        for (User usr : getUsersList()) {
            list = list + usr.getid() + ":" + usr.getName() + ";";
        }
        for (User elem : getUsersList()) {
            elem.send(list);
        }
    }

    /**
     * Metoda znajduje użytkownika zadanym ID
     *
     * @param id zadane ID
     * @return Obiekt klasy User o zadanym ID bądź null gdy nieznaleziony.
     */
    public synchronized User findUser(int id) {
        for (User tmp : getUsersList())
            if (tmp.getid() == id) {
                return tmp;
            }
        return null;
    }

    /**
     * Metoda zamyka socket w przypadku nieudanego połączenia.
     *
     * @param socket socket
     */
    public static void connectionFailed(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda wywoływana, gdy użytkownik o podanym ID zakończy połączenie.
     * Metoda wypisuje odpowiednie komunikaty, zamyka socket, usuwa użytkownika z listy,
     * aktualizuje listę i wywołuje metodę rozsyłającą ją do pozostałych użytkowników.
     *
     * @param id ID użytkownika, który zakończył połączeni
     */
    public synchronized void disconnect(int id) {
        try {
            User usr = findUser(id);
            String name = usr.getName();
            if (name.equals(null))
                name = "Unknown user";
            String address = usr.getAddress();
            usr.closeSocket();
            getUsersList().remove(usr);
            System.out.println(name + " (" + address + ") disconnected");
            getDisplay().printUsers();
            distributeList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayInit(Server s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                s.setDisplay(new ServerDisplay(s));
            }
        });
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.setDisplay(new ServerDisplay(server));
        boolean running = true;
        int client_seq = 0;
        if (server.serverInit())
            while (running) {
                try {
                    Socket tmp_socket = server.getServerSocket().accept();
                    Thread tmp = new Thread(new Connection(client_seq, tmp_socket, server));
                    tmp.start();
                    client_seq++;
                    server.getDisplay().printUsers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        else
            System.out.println("Server already started.");
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket server_socket) {
        this.serverSocket = server_socket;
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public ServerDisplay getDisplay() {
        return display;
    }

    public void setDisplay(ServerDisplay display) {
        this.display = display;
    }
}

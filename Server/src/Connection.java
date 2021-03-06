import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Wątek pojedynczego połączenia między klientami.
 */
public class Connection implements Runnable {
    private Server server;
    private int client_ID;
    private Socket socket;
    private BufferedReader input_stream;
    private PrintWriter output_stream;

    public Connection(int id, Socket socket, Server s) throws IOException {
        this.server = s;
        this.client_ID = id;
        this.socket = socket;
        this.input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output_stream = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Metoda odbiera login użytkownika upewniając się, że jest opisany właściwym nagłówkiem.
     *
     * @return login użytkownika
     * @throws IOException
     */
    private String receiveName() throws IOException {
        String s = input_stream.readLine();
        String header = s.substring(0, 5);
        if (header.equals("login")) {
            return s.substring(5);
        } else
            return null;
    }

    /**
     * Metoda sprawdza czy podany login nie jest już zajęty.
     *
     * @param client_name
     * @return prawda jeśli login już używany.
     */
    private boolean nameAlreadyTaken(String client_name) {
        for (User elem : server.getUsersList())
            if (elem.getName().equals(client_name))
                return true;
        return false;
    }

    private synchronized void addUser(User usr) {
        server.getUsersList().add(usr);
    }

    /**
     * Metoda przekazuje wiadomość do odbiorcy.
     *
     * @param msg Wiadomość - składa się z nagłówka zawierającego ID odbiorcy i wiadomości do przekazania
     */
    private void passMsg(String msg) {
        int id = Integer.parseInt(msg.substring(0, 4));
        User tmp = server.findUser(id);
        String s = String.format("%4s", Integer.toString(client_ID)).replace(' ', '0');
        tmp.send("nrmsg" + s + msg.substring(4));
    }

    /**
     * Odbiera wiadomośc i w zależności od nagłówka wykonuje odpowiednie czynności.
     * (wyświetla wiadomość lub zamyka połączenie).
     */
    private boolean readMsg() throws IOException {
        String line;
        if ((line = input_stream.readLine()) != null) {
            String header = line.substring(0, 5);
            String msg;
            if (line.length() > 5)
                msg = line.substring(5);
            else
                msg = "";
            switch (header) {
                case "nrmsg":
                    passMsg(msg);
                    break;
                case "close":
                    server.disconnect(client_ID);
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
            if ((client_name = receiveName()) == null) {
                System.out.println("Connection from " + address + " failed");
                Server.connectionFailed(socket);
                return;
            } else {
                if (nameAlreadyTaken(client_name)) {
                    while (nameAlreadyTaken(client_name)) {
                        client_name = client_name + "_";
                    }
                    output_stream.println("chngn" + client_name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            server.disconnect(client_ID);
        }
        System.out.println("New client connected from " + address);
        User new_user = new User(client_ID, client_name, address, socket, output_stream);
        addUser(new_user);
        server.getDisplay().printUsers();
        server.distributeList();
        while (true) {
            try {
                if (!readMsg())
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
                server.disconnect(client_ID);
            }
        }
    }
}

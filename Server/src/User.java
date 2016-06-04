import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Klasa użytkownika. Obiekt tej klasy zawiera wszystkie informacje o połączonym użytkowniku.
 */

public class User {
    private int id;
    private String name;
    private String address;
    private Socket socket;
    private PrintWriter output_stream;

    public User(int id, String name, String address, Socket socket, PrintWriter output) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.socket = socket;
        this.output_stream = output;
    }

    public int getid() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Socket getSocket() {
        return socket;
    }

    public void closeSocket() throws IOException {
        socket.close();
    }

    public void send(String s) {
        output_stream.println(s);
    }
}
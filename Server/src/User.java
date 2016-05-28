import java.net.Socket;

/**
 * Klasa użytkownika. Obiekt tej klasy zawiera wszystkie informacje o połączonym użytkowniku.
 */

public class User {
    private int id;
    private String name;
    private String address;
    private Socket socket;
    public User(int id, String name, String address, Socket socket){
        this.id = id;
        this.name = name;
        this.address = address;
        this.socket = socket;
    }

    public void print(){
        System.out.println(id+" "+name+" "+address);
    }

    public int getid(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public Socket getSocket(){ return socket;}
}


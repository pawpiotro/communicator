import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pawel on 23-Apr-16.
 */

public class Client {
    private int port = 12412; //default
    private String host = "127.0.0.1";  // default
    public  ClientDisplay display;// = new ClientDisplay();
    private Socket server_socket;
    public BufferedReader input_stream;
    public PrintWriter output_stream;
    public Thread receive;
    public List<Contact> contacts_list =  new ArrayList<>();
    public String name;
    public String recipient_id = null;
    /**
     * Ustanawia polaczenie z serwerem.
     * @throws IOException
     */
    private boolean makeConnection() throws IOException{
        boolean connected = false;
        while(!connected) {
            try {
                server_socket = new Socket(host, port);
                connected = true;
            } catch (ConnectException c) {
                if(!changeServer())
                    return false;
            }
        }
        input_stream = new BufferedReader(new InputStreamReader(server_socket.getInputStream()));
        output_stream = new PrintWriter(server_socket.getOutputStream(),true);
        return true;
    }
    public void disconnectFromServer(){
        try {
            display.print("Connection to server lost.");
            display.lock();
            server_socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    /**Zmienia adresata. Wybór z listy
     *
     * @param name
     */
    public void changeRecipient(String name){
        for(Contact tmp: contacts_list){
            if(name.equals(tmp.getName())){
                display.unlock();
                recipient_id = String.format("%4s", Integer.toString(tmp.getid())).replace(' ', '0');
                return;
            }
        }
    }
    /**
     * Zmiana adresu serwera, gdy domyslny nie odpowiada.
     */
    private boolean changeServer(){
        String address = "";
        boolean valid_address = false;
        while(!valid_address){
            address = JOptionPane.showInputDialog("Server unreachable. Give new ip:port");
            if(address!= null){
                if(!address.isEmpty())
                    valid_address = true;
            }
            else {
                return false;
            }
        }
        String[] parts = address.split(":");
        host = parts[0];
        port = Integer.parseInt(parts[1]);
        return true;
    }

    /**
     * Sprawdza czy string zawiera znaki specjalne (inne niz a-z lub 0-9)
     * Jezeli nie zostana znalezione zwraca true.
     * @param s
     * @return
     */
    private static boolean checkString(String s){
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        boolean b = m.find();
        return !b;
    }
    public synchronized Contact findUser(int id){
        for(Contact tmp: contacts_list)
            if(tmp.getid() == id) {
                return tmp;
            }
        return null;
    }

    private synchronized boolean recipientDisconnected(){
        try {
            for (Contact elem : contacts_list)
                if (recipient_id.equals(Integer.toString(elem.getid()))) {
                    return false;
                }
        } catch(NullPointerException e){
            return false;
        }
        return true;
    }
    public synchronized void buildContactsList(String s) {
        String[] contacts = s.split(";");
        contacts_list.clear();
        for (String tmp : contacts) {
            String[] parts = tmp.split(":");
            int id = Integer.parseInt(parts[0]);
            String tmp_name = parts[1];
            System.out.println(id+" "+tmp_name);
            if(!tmp_name.equals(name))
                contacts_list.add(new Contact(id, tmp_name));
            //if(recipientDisconnected())
              //  display.print("user discnnected");
            /*if(recipientDisconnected()){
                recipient_id = null;
                display.print("User disconnected");
                display.lock();
            }*/
            /*if (!found && (!name.equals(Client.name)))
                contacts_list.add(new Contact(id, name));*/
        }
        display.printUsers();
    }
    public synchronized void clearContactsList(){
        contacts_list.clear();
        display.printUsers();
    }
    private void displayInit(Client c){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                c.display = new ClientDisplay(c);
            }
        });
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.displayInit(client);
        client.name = "";
        boolean valid_name = false;
        String s = "Hello! Give nickname";
        while(!valid_name){
            client.name = JOptionPane.showInputDialog(s);
            if(client.name!= null){
                if(!client.name.isEmpty()) {
                    if(checkString(client.name))
                        valid_name = true;
                    else
                        s = "Give nickname (no special characters)";
                } else
                    s = "Give nickname (cannot be empty)";
            }
            else {
                return;
            }
        }
        client.display.setVisible(true);
        client.display.requestFocusOnInput();
        try {
            if(!client.makeConnection()) {
                client.display.dispose();
                return;
            }
            client.output_stream.println("login"+client.name);
            client.display.print("Witaj "+client.name+"!");
            client.receive = new Thread(new Receive(client));
            client.receive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}

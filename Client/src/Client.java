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
    private static int port = 12412; //default
    private static String host = "127.0.0.1";  // default
    public static ClientDisplay display = new ClientDisplay();
    private static Socket server_socket;
    public static BufferedReader input_stream;
    public static PrintWriter output_stream;
    public static Thread receive;
    public static List<Contact> contacts_list =  new ArrayList<>();
    public static String name;
    /**
     * Ustanawia polaczenie z serwerem.
     * @throws IOException
     */
    private static boolean makeConnection() throws IOException{
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
    public static void disconnectFromServer(){
        try {
            server_socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Zmiana adresu serwera, gdy domyslny nie odpowiada.
     */
    private static boolean changeServer(){
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
    private static boolean checkString(String s){
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        boolean b = m.find();
        return !b;
    }
    public static void main(String[] args) {
        name = "";
        boolean valid_name = false;
        String s = "Hello! Give nickname";
        while(!valid_name){
            name = JOptionPane.showInputDialog(s);
            if(name!= null){
                if(!name.isEmpty()) {
                    if(checkString(name))
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
        display.setVisible(true);
        display.requestFocusOnInput();
        try {
            if(!makeConnection()) {
                display.dispose();
                return;
            }
            output_stream.println("login"+name);
            display.print("Witaj "+name+"!");
            receive = new Thread(new Receive(input_stream));
            receive.start();
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

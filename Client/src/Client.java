import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Główna klasa.
 * Odpowiedzialna za połączenie z serwerem, wczytanie nazwy użytkownika i zweryfikowanie jej poprawności.
 */
public class Client {
    private int port = 12412; //default
    private String host = "127.0.0.1";  // default
    private ClientDisplay display;
    private Socket serverSocket;
    public BufferedReader inputStream;
    public PrintWriter outputStream;
    private Thread receive;

    private List<Contact> contactsList = new ArrayList<>();
    private String name;
    private String recipientID = "-1";

    private HashMap hashMap = new HashMap();

    public PrintWriter fileWriter;

    /**
     * Ustanawia połączenie z serwerem.
     *
     * @throws IOException
     */
    private boolean makeConnection() throws IOException {
        boolean connected = false;
        while (!connected) {
            try {
                serverSocket = new Socket(host, port);
                connected = true;
            } catch (ConnectException c) {
                if (!changeServer())
                    return false;
            }
        }
        inputStream = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        outputStream = new PrintWriter(serverSocket.getOutputStream(), true);
        return true;
    }

    /**
     * Zamknięcie połączenia.
     * Wypisanie komunikatu, zamknięcie socketa i zablokowanie pola do wpisywania wiadomości.
     */
    public void disconnectFromServer() {
        try {
            getDisplay().print("Connection to server lost.");
            getDisplay().lock();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zmienia adresata.
     *
     * @param s nazwa użytkownika wybranego z listy kontaktów
     */
    public void changeRecipient(String s) {
        closeFile();
        for (Contact tmp : getContactsList()) {
            if (s.equals(tmp.getName())) {
                getDisplay().unlock();
                setRecipientID(String.format("%4s", Integer.toString(tmp.getid())).replace(' ', '0'));
                openFile();
                if (getHashMap().containsKey(tmp.getName()))
                    getHashMap().remove(tmp.getName());
                return;
            }
        }
    }

    /**
     * Otwiera plik, do którego zapisywana jest historia rozmowy z aktualnie wybranym rozmówcą.
     * Jeśli plik nie jest pusty, wypisuje zawartą już w nim historię rozmowy.
     * Ustawia fileWriter na właściwy plik. Jest on wykorzystywany w klasie Receive do zapisu
     * przychodzacych wiadomości
     */
    private void openFile() {
        String path = "./Client/history/" + getName() + "/" + getRecipientID() + ".txt";
        File f = new File(path);
        f.getParentFile().mkdirs();
        try {
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            getDisplay().clean();
            for (String line; (line = br.readLine()) != null; ) {
                getDisplay().print(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zamyka aktualnie otwarty plik zapisu
     */
    public void closeFile() {
        String path = "./Client/history/" + getName() + "/" + getRecipientID() + ".txt";
        File f = new File(path);
        if (f.isFile())
            fileWriter.close();
    }

    /**
     * Jeśli odebrana przez receiveMsg() wiadomość nie została wysłana
     * przez aktualnie wybranego rozmówcę, jest ona zapisywana do pliku.
     *
     * @param id ID nadawcy wiadomości
     * @param s  Wiadomość
     */
    public void writeToFile(String id, String s) {
        String path = "./Client/history/" + getName() + "/" + id + ".txt";
        File f = new File(path);
        f.getParentFile().mkdirs();
        try {
            PrintWriter tmp_fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(path, true)));
            tmp_fileWriter.write(s + "\n");
            tmp_fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * W tej wersji programu po zamknięciu usuwane są pliki z historią rozmowy,
     * ponieważ ID użytkowników są nadawane dynamicznie i użytkownik po ponownym zalogowaniu
     * nie zobaczy swojej historii rozmowy. Funkcja zostanie rozwinięta w kolejnych wersjach programu.
     *
     * @param f ścieżka folderu do usunięcia
     * @throws IOException
     */
    void deleteDirectory(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteDirectory(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    /**
     * Zmiana adresu serwera, gdy domyślny serwer nie odpowiada.
     */
    private boolean changeServer() {
        String address = "";
        boolean valid_address = false;
        String[] parts;
        String s = "Server unreachable. Give new ip:port";
        while (!valid_address) {
            address = JOptionPane.showInputDialog(s);
            if (address != null) {
                if (!address.isEmpty())
                    if (address.contains(":")) {
                        try {
                            parts = address.split(":");
                            host = parts[0];
                            port = Integer.parseInt(parts[1]);
                            if (checkAddress(host))
                                valid_address = true;
                            else
                                s = "Invalid address format";
                        } catch (ArrayIndexOutOfBoundsException e) {
                            s = "Invalid input value";
                        } catch (NumberFormatException e2) {
                            s = "Invalid port value";
                        }
                    } else {
                        s = "Invalid address format.";
                    }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Sprawdza poprawnośc wpisanego adresu.
     * IP Address Regular Expression Pattern by mkyong
     * http://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
     *
     * @param ip podany adres IP do sprawdzenia
     * @return Prawda gdy adres poprawny.
     */
    private boolean checkAddress(String ip) {
        Pattern PATTERN = Pattern.compile(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        return PATTERN.matcher(ip).matches();
    }

    /**
     * Sprawdza czy string zawiera znaki specjalne (inne niz a-z lub 0-9)
     * Jezeli nie zostana znalezione zwraca true.
     *
     * @param s String do sprawdzenia
     */
    private static boolean checkString(String s) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        boolean b = m.find();
        return !b;
    }

    /**
     * Wyszukuje użytkownika z listy kontatków po ID
     *
     * @param id zadane ID
     * @return Użytkownik (obiekt klasy Contact) o zadanym ID lub null, gdy nieznaleziony.
     */
    public synchronized Contact findUser(int id) {
        for (Contact tmp : getContactsList())
            if (tmp.getid() == id) {
                return tmp;
            }
        return null;
    }

    /**
     * Tworzy listę użytkowników z otrzymanego stringa
     *
     * @param s lista użytkowników w postaci stringa
     */
    public synchronized void buildContactsList(String s) {
        String[] contacts = s.split(";");
        getContactsList().clear();
        for (String tmp : contacts) {
            String[] parts = tmp.split(":");
            int id = Integer.parseInt(parts[0]);
            String tmp_name = parts[1];
            if (!tmp_name.equals(getName()))
                getContactsList().add(new Contact(id, tmp_name));
        }
        getDisplay().printUsers();
    }

    /**
     * Czyści listę kontaków
     */
    public synchronized void clearContactsList() {
        getContactsList().clear();
        getDisplay().printUsers();
    }

    /**
     * Inicjalizuje okno programu (ramke) dla danego klienta
     *
     * @param c Klient
     */
    private void displayInit(Client c) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                c.setDisplay(new ClientDisplay(c));
            }
        });
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.displayInit(client);
        client.setName("");
        boolean valid_name = false;
        String s = "Hello! Give nickname";
        while (!valid_name) {
            client.setName(JOptionPane.showInputDialog(s));
            if (client.getName() != null) {
                if (!client.getName().isEmpty()) {
                    if (checkString(client.getName()))
                        valid_name = true;
                    else
                        s = "Give nickname (no special characters)";
                } else
                    s = "Give nickname (cannot be empty)";
            } else {
                return;
            }
        }
        client.getDisplay().setVisible(true);
        client.getDisplay().requestFocusOnInput();
        client.getDisplay().changeTitle(client.getName());
        try {
            if (!client.makeConnection()) {
                client.getDisplay().dispose();
                return;
            }
            client.outputStream.println("login" + client.getName());
            client.getDisplay().print("Witaj " + client.getName() + "!");
            client.setReceive(new Thread(new Receive(client)));
            client.getReceive().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientDisplay getDisplay() {
        return display;
    }

    public void setDisplay(ClientDisplay display) {
        this.display = display;
    }

    public Thread getReceive() {
        return receive;
    }

    public void setReceive(Thread receive) {
        this.receive = receive;
    }

    public List<Contact> getContactsList() {
        return contactsList;
    }

    public void setContactsList(List<Contact> contactsList) {
        this.contactsList = contactsList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public HashMap getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap hashMap) {
        this.hashMap = hashMap;
    }
}

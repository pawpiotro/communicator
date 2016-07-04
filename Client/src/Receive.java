import java.io.BufferedReader;
import java.io.IOException;

/**
 * Wątek nasłuchujący. Odbiera wiadomości wysłane z serwera.
 * Mogą to być zwykłe wiadomości do wyświetlenia bądź specjalne komunikaty
 * - zamknięcie połączenia
 * - aktualizacja listy podłączony użytkowników
 * - komunikat o zmianie nazwy,gdy podana jest już zajęta
 */
public class Receive implements Runnable {
    private BufferedReader stream;
    private Client client;

    public Receive(Client c) {
        this.client = c;
        this.stream = c.inputStream;
    }

    /**
     * Odebranie zwykłej wiadomości. Metoda sprawdza od kogo odebrano wiadomość
     * i wypisuje ją gdy nadawca jest aktualnie wybranym rozmówcą, a w przeciwnym wypadku
     * wywołuje funkcję writeToFile zapisującą wiadomość do pliku.
     * Użytkownik jest poinformowany o nowej wiadomości zmianą koloru nadawcy na liście.
     * @link Client#writeToFile writeToFile
     * @param msg odebrana wiadomość
     */
    private void receivedMsg(String msg) {
        int id = Integer.parseInt(msg.substring(0, 4));
        Contact tmp = client.findUser(id);
        String s = tmp.getName() + ": " + msg.substring(4);
        if (id == Integer.parseInt(client.getRecipientID())) {
            client.getDisplay().print(s);
            client.fileWriter.write(s + "\n");
        } else {
            client.writeToFile(msg.substring(0, 4), s);
            client.getHashMap().put(tmp.getName(), "newmsg");
            client.getDisplay().printUsers();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String line;
                while ((line = stream.readLine()) != null) {
                    String header = line.substring(0, 5);
                    String msg;
                    if (line.length() > 5)
                        msg = line.substring(5);
                    else
                        msg = "";
                    switch (header) {
                        case "nrmsg":
                            receivedMsg(msg);
                            break;
                        case "close":
                            client.disconnectFromServer();
                            client.clearContactsList();
                            return;
                        case "usrls":
                            client.buildContactsList(msg);
                            break;
                        case "chngn":
                            client.getDisplay().print("Name already taken. Changed to " + msg);
                            client.setName(msg);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                client.disconnectFromServer();
            } catch (NullPointerException e3) {
                e3.printStackTrace();

            }

        }
    }
}

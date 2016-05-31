/**
 * Widok. Wyswietla liste polaczonych klientow.
 * Wypisuje wszystkie komunikaty i bledy.
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class ServerDisplay extends JFrame{

    private JTextArea area2 = new JTextArea();
    private JTextArea area = new JTextArea();
    private JScrollPane pane = new JScrollPane(area);
    private JPanel panel = new JPanel();
    private JScrollPane pane2 = new JScrollPane(area2);
    private PrintStream standard_out = System.out; //stary output

    private void setCustomOutput(JTextArea area){
        PrintStream print_stream = new PrintStream(new CustomOutputStream(area));
        //PrintStream standard_out = System.out; //stary output

        //podmiana standardowego strumienia
        System.setOut(print_stream);
        System.setErr(print_stream);
    }
    public ServerDisplay(){
        Color my = new Color(10,225,225);
        //PANEL
        panel.setLayout(new GridLayout(1,2));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(my);
        panel.add(pane);
        panel.add(pane2);
        //PANE2
        pane2.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        pane2.setPreferredSize(new Dimension(160,300));
        pane2.setBackground(my);
        //AREA
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
        area.setEditable(false);
        DefaultCaret caret = (DefaultCaret) area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        //PANE
        pane.setPreferredSize(new Dimension(200, 300));
        //AREA2
        area2.setLineWrap(true);
        area2.setWrapStyleWord(true);
        area2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        area2.setEditable(false);
        //PANEL ADD

        setCustomOutput(area);
        init();
    }

    private void init() {
        setTitle("server");
        setSize(520, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(panel);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public synchronized void windowClosing(WindowEvent e)
            {
                for(User elem: Server.users_list)
                    elem.output_stream.println("close");
                System.exit(0);
            }
        });
    }

    /**
     * wyswietla wiadomosc w oknie programu
     * @param message wiadomosc do wyswietlenia
     */
    public void print(String message){
        area.append(message+"\n");
        //area.setCaretPosition(area.getDocument().getLength());
    }

    public synchronized void printUsers(){
        area2.setText("");
        area2.append("ID    Name        Address\n");
        for(User elem : Server.users_list) {
            area2.append(elem.getid() + "       " + elem.getName()+"            "+elem.getAddress()+"\n");
        }
    }

    /*public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            public void run() {
                ServerDisplay  frame = new ServerDisplay();
                //System.out.println("elo");
                frame.setVisible(true);
                for(int i = 0;i < 100; i++)
                    print("elo");
            }
        });
    }*/
}

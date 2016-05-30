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

    private static JTextArea area2 = new JTextArea();
    private static JTextArea area = new JTextArea();
    //private static PrintStream OutArea2 = new PrintStream(new CustomOutputStream(area2));
    //private static PrintStream OutArea1 = new PrintStream(new CustomOutputStream(area));
    JPanel panel = new JPanel();
    JScrollPane pane2 = new JScrollPane(area2);
    PrintStream standard_out = System.out; //stary output

    public ServerDisplay(){
        Color my = new Color(10,225,225);

        panel.setLayout(new GridLayout(1,2));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane pane = new JScrollPane(area);
        pane2.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        pane2.setBackground(my);

        DefaultCaret caret = (DefaultCaret) area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //area.setPreferredSize(new Dimension(200,300));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
        area.setEditable(false);

        pane.setPreferredSize(new Dimension(200, 300));

        PrintStream print_stream = new PrintStream(new CustomOutputStream(area));
        //PrintStream standard_out = System.out; //stary output

        //podmiana standardowego strumienia
        System.setOut(print_stream);
        //System.setErr(print_stream);

        pane2.setPreferredSize(new Dimension(160,300));

        area2.setLineWrap(true);
        area2.setWrapStyleWord(true);
        area2.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        area2.setEditable(false);

        panel.add(pane);
        panel.add(pane2);

        panel.setBackground(my);

        add(panel);
        initUI();
    }

    private void initUI() {
        setTitle("server");
        setSize(520, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

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
    public static void print(String message){
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

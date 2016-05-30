/**
 * Widok. Okno programu
 */

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class ClientDisplay extends JFrame{

    private static JTextArea area = new JTextArea();
    private static JList list = new JList();
    private static JTextArea input = new JTextArea();

    private class sendAction implements ActionListener, KeyListener {
        public void actionPerformed(ActionEvent e){
            String message = input.getText();
            if(!message.isEmpty()) {
                input.setText("");
                Client.output_stream.println("nrmsg"+message);
                print(message);
            }
        }
        public void keyPressed(KeyEvent k){
            if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                String message = input.getText();
                if(!message.isEmpty()) {
                    Client.output_stream.println("nrmsg"+message);
                    print(Client.name+": "+message);
                }
            }
        }
        public void keyReleased(KeyEvent k){
            if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                input.setText("");
            }
        }
        public void keyTyped(KeyEvent k){}
    }

    public static void requestFocusOnInput(){
        input.requestFocusInWindow();
    }
    public ClientDisplay(){
        Color my = new Color(50,150,225);

        JPanel main_panel = new JPanel();
        main_panel.setBackground(my);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(260,340));
        JPanel panel2 =  new JPanel();
        panel2.setPreferredSize(new Dimension(160,340));

        main_panel.add(panel);
        main_panel.add(panel2);

        panel.setLayout(new FlowLayout());

        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(260, 260));
        pane.setBackground(my);

        DefaultCaret caret = (DefaultCaret) area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
        area.setEditable(false);

        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
        input.setEditable(true);
        input.setPreferredSize(new Dimension(180, 36));


        JButton send = new JButton("Send");
        send.setPreferredSize(new Dimension(70,36));
        sendAction send_message = new sendAction();
        send.addActionListener(send_message);
        input.addKeyListener(send_message);

        panel.add(pane);
        panel.add(input);
        panel.add(send);


        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        list.setVisibleRowCount(-1);

        JScrollPane pane2 = new JScrollPane(list);
        pane2.setBackground(my);
        pane2.setPreferredSize(new Dimension(160,300));
        panel2.add(pane2);


        panel.setBackground(my);
        panel2.setBackground(my);

        add(main_panel);
        initUI();
    }

    private void initUI() {
        setTitle("client");
        setSize(new Dimension(460, 350));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                Client.output_stream.println("close");
                Client.receive.interrupt();
                e.getWindow().dispose();
                System.exit(0);
            }
        });
    }

    /**
     * wyswietla wiadomosc w oknie programu
     * @param message wiadomosc do wyswietlenia
     */
    static void print(String message){
        area.append(message+"\n");
    }
    static void lock(){
        input.setEditable(false);
    }
    public void printUsers(){
        String[] contacts = new String[Client.contacts_list.size()];
        for(int i = 0; i < Client.contacts_list.size(); i++) {
            contacts[i] = Client.contacts_list.get(i).getName();
        }
        list.setListData(contacts);
    }
}

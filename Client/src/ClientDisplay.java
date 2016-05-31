/**
 * Widok. Okno programu
 */

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class ClientDisplay extends JFrame{

    private JTextArea area = new JTextArea();
    private JList list = new JList();
    private JTextArea input = new JTextArea();
    private JPanel main_panel = new JPanel();
    private JPanel panel = new JPanel();
    private JPanel panel2 =  new JPanel();
    private JScrollPane pane = new JScrollPane(area);
    private JScrollPane pane2 = new JScrollPane(list);
    private JButton send = new JButton("Send");
    private sendAction send_message = new sendAction();

    private class sendAction implements ActionListener, KeyListener {
        @Override
        public void actionPerformed(ActionEvent e){
            String message = input.getText();
            if(!message.isEmpty()) {
                input.setText("");
                Client.output_stream.println("nrmsg"+message);
                print(message);
            }
        }
        @Override
        public void keyPressed(KeyEvent k){
            if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                String message = input.getText();
                if(!message.isEmpty()) {
                    Client.output_stream.println("nrmsg"+message);
                    print(Client.name+": "+message);
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent k){
            if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                input.setText("");
            }
        }
        @Override
        public void keyTyped(KeyEvent k){}
    }

    public void requestFocusOnInput(){
        input.requestFocusInWindow();
    }
    public ClientDisplay(){
        Color my = new Color(50,150,225);
        //MAIN PANEL
        main_panel.setBackground(my);
        main_panel.add(panel);
        main_panel.add(panel2);
        //PANEL
        panel.setPreferredSize(new Dimension(260,340));
        panel.setLayout(new FlowLayout());
        panel.setBackground(my);
        panel.add(pane);
        panel.add(input);
        panel.add(send);
        //PANEL2
        panel2.setPreferredSize(new Dimension(160,340));
        panel2.add(pane2);
        panel2.setBackground(my);
        //PANE
        pane.setPreferredSize(new Dimension(260, 260));
        pane.setBackground(my);
        //PANE2
        pane2.setBackground(my);
        pane2.setPreferredSize(new Dimension(160,300));
        //AREA
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
        area.setEditable(false);
        DefaultCaret caret = (DefaultCaret) area.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        //INPUT
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 2));
        input.setEditable(true);
        input.setPreferredSize(new Dimension(180, 36));
        input.addKeyListener(send_message);
        //SEND BUTTON
        send.setPreferredSize(new Dimension(70,36));
        send.addActionListener(send_message);
        //LIST
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        list.setVisibleRowCount(-1);

        initUI();
    }

    private void initUI() {
        setTitle("client");
        setSize(new Dimension(460, 350));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        add(main_panel);
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
    public void print(String message){
        area.append(message+"\n");
    }
    public void lock(){
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

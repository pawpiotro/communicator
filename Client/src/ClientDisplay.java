/**
 * Widok. Okno programu
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ClientDisplay extends JFrame {
    private Client client;
    private JTextArea area = new JTextArea();
    private JList list = new JList();
    private JTextArea input = new JTextArea();
    private JPanel main_panel = new JPanel();
    private JPanel panel = new JPanel();
    private JPanel panel2 = new JPanel();
    private JScrollPane pane = new JScrollPane(area);
    private JScrollPane pane2 = new JScrollPane(list);
    private JButton send = new JButton("Send");
    private SendAction send_message = new SendAction();
    private SelectContact select_contact = new SelectContact();

    private class SendAction implements ActionListener, KeyListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = input.getText();
            if (!message.isEmpty()) {
                input.setText("");
                client.output_stream.println("nrmsg" + client.recipient_id + message);
                print(client.name + ": " + message);
                client.file_writer.write(client.name + ": " + message + "\n");
            }
        }

        @Override
        public void keyPressed(KeyEvent k) {
            if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                String message = input.getText();
                if (!message.isEmpty()) {
                    client.output_stream.println("nrmsg" + client.recipient_id + message);
                    print(client.name + ": " + message);
                    client.file_writer.write(client.name + ": " + message + "\n");
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent k) {
            if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                input.setText("");
            }
        }

        @Override
        public void keyTyped(KeyEvent k) {
        }
    }

    private class SelectContact implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            lock();
            if (!list.isSelectionEmpty())
                client.changeRecipient(list.getSelectedValue().toString());
        }
    }

    private class MyListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list,
                                                      Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);
            if (client.hashMap.containsKey(value)) {
                setForeground(Color.blue);
            } else {
                setForeground(Color.black);
            }
            return (this);
        }
    }

    public void requestFocusOnInput() {
        input.requestFocusInWindow();
    }

    public ClientDisplay(Client c) {
        client = c;
        Color my = new Color(50, 150, 225);
        //MAIN PANEL
        main_panel.setBackground(my);
        main_panel.add(panel);
        main_panel.add(panel2);
        //PANEL
        panel.setPreferredSize(new Dimension(260, 340));
        panel.setLayout(new FlowLayout());
        panel.setBackground(my);
        panel.add(pane);
        panel.add(input);
        panel.add(send);
        //PANEL2
        panel2.setPreferredSize(new Dimension(160, 340));
        panel2.add(pane2);
        panel2.setBackground(my);
        //PANE
        pane.setPreferredSize(new Dimension(260, 260));
        pane.setBackground(my);
        //PANE2
        pane2.setBackground(my);
        pane2.setPreferredSize(new Dimension(160, 300));
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
        input.setEditable(false);
        input.setPreferredSize(new Dimension(180, 36));
        input.addKeyListener(send_message);
        //SEND BUTTON
        send.setPreferredSize(new Dimension(70, 36));
        send.addActionListener(send_message);
        //LIST
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL_WRAP);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener(select_contact);
        list.setFixedCellWidth(156);
        list.setCellRenderer(new MyListRenderer());
        initUI();
    }

    private void initUI() {
        setTitle("client");
        setSize(new Dimension(460, 350));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        add(main_panel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.output_stream.println("close");
                client.closeFile();
                client.receive.interrupt();
                e.getWindow().dispose();
                try {
                    File f = new File("./Client/history/" + client.name);
                    if (f.exists())
                        client.deleteDirectory(f);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    public void changeTitle(String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("client - " + s);
            }
        });
    }

    /**
     * wyswietla wiadomosc w oknie programu
     *
     * @param message wiadomosc do wyswietlenia
     */
    public void print(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                area.append(message + "\n");
            }
        });
    }

    public void clean() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                area.setText("");
            }
        });
    }

    public void lock() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                input.setEditable(false);
            }
        });
    }

    public void unlock() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                input.setEditable(true);
            }
        });
    }

    public void printUsers() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String[] contacts = new String[client.contacts_list.size()];
                for (int i = 0; i < client.contacts_list.size(); i++) {
                    contacts[i] = client.contacts_list.get(i).getName();
                }
                list.setListData(contacts);
            }
        });
    }
}

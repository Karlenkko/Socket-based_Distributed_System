package multicastClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * UDP multicast client GUI version main program
 */
public class MulticastEchoClientIHM extends JFrame implements MulticastSubscriber{
    private TextArea allMsgsArea = new TextArea();
    private TextField groupIPField = new TextField();
    private TextField groupPortField = new TextField();
    private TextField msgField = new TextField();
    private TextField nicknameField = new TextField();

    private Button join = new Button("join chat");
//    private Button leave = new Button("leave");
    private Button send = new Button("send");

    private JPanel upper = new JPanel();
    private JPanel middle = new JPanel();
    private JPanel lower = new JPanel();
    private ClientListenThread clientListenThread;
    private MulticastSocket mcSocket;

    private String currentInetAddress;
    private int currentPort;
    private boolean firstConnection = false;

    /**
     *
     * multicast and broadcast use the particular address
     * 224.0.0.0 to 239.255.255.255.
     * here we set a default address to 225.0.0.1, and a default port to 8888,
     * which can be modified in the GUI
     */
    public MulticastEchoClientIHM(){
        setTitle("multicast Chat Client");
        setSize(640,480);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //components
        groupIPField.setText("225.0.0.1");
        groupPortField.setText("8888");
        nicknameField.setText("Somebody");
        nicknameField.setPreferredSize(new Dimension(120, 24));
        send.setPreferredSize(new Dimension(80, 24));

        join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                join();
            }
        });

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send();
            }
        });
        send.setEnabled(false);
        // page payout
        upper.setLayout(new GridLayout(1, 6, 5, 5));
        upper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        upper.add(new JLabel("Group IP:"));
        upper.add(groupIPField);
        upper.add(new JLabel("Group port:"));
        upper.add(groupPortField);
        upper.add(join);
        this.add(upper, BorderLayout.NORTH);

        middle.setLayout(new BorderLayout());
        middle.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        middle.add(allMsgsArea);
        this.add(middle, BorderLayout.CENTER);

        lower.setLayout(new BorderLayout());
        lower.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        lower.add(nicknameField, BorderLayout.WEST);
        lower.add(msgField, BorderLayout.CENTER);
        lower.add(send, BorderLayout.EAST);
        this.add(lower, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    /**
     * joins a new chat based on the address and port in the GUI.
     * this method also clears the chat history in the previous channel
     */
    private synchronized void join() {
        try{
            if (firstConnection) {
                String quitText = nicknameField.getText() + " leaves the chat.";
                DatagramPacket msg = new DatagramPacket(quitText.getBytes(),quitText.length(), InetAddress.getByName(currentInetAddress), currentPort);
                mcSocket.send(msg);
            }
            allMsgsArea.setText("");
            // connection
            InetAddress groupAddr = InetAddress.getByName(groupIPField.getText());
            int groupPort = Integer.valueOf(groupPortField.getText());

            mcSocket = new MulticastSocket(groupPort);
            mcSocket.joinGroup(groupAddr);
            currentInetAddress = groupIPField.getText();
            currentPort = groupPort;
            clientListenThread = new ClientListenThread(groupAddr.toString() + ":" + groupPort, mcSocket, this);
            clientListenThread.start();

            String connectedInfo = nicknameField.getText() + " joins the chat of " + groupIPField.getText() + " : " + currentPort;
            DatagramPacket enteringMsg = new DatagramPacket(connectedInfo.getBytes(), connectedInfo.length(), groupAddr, groupPort);
            mcSocket.send(enteringMsg);

            firstConnection = true;
            send.setEnabled(true);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + groupIPField.getText());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:"+ groupIPField.getText());
            System.exit(1);
        }
    }

    /**
     * send a message to the current channel using the nickname, which can be
     * changed at any time.
     */
    private synchronized void send() {
        String msg = nicknameField.getText() + " says: " + msgField.getText();
        try {
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(currentInetAddress), currentPort);
            mcSocket.send(packet);
            msgField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * concrete method that writes the message into the GUI when receiving a message,
     * invoked by the Client Listen Thread
     * @param clientListenThread the Client Listen Thread which actually listens to incoming messages
     * @param msg the latest received message
     */
    @Override
    public void onReceiveMessage(ClientListenThread clientListenThread, String msg) {
        allMsgsArea.append(msg + '\n');
    }

    /**
     * the main program that launches the GUI
     * @param args arguments, currently not used here
     */
    public static void main(String[] args) {
        new MulticastEchoClientIHM();
    }

}

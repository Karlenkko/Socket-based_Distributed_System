package multicastClient;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class ClientListenThread extends Thread{
    private static MulticastSocket mcSocket;
    private MulticastSubscriber multicastSubscriber;
    private boolean consoleMode = true;

    /**
     * Constructor of the Client Listen Thread for multicast echo client console version,
     * which do not require a subscriber when receiving a message
     * @param name name of the tread, composed of its address and port
     * @param socket the multicast socket
     */
    ClientListenThread(String name, MulticastSocket socket){
        super(name);
        this.mcSocket = socket;
    }

    /**
     * Constructor of the Client Listen Thread for multicast echo client GUI version,
     * since the message display can no longer be done by a System.out.print(),
     * I choose to use the observer/subscriber design pattern to receive incoming messages
     * @param name name of the tread, composed of its address and port
     * @param socket the multicast socket
     * @param multicastSubscriber the subscriber that will be informed when receiving a message
     */
    ClientListenThread(String name, MulticastSocket socket, MulticastSubscriber multicastSubscriber){
        super(name);
        this.mcSocket = socket;
        this.multicastSubscriber = multicastSubscriber;
        consoleMode = false;
    }

    /**
     * overrides the default program of run that concretely receive messages and inform the subscriber
     */
    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                mcSocket.receive(packet);
                String msg = new String(buffer, 0, packet.getLength());
                System.out.println(msg);
                leaveTrace(msg);
                if (!consoleMode) {
                    multicastSubscriber.onReceiveMessage(this, msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in Multicast Client:" + e);
        }
    }

    private static void leaveTrace(String msg) {
        try{
            FileWriter writer = new FileWriter("UDPTrace.txt",true);
            writer.write(msg + "\n");
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

}

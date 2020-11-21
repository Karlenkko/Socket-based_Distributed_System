package multicastClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class ClientListenThread extends Thread{
    private static MulticastSocket mcSocket;
    private MulticastSubscriber multicastSubscriber;
    private boolean consoleMode = true;

    ClientListenThread(String name, MulticastSocket socket){
        super(name);
        this.mcSocket = socket;
    }

    ClientListenThread(String name, MulticastSocket socket, MulticastSubscriber multicastSubscriber){
        super(name);
        this.mcSocket = socket;
        this.multicastSubscriber = multicastSubscriber;
        consoleMode = false;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                mcSocket.receive(packet);
                String msg = new String(buffer, 0, packet.getLength());
                System.out.println(msg);
                if (!consoleMode) {
                    multicastSubscriber.onReceiveMessage(this, msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in Multicast Client:" + e);
        }
    }

}

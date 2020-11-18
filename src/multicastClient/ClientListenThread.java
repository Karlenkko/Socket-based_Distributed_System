package multicastClient;

import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class ClientListenThread extends Thread{
    private MulticastSocket mcSocket;

    ClientListenThread(String name, MulticastSocket socket){
        super(name);
        this.mcSocket = socket;
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
            }
        } catch (Exception e) {
            System.err.println("Error in Multicast Client:" + e);
        }
    }
}

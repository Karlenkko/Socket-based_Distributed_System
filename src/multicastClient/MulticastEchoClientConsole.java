package multicastClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import static java.lang.System.exit;

/**
 * UDP multicast client console version main program
 */
public class MulticastEchoClientConsole {

    /**
     * the main program of the console version multicast echo client,
     * multicast and broadcast use the particular address
     * 224.0.0.0 to 239.255.255.255.
     * here we set a default address to 225.0.0.1, and a default port to 8888,
     * which can be changed by passing parameters in args[]
     * @param args arguments
     * @throws IOException exception when handling input/output stream
     */
    public static void main(String[] args) throws IOException{
        MulticastSocket mcSocket = null;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String address = "225.0.0.1";
        int port = 8888;
        if (args.length > 0 && args.length == 2) {
//            System.out.println("Usage: java MulticastEchoClientConsole <Address> <Port>");
//            System.exit(1);
            address = args[0];
            port = Integer.parseInt(args[1]);
        }
        InetAddress groupAddr = InetAddress.getByName(address);
        int groupPort = port;

        System.out.print( "Please enter your name : " );
        String clientName = stdIn.readLine();
        try {
            mcSocket = new MulticastSocket(groupPort);
            mcSocket.joinGroup(groupAddr);
            ClientListenThread clientListenThread = new ClientListenThread(groupAddr.toString() + ":" + groupPort,mcSocket);
            clientListenThread.start();

            String connectedInfo = clientName + " joins the chat";
            DatagramPacket enteringMsg = new DatagramPacket(connectedInfo.getBytes(), connectedInfo.length(), groupAddr, groupPort);
            mcSocket.send(enteringMsg);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + address);
            exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:"+ address);
            exit(1);
        }

        String line;
        while (true) {
            line=stdIn.readLine();
            if (line.equals(".")) {
                String quitText = clientName + " leaves the chat";
                DatagramPacket msg = new DatagramPacket(quitText.getBytes(),quitText.length(),groupAddr,groupPort);
                mcSocket.send(msg);
                mcSocket.leaveGroup(groupAddr);
                stdIn.close();
                exit(0);
            }
            String stringMessage = clientName + " : " + line;
            DatagramPacket msg = new DatagramPacket(stringMessage.getBytes(),stringMessage.length(),groupAddr,groupPort);
            mcSocket.send(msg);

        }

    }


}

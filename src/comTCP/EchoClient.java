package comTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class EchoClient {

 
    /**
     *  main method
     *  accepts a connection, receives a message from client then sends an echo to the client
     **/
    public static void main(String[] args) throws IOException {
        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;

//        if (args.length != 2) {
//            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
//            System.exit(1);
//        }
        String host = "localhost";
        int port = 3100;
        try {
//            creation socket ==> connection
//      	    echoSocket = new Socket(args[0],new Integer(args[1]).intValue());
            echoSocket = new Socket(host, port);

      	    socIn = new BufferedReader(
	    		          new InputStreamReader(echoSocket.getInputStream()));    
	        socOut= new PrintStream(echoSocket.getOutputStream());

	        stdIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(echoSocket.getLocalPort());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
                             
        String line;
        while (true)
        {
            if(stdIn.ready()) {
                line=stdIn.readLine();
                if (line.equals(".")) break;

                socOut.println(line);
            }

            if(socIn.ready()) {
                line = socIn.readLine();
                System.out.println("echo: " + line);

            }

        }
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }
}



package comTCP;

import Util.HistoryHandler;

import java.net.ServerSocket;
import java.net.Socket;

public class EchoServerMultiThreaded  {
  
 	/**
  	* main method, default port set to 3100
  	**/
   public static void main(String[] args){
        ServerSocket listenSocket;
	   	int port = 3100;

  		if (args.length > 0 && args.length == 1) {
//          	System.out.println("Usage: java EchoServer <EchoServer port>");
//          	System.exit(1);
			port = Integer.parseInt(args[0]);
  		}


		try {
//			listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
			listenSocket = new ServerSocket(port);
			System.out.println("Server ready...");
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connection from:" + clientSocket.getInetAddress() + "port : " + clientSocket.getLocalPort());
				ClientThread ct = new ClientThread(clientSocket);
				ct.start();
			}
		} catch (Exception e) {
				System.err.println("Error in EchoServer:" + e);
		}
   }
}

  

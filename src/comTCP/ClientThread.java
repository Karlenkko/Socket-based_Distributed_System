package comTCP;

import Util.HistoryHandler;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	private static ArrayList<Socket> socketList = new ArrayList<>();
	PrintStream socOut = null;
	BufferedReader socIn = null;

	/**
	 * Constructor of the Client Thread, that use one thread to listen to incoming messages.
	 * it marks a new client connect socket and save it to the broadcasting list, and tells the client
	 * immediately the history message
	 * @param s
	 */
	ClientThread(Socket s) {
		this.clientSocket = s;
		socketList.add(s);
		try {
			socOut = new PrintStream(s.getOutputStream());
			String history = HistoryHandler.readAll();
			socOut.println(history);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

 	/**
  	* receives a request from client then sends an echo to all clients in the
	 * broadcasting list
  	**/
 	@Override
	public void run() {
		try {
			socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    

    		String line;
    		String msg;
    		while (true) {

    			if (socIn.ready()) {
					line=socIn.readLine();

					msg = "From " + clientSocket.getInetAddress() + " : " + clientSocket.getPort() + " , says: " + line;
					HistoryHandler.writeAMessage(msg);
					for(Socket s : socketList) {
						socOut = new PrintStream(s.getOutputStream());
//						System.out.println(msg);
						if (s.getPort() == clientSocket.getPort()) {
							socOut.println("From yourself, says: " + line);
							continue;
						}
						socOut.println(msg);
					}

				}

    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
	}
  
  }
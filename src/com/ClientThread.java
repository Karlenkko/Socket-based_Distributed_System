package com;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	private static ArrayList<Socket> socketList = new ArrayList<>();
	PrintStream socOut = null;
	BufferedReader socIn = null;

	ClientThread(Socket s) {
		this.clientSocket = s;
		socketList.add(s);
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	**/
	public void run() {
		try {
			socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    

    		String line;
    		while (true) {

    			if (socIn.ready()) {
					line=socIn.readLine();

					line = line + " from " + clientSocket.getInetAddress() + " : " + clientSocket.getPort();
					for(Socket s : socketList) {
						socOut = new PrintStream(s.getOutputStream());
						System.out.println(line);
						socOut.println(line);
					}

				}

    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
	}
  
  }

  

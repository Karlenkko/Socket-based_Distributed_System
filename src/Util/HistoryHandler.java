package Util;

import java.io.*;

public class HistoryHandler {

    /**
     * read all the histories saved at the local .txt file
     * @return the String that contains all histories
     */
    public static String readAll(){
        File history = new File("history.txt");
        BufferedReader reader = null;
        StringBuffer allMsg = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(history));
            String oneLine = null;
            while ((oneLine = reader.readLine()) != null) {
                allMsg.append(oneLine + "\n");
            }
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return allMsg.toString();
    }

    /**
     * write one line of message into the history file
     * @param msg one message sent by one user
     */
    public static void writeAMessage(String msg) {
        try{
            FileWriter writer = new FileWriter("history.txt",true);
            writer.write(msg + "\n");
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}

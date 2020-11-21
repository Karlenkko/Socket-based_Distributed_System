package multicastClient;

import sun.misc.Cleaner;

public interface MulticastSubscriber {

    abstract public void onReceiveMessage(ClientListenThread clientListenThread, String msg);

}

package multicastClient;

/**
 * multicast subscriber interface, designed for GUI version to receive instant messages,
 * methods are invoked by the Client Listen Thread
 */
public interface MulticastSubscriber {

    /**
     * action to be done when receiving a message, triggered by the Client Listen Thread
     * @param clientListenThread the Client Listen Thread which actually listens to incoming messages
     * @param msg the latest received message
     */
    abstract public void onReceiveMessage(ClientListenThread clientListenThread, String msg);

}

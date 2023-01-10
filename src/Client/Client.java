package Client;

//Interface that all multiplayer clients implement
public interface Client {

    //Method to process events from server
    public void inputEvent(String input) throws Exception;

    //Method for setting opponent(s) name(s)
    public void setOppName(String oppName);

    //Method for when leaving a multiplayer game
    //Closes socket and streams
    public void shutdown();

}

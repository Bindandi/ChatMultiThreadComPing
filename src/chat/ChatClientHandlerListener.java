package chat;

public interface ChatClientHandlerListener {
	
	void handleLogin(String username);
	void handleMessage(String username, String message);
        void ping(String username);
        void end(String username);
}

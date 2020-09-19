package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static final int PORT = 4567;
	private List<ChatClientHandler> clientHandlers = new ArrayList<>();
	
	public static void main(String[] args) {
		ChatServer server = new ChatServer();
		server.listen();
	}

	private void listen() {
		try (ServerSocket socket = new ServerSocket(PORT)) {
			while (true) {
				ChatClientHandler client = new ChatClientHandler(socket.accept(), new ChatClientHandlerListener() {
					@Override
					public void handleMessage(String username, String message) {
						for (ChatClientHandler chatClientHandler : clientHandlers) {
							chatClientHandler.send(">> " + username + ": " + message);
						}
					}
					@Override
					public void handleLogin(String username) {
						for (ChatClientHandler chatClientHandler : clientHandlers) {
							chatClientHandler.send("New user logged in: " + username);
						}
					}
                                        @Override
					public void ping(String username) {
                                            int position = getUserPosition(username);
                                            clientHandlers.get(position).pong();
					}
                                        @Override
					public void end(String username) {
                                            int position = getUserPosition(username);
                                            clientHandlers.get(position).stop();
					}
				});
				clientHandlers.add(client);
				client.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
        private int getUserPosition(String username)
        {
            int i = 0;
            while(i < clientHandlers.size() && !clientHandlers.get(i).getUsername().equals(username))
                i++;
            return i;
        }

}

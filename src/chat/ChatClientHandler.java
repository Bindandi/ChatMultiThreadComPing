package chat;

import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.logging.Logger;

public class ChatClientHandler extends Thread {
	private Socket socket;
	private String username;
	private ChatClientHandlerListener listener;
	private PrintWriter output;
	private Logger logger = Logger.getLogger(ChatClientHandler.class.getName());
        
        Ping ping;
        
	public ChatClientHandler(Socket socket, ChatClientHandlerListener listener) {
		logger.info("New user connecting...");
		this.socket = socket;
		this.listener = listener;
	}
        
        public String getUsername()
        {
            return username;
        }

	//"command<<<>>>body"
	//"login<<<>>>fulana.de.almeida"
	//"message<<<>>>Olá pessoal, boa tarde!"
	public void run() {
            logger.info("Usuario Conectado...");
		try  {
			output = new PrintWriter(socket.getOutputStream());
			Scanner input = new Scanner(socket.getInputStream());			
			while (true) {
				final String message = input.nextLine();
                                if(!message.equalsIgnoreCase("ping"))
                                {
                                    final String[] messageArray = message.split("<<<>>>");
                                    logger.info("New message: [" + message + "]");
                                    final String command = messageArray[0];
                                    final String body = messageArray[1];
                                    handleMessage(command, body);
                                }
                                else
                                    handleMessage(message);
				
								
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
        
        private void handleMessage(String command)
        {
           handleMessage(command, ""); 
        }
        
	private void handleMessage(String command, String body) {
		logger.info("Handling new message: command: " + command + ", body: " + body);
		if (command.equalsIgnoreCase("login")) {
			this.username = body;
			listener.handleLogin(body);
		} 
                else {
                    if (command.equalsIgnoreCase("message")) 
			listener.handleMessage(username, body);
                    else{
                        if(command.equalsIgnoreCase("ping")){
                            ping = new Ping(listener, socket, username);
                            ping.executar();
                        }
                    }
		}		
	}
        
        public void pong()
        {
            ping.setRetorno();
        }

	public void send(String message) {
		try {
			output.println(message);
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

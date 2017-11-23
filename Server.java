

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
	
	private static UsersList list = new UsersList();
	private static ChatHistory chatHistory = new ChatHistory();

	public static void main(String[] args) {
		
		int port = Config.PORT;
		ServerSocket socketListener;
		try{
			while(true){
				try{
					System.out.println("Trying to start server on port " + port + "...");
					socketListener = new ServerSocket(port);
					break;
				}catch(SocketException e){
					port++;
				}
			}
			
			System.out.println("Server on port " + port + " started.");
			
			while(true){
				Socket client = null;
				while(client == null){
					client = socketListener.accept();
				}
				System.out.println("Client " + client.getRemoteSocketAddress() + " connected.");
				new ClientThread(client);
			}
		}catch(SocketException e){
			System.err.println("Socket exception");
			e.printStackTrace();
		}catch(IOException e){
			System.err.println("I/O exception");
			e.printStackTrace();
		}
	}
	
	public synchronized static UsersList getUserList(){
		return list;
	}
	
	public synchronized static ChatHistory getChatHistory(){
		return chatHistory;
	}
}

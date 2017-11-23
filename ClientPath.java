

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


public class ClientPath{
	
	
	private Scanner sc = new Scanner(System.in);
	private final String adress = Config.SERVER_ADRESS;
	private final int serverPort = Config.PORT;
	private static ObjectOutputStream outputStream;
	private static ObjectInputStream inputStream;
	Socket socket;
	
	Thread inputThread;
	Thread outputThread;

	
	ClientPath(){
		System.out.println("Enter your login: ");		
		String login = this.sc.nextLine();
		
		
		try{
			InetAddress ipAddress = InetAddress.getByName(adress);
			socket = new Socket(ipAddress, serverPort);
			
			System.out.println("Connected to server");
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			
			System.out.println("Пытемся зарегестрироваться...");
			
			outputStream.writeObject(new Message(login,Config.HELLO_MESSAGE));
			
			System.out.println("Регистрация прошла успешно");
			
			ChatHistory chatHistory = (ChatHistory) inputStream.readObject();
			for(Message msg : ((ChatHistory) chatHistory).getHistory()){
				System.out.println("[" + msg.getLogin() + "]:" + msg.getMessage());
			}
			
		}catch (SocketException e) {
			System.err.println("Сервер отключили");
			try{
				socket.close();
			}catch(Exception ex){
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		inputThread = new Thread(new Runnable() {
			
			private ObjectOutputStream outputStream = ClientPath.outputStream;
			private ObjectInputStream inputStream = ClientPath.inputStream;
			private Message sMsg;

			@Override
			public void run() {
			
				try{
					while(true){
						this.sMsg = (Message) inputStream.readObject();
						if(sMsg instanceof Ping){
							outputStream.writeObject(new Ping());
						}
						else{
							System.out.println("[" + sMsg.getLogin() + "]:" + sMsg.getMessage());
						}
					}
				}catch (SocketException e) {
					System.err.println("Сервер отключен");
					try{
						socket.close();
					}catch(Exception ex){
						
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				
			}
				
		});
		
		outputThread = new Thread(new Runnable() {
			
			private ObjectOutputStream outputStream = ClientPath.outputStream;
			private String line;
			private Scanner sc = new Scanner(System.in);
			
			@Override
			public void run() {
				try{
					while(true){
						//System.out.print("[" + login + "]: ");
						line = sc.nextLine();
						if(!line.isEmpty())	outputStream.writeObject(new Message(login, line));
					}
				}catch (IOException e) {
					System.err.println("IOException");
					e.printStackTrace();
				}
			}
		});
		
		inputThread.start();
		outputThread.start();
	}
		
	
	public static void main(String[] args) {
		new ClientPath();
			
		
		/*try{			
			
			while(text != "exit"){
				text = sc.nextLine();
				outputStream.writeObject(new Message(login,text));
				Message msg = (Message) inputStream.readObject();
				System.out.println(msg.getMessage());
			}
			sc.close();
			socket.close();
			System.out.println("Disconnected");
			
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}*/
	}
		

	

}

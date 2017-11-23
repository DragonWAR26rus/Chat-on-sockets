

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.Timer;

public class ClientThread extends Thread {
	
	private final static int DELAY = 100000;
	
	private Socket socket;
	private Message c;
	private String login;
	private int inPacks = 0;
	private int outPacks = 0;
	private boolean flag = false;
	private Timer timer;
	
	public ClientThread(Socket socket){
		this.socket = socket;
		this.start();
	}
	
	public void run(){
		try{
			
			final ObjectInputStream inputStream = new ObjectInputStream(this.socket.getInputStream());
			final ObjectOutputStream outputStream = new ObjectOutputStream(this.socket.getOutputStream());

			this.c = (Message) inputStream.readObject();
			this.login = this.c.getLogin();
			
			if(!this.c.getMessage().equals(Config.HELLO_MESSAGE)){
				System.out.println("[" + this.c.getLogin() + "]:" + this.c.getMessage());
				Server.getChatHistory().addMessage(this.c);
				outputStream.writeObject(new Message("Server-bot", "Welcom to black-royal chat, " + login + "!"));
			}else{
				outputStream.writeObject(Server.getChatHistory());
				this.broadcast(Server.getUserList().getClientList(), new Message("Server-bot","The user " + login + "has been connected"));
			}
			
			Server.getUserList().addUser(login, socket, outputStream, inputStream);
			
			this.c.setOnlineUsers(Server.getUserList().getUsers());
			
			this.broadcast(Server.getUserList().getClientList(), this.c);
			
			this.timer = new Timer(DELAY, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						if(inPacks == outPacks){
							outputStream.writeObject(new Ping());
							outPacks++;
							System.out.println(outPacks + " out");
						}else{
							throw new SocketException();
						}
					}catch (SocketException e1) {
						System.out.println("packages not clash");
						System.out.println(login + " disconnected!");
						
						Server.getUserList().deleteUser(login);
						broadcast(Server.getUserList().getClientList(), new Message("Server-bot", "The user " + login + "has been connected", Server.getUserList().getUsers()));
						flag = true;
						timer.stop();
					}catch(IOException e1){
						e1.printStackTrace();
					}
					
				}
			});
			
			this.timer.start();
			
			outputStream.writeObject(new Ping());
			this.outPacks++;
			System.out.println(outPacks + " out");
			
			while(true){
				if(this.flag){
					this.flag = false;
					break;
				}
				
				this.c = (Message) inputStream.readObject();
				
				if(this.c instanceof Ping){
					this.inPacks++;
					System.out.println(this.inPacks + " in");
				}else if(!c.getMessage().equals(Config.HELLO_MESSAGE)){
					System.out.println("[" + login + "]: " + c.getMessage());
					Server.getChatHistory().addMessage(this.c);
				}else{
					outputStream.writeObject(Server.getChatHistory());
					this.broadcast(Server.getUserList().getClientList(), new Message("Server-Bot", "The user " + login + " has been connect"));
				}
				
				this.c.setOnlineUsers(Server.getUserList().getUsers());
				
				if(!(c instanceof Ping) && !c.getMessage().equals(Config.HELLO_MESSAGE)){
					System.out.println("Send broadcast Message: \"" + c.getMessage() + "\"");
					this.broadcast(Server.getUserList().getClientList(), this.c);
				}
			}
		}catch(SocketException e){
			System.out.println(login + " disconnected!");
			this.broadcast(Server.getUserList().getClientList(), new Message("Server-bot",
																			"The user " + login + " has been connected", 
																			Server.getUserList().getUsers()));
			this.timer.stop();
		}catch(IOException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	private void broadcast(ArrayList<Client> clientsArrayList, Message message){
		try{
			for(Client client : clientsArrayList){
				client.getThisObjectOutputStream().writeObject(message);
			}
		}catch(SocketException e){
			System.out.println("in broadcast: " + login + " disconnected!");
			Server.getUserList().deleteUser(login);
			this.broadcast(Server.getUserList().getClientList(), new Message("System","The user " + login + " has been disconnected", Server.getUserList().getUsers()));
			timer.stop();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI {

	private JFrame regUI;
	private String login = "";
	
	private String adress = Config.SERVER_ADRESS;
	private int serverPort = Config.PORT;
	private ChatUI chatUI = new ChatUI();;
	
	GUI(){
		
		regUI = new JFrame("Регистрация");
		regUI.setSize(200, 150);
		regUI.setLocation(700,400);
		regUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container pane;
		pane = regUI.getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		c.weightx = 0;
		c.weighty = 0;
		c.ipadx = 0;
		c.ipady = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		
		JLabel label = new JLabel("Введите никнейм");
		pane.add(label, c);
		
		c.insets = new Insets(7, 0, 0, 0);
		c.gridy = GridBagConstraints.RELATIVE;
		JTextField textField = new JTextField(10);
		pane.add(textField, c);
		
		c.insets = new Insets(10, 0, 0, 0);
		c.gridy = GridBagConstraints.RELATIVE;
		JButton acceptButton = new JButton("Войти");
		pane.add(acceptButton, c);
		
		textField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					acceptButton.doClick();
				}
			}
		});
		
		acceptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try{
					login = textField.getText();
					if(login.isEmpty()) throw new Exception();
					chatUI.setLogin(login);
					regUI.setVisible(false);
					chatUI.setVisible(true);
					try{
						chatUI.connectingToServer();
					}catch(Exception ex){
						//chatUI.addMessage("Не удалось подключиться!");
						chatUI.addMessage(new Message("System","Не удалось подключиться!"));
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		});
		
		regUI.setVisible(true);
	}
	
	
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		GUI gui = new GUI();
	}
	
	private class ChatUI extends JFrame{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextArea chatArea  = new JTextArea(20, 20);
		private JTextArea userList  = new JTextArea(20, 8);
		private JTextArea textField = new JTextArea(4, 20);
		private JButton sendButton  = new JButton("Отправить");
		private JLabel loginLabel;
		
		private boolean serverWasDown = false;
		
		private Socket socket;
		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;
		
		Thread inputThread;
		ChatUI(){
			super("Чат у Батьки");
			super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			super.setSize(600, 500);
			super.setLocation(700,300);
			
			Container pane = super.getContentPane();;
			setPropsChatUI(pane);	
		}
		
		public void setLogin(String login) {
			loginLabel.setText("User: " + login);
		}

		void setPropsChatUI(Container pane){
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			pane.setLayout(gbl);

			this.chatArea.setEditable(false);
			chatArea.setText("");
			chatArea.setLineWrap(true);
			JScrollPane chatPane = new JScrollPane(this.chatArea);
			chatPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.ipadx = 0;
			c.ipady = 0;
			c.gridheight = 8;
			c.gridwidth = 8;
			c.insets = new Insets(5,5,5,5);
			pane.add(chatPane, c);
			
			this.userList.setEditable(false);
			userList.setText("");
			JScrollPane userListPane = new JScrollPane(this.userList);
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0;
			c.weighty = 0;
			c.gridx = 9;
			c.gridy = 0;
			c.gridheight = 8;
			c.gridwidth = 2;
			pane.add(userListPane, c);
			
			JScrollPane textFieldPane = new JScrollPane(this.textField);
			textField.setLineWrap(true);
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.SOUTHEAST;
			c.gridx = 0;
			c.gridy = 8;
			c.weightx = 1;
			c.weighty = 0.25;
			c.gridwidth = 8;
			c.gridheight = 2;
			c.insets = new Insets(5,5,15,5);
			pane.add(textFieldPane, c);
			
			loginLabel = new JLabel();
			loginLabel.setHorizontalAlignment(JLabel.CENTER);
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.SOUTHWEST;
			c.gridx = 9;
			c.gridy = 8;
			c.weightx = 0;
			c.weighty = 0;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.insets = new Insets(15,20,0,15);
			pane.add(this.loginLabel, c);
			

			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.SOUTHWEST;
			c.gridx = 9;
			c.gridy = 9;
			c.weightx = 0;
			c.weighty = 0;
			c.gridwidth = 2;
			c.gridheight = 1;
			c.insets = new Insets(5,20,40,15);
			pane.add(this.sendButton, c);	
			
			textField.addKeyListener( new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER){
						sendButton.doClick();
					}
					
				}
			});
			
		}
		
		void connectingToServer(){
			try{	
				
				InetAddress ipAddress = InetAddress.getByName(adress);
				socket = new Socket(ipAddress, serverPort);
				
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream = new ObjectInputStream(socket.getInputStream());

				outputStream.writeObject(new Message(login,Config.HELLO_MESSAGE));

				ChatHistory chatHistory = (ChatHistory) inputStream.readObject();
				for(Message msg : ((ChatHistory) chatHistory).getHistory()){
					try{
						chatUI.addMessage(msg);
					}
					catch(Exception ex){
						System.err.println(chatUI);
						System.err.println(chatHistory);
						System.err.println(msg);
					}
				}
				
			}catch (SocketException e) {
				if(!serverWasDown){
					chatUI.addMessage(new Message("System","Сервер выключен"));
					try{
						socket.close();
					}catch(Exception ex){
						
					}
					serverWasDown = true;
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			inputThread = new Thread(new Runnable() {

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
								chatUI.addMessage(sMsg);
								try{
									chatUI.setOnlineUsers(sMsg.getUsers());
								}catch(UserListIsNull ex){
									System.err.println("NullPointer в UserList");
								}
							}
						}
					}catch (SocketException | NullPointerException e) {
						if(!serverWasDown){
							chatUI.addMessage(new Message("System","Сервер отключился"));
							try{
								socket.close();
							}catch(Exception ex){}
							serverWasDown = true;
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					
				}
					
			});
			
			sendButton.addActionListener(new ActionListener() {
				private String line;
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try{
						line = chatUI.getMessage();
						if(!line.isEmpty())	outputStream.writeObject(new Message(login, line));
					}catch (IOException ex) {
						System.err.println("IOException");
						ex.printStackTrace();
					}
				}
			});		
			inputThread.start();
		}
		
		public synchronized String getMessage(){
			String msg = textField.getText();
			textField.setText("");
			return msg;
		}
		
		public synchronized void addMessage(Message msg){
			String chatHistory = this.chatArea.getText();
			this.chatArea.append("[" + msg.getLogin() + "]: " + msg.getMessage() + "\n");
			this.chatArea.setCaretPosition(this.chatArea.getDocument().getLength());
		}
		
		public void setOnlineUsers(String[] users)
			throws UserListIsNull{
			if(users == null) throw new UserListIsNull();
			userList.setText("");
			for(int i = 0; i < users.length; i++){
				userList.setText(userList.getText() + users[i] + "\n");
			}
		}
	}

}

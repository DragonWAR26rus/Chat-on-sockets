

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String login;
	private String message;
	private String[] users;
	private Date time;
	
	// ����������� ��� �������
	public Message(String login, String message){
		this.login = login;
		this.message = message;
		this.time = java.util.Calendar.getInstance().getTime();
	}
	
	// ����������� ��� �������
	public Message(String login, String message, String[] users){
		this.login = login;
		this.message = message;
		this.time = java.util.Calendar.getInstance().getTime();
		this.users = users;
	}
	
	public void setOnlineUsers(String[] users){
		this.users = users;
	}
	
	public String getLogin(){
		return this.login;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public String[] getUsers(){
		return this.users;
	}
	
	public String getDate(){
		Time tm = new Time(this.time.getTime());
		return tm.toString();
	}
}

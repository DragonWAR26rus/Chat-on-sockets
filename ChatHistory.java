

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8906854505709611376L;
	private List<Message> history;
	
	public ChatHistory(){
		this.history = new ArrayList<Message>(Config.HISTORY_LENGHT);
	}
	
	public void addMessage(Message message){
		if(this.history.size() > Config.HISTORY_LENGHT){
			this.history.remove(0);
		}
		
		this.history.add(message);
	}
	
	public List<Message> getHistory(){
		return this.history;
	}
}

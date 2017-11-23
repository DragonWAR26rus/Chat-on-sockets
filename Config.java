

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static final String PROPERTIES_FILE = "./server1.properties";

	public static String HELLO_MESSAGE = "User join to the chat(Auto-message)";
	public static String SERVER_ADRESS/* = "127.0.0.1"*/;
	public static int HISTORY_LENGHT/* = 20*/;
	public static int PORT/* = 5000*/;
	
	static{
		Properties properties = new Properties();
		FileInputStream propertiesFile = null;
		
		try{		
			propertiesFile = new FileInputStream(PROPERTIES_FILE);
			properties.load(propertiesFile);
			
			PORT = Integer.parseInt(properties.getProperty("PORT"));
			//HELLO_MESSAGE = properties.getProperty("HELLO_MESSAGE");
			HISTORY_LENGHT = Integer.parseInt(properties.getProperty("HISTORY_LENGHT"));
			SERVER_ADRESS = properties.getProperty("SERVER_ADRESS");
		}catch(FileNotFoundException e){
			System.err.println("Properties config file not found");
		}catch(IOException e){
			System.err.println("Error while reading file");
		}finally{
			try{
				propertiesFile.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
}

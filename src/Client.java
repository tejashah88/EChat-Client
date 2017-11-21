import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

/**
 * Class that manages connections to server
 * @author Tejas Shah
 * @version 11/7/14
 */
public class Client {
	// for I/O
	private ObjectInputStream inFromServer;		// to read from the socket
	private ObjectOutputStream outToServer;		// to write on the socket
	private Socket socket;
	private static final String DIVIDER = "~"; 
	private String server;
	private int port;
	SimpleDateFormat sdf;
	
	private String user, pass;
	
	JFrame tempProgress;
	JProgressBar progressBar;
	int barValue = 0;
	
	EChatClientGUI cg;
	
	/**
	 * Construstor for class of Client
	 * @param server 	the server to connect to
	 * @param port		the port of the server
	 * @param user		the username of this client
	 * @param cg		an instance of the EChat GUI for client
	 */
	public Client(String server, int port, String user, String pass, EChatClientGUI cg) {
		this.server = server;
        this.port = port;
        this.sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        this.user = user;
        this.pass = pass;
        this.cg = cg;
        
        this.tempProgress = new JFrame("");
        this.tempProgress.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.tempProgress.setSize(300, 100);
        this.tempProgress.setVisible(false);
	}
	
	/**
	 * Initializes the client and connects to the server specified
	 * @return true if it could initialize everything, false if otherwise
	 */
	public boolean initialize() {
		this.display("Connecting to servers...");
		
		try {
			socket = new Socket(this.server, this.port);
			this.outToServer = new ObjectOutputStream(socket.getOutputStream());
			this.inFromServer = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			//this.error("Unable to initialize streams!");
			return false;
		}
		
		new ListenFromServer().start();
		
		try {
			this.outToServer.writeObject(new Message(MessageType.LOGIN, user + DIVIDER + pass));
		} catch (IOException e) {
			//e.printStackTrace();
			//this.error("Unable to send data to server!");
			return false;
		}
		
		this.display("Connected to: " + socket.getInetAddress().getHostName());
		
		return true;
	}
	
	/**
	 * Shuts down the client and closes the client.
	 */
	public void shutdown() {
		deInitBar();
		
		try {
			if (this.inFromServer != null)
				this.inFromServer.close();
		} catch (IOException e) {}
		
		try {
			if (this.outToServer != null)
				this.outToServer.close();
		} catch (IOException e) {}
		
		try {
			if(this.socket != null)
				this.socket.close();
		} catch (IOException e) {}
		
		System.exit(0);
	}
	
	/**
	 * Sends some text to the server
	 * @param text whatever to send to the server
	 */
	public void sendToServer(Message text) {
		try {
			this.outToServer.writeObject(text);
		} catch (IOException e) {
			e.printStackTrace();
			this.error("Lost connection to server!");
			this.shutdown();
		}
	}
	
	/**
	 * Preps the progress bar for use during a long operation.
	 * @param title the title of the progress bar
	 */
	public void initBar(String title) {
		cg.setEnabled(false);
		this.tempProgress.setTitle(title);
		
		this.progressBar = new JProgressBar();
		this.progressBar.setValue(0);
	    this.progressBar.setStringPainted(true);
	    this.progressBar.setBorder(BorderFactory.createTitledBorder(title));
	    
	    this.tempProgress.add(this.progressBar, BorderLayout.NORTH);
	    this.tempProgress.setVisible(true);
	}
	
	/**
	 * Increments the progress bar by the specified value.
	 * @param n the value to add to the progress
	 */
	public void addBarValue(int n) {
		this.barValue += n;
		
		if (this.barValue >= 100) {
			this.barValue = 100;
		}
		
		this.progressBar.setValue(this.barValue);
	}
	
	/**
	 * Decrements the progress bar by the specified value.
	 * @param n the value to subtract from the progress
	 */
	public void subBarValue(int n) {
		this.barValue -= n;
		
		if (this.barValue <= 0) {
			this.barValue = 0;
		}
		
		this.progressBar.setValue(this.barValue);
	}
	
	/**
	 * Deallocates resources from the progress bar.
	 */
	public void deInitBar() {
		if (this.progressBar != null) {
			this.tempProgress.remove(this.progressBar);
		}
		
		this.progressBar = null;
		this.tempProgress.setVisible(false);
		
		cg.setEnabled(true);
	}
	
	/**
	 * Displays a message to the console.
	 * @param o the thing to display
	 */
	public void display(Object o) {
		cg.console.append(this.getDate() + ": " + o + "\n");
		cg.console.setCaretPosition(cg.console.getText().length());
	}
	
	public void selfDisplay(Object o) {
		cg.console.append("EChat: " + o + "\n");
		cg.console.setCaretPosition(cg.console.getText().length());
	}
	
	/**
	 * Shows a message box of an error that occured.
	 * @param o the error to show
	 */
	public void error(Object o) {
		JOptionPane.showMessageDialog(null, o);
	}
	
	/**
	 * Returns the current date and time since this method was called 
	 * @return the current date and time since this method was called
	 */
	public String getDate() {
		return this.sdf.format(new Date());
	}
	
	/**
	 * Class for listening for activity from server
	 * @author Tejas Shah
	 * @version 11/7/14
	 */
	class ListenFromServer extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					Message msg = (Message) inFromServer.readObject();
					Client.this.display(msg.getText());
				} catch (ClassNotFoundException | IOException e) {
					Client.this.error("Lost connection to server!");
					Client.this.shutdown();
				}
			}
		}
	};
}
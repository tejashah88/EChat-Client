import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 * This class is used for actually running the EChat client
 * 
 * @author Tejas Shah
 * @version 11/10/14
 */
public class EChatClientGUI extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = -403429136827077132L;
	
	//init moar stuff

	static easyGUI gui = new easyGUI();
	JTextArea console;
	JTextField txtCmd;
	JButton btnSendText;
	JScrollPane consoleScroll;
	//JList lstUsers;
	JPanel panel;
	GridBagConstraints gbc;
	
	static String text = "", user, pass;
	
	static String[] usersInRoom;
	
	static boolean hasSentLogin = false;
	
	static Client client;
	
	//set up client interface
	
	private static class LoginGUI extends JFrame implements ActionListener {
		private static final long serialVersionUID = 8601400259740488083L;

		easyGUI gui = new easyGUI();
		
		JLabel lblUser, lblPass;
		JTextField txtUser, txtPass;
		JButton btnLogin, btnRegister;
		
		public LoginGUI() {
			lblUser = new JLabel("Username:");
			lblPass = new JLabel("Password:");
			txtUser = new JTextField(20);
			txtPass = new JTextField(20);
			btnLogin = new JButton("Login");
			btnRegister = new JButton("Register");
			
			gui.registerActionEvents(this, btnLogin);
			this.add(gui.addToPanel(lblUser, txtUser, lblPass, txtPass, btnLogin, btnRegister));
			
			gui.initGUI(this, "Login/Register", 300, 175);
			this.setResizable(false);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource().equals(btnLogin)) {
				user = txtUser.getText();
				pass = txtPass.getText();
				hasSentLogin = true;
			}
		}
	}
	
	public EChatClientGUI() {
		console = new JTextArea(20, 40);
		console.setEditable(false);

		txtCmd = new JTextField(40);
		btnSendText = new JButton("Send to server");
		
		//usersInRoom = new String[10];
		//lstUsers = new JList<String>();
		//lstUsers.
		
		consoleScroll = new JScrollPane(console, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
										ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		console.setLineWrap(true);
		
		gui.registerActionEvents(this, btnSendText);
		gui.registerActionEvents(this, txtCmd);
		
		//panel = gui.addToPanel(/*new GridBagLayout(),*/ consoleScroll, btnSendText, txtCmd);
		
		panel = new JPanel(new GridBagLayout()); 
		gbc = new GridBagConstraints();
		
		/* config for grid bag constants */
		{
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0.5;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			panel.add(consoleScroll, gbc);
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.5;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			panel.add(txtCmd, gbc);
			
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.5;
			gbc.gridx = 1;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			panel.add(btnSendText, gbc);
		}
		
		this.add(panel);
	}
	
	//conect to d serva
	
	public static void main(String argv[]) {
		/*LoginGUI login = new LoginGUI();
		
		while (true) {
			System.out.print("");
			if (hasSentLogin) {
				login.dispose();
				break;
			}
		}*/
		//user = JOptionPane.showInputDialog(null, "User: ");
		//pass = JOptionPane.showInputDialog(null, "Pass: ");
		
		EChatClientGUI cg = new EChatClientGUI();
		
		InetAddress address = null;
		try {
			address = InetAddress.getByName("www.easychat.duckdns.org");
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "Unable to resolve EChat server address!");
			System.exit(-1);
		}
		
		client = new Client(address.getHostAddress(), 8888, user, pass, cg);
		
		gui.initGUI(cg, "EChat Client - " + user, 650, 395);
		cg.setMinimumSize(new Dimension(650, 395));
		//gui.enableBetterGUI();
		
		//System.out.println(address.getHostAddress());
		
		int tries = 0;
		while (true) {
			if (!client.initialize()) {
				tries++;
			} else {
				break;
			}
			
			if (tries >= 15) {
				client.error("Unable to establish connection with servers!");
				client.shutdown();
				return;
			}
		}
	}
	
	//the thingy that detects wen d comand is fyrd

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(btnSendText) || event.getSource().equals(txtCmd)) {
			text = txtCmd.getText();
			boolean isEmpty = text.equals("");
			boolean isCmd = text.startsWith("/");
			
			if (!isEmpty) {
				if (isCmd) {
					switch (text.substring(1))
					{
						case "help":
							client.selfDisplay("fck off!");
							break;
							
						case "logout":
							client.shutdown();
							break;
						
						case "who":
							//client.sendToServer(new Message(MessageType.COMMAND, text));
							
							break;
					}
				} else {
					client.sendToServer(new Message(MessageType.TEXT, text));
				}
				
				txtCmd.setText("");
			}
		}
	}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		client.shutdown();
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}
}
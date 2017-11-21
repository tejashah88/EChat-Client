import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

@SuppressWarnings("serial")
public /*abstract*/ class easyGUI extends JFrame implements ActionListener{
	public JFrame initGUI(JFrame frame, String title, int width, int height) {
		frame.setTitle(title);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		return frame;
	}
	
	public JFrame initGUI(JFrame frame, String title) {
		frame.setTitle(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		return frame;
	}
	
	public void enableBetterGUI() {
		try {
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public JPanel addToPanel(LayoutManager layout, Component...widgets) {
		JPanel panel = new JPanel(layout);
		for (Component widget : widgets) {
			panel.add(widget);
		}
		return panel;
	}
	
	public void addToPanel(JPanel panel, Component...widgets) {
		for (Component widget : widgets) {
			panel.add(widget);
		}
	}
	
	public JPanel addToPanel(Component...widgets) {
		JPanel panel = new JPanel();
		for (Component widget : widgets) {
			panel.add(widget);
		}
		
		return panel;
	}
	
	public JTabbedPane addToTabPane(JTabbedPane jtPane, JPanel...panels) {
		for (JPanel panel : panels) {
			jtPane.add(panel);
		}
		
		return jtPane;
	}
	
	public JTabbedPane addToTabPane(JPanel...panels) {
		JTabbedPane jtPane = new JTabbedPane();
		for (JPanel panel : panels) {
			jtPane.add(panel);
		}
		
		return jtPane;
	}
	
	public void registerActionEvents (JFrame frame, AbstractButton...widgets) {
		for (AbstractButton widget : widgets) {
			widget.addActionListener((ActionListener) frame);
		}
	}
	
	public void registerActionEvents(JFrame frame, JTextField...widgets) {
		for (JTextField widget : widgets) {
			widget.addActionListener((ActionListener) frame);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event){}
	
	//public abstract void exitGUI();
}

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;


public class ChatWindow extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 248726832487L;
	Container contentPane;
	JButton submitButton = new JButton("Send");
	JTextArea chatArea = new JTextArea();
	JTextField inputArea = new JTextField();
	
	public ChatWindow(String username)
	{
		super();
		
		setTitle("username");
		setSize(400, 200);
		BorderLayout layout = (BorderLayout) getLayout();
		layout.setVgap(1);
		contentPane = getContentPane();
		contentPane.setBackground(new Color(150, 150, 150));
		contentPane.add(chatArea, BorderLayout.CENTER);
		JPanel lower = new JPanel();
		lower.setLayout(new BoxLayout(lower, BoxLayout.X_AXIS));
		lower.add(inputArea);
		lower.add(submitButton);
		submitButton.setMnemonic(KeyEvent.VK_S);
		submitButton.addActionListener(this);
		contentPane.add(lower, BorderLayout.PAGE_END);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent action)
	{
		if (action.getSource() == submitButton)
		{
			System.out.println("button clicked!");
		}
	}
}

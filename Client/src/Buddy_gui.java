import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author Seth Yost
 */
public class Buddy_gui extends javax.swing.JFrame {
	private Socket connection;
	private PrintWriter writer;

	/**
	 * Creates new form Buddy_gui
	 */
	public Buddy_gui(Socket s, BufferedReader r, PrintWriter w) {
		initComponents();
		writer = w;
		connection = s;
		
		ListenerThread listener = new ListenerThread(this, r);
		listener.start();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jScrollPane1 = new JScrollPane();
		AllContactsModel = new DefaultListModel();
		AllContacts = new JList(AllContactsModel);
		jLabel1 = new JLabel();
		jScrollPane2 = new JScrollPane();
		BuddiesModel = new DefaultListModel();
		Buddies = new JList(BuddiesModel);
		jLabel2 = new JLabel();
		Chat = new JButton();
		AddToBuddies = new JButton();
		GroupChat = new JButton();
		RemoveBuddy = new JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jScrollPane1.setViewportView(AllContacts);

		jLabel1.setText("All Contacts:");

		jScrollPane2.setViewportView(Buddies);

		jLabel2.setText("Buddies:");

		Chat.setText("Chat");
		Chat.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				ChatMouseClicked(evt);
			}
		});

		AddToBuddies.setText("Add to Buddies");
		AddToBuddies.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				AddToBuddiesMouseClicked(evt);
			}
		});
		AddToBuddies.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AddToBuddiesActionPerformed(evt);
			}
		});
		
		RemoveBuddy.setText("Remove Buddy");
		RemoveBuddy.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				RemoveBuddyMouseClicked(evt);
			}
		});

		GroupChat.setText("Group Chat");
		GroupChat.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				GroupChatMouseClicked(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(jScrollPane1)
					.addComponent(jScrollPane2)
					.addComponent(jLabel1)
					.addComponent(jLabel2)
					.addComponent(AddToBuddies, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addComponent(RemoveBuddy)
						.addComponent(GroupChat)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(Chat)))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jLabel1)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(AddToBuddies)
				.addGap(18, 18, 18)
				.addComponent(jLabel2)
				.addGap(3, 3, 3)
				.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(RemoveBuddy)
					.addComponent(Chat)
					.addComponent(GroupChat))
				.addContainerGap())
		);
		
		/*
		 * Codes are:
		 * 0  - CREATE ACCOUNT		C->S
		 * 1  - LOGON				C->S
		 * 2  - LOGOFF				C->S
		 * 3  - MESSAGE				C<->S
		 * 4  - BUDDY ON			S->C
		 * 5  - BUDDY OFF			S->C
		 * 6  - SUCCESSFUL LOGON	S->C
		 * 7  - FAILED LOGON		S->C
		 * 8  - ADD BUDDY			C->S
		 * 9  - REMOVE BUDDY		C->S
		 * 10 - TYPING				C<->S
		 * 11 - ENTERED TEXT		C<->S
		 * 12 - MESSAGE FAILED		S->C
		 * 13 - SET PROFILE			C->S
		 * 14 - GET PROFILE 		S->C
		 */

		pack();
	}// </editor-fold>
	
	public void addToBuddyList(String b)
	{
		BuddiesModel.addElement(b);
	}
	
	public void addToOnlineList(String u)
	{
		AllContactsModel.addElement(u);
	}
	
	public void removeFromOnlineList(String b)
	{
		for (int i = 0; i < AllContactsModel.size(); i++)
		{
			if (AllContactsModel.get(i).equals(b))
			{
				AllContactsModel.remove(i);
				return;
			}
		}
	}
	
	public void removeFromBuddyList(String b)
	{
		for (int i = 0; i < BuddiesModel.size(); i++)
		{
			if (BuddiesModel.get(i).equals(b))
			{
				BuddiesModel.remove(i);
				return;
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void RemoveBuddyMouseClicked(MouseEvent evt) {
		String b = Buddies.getSelectedValue().toString();
		if (b != null)
		{
			writer.write("9 " + getTitle() + " " + b);
			removeFromBuddyList(b);
		}
	}

	private void ChatMouseClicked(java.awt.event.MouseEvent evt) {
		
		String user = Buddies.getSelectedValue().toString();
		Chat_gui chat = new Chat_gui(user);
		chat.setTitle("Chat");
		chat.setVisible(true);
	}

	private void AddToBuddiesActionPerformed(java.awt.event.ActionEvent evt) {
		addBuddy();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void AddToBuddiesMouseClicked(java.awt.event.MouseEvent evt) {
		addBuddy();
	}
	
	private void addBuddy() {
		String u = AllContacts.getSelectedValue().toString();
		if (u != null)
		{
			removeFromOnlineList(u);
			writer.write("8 " + getTitle() + u);
		}
	}

	@SuppressWarnings("rawtypes")
	private void GroupChatMouseClicked(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		List user = Buddies.getSelectedValuesList();
		String users = user.toString();
		Chat_gui chat = new Chat_gui(users.substring(1, users.length()-2));
		chat.setVisible(true);
	}
	
	// Variables declaration - do not modify
	private javax.swing.JButton AddToBuddies;
	private javax.swing.JList AllContacts;
	private DefaultListModel<String> AllContactsModel;
	private javax.swing.JList Buddies;
	private DefaultListModel<String> BuddiesModel;
	private javax.swing.JButton Chat;
	private javax.swing.JButton GroupChat;
	private javax.swing.JButton RemoveBuddy;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	// End of variables declaration
}

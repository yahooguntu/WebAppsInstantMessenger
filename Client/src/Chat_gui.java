import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Seth Yost
 */
public class Chat_gui extends javax.swing.JFrame {

    /**
     * Creates new form Chat_gui
     * @param buddy_gui
     */
	private Buddy_gui buddy = null;
	private static Style STYLE_YOU;
	private static Style STYLE_BUDDY;
	private static Style STYLE_SERVER;
	
    public Chat_gui(String user, Buddy_gui buddy_gui) {
    	setTitle(user);
        initComponents();
        buddy = buddy_gui;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
    	
    	this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				buddy.unregisterChatWindow(getTitle());
			}
		});
    	
        jFrame1 = new javax.swing.JFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        Message = new javax.swing.JTextArea();
        Send = new javax.swing.JButton();
        Enter_send = new javax.swing.JCheckBox();
        //chat_with_label = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        conversationPane = new javax.swing.JTextPane();
        doc = conversationPane.getStyledDocument();
        status = new javax.swing.JLabel();
        

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        
        
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(jTree1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Message.setColumns(20);
        Message.setRows(5);
        Message.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                MessageKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(Message);
        
        Send.setText("Send");
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        Enter_send.setText("send on Enter");
        Enter_send.setSelected(true);

        //chat_with_label.setText("jLabel1");

        conversationPane.setEditable(false);
        jScrollPane4.setViewportView(conversationPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    	.addComponent(status)
                    	.addGap(0, 200, Short.MAX_VALUE)
                        .addComponent(Enter_send)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Send))
                    .addGroup(layout.createSequentialGroup()
                        //.addComponent(chat_with_label)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jScrollPane4)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
               // .addComponent(chat_with_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Send)
                    .addComponent(Enter_send)
                    .addComponent(status)))
        );

        pack();
        Message.requestFocusInWindow();
        
        // set up the styles
        STYLE_BUDDY = conversationPane.addStyle("buddyStyle", null);
        STYLE_SERVER = conversationPane.addStyle("serverStyle", null);
        STYLE_YOU = conversationPane.addStyle("youStyle", null);
        StyleConstants.setForeground(STYLE_BUDDY, new Color(0, 0, 255));
        StyleConstants.setForeground(STYLE_SERVER, new Color(255, 0, 0));
        StyleConstants.setForeground(STYLE_YOU, new Color(0, 0, 0));
    }

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {
    	String mess = Message.getText();
    	if(!mess.equalsIgnoreCase("\n"))
    	{
	    	Calendar cal = Calendar.getInstance();
			String scal = cal.getTime().toLocaleString();
	    	mess = "[" + scal + "] " + buddy.getTitle() + ": " + mess + "\n";
	    	
	    	appendToChatbox(mess, STYLE_YOU);
	    	fireMessage();
    	}
    }
    
    private boolean typingSent = false;

    private void MessageKeyTyped(java.awt.event.KeyEvent evt) {
    	if (typingSent == false)
    	{
    		buddy.setTyping(getTitle());
    		typingSent = true;
    	}
    	
    	//if checked send on enter otherwise do nothing.
    	if(Enter_send.isSelected())
    	{
    		char temp = evt.getKeyChar();
    		if(temp == '\n')
    		{
    			String mess = Message.getText();
	    	    Message.setText(Message.getText().substring(0, mess.length()-1));
    			if(!mess.equalsIgnoreCase("\n"))
    	    	{
	    	    	Calendar cal = Calendar.getInstance();
					String scal = cal.getTime().toLocaleString();
	    	    	mess = "[" + scal + "] " + buddy.getTitle() + ": " + mess;
	    	    	
	    	    	appendToChatbox(mess, STYLE_YOU);
	    	    	fireMessage();
    	    	}
    		}    		
    	}
    }
    
    private void fireMessage()
    {
    	typingSent = false;
    	String msg = Message.getText();
    	msg = msg.replaceAll("\n", "%40");
    	buddy.send(getTitle(), msg);
    	Message.setText("");
    }
    
    private void appendToChatbox(String s, Style style)
    {
    	try
    	{
    		doc.insertString(doc.getLength(), s, style);
    	}
    	catch(BadLocationException e)
    	{
    		System.out.println(e.getStackTrace());
    	}
    }
    
    //gets the message and appends it to the window
    protected void message(String From, String mess)
    {
    	Calendar cal = Calendar.getInstance();
		String scal = cal.getTime().toLocaleString();
    	mess = "[" + scal + "] " + From + ": " + mess + "\n";
    	mess = mess.replaceAll("%40", "\n");
    	appendToChatbox(mess, (From.equals("Server") ? STYLE_SERVER : STYLE_BUDDY));
    }
    protected void setStatus(String s)
    {
    	status.setText(s);
    }
    
    // Variables declaration - do not modify
    private javax.swing.JCheckBox Enter_send;
    private javax.swing.JTextArea Message;
    private javax.swing.JButton Send;
    //private javax.swing.JLabel chat_with_label;
    private javax.swing.JTextPane conversationPane;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTree jTree1;
    private StyledDocument doc;
    private javax.swing.JLabel status;
    // End of variables declaration
}



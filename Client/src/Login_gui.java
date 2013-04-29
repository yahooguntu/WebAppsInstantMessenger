import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Seth Yost
 */
public class Login_gui extends javax.swing.JDialog {
	private Socket connection = null;
	private BufferedReader reader;
	private PrintWriter writer;
	
    /**
     * Creates new form Login_gui
     */
    public Login_gui(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

       // jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Username = new javax.swing.JTextField();
        Password = new javax.swing.JPasswordField();
        Login_Button = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        Address = new javax.swing.JTextField();

        //jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setModal(true);

        jLabel1.setText("Login");

        jLabel2.setText("Username:");

        jLabel3.setText("Password:");

        Login_Button.setText("Login");
        Login_Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Login_ButtonMouseClicked(evt);
            }
        });
        Login_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Login_ButtonActionPerformed(evt);
            }
        });
        
        jLabel4.setText("Address:");

        Address.setText("Localhost:4225");
        Address.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddressActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(jLabel1)
                .addContainerGap(101, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(Login_Button))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Password, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(Username)
                            .addComponent(Address, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
	        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup()
	            .addGap(23, 23, 23)
	            .addComponent(jLabel1)
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(jLabel4)
	                .addComponent(Address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(jLabel2)
	                .addComponent(Username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                .addComponent(jLabel3)
	                .addComponent(Password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	            .addComponent(Login_Button)
	            .addContainerGap())
	    );

        pack();
    }// </editor-fold>

    private void Login_ButtonActionPerformed(java.awt.event.ActionEvent evt) {
        doLogon();
    }

    private void Login_ButtonMouseClicked(java.awt.event.MouseEvent evt) {
    	doLogon();
    }
    
    private void AddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddressActionPerformed
    
    private void doLogon() {
    	System.out.println("logging in");
    	try
    	{
	    	String user = Username.getText();
	    	String password = new String(Password.getPassword());
	    	String[] url = Address.getText().split(":");
	    	String message = "1 " + user + " " + password + "\n";
	    	if (connection == null)
	    	{
	    		connection = new Socket(url[0], Integer.parseInt(url[1]));
	    		reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    		writer = new PrintWriter(connection.getOutputStream());
	    	}
	    	writer.write(message);
	    	writer.flush();
	    	
	    	boolean success = false;
	    	String input = reader.readLine();
			while (!success)
			{
				if (input == null)
					break;
				
				System.out.println("Message received: " + input);
				
				if(input.substring(0, 1).compareTo("6") == 0)
				{
					success = true;
					break;
				}
				else if(input.substring(0, 1).compareTo("7") == 0)
				{
					jLabel1.setText("Login Failed.");
					Password.setText("");
					break;
				}
				
				input = reader.readLine();
			}
	    	
			if (success)
			{
				System.out.println("Login succeeded.");
				Buddy_gui buddy = new Buddy_gui(connection, reader, writer);
				buddy.setTitle(Username.getText().toLowerCase());
				buddy.setVisible(true);
				this.setVisible(false);
			}
			else
			{
				System.out.println("Login failed.");
			}
			
//	    	Thread readerThread = new Thread(this);
//			readerThread.start();
	    	
    	}
    	catch(ConnectException e)
    	{
    		System.out.println("Connection refused.");
    		jLabel1.setText("Connection refused.");
    	}
    	catch(Exception e)
    	{
    		System.err.print(e);
    	}
    	
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login_gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Login_gui dialog = new Login_gui(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
        
    }
    
    // Variables declaration - do not modify
    private javax.swing.JTextField Address;
    private javax.swing.JButton Login_Button;
    private javax.swing.JPasswordField Password;
    private javax.swing.JTextField Username;
    //private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration
}

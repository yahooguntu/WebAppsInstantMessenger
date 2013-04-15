import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnectionThread extends Thread
{
	private ThreadedIMServer server;
	private Socket connection;
	
	public ServerConnectionThread(ThreadedIMServer server, Socket connection)
	{
		super();
		
		this.server = server;
		this.connection = connection;
	}
	
	public void run()
	{
		String user = null;
		System.out.println("Thread spun off!");
		try
		{
			ServerConnectionThread currThread = (ServerConnectionThread) Thread.currentThread();
			Socket socket = currThread.getSocket();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			String input = in.readLine();
			while (input != null)
			{
				System.out.println("Received value from " + socket.getInetAddress() + ": " + input);
				
				int msgCode = Integer.parseInt(input.substring(0, input.indexOf(" ")));
				
				//add user
	            if(msgCode == 0)
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,splitLoc);
	                String pass = input.substring(splitLoc);
	                //TODO:add user
	            }            
	            //Logon
	            else if(msgCode == 1)
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,splitLoc);
	                String pass = input.substring(splitLoc);
	                //TODO:run logon check
	            }
	            //Logoff
	            else if(msgCode == 2)
	            {
	                String from = input.substring(2);
	                //TODO:logoff user
	            }
	            //Outgoing/Incoming Message
	            else if(msgCode == 3)
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,(splitLoc-2));
	                String Reciptiant = input.substring(splitLoc, (input.indexOf(" ", splitLoc)-splitLoc));
	                //TODO:get recipiant port
	                //TODO:send to resipiant
	            }
	            //logged on success
	            else if(msgCode == 6)
	            {
	                String from = input.substring(2);
	                //TODO:show main screen
	            }
	            //logon faild
	            else if(msgCode == 7)
	            {
	                String from = input.substring(2);
	                //TODO:show logon screen with error
	            }            
	            //typing
	            else if(msgCode == 10)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc);
	                //TODO:get recipiant port
	                //TODO: display text saying that other user is typing
	            }
	            //entered text
	            else if(msgCode == 11)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc);
	                //TODO:get recipiant port
	                //TODO: Other user has enterd data
	            }
	            //message failed
	            else if(msgCode == 12)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc, (input.indexOf(" ", splitLoc)-splitLoc));
	                //TODO:get sender port
	                //TODO: return error message to sender
	            }
	            /*
	             * Not sure about these two...
	             */
	            //Set Profile
	            else if(msgCode == 13)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                int secondSplit = input.indexOf(" ", splitLoc);
	                String password = input.substring(splitLoc, secondSplit-splitLoc);
	                String text = input.substring(secondSplit);
	                //TODO: Update Profile
	            }
	            //Get Profile
	            else if(msgCode == 14)
	            {
	                String from = input.substring(3); 
	                //TODO: give new window with profile info
	            }
				
				input = in.readLine();
			}
			
			// do this if we haven't registered with the dispatcher
			if (user == null)
			{
				out.close();
			}
			
			in.close();
			System.out.println("Connection closed: " + socket.getInetAddress());
			server.onCloseConnection(user);
		}
		catch (Exception e)
		{
			System.err.println("Exception thrown!");
			e.printStackTrace();
			server.userSignOff(user);
		}
	}
	
	public ThreadedIMServer getServer()
	{
		return server;
	}
	
	public Socket getSocket()
	{
		return connection;
	}
}

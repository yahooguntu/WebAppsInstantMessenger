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
		System.out.println("Thread spun off!");
		try
		{
			ServerConnectionThread currThread = (ServerConnectionThread) Thread.currentThread();
			Socket socket = currThread.getSocket();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			String user = null;
			
			String input = in.readLine();
			while (input != null)
			{
				System.out.println("Received value from " + socket.getInetAddress() + ": " + input);
				
				//calls methods in this class to notify other clients of events
				//add user
	            if(input.substring(0, 1).compareTo("0") == 0 )
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,splitLoc);
	                String pass = input.substring(splitLoc);
	                //TODO:add user
	            }            
	            //Logon
	            else if(input.substring(0, 1).compareTo("1") == 0 && (input.substring(0, 2).compareTo("11") != 0 || input.substring(0, 2).compareTo("12") != 0 || input.substring(0, 2).compareTo("13") != 0 || input.substring(0, 2).compareTo("14") != 0))
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,splitLoc);
	                String pass = input.substring(splitLoc);
	                //TODO:run logon check
	            }
	            //Logoff
	            else if(input.substring(0,1).compareTo("2") == 0 )
	            {
	                String from = input.substring(2);
	                //TODO:logoff user
	            }
	            //Outgoing/Incoming Message
	            else if(input.substring(0,1).compareTo("3") == 0)
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,(splitLoc-2));
	                String Reciptiant = input.substring(splitLoc, (input.indexOf(" ", splitLoc)-splitLoc));
	                //TODO:get recipiant port
	                //TODO:send to resipiant
	            }
	            //buddy logged on 
	            else if(input.substring(0,1).compareTo("4") == 0)
	            {
	                String from = input.substring(2);
	                //TODO:get recipiant port
	                //TODO:add buddy to buddy list
	            }
	            //buddy logged off
	            else if(input.substring(0,1).compareTo("5") == 0)
	            {
	                String from = input.substring(2);
	                //TODO:get recipiant port
	                //TODO:remmove buddy from buddy list
	            }
	            //logged on success
	            else if(input.substring(0,1).compareTo("6") == 0)
	            {
	                String from = input.substring(2);
	                //TODO:show main screen
	            }
	            //logon faild
	            else if(input.substring(0,1).compareTo("7") == 0)
	            {
	                String from = input.substring(2);
	                //TODO:show logon screen with error
	            }            
	            //add buddy
	            else if(input.substring(0,1).compareTo("8") == 0)
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc);
	                //TODO:get recipiant port
	                //TODO:add buddy to buddy list
	            }
	            //remove buddy
	            else if(input.substring(0,1).compareTo("9") == 0)
	            {
	                int splitLoc = input.indexOf(" ", 2);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc);
	                //TODO:get recipiant port
	                //TODO:remove buddy from buddy list
	            }
	            //typing
	            else if(input.substring(0,2).compareTo("10") == 0)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc);
	                //TODO:get recipiant port
	                //TODO: display text saying that other user is typing
	            }
	            //entered text
	            else if(input.substring(0,2).compareTo("11") == 0)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc);
	                //TODO:get recipiant port
	                //TODO: Other user has enterd data
	            }
	            //message failed
	            else if(input.substring(0,2).compareTo("12") == 0)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                String Reciptiant = input.substring(splitLoc, (input.indexOf(" ", splitLoc)-splitLoc));
	                //TODO:get sender port
	                //TODO: return error message to sender
	            }
	            //Set Profile
	            else if(input.substring(0,2).compareTo("13") == 0)
	            {
	                int splitLoc = input.indexOf(" ", 3);
	                String from = input.substring(2,splitLoc);
	                int secondSplit = input.indexOf(" ", splitLoc);
	                String password = input.substring(splitLoc, secondSplit-splitLoc);
	                String text = input.substring(secondSplit);
	                //TODO: Update Profile
	            }
	            //Get Profile
	            else if(input.substring(0,2).compareTo("14") == 0)
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
			System.exit(1);
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

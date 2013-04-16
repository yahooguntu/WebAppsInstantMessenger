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
		String name = connection.getInetAddress().toString();
		name = name.substring(name.indexOf("/")+1);
		this.setName(name);
	}

	public void run()
	{
		String user = null;
		BufferedReader in = null;
		PrintWriter out = null;
		System.out.println("Thread spun off for " + getName());
		try
		{
			ServerConnectionThread currThread = (ServerConnectionThread) Thread.currentThread();
			Socket socket = currThread.getSocket();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());

			String input = in.readLine();
			//TODO this needs a timeout of some sort
			while (true)
			{
				if (input == null)
				{
					System.out.println("Client disconnect: " + getName());
					break;
				}
				
				System.out.println("Received value from " + getName() + ": " + input);

				int msgCode = Integer.parseInt(input.substring(0, input.indexOf(" ")));
				String msgBody = input.substring(input.indexOf(" ") + 1);

				//add user
				//TODO
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
					String msgUsername = msgBody.substring(0, msgBody.indexOf(" ")).toLowerCase();
					String msgPassword = msgBody.substring(msgBody.indexOf(" ") + 1);
					if (server.userSignOn(msgUsername, msgPassword, out))
					{
						user = msgUsername;
						out.write("6 " + msgUsername + "\n");
						out.flush();
						System.out.println("User signed on: " + msgUsername);
					}
					else
					{
						out.write("7 " + msgUsername + "\n");
						out.flush();
						System.out.println("Incorrect password for user " + msgUsername);
					}
				}
				//Logoff
				//TODO
				else if(msgCode == 2)
				{
					String from = input.substring(2);
					//TODO:logoff user
				}
				//Outgoing/Incoming Message
				//TODO
				else if(msgCode == 3)
				{
					int splitLoc = input.indexOf(" ", 2);
					String from = input.substring(2,(splitLoc-2));
					String Reciptiant = input.substring(splitLoc, (input.indexOf(" ", splitLoc)-splitLoc));
					//TODO:get recipiant port
					//TODO:send to resipiant
				}
				//logged on success
				//TODO
				else if(msgCode == 6)
				{
					String from = input.substring(2);
					//TODO:show main screen
				}
				//logon faild
				//TODO
				else if(msgCode == 7)
				{
					String from = input.substring(2);
					//TODO:show logon screen with error
				}			
				//typing
				//TODO
				else if(msgCode == 10)
				{
					int splitLoc = input.indexOf(" ", 3);
					String from = input.substring(2,splitLoc);
					String Reciptiant = input.substring(splitLoc);
					//TODO:get recipiant port
					//TODO: display text saying that other user is typing
				}
				//entered text
				//TODO
				else if(msgCode == 11)
				{
					int splitLoc = input.indexOf(" ", 3);
					String from = input.substring(2,splitLoc);
					String Reciptiant = input.substring(splitLoc);
					//TODO:get recipiant port
					//TODO: Other user has enterd data
				}
				//message failed
				//TODO
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
				//TODO
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
				//TODO
				else if(msgCode == 14)
				{
					String from = input.substring(3); 
					//TODO: give new window with profile info
				}

				input = in.readLine();
			}
		}
		catch (StringIndexOutOfBoundsException e)
		{
			System.out.println("Invalid message format from " + getName());
		}
		catch (Exception e)
		{
			System.err.println("Exception thrown!");
			e.printStackTrace();
		}
		finally
		{
			// kill off this thread and close its resources
			System.out.println("Thread suicide: " + getName());
			if (user != null)
				server.userSignOff(user);

			server.onCloseConnection();

			try
			{
				connection.close();
			}
			catch (Exception e){}
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

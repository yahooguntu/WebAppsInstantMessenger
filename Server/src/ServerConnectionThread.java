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
				
				input = input.toLowerCase();

				int msgCode = Integer.parseInt(input.substring(0, input.indexOf(" ")));
				String msgBody = input.substring(input.indexOf(" ") + 1);

				//add user
				if(msgCode == 0)
				{
					//sign out if they're already signed in
					if (user != null)
					{
						server.userSignOff(user);
						server.queueEventDispatch(new Event(2, user));
						user = null;
					}
					
					int splitLoc = input.indexOf(" ");
					String msgUsername = input.substring(0, splitLoc);
					String msgPassword = input.substring(splitLoc + 1);
					server.addUser(msgUsername, msgPassword);
					
					//do a login
					if (server.userSignOn(msgUsername, msgPassword, out))
					{
						user = msgUsername;
						out.write("6 " + msgUsername + "\n");
						out.flush();
						server.queueEventDispatch(new Event(1, user));
						System.out.println("User signed on: " + msgUsername);
					}
					else
					{
						out.write("7 " + msgUsername + "\n");
						out.flush();
						System.out.println("Incorrect password for user " + msgUsername);
					}
				}
				//Logon
				else if(msgCode == 1)
				{
					//sign out if they're already signed in
					if (user != null)
					{
						server.userSignOff(user);
						server.queueEventDispatch(new Event(2, user));
						user = null;
					}
					
					String msgUsername = msgBody.substring(0, msgBody.indexOf(" ")).toLowerCase();
					String msgPassword = msgBody.substring(msgBody.indexOf(" ") + 1);
					if (server.userSignOn(msgUsername, msgPassword, out))
					{
						user = msgUsername;
						out.write("6 " + msgUsername + "\n");
						out.flush();
						server.queueEventDispatch(new Event(1, user));
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
				else if(msgCode == 2 && user != null && user.equals(msgBody))
				{
					server.userSignOff(user);
					server.queueEventDispatch(new Event(2, user));
					user = null;
				}
				//Outgoing/Incoming Message
				else if(msgCode == 3 && user != null)
				{
					int splitLoc = msgBody.indexOf(" ");
					int splitLoc2 = msgBody.indexOf(" ", splitLoc);
					
					if (splitLoc == -1 || splitLoc2 == -1)
					{
						out.write("12" + msgBody);
						out.flush();
					}
					else
					{
						String sender = msgBody.substring(0, splitLoc).toLowerCase();
						String recipient = msgBody.substring(splitLoc + 1, splitLoc2).toLowerCase();
						String message = msgBody.substring(splitLoc2 + 1);
						if (!user.equals(sender))
						{
							out.write("12" + msgBody);
							out.flush();
						}
						else
							server.queueEventDispatch(new Event(3, sender, recipient, message));
					}
				}
				//typing
				//TODO
				else if(msgCode == 10 && user != null)
				{
					int splitLoc = input.indexOf(" ", 3);
					String from = input.substring(2,splitLoc);
					String Reciptiant = input.substring(splitLoc);
					//TODO:get recipiant port
					//TODO: display text saying that other user is typing
				}
				//entered text
				//TODO
				else if(msgCode == 11 && user != null)
				{
					int splitLoc = input.indexOf(" ", 3);
					String from = input.substring(2,splitLoc);
					String Reciptiant = input.substring(splitLoc);
					//TODO:get recipiant port
					//TODO: Other user has enterd data
				}
				//message failed
				//TODO
				else if(msgCode == 12 && user != null)
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

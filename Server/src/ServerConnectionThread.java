import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import data.DataAccess;
import data.User;

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
		String input = null;
		System.out.println("Thread spun off for " + getName());
		try
		{
			ServerConnectionThread currThread = (ServerConnectionThread) Thread.currentThread();
			Socket socket = currThread.getSocket();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());

			input = in.readLine();
			//TODO this needs a timeout of some sort
			while (true)
			{
				try
				{ // this one is for illegal messages
					//die if the connection is closed
					if (input == null)
					{
						break;
					}

					System.out.println("Message received: " + input);

					int msgCode = Integer.parseInt(input.substring(0, input.indexOf(" ")));
					String msgBody = input.substring(input.indexOf(" ") + 1);
					String[] body = msgBody.split("[ \n]");

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

						int splitLoc = msgBody.indexOf(" ");
						String msgUsername = msgBody.substring(0, splitLoc);
						String msgPassword = msgBody.substring(splitLoc + 1);
						User toAdd = new User(java.net.URLEncoder.encode(msgUsername, "ASCII"), DataAccess.hash(msgPassword, DataAccess.generateSalt(msgUsername), 100000));
						if (!server.addUser(toAdd))
						{
							out.write("7 " + msgUsername + "\n");
							out.flush();
							System.out.println("Username taken: " + msgUsername);
						}
						else if (!server.isLoggedOn(msgUsername) && server.userSignOn(msgUsername, msgPassword, out))
							//do a login
						{
							user = msgUsername;
							out.write("6 " + msgUsername + "\n");
							out.flush();
							server.queueEventDispatch(new Event(1, user));
						}
						else
						{
							out.write("7 " + msgUsername + "\n");
							out.flush();
							System.out.println("Incorrect password or already signed in: " + msgUsername);
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
						if (!server.isLoggedOn(msgUsername) && server.userSignOn(msgUsername, msgPassword, out))
						{
							user = msgUsername;
							out.write("6 " + msgUsername + "\n");
							out.flush();
							server.queueEventDispatch(new Event(1, user));
						}
						else
						{
							out.write("7 " + msgUsername + "\n");
							out.flush();
							System.out.println("Incorrect password or already signed in: " + msgUsername);
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
						int splitLoc2 = msgBody.indexOf(" ", splitLoc + 1);

						if (splitLoc == -1 || splitLoc2 == -1)
						{
							out.write("12 " + msgBody + "\n");
							out.flush();
						}
						else
						{
							String sender = msgBody.substring(0, splitLoc).toLowerCase();
							String recipient = msgBody.substring(splitLoc + 1, splitLoc2).toLowerCase();
							String message = msgBody.substring(splitLoc2 + 1);
							if (!user.equals(sender))
							{
								out.write("12 " + msgBody + "\n");
								out.flush();
							}
							else
								server.queueEventDispatch(new Event(3, sender, recipient, message));
						}
					}
					//add buddy
					else if(msgCode == 8 && user != null && body.length == 2 && body[0].equals(user))
					{
						server.queueEventDispatch(new Event(8, user, body[1]));
					}
					//remove buddy
					else if(msgCode == 9 && user != null && body.length == 2 && body[0].equals(user))
					{
						server.queueEventDispatch(new Event(9, user, body[1]));
					}
					//typing
					else if(msgCode == 10 && user != null && body.length == 2 && body[0].equals(user))
					{
						server.queueEventDispatch(new Event(10, user, body[1]));
					}
					//entered text
					else if(msgCode == 11 && user != null && body.length == 2 && body[0].equals(user))
					{
						server.queueEventDispatch(new Event(11, user, body[1]));
					}
					/*
					 * Not sure about these two...
					 */
					//Set Profile
					//TODO
					else if(msgCode == 13)
					{

					}
					//Get Profile
					//TODO
					else if(msgCode == 14)
					{

					}

					input = in.readLine();
				}
				catch (StringIndexOutOfBoundsException e)
				{
					System.out.println("Invalid message format from " + getName() + ": " + input);
				}
			}
		}

		catch (SocketException e)
		{
			System.err.println("Connection reset!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			// kill off this thread and close its resources
			System.out.println("Thread suicide: " + getName());
			if (user != null)
			{
				server.userSignOff(user);
				server.queueEventDispatch(new Event(2, user));
			}

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

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

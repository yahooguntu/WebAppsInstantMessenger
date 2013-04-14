import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadedIMServer
extends BasicServer implements Runnable
{
	private BlockingQueue<Event> dispatchQueue;
	private ConcurrentHashMap<String, PrintWriter> printWriters;
	
	public ThreadedIMServer()
	{
		super(4225, 0);
		dispatchQueue = new ArrayBlockingQueue<Event>(20);
		System.out.println("Server started.");
	}

	public static void main(String[] args)
	{
		ThreadedIMServer myServer = new ThreadedIMServer();

		// monitor heartbeat, etc.
		ServerMonitorThread monitor = new ServerMonitorThread(myServer);
		monitor.start();

		// spins on ServerSocket
		myServer.start();
	}

	protected void serviceConnection(Socket connection) throws IOException
	{
		ServerConnectionThread connectThread = new ServerConnectionThread(this, connection);
		connectThread.start();
	}
	
	private boolean userSignOn(String user, PrintWriter output)
	{
		printWriters.put(user, output);
		try
		{
			dispatchQueue.put(new Event(1, user));
			return true;
		}
		catch (InterruptedException e)
		{
			output.write("7 " + user + "\n");
			System.err.println("User " + user + " failed to sign in!");
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean userSignOff(String user)
	{
		printWriters.remove(user);
		try
		{
			dispatchQueue.put(new Event(2, user));
			return true;
		}
		catch (InterruptedException e)
		{
			System.err.println("User " + user + " failed to sign out!");
			e.printStackTrace();
			return false;
		}
	}

	public void run()
	{
		
	}
	
	public void onCloseConnection(String user)
	{
		synchronized(this)
		{
			numConnections--;
		}
		
		if (user != null)
		{
			
		}
	}
}
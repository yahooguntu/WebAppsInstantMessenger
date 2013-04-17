import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadedIMServer
extends BasicServer implements Runnable
{
	private BlockingQueue<Event> dispatchQueue;
	private ConcurrentHashMap<String, PrintWriter> printWriters;
	private DataAbstractionObject dao;
	
	public ThreadedIMServer()
	{
		super(4225, 0);
		dispatchQueue = new ArrayBlockingQueue<Event>(20);
		printWriters = new ConcurrentHashMap<String, PrintWriter>();
		dao = new DataAbstractionObject();
		
		// fire up the dispatcher
		DispatcherThread dispatcher = new DispatcherThread(dispatchQueue, printWriters, dao);
		dispatcher.start();
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
	
	// queues up an event to be dispatched
	public void queueEventDispatch(Event e)
	{
		try
		{
			dispatchQueue.put(e);
		} catch (InterruptedException ex) {
			System.out.println("Failed to enter event into dispatch queue: " + e.toString());
			ex.printStackTrace();
		}
	}
	
	public boolean userSignOn(String user, String password, PrintWriter output)
	{
		if (dao.checkPassword(user, password))
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
		else
		{
			return false;
		}
	}
	
	public void userSignOff(String user)
	{
		printWriters.remove(user);
		try
		{
			dispatchQueue.put(new Event(2, user));
			System.out.println("User " + user + " has signed out.");
			printWriters.remove(user);
		}
		catch (InterruptedException e)
		{
			System.err.println("User " + user + " failed to sign out!");
			e.printStackTrace();
		}
	}
	
	public boolean addUser(String username, String password)
	{
		return dao.addUser(username, password);
	}

	/*
	 * The realm of the monitor thread!
	 */
	public void run()
	{
		System.out.println("Monitoring started.");
	}
	
	public void onCloseConnection()
	{
		synchronized(this)
		{
			numConnections--;
		}
	}
}
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
	private Connection connection;
	
	public ThreadedIMServer()
	{
		super(4225, 0);
		dispatchQueue = new ArrayBlockingQueue<Event>(20);
		printWriters = new ConcurrentHashMap<String, PrintWriter>();
		
		// database connection stuff
		try
		{
			String driverName = "com.mysql.jdbc.Driver";
			String connectionName = "jdbc:mysql://john.cedarville.edu/cs4220";
			String dbUsername = "cs4220";
			String dbPassword = "";
			
			Class.forName(driverName).newInstance();
			connection = DriverManager.getConnection(connectionName, dbUsername, dbPassword);
		}
		catch (Exception e)
		{
			System.err.println("Database initialization exception!");
			e.printStackTrace();
			System.exit(2);
		}
		
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
	
	public boolean userSignOn(String user, String password, PrintWriter output)
	{
		if (checkPassword(user, password))
		{
			System.out.println("User is " + user + ", output is " + output.toString());
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
	
	/*
	 * Works!
	 */
	private boolean checkPassword(String user, String password)
	{
		try
		{
			//get the hash from the db and protect against sql injection
			ResultSet result;
			result = connection.createStatement().executeQuery("SELECT * FROM `burst_ppl_User` WHERE `username` = '" + java.net.URLEncoder.encode(user, "ASCII") + "'");
			
			result.next();
			String dbHash = result.getString("hash");
			String hash = hash(password, dbHash.substring(0, 64), 100000);
			
			if (dbHash.equals(hash))
				return true;
		}
		catch (SQLException e)
		{
			return false;
		}
		catch (Exception e)
		{
			System.err.println("Something went horribly wrong!");
			e.printStackTrace();
			System.exit(3);
		}
		
		return false;
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
	
	public static String hash(String password, String salt, int iterations)
	{
		password = salt + password;
		
		for (int i = 0; i < iterations; i++)
		{
			password = hash(password);
		}
		
		return salt + password;
	}
	
    /**
     * Copied from StackExchange.
     */
    public static String hash(String password)
    {
            try
            {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(password.getBytes());

                    byte byteData[] = md.digest();

                    //convert the byte to hex format method 1
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < byteData.length; i++) {
                            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    return sb.toString();
            }
            catch (Exception e)
            {
                    e.printStackTrace();
            }
            return null;
    }
}
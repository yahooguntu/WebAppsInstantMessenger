import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadedIMServer
extends BasicServer implements Runnable
{
	public ThreadedIMServer()
	{
		super(4225, 0);
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
				
				input = in.readLine();
			}
			
			System.out.println("Connection closed: " + socket.getInetAddress());
			onCloseConnection();
		}
		catch (Exception e)
		{
			System.err.println("Exception thrown!");
			System.exit(1);
		}
	}
	
	private void onCloseConnection()
	{
		synchronized(this)
		{
			numConnections--;
		}
	}
}
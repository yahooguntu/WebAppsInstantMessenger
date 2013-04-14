import java.io.*;
import java.net.*;

public class Server implements Runnable
{

	private int listenPort;
	private int maxConnections;
	protected int numConnections;

	public Server()
	{
		this (4220, 0);
	}
	public Server(int port, int numConnects)
	{
		listenPort = port;
		maxConnections = numConnects;
		numConnections = 0;
	}

	public void start ()
	{
		System.out.println("Server started!");
		try
		{
			@SuppressWarnings("resource")
			ServerSocket listenSocket = new ServerSocket (listenPort);

			while (true)
			{
				Socket newConnect;
				if ( (maxConnections == 0) || (numConnections <= maxConnections) )
				{
					newConnect = listenSocket.accept();
					System.out.println("Accepted connection from "+newConnect.getInetAddress());
					System.out.flush();
					numConnections++;
					serviceConnection (newConnect);
				}
			}
		}
		catch (IOException ioe )
		{
			System.out.println("Unable to listen on port "+listenPort);
			ioe.printStackTrace();
		}
	}
	protected void serviceConnection (Socket connection) throws IOException
	{

		BufferedReader in = new BufferedReader
				(new InputStreamReader(connection.getInputStream()));

		PrintWriter out = new PrintWriter
				(new PrintWriter(connection.getOutputStream(), true));

		out.println("Hello from your server");
		out.flush();

		System.out.println("Closing connection");
		System.out.flush();

		connection.close();
		numConnections--;
	}
	public static void main (String [] args)
	{
		Server myServer = new Server();
		myServer.start();

		// monitor heartbeat, etc.
		ServerMonitorThread monitor = new ServerMonitorThread(myServer);
		monitor.start();

		// spins on ServerSocket
		myServer.start();
	}

	public void run()
	{
		ServerConnectionThread thisThread = (ServerConnectionThread) Thread.currentThread();
		Socket thisSocket = thisThread.getSocket();
		BufferedReader in;
		PrintWriter out;
		String user = null;
	}
}
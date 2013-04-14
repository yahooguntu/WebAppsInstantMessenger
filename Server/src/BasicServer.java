import java.io.*;
import java.net.*;

public class BasicServer
{
	private int listenPort;
	private int maxConnections;
	protected int numConnections;

	public BasicServer()
	{
		this (4220, 0);
	}
	
	public BasicServer(int port, int numConnects)
	{
		listenPort = port;
		maxConnections = numConnects;
		numConnections = 0;
	}

	public void start ()
	{

		try
		{
			ServerSocket listenSocket = new ServerSocket (listenPort);

			while (true)
			{
				Socket newConnect;
				if ( (maxConnections == 0) || (numConnections <= maxConnections) )
				{
					newConnect = listenSocket.accept();
					System.out.println("accepted "+newConnect.getInetAddress());
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
		BasicServer myServer = new BasicServer();
		myServer.start();
	}
}
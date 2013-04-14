import java.io.BufferedReader;
import java.io.IOException;
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
		ServerConnectionThread thisThread = (ServerConnectionThread) Thread.currentThread();
		Socket thisSocket = thisThread.getSocket();
		BufferedReader in;
		PrintWriter out;
		String user = null;

		/**********************
	a bunch of code deleted; this is where
	you handle the handshake with the client, and then
	put your readline busy wait
		 **************************/
	}
}
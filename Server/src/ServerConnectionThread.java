import java.net.Socket;

public class ServerConnectionThread extends Thread
{
	private ThreadedIMServer server;
	private Socket connection;
	
	public ServerConnectionThread(ThreadedIMServer server, Socket connection)
	{
		this.server = server;
		this.connection = connection;
	}
	
	public void run()
	{
		
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

import java.net.Socket;

public class ServerConnectionThread extends Thread
{
	private ThreadedIMServer server;
	private Socket connection;
	
	public ServerConnectionThread(ThreadedIMServer server, Socket connection)
	{
		super((Runnable) server);
		
		this.server = server;
		this.connection = connection;
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

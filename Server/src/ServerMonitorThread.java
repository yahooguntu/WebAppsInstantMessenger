
public class ServerMonitorThread extends Thread
{
	private ThreadedIMServer server;
	
	public ServerMonitorThread(BasicServer server)
	{
		super();
	}
	
	public void run()
	{
		System.out.println("monitoring on this thread");
	}
}

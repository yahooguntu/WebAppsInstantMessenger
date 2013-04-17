
public class DispatcherThread extends Thread
{
	ThreadedIMServer server;
	
	public DispatcherThread(ThreadedIMServer s)
	{
		super();
		server = s;
	}
	
	public void run()
	{
		System.out.println("Dispatcher thread is up!");
	}
}

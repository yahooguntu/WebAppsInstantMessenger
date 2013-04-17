import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


public class DispatcherThread extends Thread
{
	private BlockingQueue<Event> queue;
	private ConcurrentHashMap<String, PrintWriter> printWriters;
	private DataAbstractionObject dao;

	public DispatcherThread(BlockingQueue<Event> queue, ConcurrentHashMap<String, PrintWriter> printWriters, DataAbstractionObject dao)
	{
		super();
		this.queue = queue;
		this.printWriters = printWriters;
		this.dao = dao;
	}

	public void run()
	{
		System.out.println("Dispatcher thread is up!");

		while (true)
		{
			try
			{
				Event e = queue.take();

				switch (e.eventCode)
				{
				//logon
				case 1:
					System.out.println("Dispatcher thread: Logon by " + e.msg1);
					break;

					//logoff
				case 2:
					System.out.println("Dispatcher thread: Logoff by " + e.msg1);
					break;

					//message
				case 3:
					System.out.println("Dispatcher thread: Message from " + e.msg1 + " to " + e.msg2 + ": " + e.msg3);
					break;

					//typing
				case 10:
					System.out.println("Dispatcher thread: " + e.msg1 + " is typing");
					break;

					//entered text
				case 11:
					System.out.println("Dispatcher thread: " + e.msg1 + " has entered text");
					break;

				default:
					System.out.println("Dispatcher thread: Unknown eventCode " + e.eventCode);
					break;
				}
			}
			catch (InterruptedException e)
			{
				System.out.println("Dispatcher thread: InterruptedException!");
				e.printStackTrace();
			}
			catch (NullPointerException e)
			{
				System.out.println("Dispatcher thread: incorrect number of arguments!");
			}
		}

	}
}

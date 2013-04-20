import java.io.PrintWriter;
import java.util.ArrayList;
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
				PrintWriter destination = null;
				
				switch (e.eventCode)
				{
					//logon
				case 1:
					System.out.println("Dispatcher thread: Logon by " + e.msg1);
					sendToBuddies(e.msg1, "4 " + e.msg1 + "\n");
					sendInitialBuddies(e.msg1);
					break;

					//logoff
				case 2:
					System.out.println("Dispatcher thread: Logoff by " + e.msg1);
					sendToBuddies(e.msg1, "5 " + e.msg1 + "\n");
					break;

					//message
				case 3:
					destination = printWriters.get(e.msg2);
					if (destination != null)
					{
						System.out.println("Dispatcher thread: Message from " + e.msg1 + " to " + e.msg2 + ": " + e.msg3.substring(0, e.msg3.length()) + "\n");
						destination.write("3 " + e.msg1 + " " + e.msg2 + " " + e.msg3 + "\n");
						destination.flush();
					}
					else
					{
						System.out.println("Dispatcher thread: Message could not be sent from " + e.msg1 + " to " + e.msg2 + "!");
						destination = printWriters.get(e.msg1);
						if (destination != null)
						{
							destination.write("12 " + e.msg1 + " " + e.msg2 + " " + e.msg3 + "\n");
							destination.flush();
						}
					}
					break;

					//typing
				case 10:
					destination = printWriters.get(e.msg2);
					if (destination != null)
					{
						System.out.println("Dispatcher thread: " + e.msg1 + " is typing at " + e.msg2 + "\n");
						destination.write("10 " + e.msg1 + " " + e.msg2 + "\n");
						destination.flush();
					}
					else
					{
						System.out.println("Dispatcher thread: " + e.msg2 + " isn't online!");
					}
					break;

					//entered text
				case 11:
					destination = printWriters.get(e.msg2);
					if (destination != null)
					{
						System.out.println("Dispatcher thread: " + e.msg1 + " has entered text for " + e.msg2 + "\n");
						destination.write("10 " + e.msg1 + " " + e.msg2 + "\n");
						destination.flush();
					}
					else
					{
						System.err.println("Dispatcher thread (switch): No PrintWriter found for " + e.msg2 + "!");
					}
					break;

				default:
					System.err.println("Dispatcher thread: Unknown eventCode: " + e.toString());
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
	
	private void sendToBuddies(String user, String msg)
	{
		ArrayList<String> buddies = dao.getBuddies(user);
		for (String b : buddies)
		{
			PrintWriter w = printWriters.get(b);
			if (w != null)
			{
				w.write(msg);
				w.flush();
			}
		}
	}
	
	private void sendInitialBuddies(String user)
	{
		ArrayList<String> buddies = dao.getBuddies(user);
		PrintWriter userStream = printWriters.get(user);
		
		if (userStream == null)
		{
			System.err.println("Dispatcher thread (sendInitialBuddies): No Printwriter found for " + user + "!");
			return;
		}
		
		for (String b : buddies)
		{
			if (printWriters.containsKey(b))
				userStream.write("4 " + b + "\n");
			userStream.flush();
		}
	}
}

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import data.DataAccess;


public class DispatcherThread extends Thread
{
	private BlockingQueue<Event> queue;
	private ConcurrentHashMap<String, PrintWriter> printWriters;
	private DataAccess dao;

	public DispatcherThread(BlockingQueue<Event> queue, ConcurrentHashMap<String, PrintWriter> printWriters, DataAccess dao)
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
					sendMessage(e.msg1, "4 " + e.msg1 + "\n", dao.getFollowers(e.msg1));
					sendInitialBuddies(e.msg1);
					break;

					//logoff
				case 2:
					System.out.println("Dispatcher thread: Logoff by " + e.msg1);
					sendMessage(e.msg1, "5 " + e.msg1 + "\n", dao.getFollowers(e.msg1));
					break;

					//message
				case 3:
					destination = printWriters.get(e.msg2);
					if (destination != null)
					{
						System.out.println("Dispatcher thread: Message from " + e.msg1 + " to " + e.msg2 + ": " + e.msg3.substring(0, e.msg3.length()));
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
					
					//add buddy
				case 8:
					if (dao.addBuddy(e.msg1, e.msg2))
						System.out.println("Dispatcher thread: " + e.msg1 + " is now buddies with " + e.msg2);
					else
						System.out.println("Dispatcher thread: " + e.msg1 + " failed to add buddy: " + e.msg2);
					destination = printWriters.get(e.msg2);
					if (destination != null)
					{
						destination.write("4 " + e.msg1 + "\n");
						destination.flush();
					}
					break;
					
					//remove buddy
				case 9:
					if (dao.removeBuddy(e.msg1, e.msg2))
						System.out.println("Dispatcher thread: " + e.msg1 + " is no longer buddies with " + e.msg2);
					destination = printWriters.get(e.msg2);
					if (destination != null)
					{
						destination.write("5 " + e.msg1 + "\n");
						destination.flush();
					}
					break;

					//typing
				case 10:
					destination = printWriters.get(e.msg2);
					if (destination != null)
					{
						System.out.println("Dispatcher thread: " + e.msg1 + " is typing at " + e.msg2);
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
						destination.write("11 " + e.msg1 + " " + e.msg2 + "\n");
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
	
	private void sendMessage(String user, String msg, ArrayList<String> recipients)
	{
		for (String b : recipients)
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

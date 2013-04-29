import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.event.def.OnLockVisitor;

import data.Buddy;
import data.DataAccess;

public class DispatcherThread extends Thread
{
	private BlockingQueue<Event> queue;
	private ConcurrentHashMap<String, PrintWriter> printWriters;
	private DataAccess dao;
	private ArrayList<String> usersOnline;

	public DispatcherThread(BlockingQueue<Event> queue, ConcurrentHashMap<String, PrintWriter> printWriters, DataAccess dao)
	{
		super();
		this.queue = queue;
		this.printWriters = printWriters;
		this.dao = dao;
		usersOnline = new ArrayList<String>();
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
					usersOnline.add(e.msg1);
					sendBuddyList(e.msg1);
					sendToAll(e.msg1, "4 " + e.msg1 + "\n", "15 " + e.msg1 + "\n");
					break;

					//logoff
				case 2:
					System.out.println("Dispatcher thread: Logoff by " + e.msg1);
					usersOnline.remove(e.msg1);
					sendToAll(e.msg1, "5 " + e.msg1 + "\n", "16 " + e.msg1 + "\n");
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
					destination = printWriters.get(e.msg1);
					if (destination != null && printWriters.containsKey(e.msg2))
					{
						destination.write("4 " + e.msg2 + "\n");
						destination.flush();
					}
					break;
					
					//remove buddy
				case 9:
					if (dao.removeBuddy(e.msg1, e.msg2))
						System.out.println("Dispatcher thread: " + e.msg1 + " is no longer buddies with " + e.msg2);
					destination = printWriters.get(e.msg1);
					if (destination != null && printWriters.containsKey(e.msg2))
					{
						destination.write("5 " + e.msg2 + "\n");
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
	
	private void sendMessage(String user, String msg, List<Buddy> recipients)
	{
		for (Buddy b : recipients)
		{
			PrintWriter w = printWriters.get(b.getUsername());
			if (w != null)
			{
				w.write(msg);
				w.flush();
			}
		}
	}
	
	private void sendBuddyList(String user)
	{
		List<Buddy> buddies = dao.getBuddies(user);
		PrintWriter userStream = printWriters.get(user);
		
		if (userStream == null)
		{
			System.err.println("Dispatcher thread (sendInitialBuddies): No Printwriter found for " + user + "!");
			return;
		}
		
		ArrayList<String> noSend = new ArrayList<String>(buddies.size());
		for (Buddy b : buddies)
		{
			if (printWriters.containsKey(b.getBuddyname()))
				userStream.write("4 " + b.getBuddyname() + "\n");
			
			noSend.add(b.getBuddyname());
		}
		
		for (String u : usersOnline)
		{
			if (!noSend.contains(u) && !u.equals(user))
				userStream.write("15 " + u + "\n");
		}
		userStream.flush();
	}
	
	private void sendToAll(String user, String forBuddy, String forNon)
	{
		System.out.println("USER: " + user);
		System.out.println("FORBUDDY: " + forBuddy);
		System.out.println("FORNON: " + forNon);
		List<Buddy> b = dao.getFollowers(user);
		ArrayList<String> buddies = new ArrayList<String>(b.size());
		System.out.print("BUDDYLIST OF USER:");
		for (Buddy i : b)
		{
			buddies.add(i.getUsername());
			System.out.print(" " + i.getUsername());
		}
		

		for (String u : usersOnline)
		{
			PrintWriter pw = printWriters.get(u);
			if (pw != null && !u.equals(user))
			{
				if (buddies.contains(u))
				{
					System.out.println(u + " IS A FRIEND OF " + user);
					pw.write(forBuddy);
				}
				else
				{
					System.out.println(u + " IS NOT A FRIEND OF " + user);
					pw.write(forNon);
				}
				pw.flush();
			}
		}
	}
	
	@Deprecated
	private void sendToAllBut(String msg, String user)
	{
		PrintWriter[] pwArr = printWriters.values().toArray(new PrintWriter[0]);
		String[] pwVal = printWriters.keySet().toArray(new String[0]);
		
		for (int i = 0; i < pwArr.length; i++)
		{
			if (!pwVal[i].equals(user))
			{
				PrintWriter w = pwArr[i];
				w.write(msg);
				w.flush();
			}
		}
	}
}

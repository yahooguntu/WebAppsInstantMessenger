import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;


public class ListenerThread extends Thread
{
	private BufferedReader reader;
	private Buddy_gui gui;
	
	public ListenerThread(Buddy_gui g, BufferedReader r)
	{
		super();
		reader = r;
		gui = g;
	}
	
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				String input = reader.readLine();
				System.out.println("Message received: " + input);
				
				int msgCode = Integer.parseInt(input.substring(0, input.indexOf(" ")));
				String msgBody = input.substring(input.indexOf(" ") + 1);
				String[] body = msgBody.split("[ \n]");
				
				// buddy online
				if (msgCode == 4 && body.length == 1)
				{
					Runnable r = new ParameterizedRunnable(body[0]) { public void run() { gui.addToBuddyList(s); } };
					SwingUtilities.invokeLater(r);
				}
				// buddy offline
				else if (msgCode == 5 && body.length == 1)
				{
					Runnable r = new ParameterizedRunnable(body[0]) { public void run() { gui.removeFromBuddyList(s); } };
					SwingUtilities.invokeLater(r);
				}
				// regular user online
				else if (msgCode == 15 && body.length == 1)
				{
					Runnable r = new ParameterizedRunnable(body[0]) { public void run() { gui.addToOnlineList(s); } };
					SwingUtilities.invokeLater(r);
				}
				// regular user offline
				else if (msgCode == 16 && body.length == 1)
				{
					Runnable r = new ParameterizedRunnable(body[0]) { public void run() { gui.removeFromOnlineList(s); } };
					SwingUtilities.invokeLater(r);
				}
				//passes the message to the buddy gui
				else if(msgCode == 3)
				{
					Runnable r = new ParameterizedRunnable(msgBody) { public void run() { gui.pass(s); } };
					SwingUtilities.invokeLater(r);					
				}
				else
				{
					System.out.println("Unsupported message format: " + input);
				}
				
			}
		}
		catch (IOException e)
		{
			System.out.println("IO exception thrown!");
		}
		catch (NullPointerException e)
		{
			System.out.println("Connection reset!");
			System.exit(1);
		}
		
		System.out.println("Listener thread died!");
	}
	
	/*
	 * Super API restrictions avoider.
	 */
	public abstract class ParameterizedRunnable implements Runnable
	{
		String s;
		public ParameterizedRunnable(String s)
		{
			this.s = s;
		}
		
		@Override
		abstract public void run();
	}
}

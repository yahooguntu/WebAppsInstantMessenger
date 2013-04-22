import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class CLIClient implements Runnable
{
	private static String hostname = "localhost";
	private static int portNum = 4225;
	private Socket connection;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public static void main(String[] args)
	{
		CLIClient c = new CLIClient();
		
		//ChatWindow w = new ChatWindow("me");
	}
	
	public CLIClient()
	{
		try
		{
			connection = new Socket(hostname, portNum);
			System.out.println("Bound to socket on port " + portNum);
			writer = new PrintWriter(connection.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			Thread readerThread = new Thread(this);
			readerThread.start();
			
			loop();
		}
		catch (Exception e)
		{
			System.err.println("Failed to open socket!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void loop()
	{
		System.out.println("Enter message:");
		Scanner s = new Scanner(System.in);
		s.useDelimiter("\n");
		while (true)
		{
			writer.write(s.next() + "\n");
			writer.flush();
			System.out.println("Enter message:");
		}
	}
	
	public void run()
	{
		BufferedReader in = null;
		
		try
		{
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String input = in.readLine();
			while (true)
			{
				if (input == null)
					break;
				
				System.out.println("Message received: " + input);
				
				input = in.readLine();
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception thrown!");
			e.printStackTrace();
		}
		
		System.out.println("Thread suicide: reader thread");
	}
}

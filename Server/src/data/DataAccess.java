package data;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;

public class DataAccess
{
	private static SessionFactory factory;
	private Connection connection;
	
	public static void main(String[] args)
	{
		DataAccess dao = new DataAccess();
		
		dao.test();
	}
	
	private void demo()
	{
		List list = getEverything();
		for (Object o : list)
		{
			System.out.println(o.toString());
		}
	}
	
	public synchronized List getEverything()
	{
		Session session = factory.openSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			
			//this is NOT SQL
			//its HQL
			List buddies = session.createQuery("FROM java.lang.Object").list();
			
			tx.commit();
			return buddies;
		}
		catch (HibernateException e)
		{
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		} finally {
			session.close();
		}
		return null;
	}
	
	public DataAccess()
	{
		//hibernate stuff
		try
		{
			factory = new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex)
		{
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		// database connection stuff
		try
		{
			String driverName = "com.mysql.jdbc.Driver";
			String connectionName = "jdbc:mysql://john.cedarville.edu/cs4220";
			String dbUsername = "cs4220";
			String dbPassword = "";

			Class.forName(driverName).newInstance();
			connection = DriverManager.getConnection(connectionName, dbUsername, dbPassword);
		}
		catch (Exception e)
		{
			System.err.println("Database initialization exception!");
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	/* BOILERPLATE CODE
	Session session = factory.openSession();
	//Transaction tx = null;
	try
	{
		//tx = session.beginTransaction();
		
	}
	catch (HibernateException e)
	{
		e.printStackTrace();
	} finally {
		session.close();
	}
	*/
	
	private void test()
	{
		List list = getBuddies("joe");
		for (Object o : list)
		{
			System.out.println(o.toString());
		}
	}
	
	public synchronized List<Buddy> getBuddies(String user)
	{
		Session session = factory.openSession();
		try
		{
			List<Buddy> buddies = session.createQuery("FROM data.Buddy AS b WHERE b.username = '" + user + "'").list();
			
			return buddies;
		}
		catch (HibernateException e)
		{
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}
	
	public synchronized List<Buddy> getFollowers(String user)
	{
		Session session = factory.openSession();
		try
		{
			List<Buddy> buddies = session.createQuery("FROM data.Buddy AS b WHERE b.buddyname = '" + user + "'").list();
			
			return buddies;
		}
		catch (HibernateException e)
		{
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}
	
	public synchronized User getUser(String user)
	{
		Session session = factory.openSession();
		try
		{
			User u = (User) session.get(User.class, user);
			
			return u;
		}
		catch (HibernateException e)
		{
			System.err.println("Something went horribly wrong!");
			e.printStackTrace();
			System.exit(3);
		} finally {
			session.close();
		}
		
		return null;
	}
	
	public synchronized boolean addUser(String username, String password)
	{
		System.out.println("DAO: adding user " + username + "\n: ");
		password = hash(password, hash(UUID.randomUUID() + "jf298UF(*&Y872ty97Y*76t239Gy9272" + username), 100000);
		
		try
		{
			//add the user to the database
			connection.createStatement().executeUpdate("INSERT INTO `burst_ppl_User` VALUES ('" + java.net.URLEncoder.encode(username, "ASCII") + "', '" + password + "')");
			
			return true;
		}
		catch (SQLException e)
		{
			return false;
		}
		catch (Exception e)
		{
			System.err.println("Something went horribly wrong!");
			e.printStackTrace();
			System.exit(3);
		}
		
		return false;
	}
	
	public synchronized boolean addBuddy(String username, String buddy)
	{
		try
		{
			//add the user to the database
			connection.createStatement().executeUpdate("INSERT INTO `burst_ppl_Buddy` VALUES ('" + java.net.URLEncoder.encode(username, "ASCII") + "', '" + java.net.URLEncoder.encode(buddy, "ASCII") + "')");
			
			System.out.println("DAO: " + username + " is now buddies with " + buddy + "\n: ");
			return true;
		}
		catch (SQLException e)
		{
			return false;
		}
		catch (Exception e)
		{
			System.err.println("Something went horribly wrong!");
			e.printStackTrace();
			System.exit(3);
		}
		
		return false;
	}
	
	public synchronized boolean removeBuddy(String username, String buddy)
	{
		try
		{
			//add the user to the database
			connection.createStatement().executeUpdate("DELETE FROM `burst_ppl_Buddy` WHERE `username` = '" + java.net.URLEncoder.encode(username, "ASCII") + "' AND `buddyname` = '" + java.net.URLEncoder.encode(buddy, "ASCII") + "'");
			
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (Exception e)
		{
			System.err.println("Something went horribly wrong!");
			e.printStackTrace();
			System.exit(3);
		}
		
		return false;
	}

	public static String hash(String password, String salt, int iterations)
	{
		password = salt + password;
		
		for (int i = 0; i < iterations; i++)
		{
			password = hash(password);
		}
		
		return salt + password;
	}
	
    /**
     * Copied from StackExchange.
     * To hash:
     * 	String hash = hash(password, dbHash.substring(0, 64), 100000);
     */
    private static String hash(String password)
    {
            try
            {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    md.update(password.getBytes());

                    byte byteData[] = md.digest();

                    //convert the byte to hex format method 1
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < byteData.length; i++) {
                            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    return sb.toString();
            }
            catch (Exception e)
            {
                    e.printStackTrace();
            }
            return null;
    }
	
	
}

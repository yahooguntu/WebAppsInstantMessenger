package data;

import java.io.Serializable;

public class Buddy implements Serializable
{
	private static final long serialVersionUID = -4008193531871734186L;
	String username;
	String buddyname;
	
	public Buddy() {}
	
	public Buddy(String username, String buddyname) {
		super();
		this.username = username;
		this.buddyname = buddyname;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getBuddyname() {
		return buddyname;
	}
	public void setBuddyname(String buddyname) {
		this.buddyname = buddyname;
	}
	
	@Override
	public String toString()
	{
		return "[Buddy:username='" + username + "',buddyname='" + buddyname + "']";
	}
}

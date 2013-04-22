package data;

public class Buddy
{
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
}

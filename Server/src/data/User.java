package data;

import java.util.Set;

public class User {
	String username;
	String hash;
	Set buddies;
	
	public User() {}
	
	public User(String username, String hash) {
		super();
		this.username = username;
		this.hash = hash;
	}
	
	public User(String username, String hash, Set buddies) {
		super();
		this.username = username;
		this.hash = hash;
		this.buddies = buddies;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public Set getBuddies() {
		return buddies;
	}
	public void setBuddies(Set buddies) {
		this.buddies = buddies;
	}
	
	@Override
	public String toString()
	{
		return "[User:username='" + username + "',hash12='" + hash.substring(0, 12) + "']";
	}
}

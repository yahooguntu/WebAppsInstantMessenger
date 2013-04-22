package data;

import java.util.Set;

public class User {
	String username;
	String hash;
	Set buddies;
	
	public User(String username, String hash) {
		super();
		this.username = username;
		this.hash = hash;
	}
	
	public User(String username, String hash, Set buddys) {
		super();
		this.username = username;
		this.hash = hash;
		this.buddies = buddys;
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
	public Set getBuddys() {
		return buddies;
	}
	public void setBuddys(Set buddys) {
		this.buddies = buddys;
	}
	
}

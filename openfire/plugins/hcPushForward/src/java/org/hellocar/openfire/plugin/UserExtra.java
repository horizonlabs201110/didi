package org.hellocar.openfire.plugin;

public class UserExtra {
	public String userName;
	public boolean iosPush;
	public String iosToken;
	
	public UserExtra(String userName, boolean iosPush, String iosToken) {
		this.userName = userName;
		this.iosPush = iosPush;
		this.iosToken = iosToken;
	}
}
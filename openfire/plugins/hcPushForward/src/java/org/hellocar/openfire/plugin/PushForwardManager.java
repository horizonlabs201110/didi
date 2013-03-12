package org.hellocar.openfire.plugin;

import java.lang.*;

public class PushForwardManager implements IPushForwardManager, Runnable {
	private Thread mainThread = null;
	
	public static IPushForwardManager CreateInstance() {
		return new PushForwardManager();
	}
	
	public PushForwardManager() {
		mainThread = new Thread(this);
	}
	
	public void Init() {
		mainThread.start();
	}
	
	public void Terminate() {
		mainThread.join(millis);
	}
	
	public void run() {
        System.out.println("I'm running!");
	}
}
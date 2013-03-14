package org.hellocar.openfire.plugin;

public class ManualEvent {
	Object obj = new Object();
	
	public void doWait(long timeout){
		synchronized(obj){
			try {
				obj.wait(timeout);
			} 
			catch(InterruptedException e){
				//nothing to do
			}
		}
	}
	
	public void doNotify(){
		synchronized(obj){
			obj.notify();
		}
	}
	
	public void doNotifyAll(){
		synchronized(obj){
			obj.notifyAll();
		}
	}
}
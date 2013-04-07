 package org.hellocar.openfire.plugin;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;

public class Utils {
	public static final String PLUGIN_NAME = "hcPushForward";
	public static final long THREAD_PUSH_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_FORWARD_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_MESSAGE_JOIN_MILLISECONDS = 3000;
	public static final long THREAD_DATABASE_HIT_DELAY_MILLISECONDS = 100;
	private static Logger tracer = LoggerFactory.getLogger(PLUGIN_NAME);
	private static ThreadPoolExecutor threadPool = 
			new ThreadPoolExecutor(
					2,
					10,
					3, 
					TimeUnit.SECONDS, 
					new ArrayBlockingQueue<Runnable>(3), 
					new ThreadPoolExecutor.DiscardOldestPolicy());
	
	public static long getNow() {
		return (new Date()).getTime();
	}
	
	public static void debug(String msg) {
		tracer.debug(msg);
	}
	
	public static void info(String msg) {
		tracer.info(msg);
	}
	
	public static void error(String msg) {
		tracer.error(msg);
	}
	
	public static void error(String msg, Throwable e) {
		tracer.error(msg, e);
	}
	
	public static void executeTask(IThreadPoolTaskListener listener, Object state) {
		threadPool.execute(new ThreadPoolTask(listener, state));
	}
}

class ThreadPoolTask implements Runnable {
	private Object state = null;
	private IThreadPoolTaskListener listener = null;

	ThreadPoolTask(IThreadPoolTaskListener listener, Object state){
		if (listener == null) {
			throw new IllegalArgumentException("listener");
		}
		this.listener = listener;
		this.state = state;
	}
	
	public void run(){
		this.listener.taskrun(state);
	}
}

interface IThreadPoolTaskListener { 
	void taskrun(Object state);
}
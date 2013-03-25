package org.hellocar.openfire.plugin;

public interface IPushAdapter {
	void push(MessageEx msg, String token) throws Exception;
}

package org.hellocar.openfire.plugin;

import org.xmpp.packet.*;

public interface IPushAdapter {
	void push(Message message);
}

package org.hellocar.openfire.plugin;

import org.xmpp.packet.*;

public interface IForwardAdapter {
	void forward(Message message);
}

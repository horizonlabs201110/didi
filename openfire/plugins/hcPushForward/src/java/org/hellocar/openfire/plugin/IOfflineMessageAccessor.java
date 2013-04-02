package org.hellocar.openfire.plugin;

import java.util.Collection;

import org.jivesoftware.openfire.OfflineMessage;

public interface IOfflineMessageAccessor {
	int getMessageCount(String userName);
}

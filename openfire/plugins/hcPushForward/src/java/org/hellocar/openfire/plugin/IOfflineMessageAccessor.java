package org.hellocar.openfire.plugin;

import java.util.Collection;

import org.jivesoftware.openfire.OfflineMessage;

public interface IOfflineMessageAccessor {
	Collection<OfflineMessage> getMessages(String userName, boolean delete);
}

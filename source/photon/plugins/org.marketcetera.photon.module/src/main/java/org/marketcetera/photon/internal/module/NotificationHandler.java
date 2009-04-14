package org.marketcetera.photon.internal.module;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.module.SinkDataHandler;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simple handler that forwards notifications to the core notification manager.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class NotificationHandler extends SinkDataHandler {

	@Override
	public void receivedData(DataFlowID inFlowID, Object inData) {
		NotificationManager.getNotificationManager().publish((INotification) inData);
	}

}

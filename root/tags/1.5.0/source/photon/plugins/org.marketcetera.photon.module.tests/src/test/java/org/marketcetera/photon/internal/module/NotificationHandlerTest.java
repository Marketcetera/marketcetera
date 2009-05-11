package org.marketcetera.photon.internal.module;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.DataFlowID;


/* $License$ */

/**
 * Tests {@link NotificationHandler}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class NotificationHandlerTest {

	/**
	 * Tests that notifications are forwarded.
	 */
	@Test
	public void testReceiveData() throws Exception {
		ISubscriber mockSubscriber = mock(ISubscriber.class);
		INotification mockNotification = mock(INotification.class);
		NotificationManager.getNotificationManager().subscribe(mockSubscriber);
		stub(mockSubscriber.isInteresting(mockNotification)).toReturn(true);
		new NotificationHandler().receivedData(new DataFlowID("1"), mockNotification);
		// wait for notification, publisher engine uses a separate thread
		Thread.sleep(500);
		verify(mockSubscriber).publishTo(mockNotification);
	}
}

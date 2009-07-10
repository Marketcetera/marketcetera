package org.marketcetera.photon.actions;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Test;
import org.marketcetera.client.Client;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.actions.ReconnectServerJob.BrokerNotificationListener;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.mockito.ArgumentMatcher;


/**
 * Test {@link BrokerNotificationListener}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class BrokerNotificationListenerTest {

	@Test
	public void testReceiveBrokerStatus() throws Exception {
		ISubscriber mockSubscriber = mock(ISubscriber.class);
		stub(mockSubscriber.isInteresting(anyObject())).toReturn(true);
		Client mockClient = mock(Client.class);
		NotificationManager.getNotificationManager().subscribe(mockSubscriber);

		BrokerStatus status = new BrokerStatus("abc",
				new BrokerID("abc"), true);
		stub(mockClient.getBrokersStatus()).toReturn(new BrokersStatus(Collections.singletonList((status))));
		BrokerNotificationListener fixture = new BrokerNotificationListener(mockClient);
		fixture.receiveBrokerStatus(status);

		// have to wait since notifications happen in different thread
		Thread.sleep(1000);
		verify(mockSubscriber).publishTo(
				argThat(new IsExpectedStatusNotification(status)));
	}

	private static class IsExpectedStatusNotification extends
			ArgumentMatcher<INotification> {

		BrokerStatus mStatus;

		public IsExpectedStatusNotification(BrokerStatus status) {
			mStatus = status;
		}

		@Override
		public boolean matches(Object argument) {
			INotification notification = (INotification) argument;
			if (notification.getSeverity() == Severity.HIGH) {
				I18NMessage0P subject;
				I18NMessage1P details;
				if (mStatus.getLoggedOn()) {
					subject = Messages.BROKER_NOTIFICATION_BROKER_AVAILABLE;
					details = Messages.BROKER_NOTIFICATION_BROKER_AVAILABLE_DETAILS;
				} else {
					subject = Messages.BROKER_NOTIFICATION_BROKER_UNAVAILABLE;
					details = Messages.BROKER_NOTIFICATION_BROKER_UNAVAILABLE_DETAILS;
				}
				return subject.getText().equals(notification.getSubject())
						&& details.getText(
								Messages.BROKER_LABEL_PATTERN.getText(mStatus
										.getName(), mStatus.getId())).equals(
								notification.getBody());
			}
			return false;
		}

	}

}

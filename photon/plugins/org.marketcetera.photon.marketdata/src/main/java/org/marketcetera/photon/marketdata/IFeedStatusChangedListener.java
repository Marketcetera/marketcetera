package org.marketcetera.photon.marketdata;

import java.util.EventListener;

import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Interface that feed status listeners must implement.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface IFeedStatusChangedListener extends EventListener {

	/**
	 * Callback for handling feed status changes.
	 * 
	 * @param event
	 *            event describing feed status change
	 */
	void feedStatusChanged(IFeedStatusEvent event);

	/**
	 * Event object interface for feed status changes.
	 */
	@ClassVersion("$Id$")
	public interface IFeedStatusEvent {

		/**
		 * The object on which the event initially occurred.
		 * 
		 * @return the object on which the event initially occurred
		 */
		public Object getSource();

		/**
		 * Returns the old status, before the change that triggered this event.
		 * 
		 * @return the old status
		 */
		FeedStatus getOldStatus();

		/**
		 * Returns the new status, that is the feed's current status.
		 * 
		 * @return the new status
		 */
		FeedStatus getNewStatus();
	}
}
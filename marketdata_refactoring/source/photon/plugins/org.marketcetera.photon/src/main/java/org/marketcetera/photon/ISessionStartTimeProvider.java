package org.marketcetera.photon;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface to get the effective session start time and to register for change notifications. The
 * session start time is controlled by the {@link PhotonPreferences#TRADING_HISTORY_START_TIME}
 * preference. However, the value returned from this class is not only the start time, but also the
 * date of the current session.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface ISessionStartTimeProvider {

	/**
	 * Returns the current session start time. This may be null if the server is disconnected.
	 * 
	 * @return the current session start time
	 */
	Date getSessionStartTime();

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}

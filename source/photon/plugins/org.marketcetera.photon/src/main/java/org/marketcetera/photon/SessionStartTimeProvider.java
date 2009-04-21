package org.marketcetera.photon;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simple bean to manage session start time.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class SessionStartTimeProvider implements ISessionStartTimeProvider {

	private Date mSessionStartTime;

	@Override
	public Date getSessionStartTime() {
		return copyDate(mSessionStartTime);
	}

	/**
	 * Set the session start time. This fires change notification to all registered listeners.
	 * 
	 * @param sessionStartTime
	 *            the new session start time.
	 */
	public void setSessionStartTime(Date sessionStartTime) {
		if (mSessionStartTime == null && sessionStartTime == null) return;
		getPropertyChangeSupport().firePropertyChange("sessionStartTime", //$NON-NLS-1$
				mSessionStartTime, copyDate(mSessionStartTime = copyDate(sessionStartTime)));
	}

	private Date copyDate(Date date) {
		// defensive copying since Date is mutable
		return date == null ? null : new Date(date.getTime());
	}

	// Boiler plate property change code

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * @return {@link PropertyChangeSupport} for this class
	 */
	protected PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
	}
}

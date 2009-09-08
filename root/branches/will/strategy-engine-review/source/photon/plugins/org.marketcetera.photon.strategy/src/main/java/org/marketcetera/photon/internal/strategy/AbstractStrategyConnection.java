package org.marketcetera.photon.internal.strategy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.commons.lang.Validate;
import org.marketcetera.photon.internal.strategy.ui.StrategiesView;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Photon UI abstraction for a strategy connection (model for {@link StrategiesView}.
 * 
 * Provides property change notification for "state" and "displayName" properties.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class AbstractStrategyConnection {

	/**
	 * Strategy state.
	 */
	@ClassVersion("$Id$")
	public enum State {
		RUNNING, STOPPED
	}

	private final PropertyChangeSupport mPropertyChangeSupport = new PropertyChangeSupport(this);
	private State mState;
	private String mDisplayName;

	/**
	 * Constructor.
	 * 
	 * @param displayName
	 *            the human readable name for this strategy connection
	 * @throws IllegalArgumentException
	 *             if displayName is null
	 */
	public AbstractStrategyConnection(String displayName) {
		Validate.notNull(displayName);
		mState = State.STOPPED;
		mDisplayName = displayName;
	}

	/**
	 * Returns the current state of the strategy connection.
	 * 
	 * @return the current state of the connection, will never be null
	 */
	public State getState() {
		return mState;
	}

	/**
	 * Returns the human readable name for this strategy connection.
	 * 
	 * @return the human readable name for this connection, will never be null
	 */
	public String getDisplayName() {
		return mDisplayName;
	}

	/**
	 * Set the Strategy state.
	 * 
	 * @param state
	 *            the new state
	 * @throws IllegalArgumentException
	 *             if state is null
	 */
	protected void setState(State state) {
		Validate.notNull(state);
		mPropertyChangeSupport.firePropertyChange("state", //$NON-NLS-1$
				mState, mState = state);
	}

	/**
	 * Set the human readable name for the Strategy.
	 * 
	 * @param displayName
	 *            the new name
	 * @throws IllegalArgumentException
	 *             if displayName is null
	 */
	protected void setDisplayName(String displayName) {
		Validate.notNull(displayName);
		mPropertyChangeSupport.firePropertyChange("displayName", //$NON-NLS-1$
				mDisplayName, mDisplayName = displayName);
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		mPropertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		mPropertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

}
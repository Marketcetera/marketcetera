package org.marketcetera.photon.internal.strategy;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Photon UI abstraction for a registerd strategy.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class Strategy {

	/**
	 * Strategy state.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	public enum State {
		RUNNING, STOPPED
	};

	private final PropertyChangeSupport mPropertyChangeSupport = new PropertyChangeSupport(
			this);

	private State mState;

	private String mDisplayName;
	
	private final ModuleURN mURN;

	private final IFile mFile;
	
	private final String mClassName;
	
	private final Properties mParameters;
	
	/**
	 * Constructor.
	 * 
	 * @param urn the ModuleURN of the underlying strategy module
	 * @param file the script file
	 * @param className the class name of the strategy object
	 * @param parameters parameters for the script
	 */
	Strategy(ModuleURN urn, IFile file, String className, Properties parameters) {
		mURN = urn;
		mFile = file;
		mClassName = className;
		mParameters = parameters;
	}

	/**
	 * Returns the {@link ModuleURN} of this strategy.
	 * 
	 * @return the ModuleURN of the underlying strategy module
	 */
	ModuleURN getURN() {
		return mURN;
	}

	/**
	 * Returns the {@link IFile} of the script this strategy will run.
	 * 
	 * @return the file of the strategy script
	 */
	public IFile getFile() {
		return mFile;
	}
	
	/**
	 * Returns the class name of the object in the script that strategy will run.
	 * 
	 * @return the class name of the object in the strategy script
	 */
	public String getClassName() {
		return mClassName;
	}

	/**
	 * Returns the current state of the Strategy.
	 * 
	 * @return the current state of the Strategy
	 */
	public State getState() {
		return mState;
	}

	/**
	 * Returns the human readable name for the Strategy.
	 * 
	 * @return the human readable name for the Strategy
	 */
	public String getDisplayName() {
		return StringUtils.defaultString(mDisplayName);
	}
	
	/**
	 * Returns the parameters for this strategy
	 * 
	 * @return the parameters for the strategy
	 */
	public Properties getParameters() {
		// make a copy to prevent modification
		Properties copy = new Properties();
		copy.putAll(mParameters);
		return copy;
	}

	/**
	 * Set the Strategy state.
	 * 
	 * @param state
	 *            the new state
	 */
	void setState(State state) {
		State oldState = mState;
		mState = state;
		mPropertyChangeSupport.firePropertyChange("state", oldState, mState); //$NON-NLS-1$
	}

	/**
	 * Set the human readable name for the Strategy.
	 * 
	 * @param displayName
	 *            the new name
	 */
	void setDisplayName(String displayName) {
		String oldDisplayName = mDisplayName;
		mDisplayName = displayName;
		mPropertyChangeSupport.firePropertyChange(
				"displayName", oldDisplayName, mDisplayName); //$NON-NLS-1$
	}

	/**
	 * Set the strategy parameters.
	 * 
	 * @param parameters
	 *            the new parameters
	 */
	void setParameters(Properties parameters) {
		mParameters.clear();
		if (parameters != null) {
			mParameters.putAll(parameters);
		}
	}

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(String,
	 *      PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		mPropertyChangeSupport
				.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(String,
	 *      PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		mPropertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
}

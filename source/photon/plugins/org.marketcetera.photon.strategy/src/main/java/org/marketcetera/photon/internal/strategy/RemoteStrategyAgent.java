package org.marketcetera.photon.internal.strategy;

import java.net.URI;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * UI bean for a remote strategy agent.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class RemoteStrategyAgent extends AbstractStrategyConnection {

	private URI mURI;
	
	private String mUsername;
	
	private String mPassword;

	/**
	 * Constructor.
	 * 
	 * @param displayName
	 *            the human readable name for this strategy connection
	 * @throws IllegalArgumentException
	 *             if displayName is null
	 */
	public RemoteStrategyAgent(String displayName) {
		super(displayName);
	}

	/**
	 * Set the remote connection URI.
	 * 
	 * @param uri
	 *            the new URI, or null if it should be unset
	 */
	void setURI(URI uri) {
		this.mURI = uri;
	}

	/**
	 * Returns the remote connection URI.
	 * 
	 * @return the remote connection URI, or null if not set
	 */
	public URI getURI() {
		return mURI;
	}

	/**
	 * Set the remote connection username.
	 * 
	 * @param username
	 *            the new username, or null if it should be unset
	 */
	void setUsername(String username) {
		mUsername = username;
	}

	/**
	 * Returns the remote connection username.
	 * 
	 * @return the remote connection username, or null if not set
	 */
	public String getUsername() {
		return mUsername;
	}

	/**
	 * Set the remote connection password.
	 * 
	 * @param password
	 *            the new password, or null if it should be unset
	 */
	void setPassword(String password) {
		mPassword = password;
	}

	/**
	 * Returns the remote connection password.
	 * 
	 * @return the remote connection password, or null if not set
	 */
	public String getPassword() {
		return mPassword;
	}
}

package org.marketcetera.photon.internal.strategy;

import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.eclipse.core.resources.IFile;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Photon UI abstraction for a registerd strategy.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class Strategy extends AbstractStrategyConnection {

	private final ModuleURN mURN;

	private final IFile mFile;

	private final String mClassName;

	private boolean mRouteToServer;

	private Properties mParameters;

	/**
	 * Constructor.
	 * 
	 * @param displayName
	 *            the human readable name for this strategy connection
	 * @param urn
	 *            the ModuleURN of the underlying strategy module
	 * @param file
	 *            the script file
	 * @param className
	 *            the class name of the strategy object
	 * @param routeToServer
	 *            true if the strategy should send orders to the server, false otherwise
	 * @param parameters
	 *            parameters for the script
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	Strategy(String displayName, ModuleURN urn, IFile file, String className,
			boolean routeToServer, Properties parameters) {
		super(displayName);
		Validate.noNullElements(new Object[] { urn, file, className, parameters });
		mURN = urn;
		mFile = file;
		mClassName = className;
		mRouteToServer = routeToServer;
		mParameters = parameters;
	}

	/**
	 * Returns the {@link ModuleURN} of this strategy.
	 * 
	 * @return the ModuleURN of the underlying strategy module, will never be null
	 */
	ModuleURN getURN() {
		return mURN;
	}

	/**
	 * Returns the {@link IFile} of the script this strategy will run.
	 * 
	 * @return the file of the strategy script, will never be null
	 */
	public IFile getFile() {
		return mFile;
	}

	/**
	 * Returns the class name of the object in the script that strategy will run.
	 * 
	 * @return the class name of the object in the strategy script, will never be null
	 */
	public String getClassName() {
		return mClassName;
	}

	/**
	 * Returns whether the strategy should route orders to the server.
	 * 
	 * @return true if the strategy should send orders to the server, false otherwise
	 */
	public boolean getRouteToServer() {
		return mRouteToServer;
	}

	/**
	 * Returns the parameters for this strategy
	 * 
	 * @return the parameters for the strategy, will never be null
	 */
	public Properties getParameters() {
		// make a copy to prevent modification
		Properties copy = new Properties();
		copy.putAll(mParameters);
		return copy;
	}

	/**
	 * Specify whether this strategy should route orders to the server.
	 * 
	 * @param routeToServer
	 *            true if the strategy should send orders to the server, false otherwise
	 */
	void setRouteToServer(boolean routeToServer) {
		mRouteToServer = routeToServer;
	}

	/**
	 * Set the strategy parameters.
	 * 
	 * @param parameters
	 *            the new parameters
	 * @throws IllegalArgumentException
	 *             if parameters is null
	 */
	void setParameters(Properties parameters) {
		Validate.notNull(parameters);
		mParameters = parameters;
	}
}

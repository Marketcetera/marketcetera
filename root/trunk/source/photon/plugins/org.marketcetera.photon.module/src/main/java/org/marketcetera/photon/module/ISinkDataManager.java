package org.marketcetera.photon.module;

import org.marketcetera.module.SinkModule;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for registering and unregistering {@link ISinkDataHandler} objects to handle data from
 * the sink module. This interface supports a single "default" handler that can be registered to
 * handle all data that is not otherwise handled explicitly.
 * 
 * @see SinkModule
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface ISinkDataManager {

	/**
	 * Register a handler for the given class (or classes) of data. This method has no effect if the
	 * handler is already registered for that class.
	 * 
	 * @param handler
	 *            the handler to register
	 * @param classes
	 *            the classes the handler will handle
	 * @throws IllegalArgumentException
	 *             if the handler is null, if classes is empty, or if any class is null
	 */
	void register(ISinkDataHandler handler, Class<?>... classes);

	/**
	 * Register a handler that handles all classes of data not otherwise handled by other handlers.
	 * 
	 * Before registering a new default handler, the old one must be unregistered using
	 * {@link #unregisterDefault(ISinkDataHandler)}.
	 * 
	 * @param handler
	 *            the handler to register
	 * @throws IllegalArgumentException
	 *             if the handler is null
	 * @throws IllegalStateException
	 *             if a default handler has already been registered
	 */
	void registerDefault(ISinkDataHandler handler);

	/**
	 * Unregister a handler for the given class (or classes) of data. This has no effect if the
	 * provided handler is not registered for any of the provided classes.
	 * 
	 * @param handler
	 *            the handler to unregister
	 * @param classes
	 *            the classes to unregister the handler from
	 * @throws IllegalArgumentException
	 *             if the handler is null, if classes is empty, or if any class is null
	 */
	void unregister(ISinkDataHandler handler, Class<?>... classes);

	/**
	 * Unregister the default handler. This has no effect if the provided handler is not the default
	 * handler.
	 * 
	 * @param handler
	 *            the handler to register
	 * @throws IllegalArgumentException
	 *             if the handler is null
	 */
	public void unregisterDefault(ISinkDataHandler handler);

	/**
	 * Completely unregisters the handler. It will no longer receive data.
	 * 
	 * @param handler
	 *            the handler to unregister
	 * @throws IllegalArgumentException
	 *             if the handler is null
	 */
	public void unregister(ISinkDataHandler handler);
}

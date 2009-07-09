package org.marketcetera.photon.internal.module;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.Validate;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.SinkDataListener;
import org.marketcetera.photon.module.ISinkDataHandler;
import org.marketcetera.photon.module.ISinkDataManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implementation of {@link ISinkDataManager}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class SinkDataManager implements SinkDataListener, ISinkDataManager {

	/**
	 * Manages the handlers, assuming about 10 types of sink data and only about 2 concurrent
	 * subscribing threads.
	 */
	private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<ISinkDataHandler>> handlers =
			new ConcurrentHashMap<Class<?>, CopyOnWriteArraySet<ISinkDataHandler>>(10, 0.75f, 2);

	private final AtomicReference<ISinkDataHandler> defaultHandler =
			new AtomicReference<ISinkDataHandler>();

	@Override
	public void register(ISinkDataHandler handler, Class<?>... classes) {
		Validate.notNull(handler);
		Validate.noNullElements(classes);
		for (Class<?> clazz : classes) {
			handlers.putIfAbsent(clazz, new CopyOnWriteArraySet<ISinkDataHandler>());
			handlers.get(clazz).add(handler);
		}
	}

	@Override
	public void registerDefault(ISinkDataHandler handler) {
		Validate.notNull(handler);
		if (!defaultHandler.compareAndSet(null, handler)) {
			throw new IllegalStateException();
		}
	}

	@Override
	public void unregister(ISinkDataHandler handler, Class<?>... classes) {
		Validate.notNull(handler);
		Validate.noNullElements(classes);
		for (Class<?> clazz : classes) {
			handlers.get(clazz).remove(handler);
		}
	}

	@Override
	public void unregister(ISinkDataHandler handler) {
		Validate.notNull(handler);
		for (Set<ISinkDataHandler> set : handlers.values()) {
			set.remove(handler);
		}
		defaultHandler.compareAndSet(handler, null);
	}

	@Override
	public void unregisterDefault(ISinkDataHandler handler) {
		Validate.notNull(handler);
		defaultHandler.compareAndSet(handler, null);
	}

	@Override
	public void receivedData(DataFlowID inFlowID, Object inData) {
		boolean handled = false;
		for (Map.Entry<Class<?>, CopyOnWriteArraySet<ISinkDataHandler>> entry : handlers.entrySet()) {
			if (entry.getKey().isInstance(inData)) {
				for (ISinkDataHandler handler : entry.getValue()) {
					handled = true;
					handler.receivedData(inFlowID, inData);
				}
			}
		}
		if (!handled) {
			ISinkDataHandler def = defaultHandler.get();
			if (def != null) {
				def.receivedData(inFlowID, inData);
			}
		}
	}

}

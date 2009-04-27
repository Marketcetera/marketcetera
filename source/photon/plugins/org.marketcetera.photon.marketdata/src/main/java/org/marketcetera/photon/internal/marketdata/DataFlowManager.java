package org.marketcetera.photon.internal.marketdata;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.module.DataFlowException;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.model.marketdata.impl.MDItemImpl;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/* $License$ */

/**
 * Base class for data flow managers. This class handles the interactions with the
 * {@link ModuleManager} and implements all the methods required by the IDataFlowManager interface.
 * 
 * Subclasses must implement abstract methods to create concrete data items and to update them when
 * data arrives.
 * 
 * @see IDataFlowManager
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class DataFlowManager<T extends MDItemImpl, K extends Key<? super T>> implements
		IDataFlowManager<T, K> {

	private final ModuleManager mModuleManager;
	private final Map<K, ModuleURN> mSubscribers = new HashMap<K, ModuleURN>();
	private final Map<K, T> mItems = new MapMaker().makeComputingMap(new Function<K, T>() {
		@Override
		public T apply(K from) {
			return createItem(from);
		}
	});
	private final Set<Capability> mRequiredCapabilities;
	private ModuleURN mSourceModule;

	/**
	 * Constructor.
	 * 
	 * @param moduleManager
	 *            the module manager
	 * @param requiredCapabilities
	 *            the capabilities this manager requires, cannot be empty
	 * @throws IllegalArgumentException
	 *             if either parameter is null, or if requiredCapabilities is empty
	 */
	protected DataFlowManager(ModuleManager moduleManager, Set<Capability> requiredCapabilities) {
		Validate.noNullElements(new Object[] { moduleManager, requiredCapabilities });
		mModuleManager = moduleManager;
		// would like Sets.immutableEnumSet(requiredCapabilities) which may be in google
		// collections someday
		mRequiredCapabilities = Collections.unmodifiableSet(EnumSet.copyOf(requiredCapabilities));
	}

	@Override
	public final T getItem(K key) {
		Validate.notNull(key);
		return mItems.get(key);
	}

	@Override
	public final synchronized void setSourceFeed(IMarketDataFeed feed) {
		ModuleURN module = feed == null ? null : feed.getURN();
		if (mSourceModule == module) {
			return;
		}
		if (mSourceModule != null) {
			for (ModuleURN subscriber : mSubscribers.values()) {
				stopModule(subscriber);
			}
		}
		for (Map.Entry<K, T> entry : mItems.entrySet()) {
			resetItemWithLock(entry.getKey(), entry.getValue());
		}
		if (feed == null) {
			mSourceModule = null;
			return;
		}
		Set<Capability> capabilities = feed.getCapabilities();
		if (capabilities.containsAll(mRequiredCapabilities)) {
			mSourceModule = module;
			for (ModuleURN subscriber : mSubscribers.values()) {
				startModule(subscriber);
			}
		} else {
			mSourceModule = null;
			Messages.DATA_FLOW_MANAGER_CAPABILITY_UNSUPPORTED.info(this, feed.getName(),
					capabilities, mRequiredCapabilities);
		}
	}

	private void startModule(ModuleURN module) {
		assert module != null;
		try {
			if (!mModuleManager.getModuleInfo(module).getState().isStarted()) {
				mModuleManager.start(module);
			}
		} catch (InvalidURNException e) {
			// it's the URN originally returned by the module framework, should always be valid
			throw new AssertionError(e);
		} catch (ModuleNotFoundException e) {
			// no one should be removing these modules
			throw new IllegalStateException(e);
		} catch (ModuleStateException e) {
			// should not happen, this class controls the lifecycle and explicitly checks the state
			// above
			throw new IllegalStateException(e);
		} catch (ModuleException e) {
			// something went wrong stopping the module, throw runtime exception since no error
			// handling is supported at this point
			throw new IllegalStateException(e);
		}
	}

	private void stopModule(ModuleURN module) {
		assert module != null;
		try {
			if (mModuleManager.getModuleInfo(module).getState().isStarted()) {
				mModuleManager.stop(module);
			}
		} catch (InvalidURNException e) {
			// it's the URN originally returned by the module framework, should always be valid
			throw new AssertionError(e);
		} catch (ModuleNotFoundException e) {
			// no one should be removing these modules
			throw new IllegalStateException(e);
		} catch (ModuleStateException e) {
			// should not happen, this class controls the lifecycle and explicitly checks the state
			// above
			throw new IllegalStateException(e);
		} catch (DataFlowException e) {
			// something went wrong stopping the module, throw runtime exception since no error
			// handling is supported at this point
			throw new IllegalStateException(e);
		} catch (ModuleException e) {
			// something went wrong stopping the module, throw runtime exception since no error
			// handling is supported at this point
			throw new IllegalStateException(e);
		}
	}

	@Override
	public final synchronized void startFlow(K key) {
		Validate.notNull(key);
		if (mSubscribers.containsKey(key)) {
			return;
		}
		IMarketDataSubscriber subscriber = createSubscriber(key);
		try {
			ModuleURN subscriberURN = mModuleManager.createModule(
					MarketDataReceiverFactory.PROVIDER_URN, subscriber);
			if (mSourceModule != null) {
				startModule(subscriberURN);
			}
			mSubscribers.put(key, subscriberURN);
		} catch (InvalidURNException e) {
			// the provider URN should never be invalid
			throw new AssertionError(e);
		} catch (ModuleCreationException e) {
			// should not happen
			throw new IllegalStateException(e);
		} catch (MXBeanOperationException e) {
			// should not happen
			throw new IllegalStateException(e);
		} catch (ModuleException e) {
			// something went wrong creating the module, throw runtime exception since no error
			// handling is supported at this point
			throw new IllegalStateException(e);
		}
	}

	@Override
	public final synchronized void stopFlow(K key) {
		Validate.notNull(key);
		resetItemWithLock(key, getItem(key));
		ModuleURN subscriberURN = mSubscribers.remove(key);
		if (subscriberURN == null) {
			return;
		}
		stopModule(subscriberURN);
		try {
			mModuleManager.deleteModule(subscriberURN);
		} catch (InvalidURNException e) {
			// it's the URN originally returned by the module framework, should always be valid
			throw new AssertionError(e);
		} catch (ModuleNotFoundException e) {
			// no one should be removing these modules
			throw new IllegalStateException(e);
		} catch (MXBeanOperationException e) {
			// should not happen
			throw new IllegalStateException(e);
		} catch (ModuleException e) {
			// something went wrong deleting the module, throw runtime exception since no error
			// handling is supported at this point
			throw new IllegalStateException(e);
		}
	}
	
	private void resetItemWithLock(K key, T item) {
		synchronized (item) {
			resetItem(key, item);
		}
	}

	/**
	 * Hook for subclasses to create and initialize the data item for a given key.
	 * 
	 * @param key
	 *            the data key, will not be null
	 * @return a data item that will reflect the market data
	 */
	abstract protected T createItem(K key);

	/**
	 * Hook for subclasses to reset data items to their initial state since the source module is
	 * changing.  This method is called when the item's lock is being held.
	 * 
	 * @param key
	 *            the item key
	 * @param item
	 *            the item to reset
	 */
	abstract protected void resetItem(K key, T item);

	/**
	 * Hook for subclasses to provide a custom subscriber to handle market data. The internal
	 * {@link Subscriber} class should be used to respect the manager's source module.
	 * 
	 * @param key
	 *            the data key, will not be null
	 * @return a subscriber that will handle the market data
	 */
	abstract protected Subscriber createSubscriber(K key);

	/**
	 * Abstract market data subscriber providing the source module.
	 */
	@ClassVersion("$Id$")
	protected abstract class Subscriber implements IMarketDataSubscriber {

		@Override
		public ModuleURN getSourceModule() {
			return mSourceModule;
		}

		/**
		 * Convenience method for subclasses that encounter unexpected data.
		 * 
		 * @param data
		 *            the unexpected data
		 */
		protected final void reportUnexpectedData(Object data) {
			Messages.DATA_FLOW_MANAGER_UNEXPECTED_DATA.warn(DataFlowManager.this, data,
					getRequest());
		}

		/**
		 * Convenience method for subclasses that encounter data with an unexpected id.
		 * 
		 * @param data
		 *            the unexpected data
		 */
		protected final void reportUnexpectedMessageId(Object data) {
			Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.warn(DataFlowManager.this, data,
					getRequest());
		}

		/**
		 * Utility method to validates that a {@link SymbolExchangeEvent} has the expected symbol.
		 * 
		 * @param expected
		 *            the expected symbol
		 * @param event
		 *            the incoming event
		 * @return true if the symbols match, false otherwise
		 * @throws IllegalArgumentException
		 *             if any parameter is null
		 */
		protected final boolean validateSymbol(String expected, SymbolExchangeEvent event) {
			Validate.noNullElements(new Object[] { expected, event });
			return validateSymbol(expected, event.getSymbol());
		}

		/**
		 * Utility method to validates that a received event symbol against an expected one.
		 * 
		 * @param expected
		 *            the expected symbol
		 * @param symbol
		 *            the symbol on the event
		 * @return true if the symbols match, false otherwise
		 * @throws IllegalArgumentException
		 *             if expected is null
		 */
		protected final boolean validateSymbol(String expected, MSymbol symbol) {
			Validate.notNull(expected);
			final String newSymbol = symbol == null ? null : symbol.getFullSymbol();
			if (expected.equals(newSymbol)) {
				return true;
			} else {
				Messages.DATA_FLOW_MANAGER_EVENT_SYMBOL_MISMATCH.warn(DataFlowManager.this,
						newSymbol, expected);
				return false;
			}
		}
	}

}

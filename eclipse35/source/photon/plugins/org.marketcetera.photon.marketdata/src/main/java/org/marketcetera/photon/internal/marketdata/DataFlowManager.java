package org.marketcetera.photon.internal.marketdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.lang.Validate;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.photon.model.marketdata.impl.MDItemImpl;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.BindingAnnotation;

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
abstract class DataFlowManager<T extends MDItemImpl, K extends Key<? super T>> implements
		IDataFlowManager<T, K> {

	/**
	 * Identifies the {@link Executor} used by this class for Guice binding.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.PARAMETER})
	@BindingAnnotation
	@interface MarketDataExecutor {}
	
	private final ModuleManager mModuleManager;
	private final Map<K, ModuleURN> mSubscribers = new HashMap<K, ModuleURN>();
	private final Map<K, T> mItems = new MapMaker().makeComputingMap(new Function<K, T>() {
		@Override
		public T apply(K from) {
			return createItem(from);
		}
	});
	private final Set<Capability> mRequiredCapabilities;
	private final Executor mMarketDataExecutor;
	private ModuleURN mSourceModule;

	/**
	 * Constructor.
	 * 
	 * @param moduleManager
	 *            the module manager
	 * @param requiredCapabilities
	 *            the capabilities this manager requires, cannot be empty
	 * @param marketDataExecutor
	 *            an executor for long running module operations that <strong>must</strong> execute
	 *            tasks sequentially
	 * @throws IllegalArgumentException
	 *             if either parameter is null, or if requiredCapabilities is empty
	 */
	protected DataFlowManager(ModuleManager moduleManager, Set<Capability> requiredCapabilities,
			Executor marketDataExecutor) {
		Validate.noNullElements(new Object[] { moduleManager, requiredCapabilities });
		mModuleManager = moduleManager;
		// would like Sets.immutableEnumSet(requiredCapabilities) which may be in google
		// collections someday
		mRequiredCapabilities = Collections.unmodifiableSet(EnumSet.copyOf(requiredCapabilities));
		mMarketDataExecutor = marketDataExecutor;
	}

	@Override
	public final T getItem(K key) {
		Validate.notNull(key);
		return mItems.get(key);
	}

	@Override
	public final synchronized void setSourceFeed(IMarketDataFeed feed) {
		// this method restarts all modules even if the feed has not changed,
		// in case anything went wrong the previous time
		ModuleURN module = feed == null ? null : feed.getURN();
		if (mSourceModule != null) {
			for (ModuleURN subscriber : mSubscribers.values()) {
				stopModule(subscriber, false);
			}
		}
		for (K key : mItems.keySet()) {
			resetItem(key);
		}
		mSourceModule = null;
		if (feed != null) {
			Set<Capability> capabilities = feed.getCapabilities();
			if (capabilities.containsAll(mRequiredCapabilities)) {
				mSourceModule = module;
				for (ModuleURN subscriber : mSubscribers.values()) {
					startModule(subscriber);
				}
			} else {
				Messages.DATA_FLOW_MANAGER_CAPABILITY_UNSUPPORTED.info(this, feed.getName(),
						capabilities, mRequiredCapabilities);
			}
		}
	}

	private void startModule(final ModuleURN module) {
		assert module != null;
		mMarketDataExecutor.execute(new ReportingRunnable() {
			public void doRun() throws Exception {
				if (!mModuleManager.getModuleInfo(module).getState().isStarted()) {
					mModuleManager.start(module);
				}
			}
		});
	}

	private void stopModule(final ModuleURN module, final boolean delete) {
		assert module != null;
		mMarketDataExecutor.execute(new ReportingRunnable() {
			public void doRun() throws Exception {
				if (mModuleManager.getModuleInfo(module).getState().isStarted()) {
					mModuleManager.stop(module);
				}
				if (delete) {
					mModuleManager.deleteModule(module);
				}
			}
		});
	}

	private void resetItem(final K key) {
		assert key != null;
		// this goes into the queue as well since it depends on earlier stopModule operations being
		// completed
		mMarketDataExecutor.execute(new Runnable() {
			@Override
			public void run() {
				resetItem(key, getItem(key));
			}
		});
	}

	@Override
	public final synchronized void startFlow(K key) {
		Validate.notNull(key);
		if (mSubscribers.containsKey(key)) {
			return;
		}
		IMarketDataSubscriber subscriber = createSubscriber(key);
		try {
			// Unlike other module operations, module creation is done synchronously here. This is
			// because 1) we know the from the implementation of the module that it is a quick
			// operation, and 2) it is not dependent on any other potentially queued operations. As
			// a side effect, it allows assertion errors to be reported immediately
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
			// should not happen, we know we are creating it properly
			throw new AssertionError(e);
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
		ModuleURN subscriberURN = mSubscribers.remove(key);
		if (subscriberURN == null) {
			return;
		}
		stopModule(subscriberURN, true);
		resetItem(key);
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
	 * changing.
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

	private abstract static class ReportingRunnable implements Runnable {
		@Override
		public final void run() {
			try {
				doRun();
			} catch (Exception e) {
				// TODO: This should be more visible in the UI
				ExceptUtils.swallow(e);
			}
		}

		protected abstract void doRun() throws Exception;
	}

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

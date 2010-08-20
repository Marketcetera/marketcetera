package org.marketcetera.photon.internal.marketdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.lang.Validate;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.module.*;
import org.marketcetera.photon.marketdata.IMarketDataFeed;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.google.inject.BindingAnnotation;

/* $License$ */

/**
 * Base class for data flow managers. This class handles the interactions with the
 * {@link ModuleManager} and implements all the methods required by the IDataFlowManager interface.
 * <p>
 * Subclasses must implement abstract methods to create concrete data items and to update them when
 * data arrives.
 * <p>
 * Long running market data operations are queued in a separate thread using an Executor. This means
 * that there may be a delay between the time, say, {@link #startFlow(Key)} is called and when the
 * associated MDItem starts getting data.
 * 
 * @see IDataFlowManager
 * @param <T>
 *            the model object type being managed
 * @param <K>
 *            the request key being managed
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
abstract class DataFlowManager<T, K extends Key> implements
        IDataFlowManager<T, K> {

    /**
     * Identifies the {@link Executor} used by this class for Guice binding.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target( { ElementType.PARAMETER })
    @BindingAnnotation
    @interface MarketDataExecutor {
    }

    private final ModuleManager mModuleManager;
    private final Map<K, ModuleURN> mSubscribers = new HashMap<K, ModuleURN>();
    private final Map<K, T> mItems = new MapMaker()
            .makeComputingMap(new Function<K, T>() {
                @Override
                public T apply(final K from) {
                    return createItem(from);
                }
            });
    private final ImmutableSet<Capability> mRequiredCapabilities;
    private final Executor mMarketDataExecutor;
    private final IMarketDataRequestSupport mMarketDataRequestSupport;
    private ModuleURN mSourceModule;

    /**
     * Constructor.
     * 
     * @param moduleManager
     *            the module manager
     * @param requiredCapabilities
     *            the capabilities this manager requires, cannot be empty
     * @param marketDataExecutor
     *            an executor for long running module operations that
     *            <strong>must</strong> execute tasks sequentially
     * @param marketDataRequestSupport
     *            supports generating market data requests
     * @throws IllegalArgumentException
     *             if any parameter is null, or if requiredCapabilities is empty
     */
    protected DataFlowManager(final ModuleManager moduleManager,
            final Set<Capability> requiredCapabilities,
            final Executor marketDataExecutor,
            IMarketDataRequestSupport marketDataRequestSupport) {
        Validate.noNullElements(new Object[] { moduleManager,
                requiredCapabilities, marketDataExecutor,
                marketDataRequestSupport });
        mModuleManager = moduleManager;
        mRequiredCapabilities = Sets.immutableEnumSet(requiredCapabilities);
        mMarketDataExecutor = marketDataExecutor;
        mMarketDataRequestSupport = marketDataRequestSupport;
    }

    @Override
    public final T getItem(final K key) {
        Validate.notNull(key);
        return mItems.get(key);
    }

    @Override
    public final synchronized void setSourceFeed(final IMarketDataFeed feed) {
        /*
         * This method restarts all modules even if the feed has not changed, in
         * case anything went wrong the previous time.
         */
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
                Messages.DATA_FLOW_MANAGER_CAPABILITY_UNSUPPORTED.info(this,
                        feed.getName(), capabilities, mRequiredCapabilities);
            }
        }
    }

    private void startModule(final ModuleURN module) {
        assert module != null;
        mMarketDataExecutor.execute(new ReportingRunnable() {
            @Override
            public void doRun() throws Exception {
                if (!mModuleManager.getModuleInfo(module).getState()
                        .isStarted()) {
                    mModuleManager.start(module);
                }
            }
        });
    }

    private void stopModule(final ModuleURN module, final boolean delete) {
        assert module != null;
        mMarketDataExecutor.execute(new ReportingRunnable() {
            @Override
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
        /*
         * This goes into the queue as well since it depends on earlier
         * stopModule operations being completed.
         */
        mMarketDataExecutor.execute(new Runnable() {
            @Override
            public void run() {
                resetItem(key, getItem(key));
            }
        });
    }

    @Override
    public final synchronized void startFlow(final K key) {
        Validate.notNull(key);
        if (mSubscribers.containsKey(key)) {
            return;
        }
        IMarketDataSubscriber subscriber = createSubscriber(key);
        try {
            /*
             * Unlike other module operations, module creation is done
             * synchronously here. This is because 1) we know the from the
             * implementation of the module that it is a quick operation, and 2)
             * it is not dependent on any other potentially queued operations.
             * As a side effect, it allows assertion errors to be reported
             * immediately
             */
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
            // something went wrong creating the module, throw runtime exception
            // since no error handling is supported at this point
            throw new IllegalStateException(e);
        }
    }

    @Override
    public final synchronized void stopFlow(final K key) {
        Validate.notNull(key);
        ModuleURN subscriberURN = mSubscribers.remove(key);
        if (subscriberURN == null) {
            return;
        }
        stopModule(subscriberURN, true);
        resetItem(key);
    }

    @Override
    public final synchronized void restartFlow(final K key) {
        Validate.notNull(key);
        ModuleURN subscriberURN = mSubscribers.get(key);
        if (subscriberURN == null) {
            return;
        }
        stopModule(subscriberURN, false);
        startModule(subscriberURN);
    }

    /**
     * Initializes a {@link MarketDataRequest} for the provided instrument.
     * 
     * @param instrument
     *            the instrument
     * @return a {@link MarketDataRequest} with instrument information
     */
    protected final MarketDataRequestBuilder initializeRequest(Instrument instrument) {
        return mMarketDataRequestSupport.initializeRequest(instrument);
    }

    /**
     * Hook for subclasses to create and initialize the data item for a given
     * key.
     * 
     * @param key
     *            the data key, will not be null
     * @return a data item that will reflect the market data
     */
    abstract protected T createItem(K key);

    /**
     * Hook for subclasses to reset data items to their initial state since the
     * source module is changing.
     * 
     * @param key
     *            the item key
     * @param item
     *            the item to reset
     */
    abstract protected void resetItem(K key, T item);

    /**
     * Hook for subclasses to provide a custom subscriber to handle market data.
     * The internal {@link DataFlowManager.Subscriber} class should be used to
     * respect the manager's source module.
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
        protected final void reportUnexpectedData(final Object data) {
            Messages.DATA_FLOW_MANAGER_UNEXPECTED_DATA.warn(
                    DataFlowManager.this, data, getRequest());
        }

        /**
         * Convenience method for subclasses that encounter data with an
         * unexpected id.
         * 
         * @param data
         *            the unexpected data
         */
        protected final void reportUnexpectedMessageId(final Object data) {
            Messages.DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID.warn(
                    DataFlowManager.this, data, getRequest());
        }

        /**
         * Utility method to validates that a {@link MarketDataEvent} has the
         * expected instrument.
         * 
         * @param instrument
         *            the expected instrument
         * @param event
         *            the incoming event
         * @return true if the instruments match, false otherwise
         * @throws IllegalArgumentException
         *             if any parameter is null
         */
        protected final boolean validateInstrument(final Instrument instrument,
                final MarketDataEvent event) {
            Validate.noNullElements(new Object[] { instrument, event });
            return validateInstrument(instrument, event.getInstrument());
        }

        /**
         * Utility method to validate a received event instrument against
         * an expected one.
         * 
         * @param expected
         *            the expected instrument
         * @param instrument
         *            the instrument on the event
         * @return true if the instruments match, false otherwise
         * @throws IllegalArgumentException
         *             if expected is null
         */
        protected final boolean validateInstrument(final Instrument expected,
                final Instrument instrument) {
            Validate.notNull(expected);
            return true;
//            String symbol = instrument.getSymbol();
//            if(instrument instanceof HasProviderSymbol) {
//                symbol = ((HasProviderSymbol)instrument).getProviderSymbol();
//            }
//            if (expected.getSymbol().equals(symbol)) {
//                return true;
//            } else {
//                Messages.DATA_FLOW_MANAGER_EVENT_INSTRUMENT_MISMATCH.warn(
//                        DataFlowManager.this, instrument, expected);
//                return false;
//            }
        }
    }
}

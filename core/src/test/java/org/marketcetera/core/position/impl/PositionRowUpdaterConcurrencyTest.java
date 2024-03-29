package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.Trade;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.util.concurrent.Lock;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Test {@link PositionRowUpdater}. It simulates market data in one thread and creating/disposing
 * PositionRowUpdater objects in another thread.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PositionRowUpdaterConcurrencyTest {

    private final Random mGenerator = new Random();
    private static final Instrument INSTRUMENT = new Equity("METC");
    private static final String ACCOUNT = "A1";
    private static final String TRADER = "1";
    private PositionRowImpl mRow;
    private BasicEventList<Trade<?>> mTrades;
    private final Object mSimulatedDataFlowLock = new Object();
    private MockMarketData mMockMarketData;
    private Lock mLock;

    @Before
    public void before() {
//        BasicConfigurator.configure();
//        Logger.getLogger("MarketData").setLevel(Level.TRACE);
//        Logger.getLogger("ListUpdate").setLevel(Level.TRACE);
        mTrades = new BasicEventList<Trade<?>>();
        mLock = mTrades.getReadWriteLock().writeLock();
        mRow = new PositionRowImpl(INSTRUMENT, "METC", ACCOUNT, TRADER, new BigDecimal(100));
        mMockMarketData = new MockMarketData();
    }

    @Test(timeout = 10000)
    public void testConcurrentRun() throws Exception {
        mListUpdateThread.start();
        mMarketDataThread.start();
        Thread.sleep(4000);
        checkFailure();
        mListUpdateThread.interrupt();
        mMarketDataThread.interrupt();
        checkFailure();
        mListUpdateThread.join();
        mMarketDataThread.join();
    }

    private Thread mMarketDataThread = new ReportingThread("MarketData") {

        @Override
        protected void runWithReporting() throws Exception {
            while (true) {
                if (isInterrupted()) return;
                SLF4JLoggerProxy.trace(getName(), "Firing event");
                mMockMarketData.fireEvent();
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    };

    private Thread mListUpdateThread = new ReportingThread("ListUpdate") {

        private final List<PositionRowUpdater> mListeners = Lists.newLinkedList();

        @Override
        protected void runWithReporting() throws Exception {
            while (true) {
                if (isInterrupted()) return;
                if (mGenerator.nextBoolean()) {
                    mLock.lock();
                    try {
                        SLF4JLoggerProxy.trace(getName(), "Adding updater");
                        mListeners.add(new PositionRowUpdater(mRow, mTrades, mMockMarketData));
                        try {
                            sleep(26);
                        } catch (InterruptedException e) {
                            return;
                        }
                    } finally {
                        mLock.unlock();
                    }
                }
                if (!mListeners.isEmpty() && mGenerator.nextInt(3) == 1) {
                    mLock.lock();
                    try {
                        SLF4JLoggerProxy.trace(getName(), "Disposing updater");
                        mListeners.remove(getRandomElement(mListeners)).dispose();
                        try {
                            sleep(17);
                        } catch (InterruptedException e) {
                            return;
                        }
                    } finally {
                        mLock.unlock();
                    }
                }
            }
        }
    };

    private int getRandomElement(List<?> list) {
        return list.size() == 1 ? 0 : mGenerator.nextInt(list.size() - 1);
    }

    class MockMarketData implements MarketDataSupport {

        private final Set<InstrumentMarketDataListener> mListeners = Sets.newHashSet();

        @Override
        public void addInstrumentMarketDataListener(Instrument instrument, InstrumentMarketDataListener listener) {
            synchronized (mSimulatedDataFlowLock) {
                try {
                    mListeners.add(listener);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public void fireEvent() {
            synchronized (mSimulatedDataFlowLock) {
                InstrumentMarketDataEvent event = new InstrumentMarketDataEvent(
                        this, new BigDecimal(mGenerator.nextInt(5)));
                int type = mGenerator.nextInt(4);
                for (InstrumentMarketDataListener listener : mListeners) {
                    switch (type) {
                    case 0:
                        listener.symbolTraded(event);
                        break;
                    case 1:
                        listener.closePriceChanged(event);
                        break;
                    case 2:
                        listener.optionMultiplierChanged(event);
                        break;
                    case 3:
                        listener.futureMultiplierChanged(event);
                        break;
                    default:
                        throw new AssertionError();
                    }
                }
            }
        }

        @Override
        public BigDecimal getClosingPrice(Instrument instrument) {
            return null;
        }

        @Override
        public BigDecimal getLastTradePrice(Instrument instrument) {
            return null;
        }

        @Override
        public BigDecimal getOptionMultiplier(Option option) {
            return null;
        }

		@Override
		public BigDecimal getFutureMultiplier(Future future) {
			return null;
		}
		
        @Override
        public void removeInstrumentMarketDataListener(Instrument instrument, InstrumentMarketDataListener listener) {
            synchronized (mSimulatedDataFlowLock) {
                try {
                    mListeners.remove(listener);
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        @Override
        public void dispose() {
        }

    }

    // code below is duplicated from org.marketcetera.photon.test.MultiThreadedTestBase

    /**
     * Caches the first reported exception.
     */
    private Throwable mFailure;

    /**
     * Records an exception to report to JUnit. Only the first reported exception is stored.
     * 
     * @param failure
     *            the exception to record
     */
    protected synchronized void setFailure(Throwable failure) {
        if (this.mFailure == null) this.mFailure = failure;
    }

    /**
     * If an exception was recorded, it will be wrapped and thrown as a {@link RuntimeException}.
     * This should be called from the main JUnit thread so the exception will be reported.
     * 
     * @throws RuntimeException
     *             if an exception was recorded by this class
     */
    protected synchronized void checkFailure() {
        if (mFailure != null) throw new RuntimeException(mFailure);
    }

    /**
     * Convenience thread that reports any exceptions thrown by a runnable.
     */
    protected abstract class ReportingThread extends Thread {

        public ReportingThread() {
            super();
        }

        public ReportingThread(String name) {
            super(name);
        }

        @Override
        public final void run() {
            try {
                runWithReporting();
            } catch (Exception e) {
                setFailure(e);
            }
        }

        /**
         * Hook for subclass code to run. Any thrown exception will be reported.
         *
         * @throws Exception if an unexpected error occurs
         */
        protected abstract void runWithReporting() throws Exception;
    }

}

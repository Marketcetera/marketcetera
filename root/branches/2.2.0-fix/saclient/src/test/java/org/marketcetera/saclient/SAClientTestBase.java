package org.marketcetera.saclient;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Base class for testing SA Client functions.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SAClientTestBase {
    
    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
    }
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void before()
            throws Exception
    {
        MockStrategyAgent.setContextClasses(getContextClasses());
        MockStrategyAgent.startServerAndClient();
        startAgent();
        SAClientParameters defaultParams = MockStrategyAgent.DEFAULT_PARAMETERS;
        SAClientParameters modifiedParams = new SAClientParameters(defaultParams.getUsername(),
                                                                   defaultParams.getPassword(),
                                                                   defaultParams.getURL(),
                                                                   defaultParams.getHostname(),
                                                                   defaultParams.getPort(),
                                                                   getContextClasses());
        sClient = MockStrategyAgent.connectTo(modifiedParams);
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void teardown()
            throws Exception
    {
        if (sClient != null) {
            sClient.close();
            sClient = null;
        }
        stopAgent();
        MockStrategyAgent.closeServerAndClient();
    }
    /**
     * Adds additional context classes to the SA client/server context.
     * 
     * <p>Subclasses may override this method to provide additional context classes. The default
     * behavior is to return no additional context classes.
     *
     * @return a <code>Class&lt;?&gt;</code> value
     */
    protected Class<?>[] getContextClasses()
    {
        return null;
    }
    /**
     * Starts the mock strategy agent.
     *
     * @throws Exception if there were unexpected errors.
     */
    protected static void startAgent() throws Exception {
        sMockSA = new MockStrategyAgent();
    }

    /**
     * Stops the mock strategy agent if it's not running.
     *
     * @throws Exception if there were unexpected errors.
     */
    protected static void stopAgent() throws Exception {
        if (sMockSA != null) {
            sMockSA.close();
            sMockSA = null;
        }
    }

    /**
     * Resets the mock service parameters.
     */
    protected static void resetServiceParameters() {
        getMockSAService().reset();
    }

    /**
     * The SA client.
     *
     * @return the SA client.
     */
    protected static SAClient getClient() {
        return sClient;
    }

    /**
     * The module manager instance within the mock strategy agent.
     *
     * @return the module manager instance in strategy agent.
     */
    protected static ModuleManager getMM() {
        return sMockSA.getManager();
    }

    /**
     * The mock service instance.
     *
     * @return the mock service instance.
     */
    protected static MockSAServiceImpl getMockSAService() {
        return sMockSA.getService();
    }

    /**
     * A mock receiver to test data receivers.
     */
    protected static class MyDataReceiver extends ObjectQueue<Object>
            implements DataReceiver {
        @Override
        public void receiveData(Object inObject) {
            add(inObject);
        }
    }

    /**
     * A mock Connection status listener to test connection status
     * listeners.
     */
    protected static class MyConnectionStatusListener
            extends ObjectQueue<Boolean>
            implements ConnectionStatusListener {
        @Override
        public void receiveConnectionStatus(boolean inStatus) {
            add(inStatus);
        }
    }

    protected volatile static MockStrategyAgent sMockSA;
    protected volatile static SAClient sClient;

    /**
     * A base class for testing listeners.
     *
     * @param <T> the type of data received by the listeners.
     */
    private static class ObjectQueue<T> {
        /**
         * Adds the received data/notification.
         *
         * @param inValue the received data.
         */
        protected void add(T inValue) {
            mLastAdd = new Date();
            //Slow it down so that we get different mLastAdd values
            //for multiple invocations.
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            mData.add(inValue);
            if(isFail()) {
                throw new IllegalStateException();
            }
        }

        /**
         * Returns the earliest added data that hasn't been fetched yet.
         * Waits until the data is added, if no data is available already.
         *
         * @return the data added via {@link #add(Object)}.
         *
         * @throws InterruptedException if the wait for data is interrupted.
         */
        public T getNext() throws InterruptedException {
            return mData.take();
        }

        /**
         * If data has been added but hasn't been retrieved.
         *
         * @return true if data is available to be fetched.
         */
        public boolean hasData() {
            return !mData.isEmpty();
        }

        /**
         * If addition of data should throw an exception.
         *
         * @return if addition of data should throw an exception.
         */
        public boolean isFail() {
            return mFail;
        }

        /**
         * Set if addition of data should throw an exception.
         *
         * @param inFail if addition of should throw an exception.
         */
        public void setFail(boolean inFail) {
            mFail = inFail;
        }

        /**
         * Gets the timestamp of the last {@link #add(Object)} invocation.
         *
         * @return the timestamp of the last add() invocation.
         */
        public Date getLastAddTime() {
            return mLastAdd;
        }

        /**
         * Resets the object back to its initial state.
         */
        public void reset() {
            setFail(false);
            mData.clear();
            mLastAdd = null;
        }

        private volatile boolean mFail;
        private volatile Date mLastAdd;
        private final BlockingQueue<T> mData = new LinkedBlockingQueue<T>();
    }
}

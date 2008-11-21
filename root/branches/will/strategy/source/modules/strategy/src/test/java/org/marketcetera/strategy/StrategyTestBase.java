package org.marketcetera.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.module.TestMessages.FLOW_REQUESTER_PROVIDER;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.OrderCancelReject;

import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.Side;

/* $License$ */

/**
 * Base class for <code>Strategy</code> tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyTestBase
    extends ModuleTestBase
{
    public static final File SAMPLE_STRATEGY_DIR = new File("src" + File.separator + "test" + File.separator + "sample_data",
                                                            "inputs");   
    public static final File JAVA_STRATEGY = new File(SAMPLE_STRATEGY_DIR,
                                                      "JavaStrategy.java");
    public static final String JAVA_STRATEGY_NAME = "JavaStrategy";
    /**
     * Tuple which describes the location and name of a strategy.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class StrategyCoordinates
    {
        private final File file;
        private final String name;
        public static StrategyCoordinates get(File inFile,
                                              String inName)
        {
            return new StrategyCoordinates(inFile,
                                           inName);
        }
        private StrategyCoordinates(File inFile,
                                    String inName)
        {
            file = inFile;
            name = inName;
        }
        /**
         * Get the file value.
         *
         * @return a <code>File</code> value
         */
        public final File getFile()
        {
            return file;
        }
        /**
         * Get the name value.
         *
         * @return a <code>String</code> value
         */
        public final String getName()
        {
            return name;
        }
    }
    /**
     * A {@link DataReceiver} implementation that stores the data it receives.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class MockRecorderModule
        extends Module
        implements DataReceiver
    {
        /**
         * Create a new MockRecorderModule instance.
         *
         * @param inURN
         */
        protected MockRecorderModule(ModuleURN inURN)
        {
            super(inURN,
                  false);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.Module#preStart()
         */
        @Override
        protected void preStart()
                throws ModuleException
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.Module#preStop()
         */
        @Override
        protected void preStop()
                throws ModuleException
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
         */
        @Override
        public void receiveData(DataFlowID inFlowID,
                                Object inData)
                throws UnsupportedDataTypeException, StopDataFlowException
        {
            synchronized(data) {
                data.add(new DataReceived(inFlowID,
                                          inData));
            }
        }
        /**
         * Resets the collection of data received.
         */
        public void resetDataReceived()
        {
            synchronized(data) {
                data.clear();
            }
        }
        /**
         * Returns a copy of the list of the received data.
         *
         * @return a <code>list&lt;DataReceived&gt;</code> value
         */
        public List<DataReceived> getDataReceived()
        {
            synchronized(data) {
                return new ArrayList<DataReceived>(data);
            }
        }
        /**
         * collection of data received by this module
         */
        private final List<DataReceived> data = new ArrayList<DataReceived>();
        /**
         * The {@link ModuleFactory} implementation for {@link MockRecorderModule}.
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since $Release$
         */
        public static class Factory
            extends ModuleFactory<MockRecorderModule>
        {
            /**
             * used to generate unique identifiers for the instance counters
             */
            private static final AtomicLong instanceCounter = new AtomicLong();
            /**
             * provider URN for {@link StrategyDataEmissionModule}
             */
            public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:receiver:system");
            public static final Map<ModuleURN,MockRecorderModule> recorders = new HashMap<ModuleURN,MockRecorderModule>();
            /**
             * Create a new Factory instance.
             */
            public Factory()
            {
                super(PROVIDER_URN,
                      FLOW_REQUESTER_PROVIDER,
                      true,
                      false);
            }
    
            /* (non-Javadoc)
             * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
             */
            @Override
            public Module create(Object... inParameters)
                    throws ModuleCreationException
            {
                MockRecorderModule module = new MockRecorderModule(new ModuleURN(PROVIDER_URN,
                                                                                 "mockRecorderModule" + instanceCounter.incrementAndGet()));
                recorders.put(module.getURN(),
                              module);
                return module;
            }
        }
        /**
         * Stores the data received by {@link MockRecorderModule}.
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since $Release$
         */
        public static class DataReceived
        {
            /**
             * the data flow ID of the data received
             */
            private final DataFlowID dataFlowID;
            /**
             * the actual data received
             */
            private final Object data;
            /**
             * Create a new DataReceived instance.
             *
             * @param inDataFlowID a <code>DataFlowID</code> value
             * @param inData an <code>Object</code> value
             */
            private DataReceived(DataFlowID inDataFlowID,
                                 Object inData)
            {
                dataFlowID = inDataFlowID;
                data = inData;
            }
            /**
             * Get the dataFlowID value.
             *
             * @return a <code>DataFlowID</code> value
             */
            public DataFlowID getDataFlowID()
            {
                return dataFlowID;
            }
            /**
             * Get the data value.
             *
             * @return an <code>Object</code> value
             */
            public Object getData()
            {
                return data;
            }
            /* (non-Javadoc)
             * @see java.lang.Object#hashCode()
             */
            @Override
            public int hashCode()
            {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((data == null) ? 0 : data.hashCode());
                return result;
            }
            /* (non-Javadoc)
             * @see java.lang.Object#equals(java.lang.Object)
             */
            @Override
            public boolean equals(Object obj)
            {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                DataReceived other = (DataReceived) obj;
                if (data == null) {
                    if (other.data != null)
                        return false;
                } else if (!data.equals(other.data))
                    return false;
                return true;
            }
        }
    }
    /**
     * A {@link DataEmitter} implementation that emits each type of data a {@link RunningStrategy} can receive.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class StrategyDataEmissionModule
        extends Module
        implements DataEmitter
    {
        /**
         * Create a new MockRecorderModule instance.
         *
         * @param inURN
         */
        protected StrategyDataEmissionModule(ModuleURN inURN)
        {
            super(inURN,
                  false);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.Module#preStart()
         */
        @Override
        protected void preStart()
                throws ModuleException
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.Module#preStop()
         */
        @Override
        protected void preStop()
                throws ModuleException
        {
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
         */
        @Override
        public void cancel(RequestID inRequestID)
        {
            // nothing to do here
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
         */
        @Override
        public void requestData(DataRequest inRequest,
                                DataEmitterSupport inSupport)
                throws UnsupportedRequestParameterType, IllegalRequestParameterValue
        {
            try {
                sendDataTypes(inSupport);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalRequestParameterValue(null,
                                                       e);
            }
        }
        /**
         * Sends each type of data a {@link RunningStrategy} must be able to respond to.
         * 
         * <p>When a new call-back is added to {@link RunningStrategy}, this method should
         * be expanded to send that data.
         *
         * @param inSupport a <code>DataEmitterSupport</code> value to which to send the data
         * @throws Exception if an error occurs
         */
        private void sendDataTypes(DataEmitterSupport inSupport)
            throws Exception
        {
            inSupport.send(new TradeEvent(System.nanoTime(),
                                          System.currentTimeMillis(),
                                          "GOOG",
                                          "Exchange",
                                          new BigDecimal("100"),
                                          new BigDecimal("10000")));
            inSupport.send(new BidEvent(System.nanoTime(),
                                        System.currentTimeMillis(),
                                        "GOOG",
                                        "Exchange",
                                        new BigDecimal("200"),
                                        new BigDecimal("20000")));
            inSupport.send(new AskEvent(System.nanoTime(),
                                        System.currentTimeMillis(),
                                        "GOOG",
                                        "Exchange",
                                        new BigDecimal("200"),
                                        new BigDecimal("20000")));
            Message orderCancelReject = FIXVersion.FIX44.getMessageFactory().newOrderCancelReject();
            OrderCancelReject cancel = org.marketcetera.trade.Factory.getInstance().createOrderCancelReject(orderCancelReject,
                                                                                                            null);
            inSupport.send(cancel);
            Message executionReport = FIXVersion.FIX44.getMessageFactory().newExecutionReport("orderid",
                                                                                              "clOrderID",
                                                                                              "execID",
                                                                                              OrdStatus.FILLED,
                                                                                              Side.BUY,
                                                                                              new BigDecimal(100),
                                                                                              new BigDecimal(200),
                                                                                              new BigDecimal(300),
                                                                                              new BigDecimal(400),
                                                                                              new BigDecimal(500),
                                                                                              new BigDecimal(600),
                                                                                              new MSymbol("Symbol"),
                                                                                              "account");
            inSupport.send(org.marketcetera.trade.Factory.getInstance().createExecutionReport(executionReport,
                                                                                              null));
            // send an object that doesn't fit one of the categories
            inSupport.send(this);
        }
        /**
         * The {@link ModuleFactory} implementation for {@link StrategyDataEmissionModule}.
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since $Release$
         */
        public static class Factory
            extends ModuleFactory<StrategyDataEmissionModule>
        {
            /**
             * used to generate unique identifiers for the instance counters
             */
            private static final AtomicLong instanceCounter = new AtomicLong();
            /**
             * provider URN for {@link StrategyDataEmissionModule}
             */
            public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:emitter:system"); 
            /**
             * Create a new Factory instance.
             */
            public Factory()
            {
                super(PROVIDER_URN,
                      FLOW_REQUESTER_PROVIDER,
                      true,
                      false);
            }
    
            /* (non-Javadoc)
             * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
             */
            @Override
            public Module create(Object... inParameters)
                    throws ModuleCreationException
            {
                return new StrategyDataEmissionModule(new ModuleURN(PROVIDER_URN,
                                                                    "strategyDataEmissionModule" + instanceCounter.incrementAndGet()));
            }
        }
    }
    /**
     * Run before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setup()
        throws Exception
    {
        moduleManager = new ModuleManager();
        moduleManager.init();
        ordersURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        moduleManager.start(ordersURN);
        suggestionsURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        moduleManager.start(suggestionsURN);
        moduleManager.start(bogusDataFeedURN);
        factory = new StrategyModuleFactory();
        runningModules.clear();
        setPropertiesToNull();
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an error occurs
     */
    @After
    public void cleanup()
        throws Exception
    {
        for(ModuleURN strategy : runningModules) {
            try {
                moduleManager.stop(strategy);
            } catch (Exception e) {
                // ignore failures, just press ahead
            }
        }
        moduleManager.stop(ordersURN);
        moduleManager.stop(suggestionsURN);
        moduleManager.stop(bogusDataFeedURN);
        moduleManager.deleteModule(ordersURN);
        moduleManager.deleteModule(suggestionsURN);
        moduleManager.stop();
    }
    /**
     * Verifies that a strategy module can start and stop with the given parameters.
     *
     * @param inParameters an <code>Object...</code> value containing the parameters to pass to the module creation command
     * @throws Exception if an error occurs
     */
    protected void verifyStrategyStartsAndStops(Object...inParameters)
        throws Exception
    {
        ModuleURN urn = createStrategy(inParameters);
        moduleManager.stop(urn);
        assertFalse(moduleManager.getModuleInfo(urn).getState().isStarted());
        moduleManager.deleteModule(urn);
    }
    /**
     * Asserts that the values in the common strategy storage area for some well-known testing keys are null.
     */
    protected void verifyNullProperties()
    {
        verifyPropertyNull("onAsk");
        verifyPropertyNull("onBid");
        verifyPropertyNull("onCancel");
        verifyPropertyNull("onExecutionReport");
        verifyPropertyNull("onOther");
        verifyPropertyNull("onTrade");
    }
    /**
     * Asserts that the values in the common strategy storage area for some well-known testing keys are not null.
     */
    protected void verifyNonNullProperties()
    {
        verifyPropertyNonNull("onAsk");
        verifyPropertyNonNull("onBid");
        verifyPropertyNonNull("onCancel");
        verifyPropertyNonNull("onExecutionReport");
        verifyPropertyNonNull("onOther");
        verifyPropertyNonNull("onTrade");
    }
    /**
     * Sets the values in the common strategy storage area for some well-known testing keys to null.
     */
    protected void setPropertiesToNull()
    {
        Properties properties = AbstractRunningStrategy.getProperties();
        properties.clear();
        verifyNullProperties();
    }
    /**
     * Verifies the given property is non-null.
     *
     * @param inKey a <code>String</code> value
     * @return a <code>String</code> value or null
     */
    protected String verifyPropertyNonNull(String inKey)
    {
        Properties properties = AbstractRunningStrategy.getProperties();
        String property = properties.getProperty(inKey);
        assertNotNull(inKey + " is supposed to be non-null",
                      property);
        return property;
    }
    /**
     * Verifies the given property is null.
     *
     * @param inKey a <code>String</code> value
     */
    protected void verifyPropertyNull(String inKey)
    {
        Properties properties = AbstractRunningStrategy.getProperties();
        assertNull(inKey + " is supposed to be null",
                   properties.getProperty(inKey));
    }
    /**
     * Creates a strategy with the given parameters.
     * 
     * <p>The strategy is guaranteed to be running at the successful exit of this method.  Strategies created by this method
     * are tracked and shut down, if necessary, at the end of the test.
     *
     * @param inParameters an <code>Object...</code> value containing the parameters to pass to the module creation command
     * @return a <code>ModuleURN</code> value containing the URN of the strategy
     * @throws Exception if an error occurs
     */
    protected ModuleURN createStrategy(Object...inParameters)
        throws Exception
    {
        verifyNullProperties();
        return createModule(StrategyModuleFactory.PROVIDER_URN,
                            inParameters);
    }
    /**
     * Creates and starts a module with the given URN and the given parameters.
     *
     * <p>The module is guaranteed to be running at the successful exit of this method.  Modules created by this method
     * are tracked and shut down, if necessary, at the end of the test.
     *
     * @param inProvider a <code>ModuleURN</code> value
     * @param inParameters an <code>Object...</code> value containing the parameters to pass to the module creation command
     * @return a <code>ModuleURN</code> value containing the URN of the strategy
     * @throws Exception if an error occurs
     */
    protected ModuleURN createModule(ModuleURN inProvider,
                                     Object...inParameters)
        throws Exception
    {
        ModuleURN urn = moduleManager.createModule(inProvider,
                                                   inParameters);
        assertFalse(moduleManager.getModuleInfo(urn).getState().isStarted());
        moduleManager.start(urn);
        assertTrue(moduleManager.getModuleInfo(urn).getState().isStarted());
        runningModules.add(urn);
        return urn;
    }
    /**
     * global singleton module manager
     */
    protected ModuleManager moduleManager;
    /**
     * the factory to use to create the market data provider modules
     */
    protected ModuleFactory<StrategyModule> factory;
    /**
     * test destination of orders
     */
    protected ModuleURN ordersURN;
    /**
     * test destination of suggestions
     */
    protected ModuleURN suggestionsURN;
    /**
     * list of strategies started during test
     */
    protected final List<ModuleURN> runningModules = new ArrayList<ModuleURN>();
    /**
     * URN for market data provider
     */
    protected final ModuleURN bogusDataFeedURN = BogusFeedModuleFactory.INSTANCE_URN;
}

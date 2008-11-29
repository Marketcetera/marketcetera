package org.marketcetera.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.module.TestMessages.FLOW_REQUESTER_PROVIDER;

import java.beans.ExceptionListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.ReportListener;
import org.marketcetera.client.dest.DestinationsStatus;
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
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;

import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.TransactTime;

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
        implements DataReceiver, DataEmitter
    {
        /**
         * indicates if the module should emit execution reports when it receives OrderSingle objects
         */
        public static boolean shouldSendExecutionReports = true;
        public static int ordersReceived = 0;
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
            if(inData instanceof OrderSingle) {
                if(shouldSendExecutionReports) {
                    OrderSingle order = (OrderSingle)inData;
                    try {
                        List<ExecutionReport> executionReports = generateExecutionReports(order);
                        synchronized(subscribers) {
                            for(ExecutionReport executionReport : executionReports) {
                                for(DataEmitterSupport subscriber : subscribers.values()) {
                                    subscriber.send(executionReport);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new StopDataFlowException(e,
                                                        null);
                    }
                }
                ordersReceived += 1;
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
         */
        @Override
        public void cancel(DataFlowID inFlowID, RequestID inRequestID)
        {
            synchronized(subscribers) {
                subscribers.remove(inRequestID);
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
         */
        @Override
        public void requestData(DataRequest inRequest,
                                DataEmitterSupport inRequester)
                throws RequestDataException
        {
            synchronized(subscribers) {
                subscribers.put(inRequester.getRequestID(),
                                inRequester);
            }
        }
        /**
         * collection of subscribers interested in data emitter by this module
         */
        private final Map<RequestID,DataEmitterSupport> subscribers = new HashMap<RequestID,DataEmitterSupport>();
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
        public void cancel(DataFlowID inFlowID, RequestID inRequestID)
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
                                                                                              new DestinationID("some-destination"),
                                                                                              Originator.Server));
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
    public static class MockClient
        implements Client
    {
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#addExceptionListener(java.beans.ExceptionListener)
         */
        @Override
        public void addExceptionListener(ExceptionListener inArg0)
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#addReportListener(org.marketcetera.client.ReportListener)
         */
        @Override
        public void addReportListener(ReportListener inArg0)
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#close()
         */
        @Override
        public void close()
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getDestinationsStatus()
         */
        @Override
        public DestinationsStatus getDestinationsStatus()
                throws ConnectionException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getLastConnectTime()
         */
        @Override
        public Date getLastConnectTime()
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getParameters()
         */
        @Override
        public ClientParameters getParameters()
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getPositionAsOf(java.util.Date, org.marketcetera.core.MSymbol)
         */
        @Override
        public BigDecimal getPositionAsOf(Date inArg0,
                                          MSymbol inArg1)
                throws ConnectionException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getReportsSince(java.util.Date)
         */
        @Override
        public ReportBase[] getReportsSince(Date inArg0)
                throws ConnectionException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#reconnect()
         */
        @Override
        public void reconnect()
                throws ConnectionException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#reconnect(org.marketcetera.client.ClientParameters)
         */
        @Override
        public void reconnect(ClientParameters inArg0)
                throws ConnectionException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#removeExceptionListener(java.beans.ExceptionListener)
         */
        @Override
        public void removeExceptionListener(ExceptionListener inArg0)
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#removeReportListener(org.marketcetera.client.ReportListener)
         */
        @Override
        public void removeReportListener(ReportListener inArg0)
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderSingle)
         */
        @Override
        public void sendOrder(OrderSingle inArg0)
                throws ConnectionException, OrderValidationException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderReplace)
         */
        @Override
        public void sendOrder(OrderReplace inArg0)
                throws ConnectionException, OrderValidationException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderCancel)
         */
        @Override
        public void sendOrder(OrderCancel inArg0)
                throws ConnectionException, OrderValidationException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#sendOrderRaw(org.marketcetera.trade.FIXOrder)
         */
        @Override
        public void sendOrderRaw(FIXOrder inArg0)
                throws ConnectionException, OrderValidationException
        {
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
        executionReportMultiplicity = 1;
        MockRecorderModule.shouldSendExecutionReports = true;
        MockRecorderModule.ordersReceived = 0;
        moduleManager = new ModuleManager();
        moduleManager.init();
        ordersURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        moduleManager.start(ordersURN);
        suggestionsURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        moduleManager.start(suggestionsURN);
        moduleManager.start(bogusDataFeedURN);
        factory = new StrategyModuleFactory();
        runningModules.clear();
        runningModules.add(suggestionsURN);
        runningModules.add(ordersURN);
        runningModules.add(bogusDataFeedURN);
        setPropertiesToNull();
        tradeEvent = new TradeEvent(System.nanoTime(),
                                    System.currentTimeMillis(),
                                    "METC",
                                    "Q",
                                    new BigDecimal("1000.25"),
                                    new BigDecimal("1000"));
        askEvent = new AskEvent(System.nanoTime(),
                                System.currentTimeMillis(),
                                "METC",
                                "Q",
                                new BigDecimal("100.00"),
                                new BigDecimal("10000"));
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
        moduleManager.deleteModule(ordersURN);
        moduleManager.deleteModule(suggestionsURN);
        moduleManager.stop();
    }
    /**
     * Generates an <code>ExecutionReport</code> from the given <code>OrderSingle</code>.
     *
     * @param inOrder an <code>OrderSingle</code> value
     * @return an <code>ExecutionReport</code> value
     * @throws Exception if an error exists
     */
    protected static List<ExecutionReport> generateExecutionReports(OrderSingle inOrder)
        throws Exception
    {
        int multiplicity = executionReportMultiplicity;
        List<ExecutionReport> reports = new ArrayList<ExecutionReport>();
        if(inOrder.getQuantity() != null) {
            BigDecimal totalQuantity = new BigDecimal(inOrder.getQuantity().toString());
            BigDecimal lastQuantity = BigDecimal.ZERO;
            for(int iteration=0;iteration<multiplicity-1;iteration++) {
                BigDecimal thisQuantity = totalQuantity.subtract(totalQuantity.divide(new BigDecimal(Integer.toString(multiplicity))));
                totalQuantity = totalQuantity.subtract(thisQuantity);
                Message rawExeReport = FIXVersion.FIX44.getMessageFactory().newExecutionReport(inOrder.getOrderID().toString(),
                                                                                               inOrder.getOrderID().toString(),
                                                                                               "execID",
                                                                                               OrdStatus.PARTIALLY_FILLED,
                                                                                               Side.BUY,
                                                                                               thisQuantity,
                                                                                               inOrder.getPrice(),
                                                                                               lastQuantity,
                                                                                               inOrder.getPrice(),
                                                                                               inOrder.getQuantity(),
                                                                                               inOrder.getPrice(),
                                                                                               inOrder.getSymbol(),
                                                                                               inOrder.getAccount());
                rawExeReport.setField(new TransactTime(extractTransactTimeFromRunningStrategy()));
                reports.add(org.marketcetera.trade.Factory.getInstance().createExecutionReport(rawExeReport,
                                                                                               inOrder.getDestinationID(),
                                                                                               Originator.Destination));
                lastQuantity = thisQuantity;
            }
            Message rawExeReport = FIXVersion.FIX44.getMessageFactory().newExecutionReport(inOrder.getOrderID().toString(),
                                                                                           inOrder.getOrderID().toString(),
                                                                                           "execID",
                                                                                           OrdStatus.FILLED,
                                                                                           Side.BUY,
                                                                                           totalQuantity,
                                                                                           inOrder.getPrice(),
                                                                                           lastQuantity,
                                                                                           inOrder.getPrice(),
                                                                                           inOrder.getQuantity(),
                                                                                           inOrder.getPrice(),
                                                                                           inOrder.getSymbol(),
                                                                                           inOrder.getAccount());
            rawExeReport.setField(new TransactTime(extractTransactTimeFromRunningStrategy()));
            reports.add(org.marketcetera.trade.Factory.getInstance().createExecutionReport(rawExeReport,
                                                                                           inOrder.getDestinationID(),
                                                                                           Originator.Destination));
        }
        return reports;
    }
    /**
     * Extracts the date used to generate an order from a running strategy, if applicable.
     * 
     * @return a <code>Date</code> value used to generate the most recent order in a running strategy or the current time if none exists
     */
    protected static Date extractTransactTimeFromRunningStrategy()
    {
        String transactTimeString = AbstractRunningStrategy.getProperty("transactTime");
        Date transactTime = new Date();
        if(transactTimeString != null) {
            transactTime = new Date(Long.parseLong(transactTimeString));
        }
        return transactTime;
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
    protected StrategyMXBean getMXProxy(ModuleURN inModuleURN)
        throws Exception
    {
        ObjectName objectName = inModuleURN.toObjectName();
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        return JMX.newMXBeanProxy(server,
                                  objectName,
                                  StrategyMXBean.class,
                                  true);
    }
    /**
     * Gets the first strategy in the list of strategies currently running.
     *
     * @return a <code>StrategyImpl</code> value
     */
    protected final StrategyImpl getFirstRunningStrategy()
    {
        return getRunningStrategy(0);
    }
    /**
     * Gets the strategy and the given index in the list of strategies currently running.
     *
     * @param index an <code>int</code> value containing a zero-based index
     * @return a <code>StrategyImpl</code> value
     */
    protected final StrategyImpl getRunningStrategy(int index)
    {
        Set<StrategyImpl> runningStrategies = StrategyImpl.getRunningStrategies();
        StrategyImpl runningStrategy = runningStrategies.iterator().next();
        for(int i=0;i<=index;i++) {
            runningStrategy = runningStrategies.iterator().next();
        }
        return runningStrategy;
    }
    /**
     * Gets the first strategy in the list of strategies currently running.
     *
     * @return an <code>AbstractRunningStrategy</code> value
     */
    protected final AbstractRunningStrategy getFirstRunningStrategyAsAbstractRunningStrategy()
    {
        return getRunningStrategyAsAbstractRunningStrategy(0);
    }
    /**
     * Gets the strategy and the given index in the list of strategies currently running.
     *
     * @param index an <code>int</code> value containing a zero-based index
     * @return an <code>AbstractRunningStrategy</code> value
     */
    protected final AbstractRunningStrategy getRunningStrategyAsAbstractRunningStrategy(int index)
    {
        return (AbstractRunningStrategy)getRunningStrategy(index).getRunningStrategy();
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
    /**
     * trade event with generic information
     */
    protected TradeEvent tradeEvent;
    /**
     * ask event with generic information
     */
    protected AskEvent askEvent;
    /**
     * determines how many execution reports should be produced for each order received
     */
    protected static int executionReportMultiplicity = 1;
}

package org.marketcetera.strategy;

import static org.junit.Assert.*;
import static org.marketcetera.module.TestMessages.FLOW_REQUESTER_PROVIDER;
import static org.marketcetera.strategy.Status.FAILED;
import static org.marketcetera.strategy.Status.RUNNING;
import static org.marketcetera.strategy.Status.STOPPED;

import java.beans.ExceptionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.Manifest;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.client.*;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.BigDecimalUtils;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.*;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.TestMessages;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.*;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.strategy.StrategyModule.ClientFactory;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.I18NMessage;

import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.TransactTime;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Base class for <code>Strategy</code> tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class StrategyTestBase
    extends ModuleTestBase
    implements Messages
{
    public static final File SAMPLE_STRATEGY_DIR = new File("src" + File.separator + "test" + File.separator + "sample_data",
                                                            "inputs");
    /**
     * Tuple which describes the location and name of a strategy.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
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
     * @since 1.0.0
     */
    public static class MockRecorderModule
        extends Module
        implements DataReceiver, DataEmitter
    {
        /**
         * indicates if the module should emit execution reports when it receives order objects
         */
        public static boolean shouldSendExecutionReports = true;
        public static boolean shouldFullyFillOrders = true;
        public static boolean shouldIgnoreLogMessages = true;
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
            if(inData instanceof LogEvent) {
                if(shouldIgnoreLogMessages) {
                    return;
                }
            }
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
        public void cancel(DataFlowID inFlowID,
                           RequestID inRequestID)
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
         * @since 1.0.0
         */
        public static class Factory
            extends ModuleFactory
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
         * @since 1.0.0
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
            /* (non-Javadoc)
             * @see java.lang.Object#toString()
             */
            @Override
            public String toString()
            {
                return data == null ? "null data" : data.toString();
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
     * @since 1.0.0
     */
    public static class StrategyDataEmissionModule
        extends Module
        implements DataEmitter
    {
        /**
         * data to transmit
         */
        private static final List<Object> dataToSend = new ArrayList<Object>();
        /**
         * Gets the data that will be tramsitted.
         *
         * @return a <code>List&lt;Object&gt;</code> value
         */
        public static List<Object> getDataToSend()
        {
            synchronized(dataToSend) {
                return dataToSend;
            }
        }
        /**
         * Rests the data to be transmitted to its default setting.
         *
         * @throws Exception if an error occurs
         */
        public static void setDataToSendToDefaults()
            throws Exception
        {
            synchronized(dataToSend) {
                dataToSend.clear();
                dataToSend.add(EventTestBase.generateEquityTradeEvent(System.nanoTime(),
                                                                      System.currentTimeMillis(),
                                                                      new Equity("GOOG"),
                                                                      "Exchange",
                                                                      new BigDecimal("100"),
                                                                      new BigDecimal("10000")));
                dataToSend.add(EventTestBase.generateEquityBidEvent(System.nanoTime(),
                                                                    System.currentTimeMillis(),
                                                                    new Equity("GOOG"),
                                                                    "Exchange",
                                                                    new BigDecimal("200"),
                                                                    new BigDecimal("20000")));
                dataToSend.add(EventTestBase.generateEquityAskEvent(System.nanoTime(),
                                                                    System.currentTimeMillis(),
                                                                    new Equity("GOOG"),
                                                                    "Exchange",
                                                                    new BigDecimal("200"),
                                                                    new BigDecimal("20000")));
                dataToSend.add(EventTestBase.generateDividendEvent());
                Message orderCancelReject = FIXVersion.FIX44.getMessageFactory().newOrderCancelReject();
                OrderCancelReject cancel = org.marketcetera.trade.Factory.getInstance().createOrderCancelReject(orderCancelReject,
                                                                                                                null,
                                                                                                                Originator.Server,
                                                                                                                null,
                                                                                                                null);
                dataToSend.add(cancel);
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
                                                                                                  new Equity("Symbol"),
                                                                                                  "account",
                                                                                                  "text");
                dataToSend.add(org.marketcetera.trade.Factory.getInstance().createExecutionReport(executionReport,
                                                                                                  new BrokerID("some-broker"),
                                                                                                  Originator.Server,
                                                                                                  null,
                                                                                                  null));
                // send an object that doesn't fit one of the categories
                dataToSend.add(new Date());
            }
        }
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
            synchronized(dataToSend) {
                for(Object o : dataToSend) {
                    inSupport.send(o);
                }
            }
        }
        /**
         * The {@link ModuleFactory} implementation for {@link StrategyDataEmissionModule}.
         *
         * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
         * @version $Id$
         * @since 1.0.0
         */
        public static class Factory
            extends ModuleFactory
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
        public static class MockClientFactory
                implements org.marketcetera.client.ClientFactory
        {
            /* (non-Javadoc)
             * @see org.marketcetera.client.ClientFactory#getClient(org.marketcetera.client.ClientParameters)
             */
            @Override
            public Client getClient(ClientParameters inClientParameters)
                    throws ClientInitException, ConnectionException
            {
                return new MockClient();
            }
        }
        /**
         * indicates whether calls to {@link #getBrokersStatus()} should fail automatically
         */
        public static boolean getBrokersFails = false;
        /**
         * indicates whether calls to {@link #getEquityPositionAsOf(Date, Equity)} should fail automatically
         */
        public static boolean getPositionFails = false;
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
         * @see org.marketcetera.client.Client#addBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
         */
        @Override
        public void addBrokerStatusListener(BrokerStatusListener inArg0)
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#addServerStatusListener(org.marketcetera.client.ServerStatusListener)
         */
        @Override
        public void addServerStatusListener(ServerStatusListener inArg0)
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
         * @see org.marketcetera.client.Client#getBrokersStatus()
         */
        @Override
        public BrokersStatus getBrokersStatus()
                throws ConnectionException
        {
            if(getBrokersFails) {
                throw new NullPointerException("This exception is expected");
            }
            return brokers;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getUserInfo(UserID, boolean)
         */
        @Override
        public UserInfo getUserInfo(UserID id, boolean useCache)
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
         * @see org.marketcetera.client.Client#getEquityPositionAsOf(java.util.Date, org.marketcetera.trade.Equity)
         */
        @Override
        public BigDecimal getEquityPositionAsOf(Date inDate,
                                          Equity inEquity)
                throws ConnectionException
        {
            if(getPositionFails) {
                throw new NullPointerException("This exception is expected");
            }
            Position position = positions.get(inEquity);
            if(position == null) {
                return null;
            }
            return position.getPositionAt(inDate);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getAllEquityPositionsAsOf(java.util.Date)
         */
        @Override
        public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(Date inDate)
                throws ConnectionException
        {
            if(getPositionFails) {
                throw new NullPointerException("This exception is expected");
            }
            Map<PositionKey<Equity>,BigDecimal> result = new LinkedHashMap<PositionKey<Equity>,BigDecimal>();
            for(Map.Entry<Instrument,Position> entry : positions.entrySet()) {
                if(entry.getKey() instanceof Equity) {
                    final Equity equity = (Equity)entry.getKey();
                    BigDecimal value = getEquityPositionAsOf(inDate,
                                                       equity);
                    if(value != null) {
                        PositionKey<Equity> key = new PositionKey<Equity>() {
                            @Override
                            public String getAccount()
                            {
                                return null;
                            }
                            /* (non-Javadoc)
                             * @see java.lang.Object#toString()
                             */
                            @Override
                            public String toString()
                            {
                                return getInstrument().getSymbol();
                            }
                            @Override
                            public Equity getInstrument()
                            {
                                return equity;
                            }
                            @Override
                            public String getTraderId()
                            {
                                return null;
                            }
                        };
                        result.put(key,
                                   value);
                    }
                }
            }
            return result;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getAllOptionPositionsAsOf(java.util.Date)
         */
        @Override
        public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(Date inDate)
                throws ConnectionException
        {
            if(getPositionFails) {
                throw new NullPointerException("This exception is expected");
            }
            Map<PositionKey<Option>,BigDecimal> result = new LinkedHashMap<PositionKey<Option>,BigDecimal>();
            for(Map.Entry<Instrument,Position> entry : positions.entrySet()) {
                if(entry.getKey() instanceof Option) {
                    final Option option = (Option)entry.getKey();
                    BigDecimal value = getOptionPositionAsOf(inDate,
                                                             option);
                    if(value != null) {
                        PositionKey<Option> key = new PositionKey<Option>() {
                            /* (non-Javadoc)
                             * @see java.lang.Object#toString()
                             */
                            @Override
                            public String toString()
                            {
                                return getInstrument().getSymbol();
                            }
                            @Override
                            public String getAccount()
                            {
                                return null;
                            }
                            @Override
                            public Option getInstrument()
                            {
                                return option;
                            }
                            @Override
                            public String getTraderId()
                            {
                                return null;
                            }
                        };
                        result.put(key,
                                   value);
                    }
                }
            }
            return result;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getOptionPositionAsOf(java.util.Date, org.marketcetera.trade.Option)
         */
        @Override
        public BigDecimal getOptionPositionAsOf(Date inDate,
                                                Option inOption)
                throws ConnectionException
        {
            if(getPositionFails) {
                throw new NullPointerException("This exception is expected");
            }
            Position position = positions.get(inOption);
            if(position == null) {
                return null;
            }
            return position.getPositionAt(inDate);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
         */
        @Override
        public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                          String... inRootSymbols)
                throws ConnectionException
        {
            if(getPositionFails) {
                throw new NullPointerException("This exception is expected");
            }
            Set<String> rootSymbols = new HashSet<String>(Arrays.asList(inRootSymbols));
            Map<PositionKey<Option>,BigDecimal> allOptionPositions = getAllOptionPositionsAsOf(inDate);
            Map<PositionKey<Option>,BigDecimal> result = new LinkedHashMap<PositionKey<Option>,BigDecimal>();
            for(Map.Entry<PositionKey<Option>,BigDecimal> position : allOptionPositions.entrySet()) {
                if(rootSymbols.contains(position.getKey().getInstrument().getSymbol())) {
                    result.put(position.getKey(),
                               position.getValue());
                }
            }
            return result;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getAllFuturePositionsAsOf(java.util.Date)
         */
        @Override
        public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(Date inDate)
                throws ConnectionException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
         */
        @Override
        public BigDecimal getFuturePositionAsOf(Date inDate,
                                                Future inEquity)
                throws ConnectionException
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getOptionRoots(java.lang.String)
         */
        @Override
        public Collection<String> getOptionRoots(String inUnderlying)
                throws ConnectionException
        {
            if(getPositionFails) {
                throw new NullPointerException("This exception is expected");
            }
            return roots.get(inUnderlying);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getUnderlying(java.lang.String)
         */
        @Override
        public String getUnderlying(String inOptionRoot)
                throws ConnectionException
        {
            if(getPositionFails) {
                throw new NullPointerException("This exception is expected");
            }
            return underlyings.get(inOptionRoot);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getReportsSince(java.util.Date)
         */
        @Override
        public ReportBase[] getReportsSince(Date inDate)
                throws ConnectionException
        {
            if(getReportsSinceThrows != null) {
                throw getReportsSinceThrows;
            }
            List<ReportBase> reportsToReturn = new ArrayList<ReportBase>();
            for(ReportBase report : reports) {
                if(report.getSendingTime().compareTo(inDate) != -1) {
                    reportsToReturn.add(report);
                }
            }
            return reportsToReturn.toArray(new ReportBase[reportsToReturn.size()]);
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
         * @see org.marketcetera.client.Client#removeBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
         */
        @Override
        public void removeBrokerStatusListener(BrokerStatusListener inArg0)
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#removeServerStatusListener(org.marketcetera.client.ServerStatusListener)
         */
        @Override
        public void removeServerStatusListener(ServerStatusListener inArg0)
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
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#isCredentialsMatch(String, char[])
         */
        @Override
        public boolean isCredentialsMatch(String inUsername, char[] inPassword)
        {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean isServerAlive()
        {
            throw new UnsupportedOperationException();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#getUserData()
         */
        @Override
        public Properties getUserData()
                throws ConnectionException
        {
            return userdata;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.client.Client#setUserData(java.util.Properties)
         */
        @Override
        public void setUserData(Properties inProperties)
                throws ConnectionException
        {
            userdata = inProperties;
        }
        public Properties userdata;
        /**
         * reports used to feed report-related calls
         */
        private final Set<ReportBase> reports = new TreeSet<ReportBase>(ReportSendingTimeComparator.INSTANCE);
        /**
         * if non-null, will be thrown during {@link #getReportsSince(Date)}.
         */
        private volatile ConnectionException getReportsSinceThrows;
    }
    /**
     * Compares the sending times of two <code>ReportBase</code> values.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    private enum ReportSendingTimeComparator
            implements Comparator<ReportBase>
    {
        INSTANCE;
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(ReportBase inO1,
                           ReportBase inO2)
        {
            return inO1.getSendingTime().compareTo(inO2.getSendingTime());
        }
    }
    /**
     * Generates a random set of broker status objects.
     *
     * @return a <code>BrokerStatus</code> value
     */
    public static final BrokersStatus generateBrokersStatus()
    {
        List<BrokerStatus> brokers = new ArrayList<BrokerStatus>();
        for(int counter=0;counter<9;counter++) {
            brokers.add(new BrokerStatus("Broker-" + System.nanoTime(),
                                         new BrokerID("broker-" + ++counter),
                                         random.nextBoolean()));
        }
        // make sure at least one broker is logged on
        brokers.add(new BrokerStatus("Broker-" + System.nanoTime(),
                                     new BrokerID("broker-10"),
                                     true));
        return new BrokersStatus(brokers);
    }
    /**
     * A period of time during which a value is in effect.
     * 
     * <p>This class can be used to track a value which changes over time.
     * A series of <code>Interval&lt;T&gt;</code> objects can represent
     * a value that changes over time by sorting them by the interval
     * date.  To determine the value of a function represented by a series
     * of intervals, find the intersection of the desired date (D) and the interval
     * where: D > interval1.getDate() && D < interval2.getDate().
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    public static class Interval<T>
        implements Comparable<Interval<T>>
    {
        /**
         * the date at which this interval takes effect
         */
        private final Date date;
        /**
         * value for this interval
         */
        private final T value;
        /**
         * Create a new Interval instance.
         *
         * @param inDate a <code>Date</code> value
         * @param inValue a <code>T</code> value
         */
        public Interval(Date inDate,
                        T inValue)
        {
            assert(inDate != null);
            date = inDate;
            value = inValue;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((date == null) ? 0 : date.hashCode());
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
            Interval<?> other = (Interval<?>) obj;
            if (date == null) {
                if (other.date != null)
                    return false;
            } else if (!date.equals(other.date))
                return false;
            return true;
        }
        /**
         * Get the date at which this interval takes effect.
         *
         * @return a <code>Date</code> value
         */
        public final Date getDate()
        {
            return date;
        }
        /**
         * Get the interval value.
         *
         * @return a <code>T</code> value
         */
        public final T getValue()
        {
            return value;
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(Interval<T> inOther)
        {
            return getDate().compareTo(inOther.getDate());
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("[%s:%s]",
                                 getDate(),
                                 getValue());
        }
    }
    /**
     * A set of intervals representing the change of the position of a security over time.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    public static class Position
    {
        /**
         * the set of intervals that define the position change points
         */
        private final SortedSet<Interval<BigDecimal>> position = new TreeSet<Interval<BigDecimal>>();
        /**
         * the instrument for which this position is defined
         */
        private final Instrument instrument;
        /**
         * Create a new Position instance.
         * 
         * <p>The initial position is randomly generated.
         *
         * @param inInstrument an <code>Instrument</code> value
         */
        public Position(Instrument inInstrument)
        {
            this(inInstrument,
                 generateRandomPosition());
        }
        /**
         * Create a new Position instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inStartingPosition a <code>List&lt;Interval&lt;BigDecimal&gt;&gt;</code> value as the initial position
         */
        public Position(Instrument inInstrument,
                        List<Interval<BigDecimal>> inStartingPosition)
        {
            assert(inInstrument != null);
            assert(inStartingPosition != null);
            instrument = inInstrument;
            position.addAll(inStartingPosition);
        }
        /**
         * Adds a data-point to the position.
         * 
         * <p>If the given <code>Date</code> is already present in the position,
         * the position will be updated with the new quantity.
         *
         * @param inDate a <code>Date</code> value
         * @param inQuantity a <code>BigDecimal</code> value
         */
        public void add(Date inDate,
                        BigDecimal inQuantity)
        {
            position.add(new Interval<BigDecimal>(inDate,
                                                  inQuantity));
        }
        /**
         * Gets an immutable view of the position.
         *
         * @return a <code>List&lt;Interval&lt;BigDecimal&gt;&gt;</code> value
         */
        public List<Interval<BigDecimal>> getPositionView()
        {
            return Collections.unmodifiableList(new ArrayList<Interval<BigDecimal>>(position));
        }
        /**
         * Gets the position at the given date.
         *
         * @param inDate a <code>Date</code> value
         * @return a <code>BigDecimal</code> value containing the position at the given date
         */
        public BigDecimal getPositionAt(Date inDate)
        {
            Date dataPoint = new Date(inDate.getTime() + 1);
            Interval<BigDecimal> point = new Interval<BigDecimal>(dataPoint,
                                                                  BigDecimal.ZERO);
            // if there are no intervals or the asked-for date precedes our first data-point,
            //  then the position is 0
            if(position.isEmpty() ||
               position.first().compareTo(point) > 0) {
                return BigDecimal.ZERO;
            }
            SortedSet<Interval<BigDecimal>> earlierIntervals = position.headSet(point);
            if(earlierIntervals.isEmpty()) {
                // the point asked for is later than all our intervals, return the tail of the master set
                return new BigDecimal(position.last().getValue().toString());
            } else {
                // the point asked for falls somewhere within the intervals, return the last value of the tail set
                return new BigDecimal(earlierIntervals.last().getValue().toString());
            }
        }
        /**
         * The instrument for this position.
         *
         * @return an <code>Instrument</code> value
         */
        public Instrument getInstrument()
        {
            return instrument;
        }
        /**
         * Generates a random position.
         *
         * <p>The position returned is a series of <code>Interval&lt;BigDecimal&gt;</code> values
         * arranged in chronologically increasing order.  The interval values are randomly
         * distributed between [-10000,10000).  The position will begin at a randomly determined point
         * 1-52 weeks before the current time.  The minimum granularity of a position change is one
         * minute, the maximum is 5 days.
         *
         * @return a <code>List&lt;Interval&lt;BigDecimal&gt;&gt;</code> value
         */
        public static final List<Interval<BigDecimal>> generateRandomPosition()
        {
            final BigDecimal MINUS_ONE = new BigDecimal("-1");
            long currentMillis = System.currentTimeMillis();
            // start the position 1-52 wks in the past
            int seedWeek = random.nextInt(52)+1;
            long difference = (long)seedWeek * 1000 * 60 * 60 * 24 * 7;
            long seedMillis = currentMillis - difference;
            List<Interval<BigDecimal>> position = new ArrayList<Interval<BigDecimal>>();
            while(seedMillis < currentMillis) {
                position.add(new Interval<BigDecimal>(new Date(seedMillis),
                                                      BigDecimalUtils.multiply(BigDecimalUtils.multiply(new BigDecimal(10000),
                                                                                                        random.nextDouble()).setScale(0,
                                                                                                                                      RoundingMode.HALF_UP),
                                                                               (random.nextBoolean() ? MINUS_ONE : BigDecimal.ONE))));
                // minimum granularity for a change in position is 1 min, maximum is 5 days (this is entirely arbitrary)
                seedMillis += (random.nextInt(1 * 60 * 24 * 5) + 1) * 1000 * 60;
            }
            return position;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuffer output = new StringBuffer();
            output.append("Position for ").append(getInstrument()).append(SystemUtils.LINE_SEPARATOR);
            for(Interval<BigDecimal> interval : position) {
                output.append(interval).append(",");
            }
            return output.toString();
        }
    }
    /**
     * Generates positions for the given symbols. 
     *
     * @param inInstruments a <code>List&lt;Instrument&gt;</code> value containing the instruments for which to generate positions
     * @return a <code>Map&lt;Instrument,Position&gt;</code> value containing the generated positions
     */
    public static final Map<Instrument,Position> generatePositions(List<Instrument> inInstruments)
    {
        Map<Instrument,Position> positions = new HashMap<Instrument,Position>();
        for(Instrument instrument : inInstruments) {
            positions.put(instrument,
                          new Position(instrument));
        }
        return positions;
    }
    /**
     * Verifies that the event created contains the expected information.
     *
     * @param inActualEvent a <code>LogEvent</code> value containing the event to verify
     * @param inExpectedLevel a <code>Priority</code> value containing the expected priority
     * @param inException a <code>Throwable</code> value containing the expected exception or <code>null</code> for none
     * @param inExpectedMessage an <code>I18NMessage</code> value containing the expected message
     * @param inExpectedParameters a <code>Serializable[]</code> value containing the expected parameters
     */
    public static void verifyEvent(LogEvent inActualEvent,
                                   LogEventLevel inExpectedLevel,
                                   Throwable inException,
                                   I18NMessage inExpectedMessage,
                                   Serializable...inExpectedParameters)
        throws Exception
    {
        assertEquals(inExpectedLevel,
                     inActualEvent.getLevel());
        assertEquals(inException,
                     inActualEvent.getException());
        String messageText = inExpectedMessage.getMessageProvider().getText(inExpectedMessage,
                                                                            (Object[])inExpectedParameters);
        assertEquals(messageText,
                     inActualEvent.getMessage());
        // serialize event

        LogEvent serializedEvent = (LogEvent)
            SerializationUtils.deserialize
            (SerializationUtils.serialize(inActualEvent));
        assertEquals(inExpectedLevel,
                     serializedEvent.getLevel());
        if(inException == null) {
            assertNull(serializedEvent.getException());
        } else {
            assertEquals(inException.getMessage(),
                         serializedEvent.getException().getMessage());
        }
        assertEquals(messageText,
                     serializedEvent.getMessage());
    }
    /**
     * Run at the beginning of execution of all tests.
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        try {
            ClientManager.setClientFactory(new MockClient.MockClientFactory());
            ClientManager.init(null);
        } catch (ClientInitException ignored) {}
        client = (MockClient)ClientManager.getInstance();
        System.setProperty(org.marketcetera.strategy.Strategy.CLASSPATH_PROPERTYNAME,
                           StrategyTestBase.SAMPLE_STRATEGY_DIR.getCanonicalPath());
        List<Instrument> testInstruments = new ArrayList<Instrument>();
        testInstruments.add(new Equity("METC"));
        testInstruments.add(new Equity("GOOG"));
        testInstruments.add(new Equity("YHOO"));
        testInstruments.add(new Equity("ORCL"));
        testInstruments.add(new Equity("AAPL"));
        testInstruments.add(new Equity("JAVA"));
        testInstruments.add(new Equity("MSFT"));
        testInstruments.add(new Option("METC1",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Call));
        testInstruments.add(new Option("METC2",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Put));
        testInstruments.add(new Option("METC3",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Call));
        testInstruments.add(new Option("METC4",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Put));
        roots.putAll("METC",
                     Arrays.asList(new String[] { "METC1", "METC2", "METC3", "METC4" } ));
        underlyings.put("METC1",
                        "METC");
        underlyings.put("METC2",
                        "METC");
        underlyings.put("METC3",
                        "METC");
        underlyings.put("METC4",
                        "METC");
        testInstruments.add(new Option("MSFT1",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Call));
        testInstruments.add(new Option("MSFT2",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Put));
        testInstruments.add(new Option("MSFT3",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Call));
        testInstruments.add(new Option("MSFT4",
                                       DateUtils.dateToString(new Date(),
                                                              DateUtils.DAYS),
                                        EventTestBase.generateDecimalValue(),
                                        OptionType.Put));
        roots.putAll("MSFT",
                     Arrays.asList(new String[] { "MSFT1", "MSFT2", "MSFT3", "MSFT4" } ));
        underlyings.put("MSFT1",
                        "MSFT");
        underlyings.put("MSFT2",
                        "MSFT");
        underlyings.put("MSFT3",
                        "MSFT");
        underlyings.put("MSFT4",
                        "MSFT");
        positions.putAll(generatePositions(testInstruments));
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
        StringBuilder classpath = new StringBuilder();
        for(String path : getClassPath()) {
            classpath.append(path).append(File.pathSeparator);
        }
        System.setProperty(JavaCompilerExecutionEngine.CLASSPATH_KEY,
                           classpath.toString());
        brokers = generateBrokersStatus();
        MockClient.getBrokersFails = false;
        MockClient.getPositionFails = false;
        executionReportMultiplicity = 1;
        MockRecorderModule.shouldSendExecutionReports = true;
        MockRecorderModule.shouldFullyFillOrders = true;
        MockRecorderModule.shouldIgnoreLogMessages = true;
        MockRecorderModule.ordersReceived = 0;
        getClientFails = false;
        final MockClient testClient = new MockClient();
        StrategyModule.clientFactory = new ClientFactory() {
            @Override
            public Client getClient()
                    throws ClientInitException
            {
                if(getClientFails) {
                    throw new ClientInitException(TestMessages.EXPECTED_EXCEPTION);
                }
                return testClient;
            }
        };
        moduleManager = new ModuleManager();
        moduleManager.init();
        outputURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        moduleManager.start(outputURN);
        moduleManager.start(bogusDataFeedURN);
        factory = new StrategyModuleFactory();
        runningModules.clear();
        runningModules.add(outputURN);
        runningModules.add(bogusDataFeedURN);
        setPropertiesToNull();
        tradeEvent = EventTestBase.generateEquityTradeEvent(System.nanoTime(),
                                                            System.currentTimeMillis(),
                                                            new Equity("METC"),
                                                            "Q",
                                                            new BigDecimal("1000.25"),
                                                            new BigDecimal("1000"));
        askEvent = EventTestBase.generateEquityAskEvent(System.nanoTime(),
                                                        System.currentTimeMillis(),
                                                        new Equity("METC"),
                                                        "Q",
                                                        new BigDecimal("100.00"),
                                                        new BigDecimal("10000"));
        StrategyDataEmissionModule.setDataToSendToDefaults();
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
        cancelDataFlows(null);
        for(ModuleURN strategy : runningModules) {
            try {
                moduleManager.stop(strategy);
            } catch (Exception e) {
                // ignore failures, just press ahead
            }
        }
        try {
            moduleManager.stop(outputURN);
        } catch (ModuleException ignore) {
                // ignore failures, just press ahead
        }
        moduleManager.deleteModule(outputURN);
        moduleManager.stop();
    }
    /**
     * Cancels all active data flows.
     * @param inStrategyURN a <code>ModuleURN</code> containing a strategy URN for which to cancel flows
     *   or null to cancel all flows
     */
    protected final void cancelDataFlows(ModuleURN inStrategyURN)
    {
        synchronized(dataFlowsByStrategy) {
            Collection<List<DataFlowID>> flowsToCancel;
            if(inStrategyURN == null) {
                flowsToCancel = dataFlowsByStrategy.values();
            } else {
                List<DataFlowID> singleList = dataFlowsByStrategy.get(inStrategyURN);
                if(singleList == null) {
                    return;
                }
                flowsToCancel = new ArrayList<List<DataFlowID>>();
                flowsToCancel.add(singleList);
            }
            for(List<DataFlowID> flows : flowsToCancel) {
                for(DataFlowID dataFlow : flows) {
                    try {
                        moduleManager.cancel(dataFlow);
                    } catch (Exception e) {
                        // ignore all exceptions and keep canceling
                    }
                }
            }
            dataFlowsByStrategy.clear();
        }
    }
    /**
     * Starts the given strategy and hooks it up to the mock ORS client.
     *
     * @param inStrategyURN a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    protected final void startStrategy(ModuleURN inStrategyURN)
        throws Exception
    {
        moduleManager.start(inStrategyURN);
        setupMockORSConnection(inStrategyURN);
        verifyStrategyReady(inStrategyURN);
    }
    /**
     * Stops the given strategy and cancels all active data flows.
     *
     * @param inStrategyURN a <code>
     * @throws Exception
     */
    protected final void stopStrategy(ModuleURN inStrategyURN)
        throws Exception
    {
        cancelDataFlows(null);
        moduleManager.stop(inStrategyURN);
        verifyStrategyStopped(inStrategyURN);
    }
    /**
     * Sets up a connection to the testing ORSClient for execution reports.
     * 
     * <p>The data flow established will be automatically stopped by invocations of
     * {@link #cancelDataFlows(ModuleURN)}.
     *
     * @param inStrategyURN a <code>ModuleURN</code> connecting the module to which to plumb the ORSClient output
     * @return a <code>DataFlowID</code> representing the data flow
     * @throws Exception if an error occurs
     */
    protected final DataFlowID setupMockORSConnection(ModuleURN inStrategyURN)
        throws Exception
    {
        DataFlowID flowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(outputURN),
                                                                             new DataRequest(inStrategyURN) },
                                                         false);
        synchronized(dataFlowsByStrategy) {
            List<DataFlowID> flows = dataFlowsByStrategy.get(inStrategyURN);
            if(flows == null) {
                flows = new ArrayList<DataFlowID>();
                dataFlowsByStrategy.put(inStrategyURN,
                                        flows);
            }
            flows.add(flowID);
        }
        return flowID;
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
        List<ExecutionReport> reports = new ArrayList<ExecutionReport>();
        for(Message rawExeReport : generateFixExecutionReports(inOrder)) {
            reports.add(org.marketcetera.trade.Factory.getInstance().createExecutionReport(rawExeReport,
                                                                                           inOrder.getBrokerID(),
                                                                                           Originator.Broker,
                                                                                           null,
                                                                                           null));
        }
        return reports;
    }
    /**
     * Generates FIX <code>Message</code> objects that contain execution reports for partial and/or
     * complete fills of the given order.
     *
     * <p>The number of objects returned can be adjusted by changing the value of {@link #executionReportMultiplicity}.
     * Whether or not the list partially or fully fills the given order can be adjusted by changing the
     * value of {@link MockRecorderModule#shouldFullyFillOrders}.
     *
     * @param inOrder an <code>OrderSingle</code> value
     * @return a <code>List&lt;Message&gt;</code> value
     * @throws Exception if an error occurs
     */
    protected static List<Message> generateFixExecutionReports(OrderSingle inOrder)
        throws Exception
    {
        int multiplicity = executionReportMultiplicity;
        List<Message> reports = new ArrayList<Message>();
        if(inOrder.getQuantity() != null) {
            BigDecimal totalQuantity = new BigDecimal(inOrder.getQuantity().toString());
            BigDecimal lastQuantity = BigDecimal.ZERO;
            for(int iteration=0;iteration<multiplicity-1;iteration++) {
                BigDecimal thisQuantity = totalQuantity.subtract(totalQuantity.divide(new BigDecimal(Integer.toString(multiplicity))));
                totalQuantity = totalQuantity.subtract(thisQuantity);
                Message rawExeReport = generateFixExecutionReport(inOrder,
                                                                  OrdStatus.PARTIALLY_FILLED,
                                                                  thisQuantity,
                                                                  lastQuantity,
                                                                  FIXVersion.FIX44);
                reports.add(rawExeReport);
                lastQuantity = thisQuantity;
            }
            Message rawExeReport = generateFixExecutionReport(inOrder,
                                                              MockRecorderModule.shouldFullyFillOrders ? OrdStatus.FILLED : OrdStatus.PARTIALLY_FILLED,
                                                              totalQuantity,
                                                              lastQuantity,
                                                              FIXVersion.FIX44);
            reports.add(rawExeReport);
        }
        return reports;
    }
    /**
     * Generates a FIX <code>Message</code> containing an execution report of the given
     * status for the given order.
     * 
     * <p><em>Warning</em> - most of the attributes of the FIX message returned are arbitrary and possibly
     * incorrect.  It is the caller's responsibility to review and modify the returned value
     * for the intended purpose.
     *
     * @param inOrder an <code>OrderSingle</code> value
     * @param inOrderStatus a <code>char</code> value corresponding to an {@link OrdStatus} value
     * @param inQuantity a <code>BigDecimal</code> value
     * @param inLastQuantity a <code>BigDecimal</code> value
     * @return a <code>Message</code> value
     * @throws Exception if an error occurs
     */
    protected static Message generateFixExecutionReport(OrderSingle inOrder,
                                                        char inOrderStatus,
                                                        BigDecimal inQuantity,
                                                        BigDecimal inLastQuantity,
                                                        FIXVersion inFIXVersion)
        throws Exception
    {
        Message exeReport = inFIXVersion.getMessageFactory().newExecutionReport(inOrder.getOrderID().toString(),
                                                                                inOrder.getOrderID().toString(),
                                                                                "execID",
                                                                                inOrderStatus,
                                                                                Side.BUY,
                                                                                inQuantity,
                                                                                inOrder.getPrice(),
                                                                                inLastQuantity,
                                                                                inOrder.getPrice(),
                                                                                inOrder.getQuantity(),
                                                                                inOrder.getPrice(),
                                                                                inOrder.getInstrument(),
                                                                                inOrder.getAccount(),
                                                                                inOrder.getText());
        exeReport.setField(new TransactTime(extractTransactTimeFromRunningStrategy()));
        return exeReport;
    }
    /**
     * Creates an <code>OrderSingle</code> value with the given <code>OrderID</code>.
     * 
     * <p>If a null <code>OrderID</code> is given, a new <code>OrderID</code> is assigned to
     * the <code>OrderSingle</code>.
     *
     * @param inOrderID an <code>OrderID</code> value or <code>null</code>
     * @return an <code>OrderSingle</code> value
     */
    protected static OrderSingle createOrderWithID(OrderID inOrderID)
    {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setOrderType(OrderType.Limit);
        order.setPrice(new BigDecimal("100.23"));
        order.setQuantity(new BigDecimal("10000"));
        order.setSide(org.marketcetera.trade.Side.Buy);
        order.setInstrument(new Equity("METC"));
        if(inOrderID != null) {
            order.setOrderID(inOrderID);
        }
        return order;
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
     * Waits until the given strategy has either started or erred out.
     *
     * @param inStrategyURN a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    protected void verifyStrategyReady(final ModuleURN inStrategyURN)
        throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Status status = getStatus(inStrategyURN);
                return status.equals(RUNNING) || status.equals(FAILED);
            }
        });
    }
    /**
     * Waits until the given strategy has stopped, either with or without error.
     *
     * @param inStrategyURN a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    protected void verifyStrategyStopped(final ModuleURN inStrategyURN)
        throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Status status = getStatus(inStrategyURN);
                return status.equals(STOPPED) || status.equals(FAILED);
            }
        });
    }
    /**
     * Verifies that the given strategy is at the given status.
     *
     * @param inStrategy a <code>ModuleURN</code> value
     * @param inStatus a <code>Status</code> value
     * @throws Exception if an error occurs
     */
    protected void verifyStrategyStatus(ModuleURN inStrategy,
                                        Status inStatus)
        throws Exception
    {
        assertEquals(inStatus,
                     getStatus(inStrategy));
    }
    /**
     * Returns the status of the given strategy.
     *
     * @param inStrategy a <code>ModuleURN</code> value
     * @return a <code>Status</code> value
     * @throws Exception if an error occurs
     */
    protected Status getStatus(ModuleURN inStrategy)
        throws Exception
    {
        return Status.valueOf(getMXProxy(inStrategy).getStatus());
    }
    /**
     * Asserts that the values in the common strategy storage area for some well-known testing keys are null.
     */
    protected void verifyNullProperties()
    {
        verifyPropertyNull("onAsk");
        verifyPropertyNull("onBid");
        verifyPropertyNull("onCancel");
        verifyPropertyNull("onDividend");
        verifyPropertyNull("onExecutionReport");
        verifyPropertyNull("onOther");
        verifyPropertyNull("onTrade");
    }
    /**
     * Asserts that the values in the common strategy storage area for some well-known testing keys are not null.
     * @throws Exception if an error occurs
     */
    protected void verifyNonNullProperties()
        throws Exception
    {
        verifyPropertyNonNull("onAsk");
        verifyPropertyNonNull("onBid");
        verifyPropertyNonNull("onCancel");
        verifyPropertyNonNull("onDividend");
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
     * @throws Exception if an error occurs
     */
    protected String verifyPropertyNonNull(final String inKey)
        throws Exception
    {
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return AbstractRunningStrategy.getProperty(inKey) != null;
            }
        });
        return AbstractRunningStrategy.getProperty(inKey);
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
        LinkedList<Object> actualParameters = new LinkedList<Object>(Arrays.asList(inParameters));
        if(inParameters.length <= 6) {
            actualParameters.addFirst(null);
        }
        ModuleURN strategyURN = createModule(StrategyModuleFactory.PROVIDER_URN,
                                             actualParameters.toArray());
        theStrategy = strategyURN;
        verifyStrategyReady(strategyURN);
        return strategyURN;
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
     * Returns an <code>MXBean</code> interface to the given strategy.
     *
     * @param inModuleURN a <code>ModuleURN</code> value containing a strategy
     * @return a <code>StrategyMXBean</code> value
     * @throws Exception if an error occurs
     */
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
     * Gets a handle to the given strategy;
     * 
     * @param inStrategyURN a <code>ModuleURN</code> value
     * 
     * <p>Note that this method will <em>fail</em> if the given strategy is not running
     *
     * @return a <code>StrategyImpl</code> value
     */
    protected final StrategyImpl getRunningStrategy(ModuleURN inStrategyURN)
    {
        Set<StrategyImpl> runningStrategies = StrategyImpl.getRunningStrategies();
        for(StrategyImpl runningStrategy : runningStrategies) {
            if(runningStrategy.getDefaultNamespace().equals(inStrategyURN.instanceName())) {
                return runningStrategy;
            }
        }
        fail(inStrategyURN + " not currently running");
        return null;
    }
    /**
     * Constructs a classpath to use for Java compilation.
     * 
     * <p>This method will make a best-effort to create the classpath,
     * ignoring errors that occur during the collection.  This method
     * is not expected to throw exceptions, muddling on instead.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    private Set<String> getClassPath()
    {
        // get the classloader that was used to load this class
        ClassLoader classLoader = getClass().getClassLoader();
        // this collection will hold all the paths we find, duplicates discarded, in the order they appear
        Set<String> paths = new LinkedHashSet<String>();
//        //Collect all URLs from the URL Class Loaders.
//        do {
//            if(classLoader instanceof URLClassLoader) {
//                URLClassLoader urlClassLoader = (URLClassLoader)classLoader;
//                for(URL url : urlClassLoader.getURLs()) {
//                    try {
//                        paths.add(url.toURI().getPath());
//                    } catch (URISyntaxException ignore) {
//                    }
//                }
//            }
//            // traverse the classloader tree upwards until no more remain
//        } while((classLoader = classLoader.getParent()) != null);
//        // reset the classloader to the current
//        classLoader = getClass().getClassLoader();
        //iterate through the manifests of all the jars to find the
        // values of their Class-Path attribute value and add them to the
        // set.
        try {
            Enumeration<URL> resourceEnumeration = classLoader.getResources("META-INF/MANIFEST.MF");
            while(resourceEnumeration.hasMoreElements()) {
                URL resourceURL = resourceEnumeration.nextElement();
                InputStream is = null;
                try {
                    // open the resource
                    is = resourceURL.openStream();
                    Manifest manifest = new Manifest(is);
                    String theClasspath = manifest.getMainAttributes().getValue("Class-Path");
                    if(theClasspath != null &&
                       !theClasspath.trim().isEmpty()) {
                        //manifest classpath is space separated URLs
                        for(String path : theClasspath.split(" ")) {
                            try {
                                URL pathURL = new URL(path);
                                paths.add(pathURL.toURI().getPath());
                            } catch (MalformedURLException ignore) {
                            } catch (URISyntaxException ignore) {
                            }
                        }
                    }
                } catch (IOException ignore) {
                } finally {
                    if(is != null) {
                        try {
                            is.close();
                        } catch (IOException ignore) {
                        }
                    }
                }
            }
        } catch (IOException ignore) {
        }
        return paths;
    }
    /**
     * random number generator for public use
     */
    public static final Random random = new Random(System.nanoTime());
    /**
     * indicates if the getClient call in the StrategyModule should fail
     */
    protected static boolean getClientFails;
    /**
     * global singleton module manager
     */
    protected ModuleManager moduleManager;
    /**
     * the factory to use to create the market data provider modules
     */
    protected ModuleFactory factory;
    /**
     * test destination of output
     */
    protected ModuleURN outputURN;
    /**
     * list of strategies started during test
     */
    protected final List<ModuleURN> runningModules = new ArrayList<ModuleURN>();
    /**
     * data flows by the strategy that caused their creation
     */
    private final Map<ModuleURN,List<DataFlowID>> dataFlowsByStrategy = new HashMap<ModuleURN,List<DataFlowID>>();
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
     * can be used to track a central strategy
     */
    protected ModuleURN theStrategy;
    /**
     * positions for a set of symbols
     */
    protected final static Map<Instrument,Position> positions = new LinkedHashMap<Instrument,Position>();
    /**
     * list of option roots for a given single underlying symbol
     */
    protected final static Multimap<String,String> roots = LinkedHashMultimap.create();
    /**
     * the underlying symbol for each root
     */
    protected final static Map<String,String> underlyings = new LinkedHashMap<String,String>();
    /**
     * a set of test brokers
     */
    protected static BrokersStatus brokers;
    /**
     * determines how many execution reports should be produced for each order received
     */
    protected static int executionReportMultiplicity = 1;
    /**
     * test client used to simulate connections to the server
     */
    protected static MockClient client;
}

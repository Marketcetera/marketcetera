package org.marketcetera.client;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.*;

import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Currency;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Test {@link Client} implementation.
 * 
 * <p>This test client is used by many different tests. Users may customize this class or derive
 * new ones from it. Please take care to leave current implementation intact in order to avoid
 * disrupting other tests. The exception to this rule is for methods that are current unimplemented
 * (throw <code>UnsupportedOperationException</code>).
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
public class MockClient
        implements Client
{
    /**
     * Create a new MockClient instance.
     *
     * @param inClientParameters a <code>ClientParameters</code> value
     */
    public MockClient(ClientParameters inClientParameters)
    {
        clientParameters = inClientParameters;
    }
    /**
     * Create a new MockClient instance.
     */
    public MockClient()
    {
        this(null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void sendOrder(OrderSingle inOrderSingle)
            throws ConnectionException, OrderValidationException
    {
        if(sendOrderSingleConnectionException != null) {
            throw sendOrderSingleConnectionException;
        }
        if(sendOrderSingleValidationException != null) {
            throw sendOrderSingleValidationException;
        }
        orders.add(inOrderSingle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void sendOrder(OrderReplace inOrderReplace)
            throws ConnectionException, OrderValidationException
    {
        if(sendOrderReplaceConnectionException != null) {
            throw sendOrderReplaceConnectionException;
        }
        if(sendOrderReplaceValidationException != null) {
            throw sendOrderReplaceValidationException;
        }
        replaces.add(inOrderReplace);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void sendOrder(OrderCancel inOrderCancel)
            throws ConnectionException, OrderValidationException
    {
        if(sendOrderCancelConnectionException != null) {
            throw sendOrderCancelConnectionException;
        }
        if(sendOrderCancelValidationException != null) {
            throw sendOrderCancelValidationException;
        }
        cancels.add(inOrderCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrderRaw(org.marketcetera.trade.FIXOrder)
     */
    @Override
    public void sendOrderRaw(FIXOrder inFIXOrder)
            throws ConnectionException, OrderValidationException
    {
        throw new UnsupportedOperationException(); // TODO
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
     * @see org.marketcetera.client.Client#getEquityPositionAsOf(java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(Date inDate,
                                            Equity inEquity)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllEquityPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(Date inDate,
                                            Future inFuture)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllFuturePositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(Date inDate)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionPositionAsOf(java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(Date inDate,
                                            Option inOption)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllOptionPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                       String... inRootSymbols)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inOptionRoot)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void addReportListener(ReportListener inListener)
    {
        reportListeners.add(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void removeReportListener(ReportListener inListener)
    {
        reportListeners.remove(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addServerStatusListener(org.marketcetera.client.ServerStatusListener)
     */
    @Override
    public void addServerStatusListener(ServerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeServerStatusListener(org.marketcetera.client.ServerStatusListener)
     */
    @Override
    public void removeServerStatusListener(ServerStatusListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void addExceptionListener(ExceptionListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void removeExceptionListener(ExceptionListener inListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#close()
     */
    @Override
    public void close()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#reconnect()
     */
    @Override
    public void reconnect()
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#reconnect(org.marketcetera.client.ClientParameters)
     */
    @Override
    public void reconnect(ClientParameters inParameters)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getParameters()
     */
    @Override
    public ClientParameters getParameters()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getLastConnectTime()
     */
    @Override
    public Date getLastConnectTime()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUserInfo(org.marketcetera.trade.UserID, boolean)
     */
    @Override
    public UserInfo getUserInfo(UserID inId,
                                boolean inUseCache)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#isCredentialsMatch(java.lang.String, char[])
     */
    @Override
    public boolean isCredentialsMatch(String inUsername,
                                      char[] inPassword)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#isServerAlive()
     */
    @Override
    public boolean isServerAlive()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#setUserData(java.util.Properties)
     */
    @Override
    public void setUserData(Properties inProperties)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUserData()
     */
    @Override
    public Properties getUserData()
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
            throws ConnectionException
    {
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#deleteReport(org.marketcetera.trade.ExecutionReportImpl)
     */
    @Override
    public void deleteReport(ExecutionReportImpl inReport)
            throws ConnectionException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#findRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID findRootOrderIdFor(OrderID inOrderID)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOpenOrders()
     */
    @Override
    public List<ReportBaseImpl> getOpenOrders()
            throws ConnectionException
    {
        return openOrders;
    }
    @Override
    public BigDecimal getCurrencyPositionAsOf(Date inDate, Currency inCurrency)
            throws ConnectionException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf(
            Date inDate) throws ConnectionException {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addReport(org.marketcetera.trade.FIXMessageWrapper, org.marketcetera.trade.BrokerID, org.marketcetera.trade.Hierarchy)
     */
    @Override
    public void addReport(FIXMessageWrapper inReport,
                          BrokerID inBrokerID,
                          Hierarchy inHierarchy)
            throws ConnectionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Gets the <code>ClientParameters</code> value used to initialize the client.
     *
     * @return a <code>ClientParameters</code> value or <code>null</code>
     */
    public ClientParameters getClientParameters()
    {
        return clientParameters;
    }
    /**
     * Gets the registered report listeners.
     *
     * @return a <code>Collection&lt;ReportListener&gt;</code> value
     */
    public Collection<ReportListener> getReportListeners()
    {
        return reportListeners;
    }
    /**
     * Gets the reports used to feed report-based operations.
     *
     * @return a <code>Set&lt;ReportBase&gt;</code> value
     */
    public Set<ReportBase> getReports()
    {
        return reports;
    }
    /**
     * Gets the orders collected via {@link #sendOrder(OrderSingle)}.
     *
     * @return a <code>Collection&lt;OrderSingle&gt;</code> value
     */
    public Collection<OrderSingle> getOrders()
    {
        return orders;
    }
    /**
     * Get the replaces value.
     *
     * @return a <code>Collection&lt;OrderReplace&gt;</code> value
     */
    public Collection<OrderReplace> getReplaces()
    {
        return replaces;
    }
    /**
     * Get the cancels value.
     *
     * @return a <code>Collection&lt;OrderCancel&gt;</code> value
     */
    public Collection<OrderCancel> getCancels()
    {
        return cancels;
    }
    /**
     * Sends the given <code>ReportBase</code> to registered report listeners.
     *
     * @param inReport a <code>ReportBase</code> value
     */
    public void sendToListeners(ReportBase inReport)
    {
        for(ReportListener reportListener : reportListeners) {
            if(inReport instanceof ExecutionReport) {
                reportListener.receiveExecutionReport((ExecutionReport)inReport);
            } else if(inReport instanceof OrderCancelReject) {
                reportListener.receiveCancelReject((OrderCancelReject)inReport);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
    /**
     * Sets the exception to be thrown during {@link #getReportsSince(Date)}.
     *
     * @param inException a <code>ConnectionException</code> value or <code>null</code>
     */
    public void setGetReportsSinceException(ConnectionException inException)
    {
        getReportsSinceThrows = inException;
    }
    /**
     * Sets the exception to be thrown during {@link #sendOrder(OrderSingle)}.
     *
     * @param inException a <code>ConnectionException</code> value or <code>null</code>
     */
    public void setSendOrderSingleException(ConnectionException inException)
    {
        sendOrderSingleConnectionException = inException;
    }
    /**
     * Sets the sendOrderReplaceConnectionException value.
     *
     * @param a <code>ConnectionException</code> value
     */
    public void setSendOrderReplaceConnectionException(ConnectionException inSendOrderReplaceConnectionException)
    {
        sendOrderReplaceConnectionException = inSendOrderReplaceConnectionException;
    }
    /**
     * Sets the sendOrderCancelConnectionException value.
     *
     * @param a <code>ConnectionException</code> value
     */
    public void setSendOrderCancelConnectionException(ConnectionException inSendOrderCancelConnectionException)
    {
        sendOrderCancelConnectionException = inSendOrderCancelConnectionException;
    }
    /**
     * Sets the sendOrderSingleValidationException value.
     *
     * @param an <code>OrderValidationException</code> value
     */
    public void setSendOrderSingleValidationException(OrderValidationException inSendOrderSingleValidationException)
    {
        sendOrderSingleValidationException = inSendOrderSingleValidationException;
    }
    /**
     * Sets the sendOrderReplaceValidationException value.
     *
     * @param an <code>OrderValidationException</code> value
     */
    public void setSendOrderReplaceValidationException(OrderValidationException inSendOrderReplaceValidationException)
    {
        sendOrderReplaceValidationException = inSendOrderReplaceValidationException;
    }
    /**
     * Sets the sendOrderCancelValidationException value.
     *
     * @param an <code>OrderValidationException</code> value
     */
    public void setSendOrderCancelValidationException(OrderValidationException inSendOrderCancelValidationException)
    {
        sendOrderCancelValidationException = inSendOrderCancelValidationException;
    }
    /**
     * Clears the client state.
     */
    public void reset()
    {
        reportListeners.clear();
        reports.clear();
        orders.clear();
        replaces.clear();
        cancels.clear();
        openOrders.clear();
        setGetReportsSinceException(null);
        setSendOrderSingleException(null);
        setSendOrderReplaceConnectionException(null);
        setSendOrderCancelConnectionException(null);
        setSendOrderSingleValidationException(null);
        setSendOrderReplaceValidationException(null);
        setSendOrderCancelValidationException(null);
    }
    /**
     * Creates {@link MockClient} objects.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    public static class MockClientFactory
            implements ClientFactory
    {
        /* (non-Javadoc)
         * @see org.marketcetera.client.ClientFactory#getClient(org.marketcetera.client.ClientParameters)
         */
        @Override
        public Client getClient(ClientParameters inClientParameters)
                throws ClientInitException, ConnectionException
        {
            return new MockClient(inClientParameters);
        }
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
     * reports used to feed report-related calls
     */
    private final Set<ReportBase> reports = new TreeSet<ReportBase>(ReportSendingTimeComparator.INSTANCE);
    /**
     * open orders (in addition to existing reports to return)
     */
    private final List<ReportBaseImpl> openOrders = Lists.newArrayList();
    /**
     * orders sent since last {@link #reset()}
     */
    private final Collection<OrderSingle> orders = new ArrayList<OrderSingle>();
    /**
     * replaces sent since last {@link #reset()}
     */
    private final Collection<OrderReplace> replaces = new ArrayList<OrderReplace>();
    /**
     * cancels sent since last {@link #reset()}
     */
    private final Collection<OrderCancel> cancels = new ArrayList<OrderCancel>();
    /**
     * client parameters used to instantiate the client, may be <code>null</code>
     */
    private final ClientParameters clientParameters;
    /**
     * listeners registered for execution reports
     */
    private final Collection<ReportListener> reportListeners = new LinkedHashSet<ReportListener>();
    /**
     * if non-null, will be thrown during {@link #getReportsSince(Date)}.
     */
    private volatile ConnectionException getReportsSinceThrows;
    /**
     * if non-null, will be thrown during {@link #sendOrder(OrderSingle)}
     */
    private volatile ConnectionException sendOrderSingleConnectionException = null;
    /**
     * if non-null, will be thrown during {@link #sendOrder(OrderReplace)
     */
    private volatile ConnectionException sendOrderReplaceConnectionException = null;
    /**
     * if non-null, will be thrown during {@link #sendCancel(OrderCancel)
     */
    private volatile ConnectionException sendOrderCancelConnectionException = null;
    /**
     * if non-null, will be thrown during {@link #sendOrder(OrderSingle)}
     */
    private volatile OrderValidationException sendOrderSingleValidationException = null;
    /**
     * if non-null, will be thrown during {@link #sendOrder(OrderReplace)
     */
    private volatile OrderValidationException sendOrderReplaceValidationException = null;
    /**
     * if non-null, will be thrown during {@link #sendCancel(OrderCancel)
     */
    private volatile OrderValidationException sendOrderCancelValidationException = null;
}

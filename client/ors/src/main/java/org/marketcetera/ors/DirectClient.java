package org.marketcetera.ors;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.client.*;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.jms.OrderEnvelope;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.Util;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportPersistenceException;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.security.SingleSimpleUserQuery;
import org.marketcetera.ors.symbol.SymbolResolverServices;
import org.marketcetera.ors.ws.Messages;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: ServiceImpl.java 16664 2013-08-23 23:06:00Z colin $")
public class DirectClient
        implements Client
{
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void sendOrder(OrderSingle inOrderSingle)
            throws ConnectionException, OrderValidationException
    {
        OrderRoutingSystem.getInstance().getRequestHandler().receiveMessage(new OrderEnvelope(inOrderSingle,
                                                                                              sessionId));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void sendOrder(OrderReplace inOrderReplace)
            throws ConnectionException, OrderValidationException
    {
        OrderRoutingSystem.getInstance().getRequestHandler().receiveMessage(new OrderEnvelope(inOrderReplace,
                                                                                              sessionId));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void sendOrder(OrderCancel inOrderCancel)
            throws ConnectionException, OrderValidationException
    {
        OrderRoutingSystem.getInstance().getRequestHandler().receiveMessage(new OrderEnvelope(inOrderCancel,
                                                                                              sessionId));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrderRaw(org.marketcetera.trade.FIXOrder)
     */
    @Override
    public void sendOrderRaw(FIXOrder inFIXOrder)
            throws ConnectionException, OrderValidationException
    {
        OrderRoutingSystem.getInstance().getRequestHandler().receiveMessage(new OrderEnvelope(inFIXOrder,
                                                                                              sessionId));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getReportsSince(java.util.Date)
     */
    @Override
    public ReportBase[] getReportsSince(Date inDate)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getReportsSince(user,
                                                         inDate);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        } catch (ReportPersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getEquityPositionAsOf(java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(Date inDate,
                                            Equity inEquity)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getEquityPositionAsOf(user,
                                                               inDate,
                                                               inEquity);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllEquityPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getAllEquityPositionsAsOf(user,
                                                                   inDate);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(Date inDate,
                                            Future inFuture)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getFuturePositionAsOf(user,
                                                               inDate,
                                                               inFuture);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllFuturePositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(Date inDate)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getAllFuturePositionsAsOf(user,
                                                                   inDate);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionPositionAsOf(java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(Date inDate,
                                            Option inOption)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getOptionPositionAsOf(user,
                                                               inDate,
                                                               inOption);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllOptionPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getAllOptionPositionsAsOf(user,
                                                                   inDate);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                      String... inRootSymbols)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getOptionPositionsAsOf(user,
                                                                inDate,
                                                                inRootSymbols);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getCurrencyPositionAsOf(java.util.Date, org.marketcetera.trade.Currency)
     */
    @Override
    public BigDecimal getCurrencyPositionAsOf(Date inDate,
                                              Currency inCurrency)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getCurrencyPositionAsOf(user,
                                                                 inDate,
                                                                 inCurrency);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllCurrencyPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getAllCurrencyPositionsAsOf(user,
                                                                     inDate);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inOptionRoot)
            throws ConnectionException
    {
        OptionRootUnderlyingMap map = OptionRootUnderlyingMap.getInstance();
        return map == null ? null: map.getUnderlying(inOptionRoot);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
            throws ConnectionException
    {
        OptionRootUnderlyingMap map = OptionRootUnderlyingMap.getInstance();
        return map == null ? null: map.getOptionRoots(inUnderlying);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void addReportListener(ReportListener inListener)
    {
        subscriptionManager.addReportListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void removeReportListener(ReportListener inListener)
    {
        subscriptionManager.removeReportListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        subscriptionManager.addBrokerStatusListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        subscriptionManager.removeBrokerStatusListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addServerStatusListener(org.marketcetera.client.ServerStatusListener)
     */
    @Override
    public void addServerStatusListener(ServerStatusListener inListener)
    {
        subscriptionManager.addServerStatusListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeServerStatusListener(org.marketcetera.client.ServerStatusListener)
     */
    @Override
    public void removeServerStatusListener(ServerStatusListener inListener)
    {
        subscriptionManager.removeServerStatusListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void addExceptionListener(ExceptionListener inListener)
    {
        subscriptionManager.addExceptionListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void removeExceptionListener(ExceptionListener inListener)
    {
        subscriptionManager.removeExceptionListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#close()
     */
    @Override
    public void close()
    {
        // nothing to do
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#reconnect()
     */
    @Override
    public void reconnect()
            throws ConnectionException
    {
        reconnect(parameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#reconnect(org.marketcetera.client.ClientParameters)
     */
    @Override
    public void reconnect(ClientParameters inParameters)
            throws ConnectionException
    {
        parameters = inParameters;
        try {
            refreshParameters();
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
        connectTime = new Date();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getParameters()
     */
    @Override
    public ClientParameters getParameters()
    {
        return parameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getLastConnectTime()
     */
    @Override
    public Date getLastConnectTime()
    {
        return connectTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
            throws ConnectionException
    {
        return OrderRoutingSystem.getInstance().getBrokers().getStatus(user.getName());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUserInfo(org.marketcetera.trade.UserID, boolean)
     */
    @Override
    public UserInfo getUserInfo(UserID inId,
                                boolean inUseCache)
            throws ConnectionException
    {
        try {
            SimpleUser user = (new SingleSimpleUserQuery(inId.getValue())).fetch();
            return new UserInfo(user.getName(),
                                user.getUserID(),
                                user.isActive(),
                                user.isSuperuser(),
                                Util.propertiesFromString(user.getUserData()));
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#isCredentialsMatch(java.lang.String, char[])
     */
    @Override
    public boolean isCredentialsMatch(String inUsername,
                                      char[] inPassword)
    {
        return ObjectUtils.equals(inUsername, parameters.getUsername()) && ObjectUtils.equals(inPassword,parameters.getPassword());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#isServerAlive()
     */
    @Override
    public boolean isServerAlive()
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#setUserData(java.util.Properties)
     */
    @Override
    public void setUserData(Properties inProperties)
            throws ConnectionException
    {
        try {
            SimpleUser workingUser = new SingleSimpleUserQuery(user.getId()).fetch();
            workingUser.setUserData(Util.propertiesToString(inProperties));
            workingUser.save();
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUserData()
     */
    @Override
    public Properties getUserData()
            throws ConnectionException
    {
        try {
            return Util.propertiesFromString(new SingleSimpleUserQuery(user.getId()).fetch().getUserData());
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addReport(org.marketcetera.trade.FIXMessageWrapper, org.marketcetera.trade.BrokerID)
     */
    @Override
    public void addReport(FIXMessageWrapper inReport,
                          BrokerID inBrokerID)
            throws ConnectionException
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {} from user({}) for {} to add", //$NON-NLS-1$
                               inReport,
                               user,
                               inBrokerID);
        try {
            ExecutionReport newReport = Factory.getInstance().createExecutionReport(inReport.getMessage(),
                                                                                    inBrokerID,
                                                                                    Originator.Broker,
                                                                                    user.getUserID(),
                                                                                    user.getUserID());
            OrderRoutingSystem.getInstance().getOrderReceiver().addReport(newReport);
        } catch (Exception e) {
            Messages.CANNOT_ADD_REPORT.warn(this,
                                            user,
                                            inBrokerID,
                                            ExceptionUtils.getRootCauseMessage(e));
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#deleteReport(org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void deleteReport(ExecutionReport inReport)
            throws ConnectionException
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {} from {} to delete", //$NON-NLS-1$
                               inReport,
                               user);
        try {
            OrderRoutingSystem.getInstance().getOrderReceiver().deleteReport(inReport);
        } catch (Exception e) {
            Messages.CANNOT_DELETE_REPORT.warn(this,
                                               user,
                                               ExceptionUtils.getRootCauseMessage(e));
            throw new ConnectionException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
            throws ConnectionException
    {
        return symbolResolverServices.resolveSymbol(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOpenOrders()
     */
    @Override
    public List<ReportBase> getOpenOrders()
            throws ConnectionException
    {
        try {
            return reportHistoryServices.getOpenOrders(user);
        } catch (PersistenceException e) {
            throw new ConnectionException(e);
        }
    }
    /**
     * Create a new DirectClient instance.
     *
     * @param inParameters
     * @param inSymbolResolverServices 
     * @param inReportHistoryServices 
     * @throws ClientInitException 
     */
    DirectClient(ClientParameters inParameters,
                 ReportHistoryServices inReportHistoryServices,
                 SymbolResolverServices inSymbolResolverServices)
            throws ClientInitException
    {
        Validate.notNull(inParameters);
        parameters = inParameters;
        try {
            refreshParameters();
        } catch (PersistenceException e) {
            throw new ClientInitException(e);
        }
        reportHistoryServices = inReportHistoryServices;
        symbolResolverServices = inSymbolResolverServices;
        connectTime = new Date();
    }
    /**
     * 
     *
     *
     * @throws PersistenceException
     */
    private void refreshParameters()
            throws PersistenceException
    {
        user = new SingleSimpleUserQuery(parameters.getUsername()).fetch();
        sessionId = SessionId.generate(); // TODO somehow, we need to set this value properly
    }
    /**
     * 
     */
    private final ClientSubscriptionManager subscriptionManager = new ClientSubscriptionManager();
    /**
     * 
     */
    private volatile Date connectTime = new Date();
    /**
     * 
     */
    private volatile ClientParameters parameters;
    /**
     * 
     */
    private volatile SimpleUser user;
    /**
     * 
     */
    private volatile SessionId sessionId;
    /**
     * 
     */
    private final ReportHistoryServices reportHistoryServices;
    /**
     * 
     */
    private final SymbolResolverServices symbolResolverServices;
}

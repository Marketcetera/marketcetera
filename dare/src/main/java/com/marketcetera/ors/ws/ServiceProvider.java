package com.marketcetera.ors.ws;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.Util;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Message;

import com.marketcetera.ors.OptionRootUnderlyingMap;
import com.marketcetera.ors.OrderRoutingSystem;
import com.marketcetera.ors.brokers.BrokerService;
import com.marketcetera.ors.dao.UserService;
import com.marketcetera.ors.history.ReportHistoryServices;
import com.marketcetera.ors.security.SimpleUser;

/* $License$ */

/**
 * Provides Server services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ServiceProvider.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.5.0
 */
public class ServiceProvider
{
    /**
     * Validates and starts object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(historyServices);
        Validate.notNull(brokerService);
        Validate.notNull(userService);
        Validate.notNull(idFactory);
        Validate.notNull(symbolResolverServices);
    }
    /**
     * Gets the reports since the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>ReportBaseImpl[]</code> value
     */
    public ReportBaseImpl[] getReportsSince(SimpleUser inUser,
                                            Date inDate)
    {
        return getHistoryServices().getReportsSince(inUser,
                                                    inDate);
    }
    /**
     * Returns the open orders visible to the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @return a <code>List&lt;ReportBaseImpl&gt;</code> value
     */
    public List<ReportBaseImpl> getOpenOrders(SimpleUser inUser)
    {
        return getHistoryServices().getOpenOrders(inUser);
    }
    /**
     * Adds the given message to the system data bus.
     *
     * @param inReport a <code>Message</code> value
     * @param inBrokerID a <code>BrokerID</code> value
     * @param inUserId a <code>UserID</code> value
     * @param inHierarchy a <code>Hierarchy</code> value
     */
    public void addReport(Message inReport,
                          BrokerID inBrokerID,
                          UserID inUserId,
                          Hierarchy inHierarchy)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {} from user({}) for {} to add", //$NON-NLS-1$
                               inReport,
                               inUserId,
                               inBrokerID);
        try {
            ReportBase newReport;
            if(FIXMessageUtil.isExecutionReport(inReport)) {
                newReport = Factory.getInstance().createExecutionReport(inReport,
                                                                        inBrokerID,
                                                                        Originator.Broker,
                                                                        inHierarchy,
                                                                        inUserId,
                                                                        inUserId);
            } else if(FIXMessageUtil.isCancelReject(inReport)) {
                newReport = Factory.getInstance().createOrderCancelReject(inReport,
                                                                          inBrokerID,
                                                                          Originator.Broker,
                                                                          inUserId,
                                                                          inUserId);
            } else {
                throw new UnsupportedOperationException();
            }
            OrderRoutingSystem.getInstance().getOrderReceiver().addReport(newReport);
        } catch (Exception e) {
            Messages.CANNOT_ADD_REPORT.warn(this,
                                            e,
                                            inUserId,
                                            inBrokerID,
                                            ExceptionUtils.getRootCauseMessage(e));
            throw new RuntimeException(ExceptionUtils.getRootCauseMessage(e));
        }
    }
    /**
     * Deletes the given report from the system persistence.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @param inUserId a <code>UserID</code> value
     * @throws PersistenceException if the report cannot be deleted
     */
    public void deleteReport(ExecutionReport inReport,
                             UserID inUserId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {} from {} to delete", //$NON-NLS-1$
                               inReport,
                               inUserId);
        try {
            OrderRoutingSystem.getInstance().getOrderReceiver().deleteReport(inReport);
        } catch (Exception e) {
            Messages.CANNOT_DELETE_REPORT.warn(this,
                                               inUserId,
                                               ExceptionUtils.getRootCauseMessage(e));
            throw new PersistenceException(e);
        }
    }
    /**
     * Gets the brokers' status.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>BrokersStatus</code> value
     */
    public BrokersStatus getBrokersStatus(String inUsername)
    {
        // TODO filter by visible users
        return brokerService.getBrokersStatus();
    }
    /**
     * Gets info for the user with the given id.
     *
     * @param inUserId a <code>UserID</code> value
     * @return a <code>UserInfo</code> value
     * @throws PersistenceException
     */
    public UserInfo getUserInfo(UserID inUserId)
    {
        SimpleUser u = userService.findOne(inUserId.getValue());
        return new UserInfo(u.getName(),u.getUserID(),u.isActive(),u.isSuperuser(),Util.propertiesFromString(u.getUserData()));
    }
    /**
     * Gets the position visible of the given equity as of the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inEquity an <code>Equity</code> value
     * @return a <code>BigDecimal</code>value
     */
    public BigDecimal getEquityPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Equity inEquity)
    {
        return getHistoryServices().getEquityPositionAsOf(inUser,
                                                          inDate,
                                                          inEquity);
    }
    /**
     * Gets all the equity positions visible to the given user as of the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Equity&gt;,BigDecimal&gt;</code>value
     */
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(SimpleUser inUser,
                                                                                Date inDate)
    {
        return new MapWrapper<PositionKey<Equity>,BigDecimal>(getHistoryServices().getAllEquityPositionsAsOf(inUser,
                                                                                                             inDate));
    }
    /**
     * Gets the position visible to the given user of the given currency as of the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inCurrency a <code>Currency</code> value
     * @return a <code>BigDecimal</code>value
     */
    public BigDecimal getCurrencyPositionAsOf(SimpleUser inUser,
                                              Date inDate,
                                              Currency inCurrency)
    {
        return getHistoryServices().getCurrencyPositionAsOf(inUser,
                                                            inDate,
                                                            inCurrency);
    }
    /**
     * Gets all the currency positions visible to the given user as of the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Currency&gt;,BigDecimal&gt;</code>value
     */
    public MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(SimpleUser inUser,
                                                                                    Date inDate)
    {
        return new MapWrapper<PositionKey<Currency>,BigDecimal>(getHistoryServices().getAllCurrencyPositionsAsOf(inUser,
                                                                                                                 inDate));
    }
    /**
     * Gets the position visible to the given user for the given future.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inFuture a <code>Future</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getFuturePositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Future inFuture)
    {
        return getHistoryServices().getFuturePositionAsOf(inUser,
                                                          inDate,
                                                          inFuture);
    }
    /**
     * Gets all future positions visible to the given user as of the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Future&gt;,BigDecimal&gt;</code> value
     */
    public MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(SimpleUser inUser,
                                                                                Date inDate)
    {
        return new MapWrapper<PositionKey<Future>,BigDecimal>(getHistoryServices().getAllFuturePositionsAsOf(inUser,
                                                                                                             inDate));
    }
    /**
     * Gets the position visible to the given user for the given option.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inOption an <code>Option</code> value
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOptionPositionAsOf(SimpleUser inUser,
                                            Date inDate,
                                            Option inOption)
     {
        return getHistoryServices().getOptionPositionAsOf(inUser,
                                                          inDate,
                                                          inOption);
     }
    /**
     * Gets all option positions visible to the given user as of the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     */
    public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(SimpleUser inUser,
                                                                                Date inDate)
    {
        return new MapWrapper<PositionKey<Option>,BigDecimal>(getHistoryServices().getAllOptionPositionsAsOf(inUser,
                                                                                                             inDate));
    }
    /**
     * Gets all option positions visible to the given user as of the given date.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @param inDate a <code>Date</code> value
     * @param inSymbols a <code>String...</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     */
    public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(SimpleUser inUser,
                                                                             Date inDate,
                                                                             String...inSymbols)
    {
        return new MapWrapper<PositionKey<Option>,BigDecimal>(getHistoryServices().getOptionPositionsAsOf(inUser,
                                                                                                          inDate,
                                                                                                          inSymbols));
    }
    /**
     * Gets the next order id value.
     *
     * @return a <code>String</code> value
     * @throws CoreException if an id cannot be generated
     */
    public String getNextOrderId()
        throws CoreException
    {
        return getIdFactory().getNext();
    }
    /**
     * Gets the underlying symbol of the given root.
     *
     * @param inOptionRoot a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String getUnderlying(String inOptionRoot)
    {
        OptionRootUnderlyingMap map = OptionRootUnderlyingMap.getInstance();
        return map == null? null: map.getUnderlying(inOptionRoot);
    }
    /**
     * Gets the option roots for the given underlying value.
     *
     * @param inUnderlying a <code>String</code> value
     * @return a <code>Collection&lt;String&gt;</code> value
     */
    public Collection<String> getOptionRoots(String inUnderlying)
    {
        OptionRootUnderlyingMap map = OptionRootUnderlyingMap.getInstance();
        return map == null? null: map.getOptionRoots(inUnderlying);
    }
    /**
     * Gets the user data associated with the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String getUserData(String inUsername)
    {
        return userService.findByName(inUsername).getUserData();
    }
    /**
     * Resolves the given symbol to an <code>Instrument</code>.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    public Instrument resolveSymbol(String inSymbol)
    {
        return symbolResolverServices.resolveSymbol(inSymbol);
    }
    /**
     * Gets the order ID of the root order in the order chain for the given order ID.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value or <code>null</code>
     */
    public OrderID getRootOrderIdFor(OrderID inOrderID)
    {
        return getHistoryServices().getRootOrderIdFor(inOrderID);
    }
    /**
     * Sets the user data associated with the given username.
     *
     * @param inUsername a <code>String</code> value
     * @param inUserData a <code>String</code> value
     */
    public void setUserData(String inUsername,
                                 String inUserData)
    {
        userService.updateUserDataByName(inUsername,
                                         inUserData);
    }
    /**
     * Get the historyServices value.
     *
     * @return a <code>ReportHistoryServices</code> value
     */
    public ReportHistoryServices getHistoryServices()
    {
        return historyServices;
    }
    /**
     * Sets the historyServices value.
     *
     * @param inHistoryServices a <code>ReportHistoryServices</code> value
     */
    public void setHistoryServices(ReportHistoryServices inHistoryServices)
    {
        historyServices = inHistoryServices;
    }
    /**
     * Get the userService value.
     *
     * @return a <code>UserService</code> value
     */
    public UserService getUserService()
    {
        return userService;
    }
    /**
     * Sets the userService value.
     *
     * @param inUserService a <code>UserService</code> value
     */
    public void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * Get the idFactory value.
     *
     * @return an <code>IDFactory</code> value
     */
    public IDFactory getIdFactory()
    {
        return idFactory;
    }
    /**
     * Sets the idFactory value.
     *
     * @param inIdFactory an <code>IDFactory</code> value
     */
    public void setIdFactory(IDFactory inIdFactory)
    {
        idFactory = inIdFactory;
    }
    /**
     * Get the symbolResolverServices value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    public SymbolResolverService getSymbolResolverServices()
    {
        return symbolResolverServices;
    }
    /**
     * Sets the symbolResolverServices value.
     *
     * @param inSymbolResolverServices a <code>SymbolResolverService</code> value
     */
    public void setSymbolResolverServices(SymbolResolverService inSymbolResolverServices)
    {
        symbolResolverServices = inSymbolResolverServices;
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to user objects
     */
    @Autowired
    private UserService userService;
    /**
     * provides history services
     */
    @Autowired
    private ReportHistoryServices historyServices;
    /**
     * provides unique ids
     */
    @Autowired
    private IDFactory idFactory;
    /**
     * provides symbol resolution services
     */
    @Autowired
    private SymbolResolverService symbolResolverServices;
}

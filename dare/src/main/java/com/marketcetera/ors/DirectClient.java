package com.marketcetera.ors;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.marketcetera.client.BrokerStatusListener;
import org.marketcetera.client.BrokerStatusPublisher;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.OrderModifier;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.ReportListener;
import org.marketcetera.client.ReportPublisher;
import org.marketcetera.client.Validations;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.jms.DataEnvelope;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.Event;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReportImpl;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.admin.service.AuthorizationService;
import com.marketcetera.ors.dao.ReportService;
import com.marketcetera.ors.dao.UserService;
import com.marketcetera.ors.security.SimpleUser;
import com.marketcetera.ors.ws.ClientSession;
import com.marketcetera.ors.ws.ServiceProvider;

/* $License$ */

/**
 * Provides in-process access the client services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DirectClient.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.5.0
 */
public class DirectClient
        implements Client
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(username);
        Validate.notNull(userService);
        Validate.notNull(serviceProvider);
        Validate.notNull(reportService);
        Validate.notNull(brokerStatusPublishers);
        Validate.notNull(reportPublisher);
        Validate.notNull(requestHandler);
        user = userService.findByName(username);
        Validate.notNull(user);
        params = new ClientParameters(username,
                                      username.toCharArray(),
                                      null,
                                      null,
                                      0);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void sendOrder(OrderSingle inOrderSingle)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inOrderSingle);
        modifyOrder(inOrderSingle);
        DataEnvelope envelope = new DataEnvelope(inOrderSingle,
                                                 getSessionId());
        requestHandler.receiveMessage(envelope);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void sendOrder(OrderReplace inOrderReplace)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inOrderReplace);
        modifyOrder(inOrderReplace);
        DataEnvelope envelope = new DataEnvelope(inOrderReplace,
                                                 getSessionId());
        requestHandler.receiveMessage(envelope);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void sendOrder(OrderCancel inOrderCancel)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inOrderCancel);
        modifyOrder(inOrderCancel);
        DataEnvelope envelope = new DataEnvelope(inOrderCancel,
                                                 getSessionId());
        requestHandler.receiveMessage(envelope);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendOrderRaw(org.marketcetera.trade.FIXOrder)
     */
    @Override
    public void sendOrderRaw(FIXOrder inFIXOrder)
            throws ConnectionException, OrderValidationException
    {
        Validations.validate(inFIXOrder);
        modifyOrder(inFIXOrder);
        DataEnvelope envelope = new DataEnvelope(inFIXOrder,
                                                 getSessionId());
        requestHandler.receiveMessage(envelope);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#sendEvent(org.marketcetera.event.Event)
     */
    @Override
    public void sendEvent(Event inEvent)
            throws ConnectionException
    {
        DataEnvelope envelope = new DataEnvelope(inEvent,
                                                 getSessionId());
        requestHandler.receiveMessage(envelope);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getReportsSince(java.util.Date)
     */
    @Override
    public ReportBase[] getReportsSince(Date inDate)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewReportAction.name());
        return serviceProvider.getReportsSince(user,
                                               inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getEquityPositionAsOf(java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(Date inDate,
                                            Equity inEquity)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getEquityPositionAsOf(user,
                                                     inDate,
                                                     inEquity);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllEquityPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getAllEquityPositionsAsOf(user,
                                                         inDate).getMap();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(Date inDate,
                                            Future inFuture)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getFuturePositionAsOf(user,
                                                     inDate,
                                                     inFuture);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllFuturePositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(Date inDate)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getAllFuturePositionsAsOf(user,
                                                         inDate).getMap();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionPositionAsOf(java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(Date inDate,
                                            Option inOption)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getOptionPositionAsOf(user,
                                                     inDate,
                                                     inOption);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllOptionPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getAllOptionPositionsAsOf(user,
                                                         inDate).getMap();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                      String... inRootSymbols)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getOptionPositionsAsOf(user,
                                                      inDate,
                                                      inRootSymbols).getMap();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getCurrencyPositionAsOf(java.util.Date, org.marketcetera.trade.Currency)
     */
    @Override
    public BigDecimal getCurrencyPositionAsOf(Date inDate,
                                              Currency inCurrency)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getCurrencyPositionAsOf(user,
                                                       inDate,
                                                       inCurrency);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllCurrencyPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(Date inDate)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewPositionAction.name());
        return serviceProvider.getAllCurrencyPositionsAsOf(user,
                                                           inDate).getMap();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inOptionRoot)
            throws ConnectionException
    {
        return serviceProvider.getUnderlying(inOptionRoot);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
            throws ConnectionException
    {
        return serviceProvider.getOptionRoots(inUnderlying);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void addReportListener(ReportListener inListener)
    {
        reportPublisher.addReportListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeReportListener(org.marketcetera.client.ReportListener)
     */
    @Override
    public void removeReportListener(ReportListener inListener)
    {
        reportPublisher.removeReportListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void addBrokerStatusListener(BrokerStatusListener inListener)
    {
        for(BrokerStatusPublisher publisher : brokerStatusPublishers) {
            publisher.addBrokerStatusListener(inListener);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeBrokerStatusListener(org.marketcetera.client.BrokerStatusListener)
     */
    @Override
    public void removeBrokerStatusListener(BrokerStatusListener inListener)
    {
        for(BrokerStatusPublisher publisher : brokerStatusPublishers) {
            publisher.removeBrokerStatusListener(inListener);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void addServerStatusListener(ServerStatusListener inListener)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeServerStatusListener(org.marketcetera.core.notifications.ServerStatusListener)
     */
    @Override
    public void removeServerStatusListener(ServerStatusListener inListener)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void addExceptionListener(ExceptionListener inListener)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeExceptionListener(java.beans.ExceptionListener)
     */
    @Override
    public void removeExceptionListener(ExceptionListener inListener)
    {
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
        // nothing to do
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#reconnect(org.marketcetera.client.ClientParameters)
     */
    @Override
    public void reconnect(ClientParameters inParameters)
            throws ConnectionException
    {
        // nothing to do
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getParameters()
     */
    @Override
    public ClientParameters getParameters()
    {
        return params;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getLastConnectTime()
     */
    @Override
    public Date getLastConnectTime()
    {
        return new Date();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getBrokersStatus()
     */
    @Override
    public BrokersStatus getBrokersStatus()
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewBrokerStatusAction.name());
        return serviceProvider.getBrokersStatus(username);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUserInfo(org.marketcetera.trade.UserID, boolean)
     */
    @Override
    public UserInfo getUserInfo(UserID inId,
                                boolean inUseCache)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewUserDataAction.name());
        return serviceProvider.getUserInfo(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#isCredentialsMatch(java.lang.String, char[])
     */
    @Override
    public boolean isCredentialsMatch(String inUsername,
                                      char[] inPassword)
    {
        return true;
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
        authzService.authorize(getUsername(),
                               TradingPermissions.WriteUserDataAction.name());
        serviceProvider.setUserData(username,
                                    Util.propertiesToString(inProperties));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUserData()
     */
    @Override
    public Properties getUserData()
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewUserDataAction.name());
        return Util.propertiesFromString(serviceProvider.getUserData(username));
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
        authzService.authorize(getUsername(),
                               TradingPermissions.AddReportAction.name());
        serviceProvider.addReport(inReport.getMessage(),
                                  inBrokerID,
                                  user.getUserID(),
                                  inHierarchy);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#deleteReport(org.marketcetera.trade.ExecutionReportImpl)
     */
    @Override
    public void deleteReport(ExecutionReportImpl inReport)
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.DeleteReportAction.name());
        serviceProvider.deleteReport(inReport,
                                     user.getUserID());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
            throws ConnectionException
    {
        return serviceProvider.resolveSymbol(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getOpenOrders()
     */
    @Override
    public List<ReportBaseImpl> getOpenOrders()
            throws ConnectionException
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewOpenOrdersAction.name());
        return serviceProvider.getOpenOrders(user);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#findRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID findRootOrderIdFor(OrderID inOrderID)
    {
        authzService.authorize(getUsername(),
                               TradingPermissions.ViewOpenOrdersAction.name());
        return reportService.getRootOrderIdFor(inOrderID);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#addOrderModifier(org.marketcetera.client.OrderModifier)
     */
    @Override
    public void addOrderModifier(OrderModifier inOrderModifier)
    {
        synchronized(orderModifiers) {
            orderModifiers.addLast(inOrderModifier);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#removeOrderModifier(org.marketcetera.client.OrderModifier)
     */
    @Override
    public void removeOrderModifier(OrderModifier inOrderModifier)
    {
        synchronized(orderModifiers) {
            orderModifiers.remove(inOrderModifier);
        }
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
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
     * Get the user value.
     *
     * @return a <code>SimpleUser</code> value
     */
    public SimpleUser getUser()
    {
        return user;
    }
    /**
     * Sets the user value.
     *
     * @param inUser a <code>SimpleUser</code> value
     */
    public void setUser(SimpleUser inUser)
    {
        user = inUser;
    }
    /**
     * Get the serviceProvider value.
     *
     * @return a <code>ServiceProvider</code> value
     */
    public ServiceProvider getServiceProvider()
    {
        return serviceProvider;
    }
    /**
     * Sets the serviceProvider value.
     *
     * @param inServiceProvider a <code>ServiceProvider</code> value
     */
    public void setServiceProvider(ServiceProvider inServiceProvider)
    {
        serviceProvider = inServiceProvider;
    }
    /**
     * Get the reportService value.
     *
     * @return a <code>ReportService</code> value
     */
    public ReportService getReportService()
    {
        return reportService;
    }
    /**
     * Sets the reportService value.
     *
     * @param inReportService a <code>ReportService</code> value
     */
    public void setReportService(ReportService inReportService)
    {
        reportService = inReportService;
    }
    /**
     * Get the reportPublisher value.
     *
     * @return a <code>ReportPublisher</code> value
     */
    public ReportPublisher getReportPublisher()
    {
        return reportPublisher;
    }
    /**
     * Sets the reportPublisher value.
     *
     * @param inReportPublisher a <code>ReportPublisher</code> value
     */
    public void setReportPublisher(ReportPublisher inReportPublisher)
    {
        reportPublisher = inReportPublisher;
    }
    /**
     * Get the brokerStatusPublisher value.
     *
     * @return a <code>List&lt;BrokerStatusPublisher&gt;</code> value
     */
    public List<BrokerStatusPublisher> getBrokerStatusPublishers()
    {
        return brokerStatusPublishers;
    }
    /**
     * Sets the brokerStatusPublisher value.
     *
     * @param inBrokerStatusPublishers a <code>List&lt;BrokerStatusPublisher&gt;</code> value
     */
    public void setBrokerStatusPublishers(List<BrokerStatusPublisher> inBrokerStatusPublishers)
    {
        brokerStatusPublishers.clear();
        if(inBrokerStatusPublishers != null) {
            brokerStatusPublishers.addAll(inBrokerStatusPublishers);
        }
    }
    /**
     * Get the sessionManager value.
     *
     * @return a <code>SessionManager<ClientSession></code> value
     */
    public SessionManager<ClientSession> getSessionManager()
    {
        return sessionManager;
    }
    /**
     * Sets the sessionManager value.
     *
     * @param inSessionManager a <code>SessionManager<ClientSession></code> value
     */
    public void setSessionManager(SessionManager<ClientSession> inSessionManager)
    {
        sessionManager = inSessionManager;
    }
    /**
     * Get the requestHandler value.
     *
     * @return a <code>RequestHandler</code> value
     */
    public RequestHandler getRequestHandler()
    {
        return requestHandler;
    }
    /**
     * Sets the requestHandler value.
     *
     * @param inRequestHandler a <code>RequestHandler</code> value
     */
    public void setRequestHandler(RequestHandler inRequestHandler)
    {
        requestHandler = inRequestHandler;
    }
    /**
     * Get the authzService value.
     *
     * @return an <code>AuthorizationService</code> value
     */
    public AuthorizationService getAuthzService()
    {
        return authzService;
    }
    /**
     * Sets the authzService value.
     *
     * @param inAuthzService an <code>AuthorizationService</code> value
     */
    public void setAuthzService(AuthorizationService inAuthzService)
    {
        authzService = inAuthzService;
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionId</code> value
     */
    @Override
    public SessionId getSessionId()
    {
        synchronized(sessionIdLock) {
            if(sessionId == null) {
                sessionId = generateSessionId();
            }
        }
        return sessionId;
    }
    /**
     * Generates a session id.
     *
     * @return a <code>SessionId</code> value
     */
    protected SessionId generateSessionId()
    {
        StatelessClientContext context = new StatelessClientContext();
        context.setAppId(new AppId("DirectClient"));
        context.setClientId(sessionManager.getServerId());
        context.setVersionId(new VersionId(ApplicationVersion.DEFAULT_VERSION.getVersionInfo()));
        LocaleWrapper locale = new LocaleWrapper(Locale.getDefault());
        context.setLocale(locale);
        sessionId = SessionId.generate();
        SessionHolder<ClientSession> sessionHolder = new SessionHolder<>(username,
                                                                         context);
        sessionManager.put(sessionId,
                           sessionHolder);
        return sessionId;
    }
    /**
     * Get the sessionIdLock value.
     *
     * @return a <code>Object</code> value
     */
    protected Object getSessionIdLock()
    {
        return sessionIdLock;
    }
    /**
     * Modify the given order.
     *
     * @param inOrder an <code>Order</code> value
     */
    private void modifyOrder(Order inOrder)
    {
        synchronized(orderModifiers) {
            for(OrderModifier orderModifier : orderModifiers) {
                orderModifier.modify(inOrder);
            }
        }
    }
    /**
     * stores client parameter values
     */
    private ClientParameters params;
    /**
     * username value for whom all operations are executed
     */
    private String username;
    /**
     * user for whom all operations are executed
     */
    private SimpleUser user;
    /**
     * provides access to user services
     */
    @Autowired
    private UserService userService;
    /**
     * provides actual services
     */
    @Autowired
    private ServiceProvider serviceProvider;
    /**
     * provides access to report services
     */
    @Autowired
    private ReportService reportService;
    /**
     * provides access to report publishing services
     */
    @Autowired
    private ReportPublisher reportPublisher;
    /**
     * provides broker status publisher services
     */
    private final List<BrokerStatusPublisher> brokerStatusPublishers = new ArrayList<>();
    /**
     * holds order modifiers, may be empty
     */
    private final Deque<OrderModifier> orderModifiers = new LinkedList<OrderModifier>();
    /**
     * holds client sessions
     */
    private SessionManager<ClientSession> sessionManager;
    /**
     * session id generated for all activity with this session
     */
    private SessionId sessionId;
    /**
     * handles order requests
     */
    @Autowired
    private RequestHandler requestHandler;
    /**
     * guards access to the session id object
     */
    private final Object sessionIdLock = new Object();
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}

package org.marketcetera.ors.ws;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jws.WebParam;

import org.marketcetera.admin.service.AuthorizationService;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.client.Service;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.TradingPermissions;
import org.marketcetera.ors.history.ReportPersistenceException;
import org.marketcetera.ors.rpc.RpcServerAdapter;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionReportImpl;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.RemoteRunner;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Message;


/* $License$ */

/**
 * The implementation of the application's web services.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ServiceImpl
        extends ServiceBaseImpl<ClientSession>
        implements Service,RpcServerAdapter
{
    /**
     * Creates a new service implementation with the given session
     * manager, brokers, and report history services provider.
     *
     * @param sessionManager The session manager, which may be null.
     */
    public ServiceImpl(SessionManager<ClientSession> sessionManager)
    {
        super(sessionManager);
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
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getBrokersStatus(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public BrokersStatus getBrokersStatus(ClientContext inContext)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BrokersStatus>(getSessionManager()) {
            @Override
            protected BrokersStatus call(ClientContext context,
                                         SessionHolder<ClientSession> sessionHolder)
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewBrokerStatusAction.name());
                return getBrokersStatus(getSessionManager().get(context.getSessionId()).getUser());
            }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getUserInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.UserID)
     */
    @Override
    public UserInfo getUserInfo(ClientContext inContext,
                                final UserID inUserId)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,UserInfo>(getSessionManager()) {
            @Override
            protected UserInfo call(ClientContext inContext,
                                    SessionHolder<ClientSession> inSessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(inSessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewUserDataAction.name());
                return getUserInfo(inUserId);
            }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getReportsSince(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public ReportBaseImpl[] getReportsSince(ClientContext inContext,
                                            final DateWrapper inDate)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,ReportBaseImpl[]>(getSessionManager()) {
            @Override
            protected ReportBaseImpl[] call(ClientContext inContext,
                                            SessionHolder<ClientSession> inSessionHolder)
                throws ReportPersistenceException,
                       PersistenceException
            {
                authzService.authorize(inSessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewReportAction.name());
                return serviceProvider.getReportsSince(inSessionHolder.getSession().getUser(),
                                                       inDate.getRaw());
            }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getEquityPositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(ClientContext inContext,
                                            final DateWrapper inDate,
                                            final Equity inEquity)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>(getSessionManager()) {
            @Override
            protected BigDecimal call(ClientContext inContext,
                                      SessionHolder<ClientSession> inSessionHolder)
                throws PersistenceException
            {
                authzService.authorize(inSessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getEquityPositionAsOf(inSessionHolder.getSession().getUser(),
                                                             inDate.getRaw(),
                                                             inEquity);
            }}).execute(inContext);
    }
    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(ClientContext inContext,
                                                                                final DateWrapper inDate)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Equity>,BigDecimal>>(getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Equity>,BigDecimal> call(ClientContext context,
                                                                      SessionHolder<ClientSession> inSessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(inSessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getAllEquityPositionsAsOf(inSessionHolder.getSession().getUser(),
                                                                 inDate.getRaw());
            }}).execute(inContext);
    }
    @Override
    public BigDecimal getCurrencyPositionAsOf(ClientContext context,
                                              final DateWrapper inDate,
                                              final Currency inCurrency)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>(getSessionManager()) {
            @Override
            protected BigDecimal call(ClientContext inContext,
                                      SessionHolder<ClientSession> inSessionHolder)
                throws PersistenceException
            {
                authzService.authorize(inSessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getCurrencyPositionAsOf(inSessionHolder.getSession().getUser(),
                                                               inDate.getRaw(),
                                                               inCurrency);
            }}).execute(context);
    }
    @Override
    public MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(ClientContext context,
                                                                                    final DateWrapper inDate)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Currency>,BigDecimal>>(getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Currency>,BigDecimal> call(ClientContext context,
                                                                        SessionHolder<ClientSession> inSessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(inSessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getAllCurrencyPositionsAsOf(inSessionHolder.getSession().getUser(),
                                                                   inDate.getRaw());
            }}).execute(context);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getAllFuturePositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(ClientContext inContext,
                                                                                 final DateWrapper inDate)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Future>,BigDecimal>>(getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Future>,BigDecimal> call(ClientContext context,
                                                                      SessionHolder<ClientSession> sessionHolder)
                     throws PersistenceException
             {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getAllFuturePositionsAsOf(sessionHolder.getSession().getUser(),
                                                                 inDate.getRaw());
             }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getFuturePositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(ClientContext inContext,
                                            final DateWrapper inDate,
                                            final Future inFuture)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>(getSessionManager()) {
            @Override
            protected BigDecimal call(ClientContext context,
                                      SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getFuturePositionAsOf(sessionHolder.getSession().getUser(),
                                                             inDate.getRaw(),
                                                             inFuture);
            }}).execute(inContext);
    }
    @Override
    public BigDecimal getOptionPositionAsOf(ClientContext context,
                                            final DateWrapper date,
                                            final Option inOption)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,BigDecimal>
                (getSessionManager()) {
            @Override
            protected BigDecimal call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getOptionPositionAsOf(sessionHolder.getSession().getUser(),
                                                             date.getRaw(),
                                                             inOption);
            }}).execute(context);
    }
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(ClientContext context,
                                                                                final DateWrapper inDate)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Option>,BigDecimal>>(getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Option>,BigDecimal> call(ClientContext context,
                                                                      SessionHolder<ClientSession> inSessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(inSessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getAllOptionPositionsAsOf(inSessionHolder.getSession().getUser(),
                                                                 inDate.getRaw());
            }}).execute(context);
    }
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(ClientContext context,
                                                                             final DateWrapper date,
                                                                             final String...rootSymbols)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,MapWrapper<PositionKey<Option>,BigDecimal>>(getSessionManager()) {
            @Override
            protected MapWrapper<PositionKey<Option>,BigDecimal> call(ClientContext context,
                                                                      SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewPositionAction.name());
                return serviceProvider.getOptionPositionsAsOf(sessionHolder.getSession().getUser(),
                                                              date.getRaw(),
                                                              rootSymbols);
            }}).execute(context);
    }


    @Override
    public String getNextOrderID
        (ClientContext context)
        throws RemoteException
    {
        return (new RemoteCaller<ClientSession,String>
                (getSessionManager()) {
            @Override
            protected String call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws CoreException
            {
                return getNextOrderID();
            }}).execute(context);
    }

    @Override
    public String getUnderlying
            (ClientContext context,
             final String optionRoot)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,String>
                (getSessionManager()) {
            @Override
            protected String call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws CoreException
            {
                return getUnderlying(optionRoot);
            }}).execute(context);
    }

    @Override
    public Collection<String> getOptionRoots
            (ClientContext context,
             final String underlying)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,Collection<String>>
                (getSessionManager()) {
            @Override
            protected Collection<String> call
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
                throws CoreException
            {
                return getOptionRoots(underlying);
            }}).execute(context);
    }

    @Override
    public void heartbeat
        (ClientContext context)
        throws RemoteException
    {
        (new RemoteRunner<ClientSession>
         (getSessionManager()) {
            @Override
            protected void run
                (ClientContext context,
                 SessionHolder<ClientSession> sessionHolder)
            {
                // The enclosing RemoteRunner takes care of marking
                // the session as active.
                SLF4JLoggerProxy.debug
                    (this,"Received heartbeat for: {}", //$NON-NLS-1$
                     context.getSessionId());
            }}).execute(context);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getUserData(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public String getUserData(final ClientContext inContext)
            throws RemoteException
    {
        String userData = (new RemoteCaller<ClientSession,String>(getSessionManager()) {
            @Override
            protected String call(ClientContext context,
                                  SessionHolder<ClientSession> sessionHolder)
                    throws CoreException, PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewUserDataAction.name());
                return getUserData(getSessionManager().get(inContext.getSessionId()).getSession().getUser().getName());
        }}).execute(inContext);
        return userData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#setUserData(org.marketcetera.util.ws.stateful.ClientContext, java.util.Properties)
     */
    @Override
    public void setUserData(final ClientContext inContext,
                            final String inData)
            throws RemoteException
    {
        (new RemoteRunner<ClientSession>(getSessionManager()) {
            @Override
            protected void run(ClientContext context,
                               SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.WriteUserDataAction.name());
                setUserData(getSessionManager().get(inContext.getSessionId()).getSession().getUser().getName(),
                                inData);
            }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#addReport(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.FIXMessageWrapper, org.marketcetera.trade.BrokerID, org.marketcetera.trade.Hierarchy)
     */
    @Override
    public void addReport(final ClientContext inContext,
                          final FIXMessageWrapper inReport,
                          final BrokerID inBrokerID,
                          final Hierarchy inHierarchy)
            throws RemoteException
    {
        (new RemoteRunner<ClientSession>(getSessionManager()) {
            @Override
            protected void run(ClientContext context,
                               SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.AddReportAction.name());
                serviceProvider.addReport(inReport.getMessage(),
                                          inBrokerID,
                                          getSessionManager().get(inContext.getSessionId()).getSession().getUser().getUserID(),
                                          inHierarchy);
            }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#deleteReport(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void deleteReport(final @WebParam(name="context")ClientContext inContext,
                             final @WebParam(name="report")ExecutionReportImpl inReport)
            throws RemoteException
    {
        (new RemoteRunner<ClientSession>(getSessionManager()) {
            @Override
            protected void run(ClientContext context,
                               SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.DeleteReportAction.name());
                serviceProvider.deleteReport(inReport,
                                             getSessionManager().get(inContext.getSessionId()).getSession().getUser().getUserID());
            }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#resolveSymbol(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(@WebParam(name = "context")ClientContext inContext,
                                    final @WebParam(name = "symbol")String inSymbol)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,Instrument>(getSessionManager()) {
            @Override
            protected Instrument call(ClientContext context,
                                  SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                return resolveSymbol(inSymbol);
        }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#getOpenOrders(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public List<ReportBaseImpl> getOpenOrders(@WebParam(name = "context")ClientContext inContext)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,List<ReportBaseImpl>>(getSessionManager()) {
            @Override
            protected List<ReportBaseImpl> call(ClientContext context,
                                            SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewOpenOrdersAction.name());
                return serviceProvider.getOpenOrders(sessionHolder.getSession().getUser());
        }}).execute(inContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Service#findRootOrderIdFor(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID getRootOrderIdFor(ClientContext inServiceContext,
                                     final OrderID inOrderID)
            throws RemoteException
    {
        return (new RemoteCaller<ClientSession,OrderID>(getSessionManager()) {
            @Override
            protected OrderID call(ClientContext context,
                                   SessionHolder<ClientSession> sessionHolder)
                    throws PersistenceException
            {
                authzService.authorize(sessionHolder.getSession().getUser().getName(),
                                       TradingPermissions.ViewOpenOrdersAction.name());
                return getRootOrderIdFor(inOrderID);
        }}).execute(inServiceContext);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getNextOrderID()
     */
    @Override
    public String getNextOrderID()
    {
        return serviceProvider.getNextOrderId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getReportsSince(java.lang.String, java.util.Date)
     */
    @Override
    public ReportBaseImpl[] getReportsSince(String inUsername,
                                            Date inOrigin)
    {
        return serviceProvider.getReportsSince(userService.findByName(inUsername),
                                               inOrigin);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOpenOrders(java.lang.String)
     */
    @Override
    public List<ReportBaseImpl> getOpenOrders(String inUsername)
    {
        return serviceProvider.getOpenOrders(userService.findByName(inUsername));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getBrokersStatus(java.lang.String)
     */
    @Override
    public BrokersStatus getBrokersStatus(String inUsername)
    {
        return serviceProvider.getBrokersStatus(inUsername);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getEquityPositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(String inUsername,
                                            Date inOrigin,
                                            Equity inInstrument)
    {
        return serviceProvider.getEquityPositionAsOf(userService.findByName(inUsername),
                                                     inOrigin,
                                                     inInstrument);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOptionPositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(String inUsername,
                                            Date inOrigin,
                                            Option inInstrument)
    {
        return serviceProvider.getOptionPositionAsOf(userService.findByName(inUsername),
                                                     inOrigin,
                                                     inInstrument);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getFuturePositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(String inUsername,
                                            Date inOrigin,
                                            Future inInstrument)
    {
        return serviceProvider.getFuturePositionAsOf(userService.findByName(inUsername),
                                                     inOrigin,
                                                     inInstrument);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getCurrencyPositionAsOf(java.lang.String, java.util.Date, org.marketcetera.trade.Currency)
     */
    @Override
    public BigDecimal getCurrencyPositionAsOf(String inUsername,
                                              Date inOrigin,
                                              Currency inInstrument)
    {
        return serviceProvider.getCurrencyPositionAsOf(userService.findByName(inUsername),
                                                       inOrigin,
                                                       inInstrument);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllCurrencyPositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(String inUsername,
                                                                                    Date inDate)
    {
        return serviceProvider.getAllCurrencyPositionsAsOf(userService.findByName(inUsername),
                                                           inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllFuturePositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(String inUsername,
                                                                                Date inDate)
    {
        return serviceProvider.getAllFuturePositionsAsOf(userService.findByName(inUsername),
                                                         inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllEquityPositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(String inUsername,
                                                                                Date inDate)
    {
        return serviceProvider.getAllEquityPositionsAsOf(userService.findByName(inUsername),
                                                         inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getAllOptionPositionsAsOf(java.lang.String, java.util.Date)
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(String inUsername,
                                                                                Date inDate)
    {
        return serviceProvider.getAllOptionPositionsAsOf(userService.findByName(inUsername),
                                                         inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOptionPositionsAsOf(java.lang.String, java.util.Date, java.lang.String[])
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(String inUsername,
                                                                             Date inDate,
                                                                             String[] inArray)
    {
        return serviceProvider.getOptionPositionsAsOf(userService.findByName(inUsername),
                                                      inDate,
                                                      inArray);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getUserInfo(org.marketcetera.trade.UserID)
     */
    @Override
    public UserInfo getUserInfo(UserID inUserID)
    {
        return serviceProvider.getUserInfo(inUserID);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inSymbol)
    {
        return serviceProvider.getUnderlying(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inSymbol)
    {
        return serviceProvider.getOptionRoots(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getUserData(java.lang.String)
     */
    @Override
    public String getUserData(String inUsername)
    {
        return serviceProvider.getUserData(inUsername);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#setUserData(java.lang.String, java.lang.String)
     */
    @Override
    public void setUserData(String inUser,
                            String inString)
    {
        serviceProvider.setUserData(inUser,
                                    inString);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.rpc.RpcServerAdapter#addReport(java.lang.String, quickfix.Message, org.marketcetera.trade.BrokerID, org.marketcetera.trade.Hierarchy)
     */
    @Override
    public void addReport(String inUsername,
                          Message inMessage,
                          BrokerID inBrokerID,
                          Hierarchy inHierarchy)
    {
        serviceProvider.addReport(inMessage,
                                  inBrokerID,
                                  userService.findByName(inUsername).getUserID(),
                                  inHierarchy);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#deleteReport(java.lang.String, org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void deleteReport(String inUsername,
                             ExecutionReport inMessage)
    {
        serviceProvider.deleteReport(inMessage,
                                     userService.findByName(inUsername).getUserID());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        return serviceProvider.resolveSymbol(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.rpc.RpcServerAdapter#getRootOrderIdFor(org.marketcetera.trade.OrderID)
     */
    @Override
    public OrderID getRootOrderIdFor(OrderID inOrderID)
    {
        return serviceProvider.getRootOrderIdFor(inOrderID);
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
     * provides actual services
     */
    @Autowired
    private ServiceProvider serviceProvider;
    /**
     * provides access to user objects
     */
    @Autowired
    private UserService userService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
}

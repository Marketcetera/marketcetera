package org.marketcetera.server.ws.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marketcetera.api.server.ClientContext;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.server.ServerApp;
import org.marketcetera.server.ws.Services;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;

/* $License$ */

/**
 * Provides the web services of the server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ServicesImpl
        implements Services
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getProviders(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public List<ModuleURN> getProviders(ClientContext inContext)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getInstances(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(ClientContext inContext,
                                        ModuleURN inProviderURN)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getModuleInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(ClientContext inContext,
                                    ModuleURN inURN)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#start(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void start(ClientContext inContext,
                      ModuleURN inURN)
            throws RemoteException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#stop(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stop(ClientContext inContext,
                     ModuleURN inURN)
            throws RemoteException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#delete(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public void delete(ClientContext inCtx,
                       ModuleURN inURN)
            throws RemoteException
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public MapWrapper<String,Object> getProperties(ClientContext inContext,
                                                   ModuleURN inURN)
            throws RemoteException
    {
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#setProperties(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN, org.marketcetera.util.ws.wrappers.MapWrapper)
     */
    @Override
    public MapWrapper<String,Object> setProperties(ClientContext inContext,
                                                   ModuleURN inURN,
                                                   MapWrapper<String,Object> inProperties)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#createStrategy(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.saclient.CreateStrategyParameters)
     */
    @Override
    public ModuleURN createStrategy(ClientContext inContext,
                                    CreateStrategyParameters inParameters)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getStrategyCreateParms(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.module.ModuleURN)
     */
    @Override
    public CreateStrategyParameters getStrategyCreateParms(ClientContext inContext,
                                                           ModuleURN inURN)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getBrokersStatus(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public BrokersStatus getBrokersStatus(ClientContext inContext)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getUserInfo(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.trade.UserID)
     */
    @Override
    public UserInfo getUserInfo(ClientContext inContext,
                                UserID inId)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getReportsSince(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public List<ReportBaseImpl> getReportsSince(ClientContext inContext,
                                                DateWrapper inDate)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getEquityPositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getEquityPositionAsOf(ClientContext inContext,
                                            DateWrapper inDate,
                                            Equity inEquity)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getAllEquityPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(ClientContext inContext,
                                                                                DateWrapper inDate)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getFuturePositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(ClientContext inContext,
                                            DateWrapper inDate,
                                            Future inFuture)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getAllFuturePositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(ClientContext inContext,
                                                                                DateWrapper inDate)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getOptionPositionAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(ClientContext inContext,
                                            DateWrapper inDate,
                                            Option inOption)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getAllOptionPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper)
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(ClientContext inContext,
                                                                                DateWrapper inDate)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getOptionPositionsAsOf(org.marketcetera.util.ws.stateful.ClientContext, org.marketcetera.util.ws.wrappers.DateWrapper, java.lang.String[])
     */
    @Override
    public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(ClientContext inContext,
                                                                             DateWrapper inDate,
                                                                             String...inRootSymbols)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getNextOrderID(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public String getNextOrderID(ClientContext inContext)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getUnderlying(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public String getUnderlying(ClientContext inContext,
                                String inOptionRoot)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getOptionRoots(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(ClientContext inContext,
                                             String inUnderlying)
            throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#heartbeat(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public void heartbeat(ClientContext inContext)
            throws RemoteException
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#getUserData(org.marketcetera.util.ws.stateful.ClientContext)
     */
    @Override
    public String getUserData(ClientContext inContext)
            throws RemoteException
    {
        ServerApp.getInstance().getContextValidator().validate(inContext);
        return "This is only a test";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.Services#setUserData(org.marketcetera.util.ws.stateful.ClientContext, java.lang.String)
     */
    @Override
    public void setUserData(ClientContext inContext,
                            String inData)
            throws RemoteException
    {
        // TODO Auto-generated method stub
    }
}

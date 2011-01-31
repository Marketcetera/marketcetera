package org.marketcetera.server.ws;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.marketcetera.api.server.ClientContext;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.Util;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessServiceBase;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;

/* $License$ */

/**
 * Provides services for the Marketcetera platform.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@WebService(targetNamespace="http://marketcetera.org/services")
@ClassVersion("$Id$")
public interface Services
        extends StatelessServiceBase
{
    /**
     * Returns the list of available module providers.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @return a <code>List&lt;ModuleURN&gt;</code> value containing the <code>ModuleURN</code> values for the available providers
     * @throws RemoteException if there were errors communicating with the server
     */
    public List<ModuleURN> getProviders(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
    /**
     * Returns the list of available module instances.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inProviderURN a <code>ModuleURN</code> value containing the provider URN or <code>null</code>. If provided, the instances
     *   from the specified provider only are returned. If <code>null</code>, all the available instances are returned.
     * @return a <code>List&lt;ModuleURN&gt;</code> value containing the <code>ModuleURN</code> values for the available instances
     * @throws RemoteException if there were errors communicating with the server
     */
    public List<ModuleURN> getInstances(@WebParam(name="context")ClientContext inContext,
                                        @WebParam(name="urn")ModuleURN inProviderURN)
            throws RemoteException;
    /**
     * Gets the <code>ModuleInfo</code> for the module with the specified <code>ModuleURN</code>.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inURN a <code>ModuleURN</code> value containing the URN of the module for which to return information
     * @return a <code>ModuleInfo</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public ModuleInfo getModuleInfo(@WebParam(name="context")ClientContext inCtx,
                                    @WebParam(name="urn")ModuleURN inURN)
            throws RemoteException;
    /**
     * Starts the module with the specified URN.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inURN a <code>ModuleURN</code> value containing the URN of the module to start
     * @throws RemoteException if there were errors communicating with the server
     */
    public void start(@WebParam(name="context")ClientContext inContext,
                      @WebParam(name="urn")ModuleURN inURN)
            throws RemoteException;
    /**
     * Stops the module with the specified URN.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inURN a <code>ModuleURN</code> value containing the URN of the module to stop
     * @throws RemoteException if there were errors communicating with the server
     */
    public void stop(@WebParam(name="context")ClientContext inContext,
                     @WebParam(name="urn")ModuleURN inURN)
            throws RemoteException;
    /**
     * Deletes the module with the specified URN.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inURN a <code>ModuleURN</code> value containing the URN of the module to delete
     * @throws RemoteException if there were errors communicating with the server
     */
    public void delete(@WebParam(name="context")ClientContext inCtx,
                       @WebParam(name="urn")ModuleURN inURN)
            throws RemoteException;
    /**
     * Gets the properties of the module with the specified URN.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inURN a <code>ModuleURN</code> value containing the URN of the module for which to return the properties
     * @return a <code>MapWrapper&lt;String,Object&gt;</code> value containing the property values of the module with the specified URN
     * @throws RemoteException if there were errors communicating with the server
     */
    public MapWrapper<String,Object> getProperties(@WebParam(name="context")ClientContext inContext,
                                                   @WebParam(name="urn")ModuleURN inURN)
            throws RemoteException;
    /**
     * Sets the properties of the module with the specified URN.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inURN a <code>ModuleURN</code> value containing the URN of the module for which to set the properties
     * @param inProperties a <code>MapWrapper&lt;String,Object&gt;</code> value containing the property values to set
     * @return a <code>MapWrapper&lt;String,Object&gt;</code> value containing the map of the properties that were successfully updated.
     *   If a particular could not be updated, its value will contain the exception with the details on the failure.
     * @throws RemoteException if there were errors communicating with the server
     */
    public MapWrapper<String,Object> setProperties(@WebParam(name="context")ClientContext inContext,
                                                   @WebParam(name="urn")ModuleURN inURN,
                                                   @WebParam(name="properties")MapWrapper<String,Object> inProperties)
            throws RemoteException;
    /**
     * Creates a strategy.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inParameters a <code>CreateStrategyParameters</code> value containing the strategy creation parameters
     * @return a <code>ModuleURN</code> value containing the URN of the strategy module created
     * @throws RemoteException if there were errors communicating with the server
     */
    public ModuleURN createStrategy(@WebParam(name="context")ClientContext inContext,
                                    @WebParam(name="parameters")CreateStrategyParameters inParameters)
            throws RemoteException;
    /**
     * Returns the parameters used to create a strategy.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inURN a <code>ModuleURN</code> value containing the URN of the module for which to return parameter information
     * @return a <code>CreateStrategyParameters</code> value containing the parameters supplied when creating the strategy.
     * @throws RemoteException if there were errors communicating with the server
     */
    public CreateStrategyParameters getStrategyCreateParms(@WebParam(name="context")ClientContext inContext,
                                                           @WebParam(name="urn")ModuleURN inURN)
            throws RemoteException;
    /**
     * Gets the server broker statuses.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @return a <code>BrokersStatus</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public BrokersStatus getBrokersStatus(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
    /**
     * Gets the information of the user with the given ID.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inId a <code>UserID</code> value
     * @return a <code>UserInfo</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public UserInfo getUserInfo(@WebParam(name="context")ClientContext inContext,
                                @WebParam(name="userID")UserID inId)
            throws RemoteException;
    /**
     * Returns all the reports generated and received by the server since the supplied date.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @return a <code>List&lt;ReportBaseImpl&gt;</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public List<ReportBaseImpl> getReportsSince(@WebParam(name="context")ClientContext inContext,
                                                @WebParam(name="date")DateWrapper inDate)
            throws RemoteException;
    /**
     * Returns the position of the given <code>Equity</code> instrument.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @param inEquity an <code>Equity</code> value
     * @return a <code>BigDecimal</code> value containing the position of the given <code>Equity</code>
     * @throws RemoteException if there were errors communicating with the server
     */
    public BigDecimal getEquityPositionAsOf(@WebParam(name="context")ClientContext inContext,
                                            @WebParam(name="date")DateWrapper inDate,
                                            @WebParam(name="equity")Equity inEquity)
            throws RemoteException;
    /**
     * Returns the positions for all <code>Equity</code> instruments.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Equity&gt;,BigDecimal&gt;</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(@WebParam(name="context")ClientContext inContext,
                                                                                @WebParam(name="date")DateWrapper inDate)
            throws RemoteException;
    /**
     * Returns the position of the given <code>Future</code> instrument.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @param inFuture a <code>Future</code> value
     * @return a <code>BigDecimal</code> value containing the position of the given <code>Future</code>
     * @throws RemoteException if there were errors communicating with the server
     */
    public BigDecimal getFuturePositionAsOf(@WebParam(name="context")ClientContext inContext,
                                            @WebParam(name="date")DateWrapper inDate,
                                            @WebParam(name="future")Future inFuture)
            throws RemoteException;
    /**
     * Returns the positions for all <code>Future</code> instruments.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Future&gt;,BigDecimal&gt;</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(@WebParam(name="context")ClientContext inContext,
                                                                                @WebParam(name="date")DateWrapper inDate)
            throws RemoteException;
    /**
     * Returns the position of the given <code>Option</code> instrument.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @param inOption an <code>Option</code> value
     * @return a <code>BigDecimal</code> value containing the position of the given <code>Option</code>
     * @throws RemoteException if there were errors communicating with the server
     */
    public BigDecimal getOptionPositionAsOf(@WebParam(name="context")ClientContext inContext,
                                            @WebParam(name="date")DateWrapper inDate,
                                            @WebParam(name="option")Option inOption)
            throws RemoteException;
    /**
     * Returns the positions for all <code>Option</code> instruments.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(@WebParam(name="context")ClientContext inContext,
                                                                                @WebParam(name="date")DateWrapper inDate)
             throws RemoteException;
    /**
     * Returns the positions for all <code>Option</code> instruments for the set of option 
     * root symbols supplied.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inDate a <code>DateWrapper</code> value
     * @param inRootSymbols a <code>String[]</code> value containing the option root symbols
     * @return a <code>MapWrapper&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     * @throws RemoteException if there were errors communicating with the server
     */
    public MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(@WebParam(name="context")ClientContext inContext,
                                                                             @WebParam(name="date")DateWrapper inDate,
                                                                             @WebParam(name="rootSymbols")String...inRootSymbols)
             throws RemoteException;
    /**
     * Returns the next server order ID to the client with the given context.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @return a <code>String</code> value containing the next ID
     * @throws RemoteException if there were errors communicating with the server
     */
    public String getNextOrderID(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
    /**
     * Returns the underlying symbol for the supplied option root.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inOptionRoot a <code>String</code> value containing the option root symbol
     * @return a <code>String</code> value containing the underlying symbol for the supplied option root or <code>null</code> if
     *   no mapping was found.
     * @throws RemoteException if there were errors communicating with the server
     */
    public String getUnderlying(@WebParam(name="context")ClientContext context,
                                @WebParam(name="optionRoot")String inOptionRoot)
            throws RemoteException;
    /**
     * Returns the collection of known option roots for the underlying symbol.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inUnderlying a <code>String</code> value containing the underlying symbol
     * @return a <code>Collection&lt;String&gt;</code> value containing the sorted collection of option roots if mappings are found for
     *  the option root, may be empty
     * @throws RemoteException if there were errors communicating with the server
     */
    public Collection<String> getOptionRoots(@WebParam(name="context")ClientContext inContext,
                                             @WebParam(name="underlying")String inUnderlying)
            throws RemoteException;
    /**
     * Sends a heartbeat to the server.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @throws RemoteException if there were errors communicating with the server
     */
    public void heartbeat(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
    /**
     * Gets the user data associated with the given context.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @return a <code>String</code> value containing the user data in the format defined {@link Util#propertiesToString(java.util.Properties) here} 
     * @throws RemoteException if there were errors communicating with the server
     */
    public String getUserData(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
    /**
     * Sets the user data associated with the given context.
     *
     * @param inContext a <code>ClientContent</code> value providing the caller's session information
     * @param inData a <code>String</code> value containing the user data in the format defined {@link Util#propertiesFromString(String) here} 
     * @throws RemoteException if there were errors communicating with the server
     */
    public void setUserData(@WebParam(name="context")ClientContext inContext,
                            @WebParam(name="userData")String inData)
            throws RemoteException;
}

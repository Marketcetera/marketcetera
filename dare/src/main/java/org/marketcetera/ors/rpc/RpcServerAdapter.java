package org.marketcetera.ors.rpc;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.MapWrapper;

import quickfix.Message;

/* $License$ */

/**
 * Provides an adapter between an {@link RPCServer} and its implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.2
 */
@ClassVersion("$Id$")
public interface RpcServerAdapter
{
    /**
     * Gets the next order id.
     *
     * @return a <code>String</code> value
     */
    String getNextOrderID();
    /**
     * Gets the reports for the given user since the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inOrigin a <code>Date</code> value
     * @return a <code>ReportBaseImpl[]</code> value
     */
    ReportBaseImpl[] getReportsSince(String inUsername,
                                     Date inOrigin);
    /**
     * Gets the open orders for the given user.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>Listlt;ReportBaseImpl&gt;</code> value
     */
    List<ReportBaseImpl> getOpenOrders(String inUsername);
    /**
     * Gets the status of the brokers for the given user.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>BrokersStatus</code> value
     */
    BrokersStatus getBrokersStatus(String inUsername);
    /**
     * Gets the position of the given equity for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inOrigin a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getEquityPositionAsOf(String inUsername,
                                     Date inOrigin,
                                     Equity inInstrument);
    /**
     * Gets the position of the given option for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inOrigin a <code>Date</code> value
     * @param inInstrument an <code>Option</code> value
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getOptionPositionAsOf(String inUsername,
                                     Date inOrigin,
                                     Option inInstrument);
    /**
     * Gets the position of the given future for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inOrigin a <code>Date</code> value
     * @param inInstrument a <code>Future</code> value
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getFuturePositionAsOf(String inUsername,
                                     Date inOrigin,
                                     Future inInstrument);
    /**
     * Gets the position of the given currency for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inOrigin a <code>Date</code> value
     * @param inInstrument a <code>Currency</code> value
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getCurrencyPositionAsOf(String inUsername,
                                       Date inOrigin,
                                       Currency inInstrument);
    /**
     * Gets all currency positions for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Currency&gt;,BigDecimal&gt;</code> value
     */
    MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(String inUsername,
                                                                             Date inDate);
    /**
     * Gets all future positions for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Future&gt;,BigDecimal&gt;</code> value
     */
    MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(String inUsername,
                                                                         Date inDate);
    /**
     * Gets all equity positions for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Equity&gt;,BigDecimal&gt;</code> value
     */
    MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(String inUsername,
                                                                         Date inDate);
    /**
     * Gets all option positions for the given user and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inDate a <code>Date</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     */
    MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(String inUsername,
                                                                         Date inDate);
    /**
     * Gets all option positions for the given option roots, the given user, and the given origin.
     *
     * @param inUsername a <code>String</code> value
     * @param inDate a <code>Date</code> value
     * @param inOptionRoots a <code>String[]</code> value
     * @return a <code>MapWrapper&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     */
    MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(String inUsername,
                                                                      Date inDate,
                                                                      String[] inOptionRoots);
    /**
     * Gets info for the given user.
     *
     * @param inUserID a <code>UserID</code> value
     * @return a <code>UserInfo</code> value
     */
    UserInfo getUserInfo(UserID inUserID);
    /**
     * Gets the underlying symbol for the given option root.
     *
     * @param inOptionRoot a <code>String</code> value
     * @return a <code>String</code> value
     */
    String getUnderlying(String inOptionRoot);
    /**
     * Gets the option roots for the given option symbol.
     *
     * @param inSymbol a <code>String</code> value
     * @return a <code>Collection&lt;String&gt;</code> value
     */
    Collection<String> getOptionRoots(String inSymbol);
    /**
     * Resolves the given symbol to an instrument.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    Instrument resolveSymbol(String inSymbol);
    /**
     * Gets the root order ID for the given order ID.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return an <code>OrderID</code> value
     */
    OrderID getRootOrderIdFor(OrderID inOrderID);
    /**
     * Gets the user data for the given user.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>String</code> value
     */
    String getUserData(String inUsername);
    /**
     * Sets the user data for the given user.
     *
     * @param inUsername a <code>String</code> value
     * @param inString a <code>String</code> value
     */
    void setUserData(String inUsername,
                     String inString);
    /**
     * Sets the user data for the given user.
     *
     * @param inUsername a <code>String</code> value
     * @param inMessage a <code>Message</code> value
     * @param inBrokerID a <code>BrokerID</code> value
     * @param inHierarchy a <code>Hierarchy</code> value
     */
    void addReport(String inUsername,
                   Message inMessage,
                   BrokerID inBrokerID,
                   Hierarchy inHierarchy);
    /**
     * Deletes the given report.
     *
     * @param inUsername a <code>String</code> value
     * @param inMessage an <code>ExecutionReport</code> value
     */
    void deleteReport(String inUsername,
                      ExecutionReport inMessage);
}

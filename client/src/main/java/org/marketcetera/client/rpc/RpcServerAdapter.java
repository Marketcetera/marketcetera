package org.marketcetera.client.rpc;

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
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface RpcServerAdapter
{
    /**
     *
     *
     * @return
     */
    String getNextOrderID();
    /**
     * 
     *
     *
     * @param inUsername
     * @param inOrigin
     * @return
     */
    ReportBaseImpl[] getReportsSince(String inUsername,
                                     Date inOrigin);
    /**
     * 
     *
     *
     * @param inUsername
     * @return
     */
    List<ReportBaseImpl> getOpenOrders(String inUsername);
    /**
     *
     *
     * @param inUser
     * @return
     */
    BrokersStatus getBrokersStatus(String inUser);
    /**
     *
     *
     * @param inUser
     * @param inOrigin
     * @param inInstrument
     * @return
     */
    BigDecimal getEquityPositionAsOf(String inUser,
                                     Date inOrigin,
                                     Equity inInstrument);
    /**
     *
     *
     * @param inUser
     * @param inOrigin
     * @param inInstrument
     * @return
     */
    BigDecimal getOptionPositionAsOf(String inUser,
                                     Date inOrigin,
                                     Option inInstrument);
    /**
     *
     *
     * @param inUser
     * @param inOrigin
     * @param inInstrument
     * @return
     */
    BigDecimal getFuturePositionAsOf(String inUser,
                                     Date inOrigin,
                                     Future inInstrument);
    /**
     *
     *
     * @param inUser
     * @param inOrigin
     * @param inInstrument
     * @return
     */
    BigDecimal getCurrencyPositionAsOf(String inUser,
                                       Date inOrigin,
                                       Currency inInstrument);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    MapWrapper<PositionKey<Currency>,BigDecimal> getAllCurrencyPositionsAsOf(String inUser,
                                                                             Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    MapWrapper<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(String inUser,
                                                                         Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    MapWrapper<PositionKey<Equity>,BigDecimal> getAllEquityPositionsAsOf(String inUser,
                                                                         Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @return
     */
    MapWrapper<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(String inUser,
                                                                         Date inDate);
    /**
     *
     *
     * @param inUser
     * @param inDate
     * @param inArray
     * @return
     */
    MapWrapper<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(String inUser,
                                                                      Date inDate,
                                                                      String[] inArray);
    /**
     *
     *
     * @param inUserID
     * @return
     */
    UserInfo getUserInfo(UserID inUserID);
    /**
     *
     *
     * @param inSymbol
     * @return
     */
    String getUnderlying(String inSymbol);
    /**
     *
     *
     * @param inSymbol
     * @return
     */
    Collection<String> getOptionRoots(String inSymbol);
    /**
     *
     *
     * @param inSymbol
     * @return
     */
    Instrument resolveSymbol(String inSymbol);
    /**
     *
     *
     * @param inOrderID
     * @return
     */
    OrderID getRootOrderIdFor(OrderID inOrderID);
    /**
     *
     *
     * @param inUser
     * @return
     */
    String getUserData(String inUser);
    /**
     *
     *
     * @param inUser
     * @param inString
     */
    void setUserData(String inUser,
                     String inString);
    /**
     *
     *
     * @param inUser
     * @param inMessage
     * @param inBrokerID
     */
    void addReport(String inUser,
                   Message inMessage,
                   BrokerID inBrokerID);
    /**
     *
     *
     * @param inUser
     * @param inMessage
     */
    void deleteReport(String inUser,
                      ExecutionReport inMessage);
}

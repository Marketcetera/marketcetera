package org.marketcetera.strategy;

import org.marketcetera.event.EventBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Services available to strategies to emit data of various types to the strategy agent framework.
 * 
 * <p>Data transmitted via the methods in this interface will be emitted via the implementer's {@link DataEmitter}
 * implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
interface OutboundServicesProvider
{
    /**
     * Sends an order to the destination or destinations established in the strategy module.
     *
     * @param inOrder an <code>OrderSingle</code> value
     */
    void sendOrder(OrderSingle inOrder);
    /**
     * Sends an order cancel request to the established destination or destinations.
     *
     * @param inCancel an <code>OrderCancel</code> value
     */
    void cancelOrder(OrderCancel inCancel);
    /**
     * Sends an order replace request to the established destination or destinations.
     *
     * @param inReplace an <code>OrderReplace</code> value
     */
    void cancelReplace(OrderReplace inReplace);
    /**
     * Sends a trade suggestion to the destination or destinations established in the strategy module.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    void sendSuggestion(Suggestion inSuggestion);
    /**
     * Sends an event to the destination or destinations established in the strategy module.
     *
     * @param inEvent an <code>EventBase</code> value containing the event to send
     * @param inProvider a <code>String</code> value containing the value of a CEP provider to which to send the event or null
     *   to send the event to subscribers only  
     * @param inNamespace a <code>String</code> value containing the namespace of an existing CEP query or null to
     *   send the event to subscribers only 
     */
    void sendEvent(EventBase inEvent,
                   String inProvider,
                   String inNamespace);
    /**
     * Creates a market data request.
     * 
     * <p>The <code>inSource</code> parameter must contain the identifier of a started market data provider
     * module.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value indicating what data to request
     * @param inSource a <code>String</code> value indicating what market data provider from which to request the data
     * @return an <code>int</code> value containing an identifier corresponding to this market data request
     */
    int requestMarketData(MarketDataRequest inRequest,
                          String inSource);
    /**
     * Creates a market data request processed by a complex event processor. 
     *
     * <p>The <code>inMarketDataSource</code> parameter must contain the identifier of a started market data provider
     * module.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value containing the request to execute
     * @param inMarketDataSource a <code>String</code> value containing the source from which to request the market data
     * @param inStatements a <code>String[]</code> value indicating what data to request
     * @param inSource a <code>String</code> value indicating what complex event processor provider from which to request the data
     * @param inNamespace a <code>String</code> value indicating what complex event processor namespace to use
     * @return an <code>int</code> value containing the request handle or 0 if the request failed
     */
    int requestProcessedMarketData(MarketDataRequest inRequest,
                                   String inMarketDataSource,
                                   String[] inStatements,
                                   String inCEPSource,
                                   String inNamespace);
    /**
     * Cancels a given market data request.
     * 
     * <p>If the given <code>inDataRequestID</code> identifier does not correspond to an active market data
     * request, this method does nothing.
     *
     * @param inDataRequestID an <code>int</code> value identifying the market data request to cancel
     */
    void cancelMarketDataRequest(int inDataRequestID);
    /**
     * Cancels all market data requests for this strategy.
     *
     * <p>If there are no active market data requests for this strategy, this method does nothing.
     */
    void cancelAllMarketDataRequests();
    /**
     * Creates a complex event processor request.
     * 
     * <p>The <code>inSource</code> parameter must contain the identifier of a started complex event processor provider
     * module.
     * 
     * @param inStatements a <code>String[]</code> value indicating what data to request
     * @param inSource a <code>String</code> value indicating what complex event processor provider from which to request the data
     * @param inNamespace a <code>String</code> value indicating what complex event processor namespace to use
     * @return an <code>int</code> value containing an identifier corresponding to this complex event processor request
     */
    int requestCEPData(String[] inStatements,
                       String inSource,
                       String inNamespace);
    /**
     * Cancels a given complex event processor data request.
     * 
     * <p>If the given <code>inDataRequestID</code> identifier does not correspond to an active complex event processor data
     * request, this method does nothing.
     *
     * @param inDataRequestID an <code>int</code> value identifying the complex event processor request to cancel
     */
    void cancelCEPRequest(int inDataRequestID);
    /**
     * Cancels all complex event processor requests for this strategy.
     *
     * <p>If there are no active complex event processor requests for this strategy, this method does nothing.
     */
    void cancelAllCEPRequests();
    /**
     * Sends a FIX message to Order subscribers.
     *
     * @param inMessage a <code>Message</code> value
     * @param inDestination a <code>DestinationID</code> value
     */
    void sendMessage(Message inMessage,
                     DestinationID inDestination);
    /**
     * Broadcast a change in strategy status.
     *
     * @param inOldStatus a <code>Status</code> value
     * @param inNewStatus a <code>Status</code> value
     */
    void statusChanged(Status inOldStatus,
                       Status inNewStatus);
}


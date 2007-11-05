package org.marketcetera.photon.scripting;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.marketcetera.core.IDFactory;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.TradeRecommendationView;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXValueExtractor;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.field.OrderID;

/**
 * This is the base class for all JRuby scripts and Java classes that implement
 * trading strategies.  The object provides callback definitions, such as
 * {@link #on_market_data_snapshot(Message)} and {@link #on_execution_report(Message)}
 * that should be implemented by subclasses in order to receive notifications
 * of the outside world.
 * 
 * Additionally Strategy provides utility methods for manipulating FIX
 * messages, facilities for executing code in the future, as well as 
 * a message factory and ID factory.
 * 
 * @author tkuznets
 *
 */
public abstract class Strategy {

	private FIXValueExtractor extractor;
	private PhotonPlugin plugin;
	private Logger logger;
    private String registrationName;

    public Strategy() {
		plugin = PhotonPlugin.getDefault();
		logger = PhotonPlugin.getMainConsoleLogger();
		extractor = new FIXValueExtractor(plugin.getFIXDataDictionary().getDictionary(), 
				plugin.getMessageFactory());
	}

    /** 
	 * Sets the human readable name of this strategy.
     */
    public void setName(String inName) {
        registrationName = inName;
    }

    /** 
	 * Gets the human readable name of this strategy.
	 * @return the human readable name of this strategy
     */
    public String getName() {
        return registrationName;
    }

    /**
     * This callback is invoked after the strategy object is fully instantiated.
     */
    public void on_create()
    {
    }
    
    /**
     * This callback is invoked before the strategy object is disposed, in order
     * to allow for cleanup of resources.
     */
    public void on_dispose()
    {
    }
    
    /**
     * This callback is invoked every time an order-related (that is non-market data)
     * FIX message is received by the application for the strategy.  The default implementation
     * decides whether the message represents a trade (based on whether a LastPx is specified
     * in an ExecutionReport, and delegates to {@link Strategy#on_execution_report(Message)}.
     * 
     * Subclassers of Strategy may override this method if they require more control or
     * other semantics.
     * 
     * @param message the FIX message received by Photon.
     */
    public void on_fix_message(Message message)
	{
		if(FIXMessageUtil.isExecutionReport(message)) {
			if(message.isSetField(LastPx.FIELD) && message.isSetField(OrderID.FIELD)) {
				try {
					BigDecimal lastPx = new BigDecimal(message.getString(LastPx.FIELD));
					if(!BigDecimal.ZERO.equals(lastPx)) {
						on_execution_report(message);
					}
				} catch (FieldNotFound ignored) {
				}
			}
		}		
	}
	
    /**
     * This callback is invoked every time a market-data message is received by
     * Photon.  The default implementation determines if the market data message
     * is a "Market Data -- Snapshot/Full Refresh" (35=W), and delegates to 
     * {@link Strategy#on_market_data_snapshot(Message)}.
     * 
     * Subclassers of Strategy may override this method if they require more control
     * or other semantics.
	 * 
     * @param message the market data message
     */
    public void on_market_data_message(Message message)
	{
		if(FIXMessageUtil.isMarketDataSnapshotFullRefresh(message)) {
			on_market_data_snapshot(message);
		}
	}

    /**
     * This method is invoked by the default implementation of {@link Strategy#on_market_data_message(Message)}
     * when a "Market Data -- Snapshot/Full Refresh" (35=W) is received by Photon.
     * 
     * @param message the market data message
     */
	public abstract void on_market_data_snapshot(Message message);

	/**
	 * This method is invoked by the default implementation of {@link Strategy#on_fix_message(Message)}
	 * when an execution report that represents a trade is received.
	 * 
	 * @param message the execution report
	 */
	public abstract void on_execution_report(Message message);

	/** 
	 * extractMD is a utility method for extracting market data from a QuickFIX message.
	 * It can extract an arbitrary numerical field value from a "NoMDEntries" repeating
	 * group with a matching mdEntryType.
	 * 
	 * For example a typical market data message might 
	 * contain two instances of the "NoMDEntries" repeating group, one for the bid, and one
	 * for the offer.  Thus to extract the bid price, one might call this function with that
	 * message, MDEntryType.BID as the mdEntryType, and MDEntryPx.FIELD as the fieldID.  To 
	 * extract the bid size, one would use the same parameters except using MDEntrySize.FIELD
	 * as the fieldID.
	 * 
	 * @param message the message containing repeating groups
	 * @param mdEntryType the value of MDEntryType to match (e.g. BID='0')
	 * @param fieldID the field ID of the field to extract.
	 * 
	 * @return the value of the specified field from the specified group
	 */
	public BigDecimal extractMD(Message message, char mdEntryType, int fieldID) {
		return (BigDecimal) extractor.extractValue(message, fieldID, NoMDEntries.FIELD,
				MDEntryType.FIELD, mdEntryType);
	}

	/**
	 * Sends a message to the message queue for routing to a counterparty.
	 * 
	 * @param message the message to send to a counterparty
	 */
	public void sendFIXMessage(quickfix.Message message)
	{
		plugin.getPhotonController().handleInternalMessage(message);
	}
	
	public void addTradeRecommendation(quickfix.Message message){
		addTradeRecommendation(message, null);
	}

	public void addTradeRecommendation(quickfix.Message message, Double score){
		TradeRecommendationView.addTradeRecommendation(message, score);
	}

	
	/**
     * Causes the {@link #timeout_callback} function to be called after the specified number of milliseconds,
     * passing the clientData object into it
	 *
	 * @param millis the number of milliseconds to delay before calling the callback.
	 * @param clientData the object passed back to the {@link #timeout_callback(Object)} function
	 * 
	 * @see Strategy#registerTimedCallback(long, TimeUnit, Object)
     * @return a {@link ScheduledFuture} object that can be used to cancel the callback or join the result.
	 */
	public ScheduledFuture<?> registerTimedCallback(final long millis, final Object clientData)
	{
		return registerTimedCallback(millis, TimeUnit.MILLISECONDS, clientData);
	}
	
    /** 
     * Causes the {@link #timeout_callback} function to be called after the specified delay,
     * passing the clientData object into it
     * @param delay Length of the delay before calling the {@link #timeout_callback} function
     * @param unit  Units of the delay
     * @param clientData    the object passed back to the {@link #timeout_callback} function.
     * @return a {@link ScheduledFuture} object that can be used to cancel the callback or join the result.
     */
	public ScheduledFuture<?> registerTimedCallback(final long delay, TimeUnit unit, final Object clientData)
	{
		return getScriptRegistry().registerTimedCallback(this, delay, unit, clientData);
	}

	/**
	 * This method is protected to allow testing instances to override this method.
	 * Most subclasses should not override this method.
	 * @return the script registry.
	 */
	protected ScriptRegistry getScriptRegistry() {
        return plugin.getScriptRegistry();
    }

	/**
	 * The {@link FIXMessageFactory} that should be used by subclasses of Strategy
	 * for creating FIX messages such as orders, cancels and replaces.
	 * @return the FIXMessageFactory
	 */
    public FIXMessageFactory getMessageFactory()
	{
		return plugin.getMessageFactory(); 
	}
	
	/**
	 * The {@link IDFactory} that should be used by subclasses of Strategy
	 * for creating unique IDs (e.g. for ClOrdIds).
	 * 
	 * @return the IDFactory
	 */
	public IDFactory getIDFactory()
	{
		return plugin.getIDFactory();
	}
	
	/** 
	 * This method is invoked in response to a call to {@link #registerTimedCallback},
	 * after the specified delay.
	 * 
	 * Subclasses of Strategy should override this method if they need 
	 * anything done in the callback method.   The default implementation
	 * does nothing.
	 */
	public void timeout_callback(Object clientData)
	{
		// no-op
	}
	
	/**
	 * This method accesses the local FIX message history to find the most recent 
	 * execution report for the specified ClOrdID.  If one exists, it will be returned.
	 * 
	 * @param clOrdID the "client order id" of the order for which an execution report is required.
	 * @return the latest execution report we have for this clOrdID, or null if we don't have it
	 *
	 */
	public Message getLatestExecutionReport(String clOrdID)
	{
		return plugin.getFIXMessageHistory().getLatestExecutionReport(clOrdID);
	}
	
	/**
	 * This method provides a "panic button" that will cause Photon to issue cancel 
	 * requests for all orders that it believes are open.  Because this method cancels
	 * indiscriminately it should be used sparingly.
	 */
	public void cancelAllOpenOrders() 
	{
		plugin.getPhotonController().cancelAllOpenOrders();
	}
}
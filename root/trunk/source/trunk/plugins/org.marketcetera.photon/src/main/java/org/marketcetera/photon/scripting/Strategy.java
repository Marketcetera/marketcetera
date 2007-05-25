package org.marketcetera.photon.scripting;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jruby.exceptions.RaiseException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXValueExtractor;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.field.OrderID;

public abstract class Strategy {

	private FIXValueExtractor extractor;
	private PhotonPlugin plugin;
	private Logger logger;
	
	public Strategy() {
		plugin = PhotonPlugin.getDefault();
		logger = PhotonPlugin.getMainConsoleLogger();
		extractor = new FIXValueExtractor(plugin.getFIXDataDictionary().getDictionary(), 
				plugin.getMessageFactory());
	}

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
	
	public void on_market_data_message(Message message)
	{
		if(FIXMessageUtil.isMarketDataSnapshotFullRefresh(message)) {
			on_market_data_snapshot(message);
		}
	}
	
	public abstract void on_market_data_snapshot(Message message);

	public abstract void on_execution_report(Message message);

	public BigDecimal extractMD(Message message, char mdEntryType, int fieldID) {
		return (BigDecimal) extractor.extractValue(message, fieldID, NoMDEntries.FIELD,
				MDEntryType.FIELD, mdEntryType);
	}
	
	public void sendFIXMessage(quickfix.Message message)
	{
		plugin.getPhotonController().handleInternalMessage(message);
	}

	
	public void registerTimedCallback(final long millis, final Object clientData) throws InterruptedException
	{
		registerTimedCallback(millis, TimeUnit.MILLISECONDS, clientData);
	}
	
	public void registerTimedCallback(final long timeout, TimeUnit unit, final Object clientData) throws InterruptedException
	{
		plugin.getScriptRegistry().getScheduler().schedule(new Runnable(){
			public void run() {
				if(logger.isDebugEnabled()) { logger.debug("starting ruby callback"); }
				try {
					timeout_callback(clientData);
				} catch(RaiseException ex) {
					logger.error("Error in timeout_callback function: "+ex.getException(), ex.getCause());
				}
				if(logger.isDebugEnabled()) { logger.debug("finished ruby callback"); }			
			}
		}, timeout, unit);
		if(logger.isDebugEnabled()) { logger.debug("registering timeout callback for "+timeout + " in "+unit); }
	}
	
	public FIXMessageFactory getMessageFactory() 
	{
		return plugin.getMessageFactory(); 
	}
	
	public IDFactory getIDFactory()
	{
		return plugin.getIDFactory();
	}
	
	/** Subclasses of Strategy should override this method if they need 
	 * anything done in the callback method. 
	 * For example, will be a perfect place to send a cancel if the order 
	 * never goes through
	 */
	public void timeout_callback(Object clientData)
	{
		// no-op
	}
	
	/** Returns the latest execution report we have for this clOrdID, or null if we don't have it */
	public Message getLatestExecutionReport(String clOrdID)
	{
		return plugin.getFIXMessageHistory().getLatestExecutionReport(clOrdID);
	}
	
	/** Panic button: cancel all open orders */
	public void cancelAllOpenOrders() 
	{
		plugin.getPhotonController().cancelAllOpenOrders();
	}
}
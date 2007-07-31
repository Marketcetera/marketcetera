package org.marketcetera.photon.scripting;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
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
    private String registrationName;

    public Strategy() {
		plugin = PhotonPlugin.getDefault();
		logger = PhotonPlugin.getMainConsoleLogger();
		extractor = new FIXValueExtractor(plugin.getFIXDataDictionary().getDictionary(), 
				plugin.getMessageFactory());
	}

    /** provide a setter for the strategy registration name so we can check later
     *  what our own name is
     */
    public void setName(String inName) {
        registrationName = inName;
    }

    public String getName() {
        return registrationName;
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

	
	public ScheduledFuture<?> registerTimedCallback(final long millis, final Object clientData) throws InterruptedException
	{
		return registerTimedCallback(millis, TimeUnit.MILLISECONDS, clientData);
	}
	
    /** Sets up for the {@link #timeout_callback} function to be called after the specified delay,
     * passing the clientdata object into it
     * @param delay Length of the delay before calling the {@link #timeout_callback} function
     * @param unit  Units of the delay
     * @param clientData    ClientData object passed back to the {@link #timeout_callback} function.
     */
	public ScheduledFuture<?> registerTimedCallback(final long delay, TimeUnit unit, final Object clientData) throws InterruptedException
	{
		return getScriptRegistry().registerTimedCallback(this, delay, unit, clientData);
	}

    /** to be overridden by tests */
    protected ScriptRegistry getScriptRegistry() {
        return plugin.getScriptRegistry();
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
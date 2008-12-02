package org.marketcetera.photon.views;

import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.MDEntryType;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

/**
 * This is a relatively trivial subclass of {@link OrderTicketController},
 * that implements the required {@link #doOnQuote(Message)}.
 * 
 * @author gmiller
 *
 */
public class StockOrderTicketController
	 extends OrderTicketController<StockOrderTicketModel> 
{
	/**
	 * Create a new controller
	 * 
	 * @param orderTicketModel the order ticket model
	 */
	public StockOrderTicketController(OrderTicketModel orderTicketModel) 
	{
		super((StockOrderTicketModel)orderTicketModel);
	}

    /**
	 * If the message represents either a {@link MsgType#MARKET_DATA_INCREMENTAL_REFRESH}
	 * or a {@link MsgType#MARKET_DATA_SNAPSHOT_FULL_REFRESH}, loop through the 
	 * market data entries and update the market data in the order ticket model.
	 * 
	 */
	protected void doOnPrimaryQuote(Message message)
	{
		if ((FIXMessageUtil.isMarketDataIncrementalRefresh(message) || 
		     FIXMessageUtil.isMarketDataSnapshotFullRefresh(message))) {
			try {
				int noEntries = message.getInt(NoMDEntries.FIELD);
				for (int i = 0; i < noEntries; i++){
					MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
					message.getGroup(i+1, group);
					char currentEntryType = group.getChar(MDEntryType.FIELD);
					if (MDEntryType.BID==currentEntryType){
						getOrderTicketModel().addBid(group);
					} else if (MDEntryType.OFFER==currentEntryType){
						getOrderTicketModel().addOffer(group);
					}
				}
			} catch (Exception ex){
	            PhotonPlugin.getMainConsoleLogger().error(ex);
			}
		}
	}
	/**
	 * Clears the message in the stock order ticket model.
	 */
	public void clear() {
		getOrderTicketModel().clearOrderMessage();
	}
}

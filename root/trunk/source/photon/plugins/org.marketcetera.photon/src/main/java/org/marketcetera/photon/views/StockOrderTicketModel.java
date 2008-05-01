package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXMessageFactory;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.SecurityType;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

/**
 * Implements the model of a stock order ticket.  It is
 * a fairly trivial subclass of {@link OrderTicketModel}
 * that adds two lists one each for bids and asks (stock
 * market data).
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 *
 */
public class StockOrderTicketModel 
    extends OrderTicketModel 
{
    /**
     * the bids collected by this stock order ticket
     */
    private final List<MarketDataSnapshotFullRefresh.NoMDEntries> bids = new ArrayList<MarketDataSnapshotFullRefresh.NoMDEntries>();
    /**
     * the offers collected by this stock order ticket
     */
    private final List<MarketDataSnapshotFullRefresh.NoMDEntries> offers = new ArrayList<MarketDataSnapshotFullRefresh.NoMDEntries>();
	/**
	 * Create a {@link StockOrderTicketModel} with the given
	 * {@link FIXMessageFactory} for message creation and
	 * augmentation.
	 * 
	 * @param messageFactory the message factory
	 */
	public StockOrderTicketModel(FIXMessageFactory messageFactory) 
	{
		super(messageFactory);
	}
	/**
	 * Add a bid to the stock order ticket.
	 * 
	 * @param inBid a <code>MarketDataSnapshotFullRefresh.NoMDEntries</code> value
	 */
    public void addBid(MarketDataSnapshotFullRefresh.NoMDEntries inBid)
    {        
        OrderTicketPublication publication = new OrderTicketPublication(OrderTicketPublication.Type.BID,
                                                                        inBid);
        synchronized(bids) {
            bids.add(inBid);
        }
        try {
            getPublisher().publishAndWait(publication);
        } catch (InterruptedException e) {
            PhotonPlugin.getMainConsoleLogger().error(e);
        } catch (ExecutionException e) {
            PhotonPlugin.getMainConsoleLogger().error(e);
        }
    }
    public void addOffer(MarketDataSnapshotFullRefresh.NoMDEntries inOffer)
    {
        OrderTicketPublication publication = new OrderTicketPublication(OrderTicketPublication.Type.OFFER,
                                                                        inOffer);
        synchronized(offers) {
            offers.add(inOffer);
        }
        try {
            getPublisher().publishAndWait(publication);
        } catch (InterruptedException e) {
            PhotonPlugin.getMainConsoleLogger().error(e);
        } catch (ExecutionException e) {
            PhotonPlugin.getMainConsoleLogger().error(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.OrderTicketModel#createNewOrder()
     */
    @Override
    protected Message createNewOrder()
    {
        Message aMessage = getMessageFactory().newBasicOrder();
        aMessage.setString(SecurityType.FIELD, SecurityType.COMMON_STOCK);
        aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
        return aMessage;
    }
	/**
	 * Subscribes to changes to the model.
	 * 
	 * <p>Subscribers will be notified when new bids and offers are added.
	 * 
	 * @param inSubscriber an <code>ISubscriber</code> value
	 */
	void subscribe(ISubscriber inSubscriber)
	{
	    getPublisher().subscribe(inSubscriber);
	}
}

package org.marketcetera.photon.views;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.marketdata.IMarketDataFeedToken;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.quickfix.FIXMessageFactory;

import quickfix.Message;
import quickfix.field.MsgType;

/* $License$ */

/**
 * Abstract base class for controllers of order tickets.
 * 
 * This controller is responsible for handling subscriptions to market
 * data on behalf of the order ticket.  In general this is accomplished by listening
 * for change events on the {@link OrderTicketModel}, and based on those 
 * events issuing subscribe messages to the market data feed.  Market data 
 * messages are then received by this controller which updates
 * the order ticket model.
 * 
 * This controller is also a listener for changes in the Eclipse property store.
 * Based on changes to the custom fields property, this controller will update
 * the custom fields in the order ticket model.
 * 
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class OrderTicketController <T extends OrderTicketModel>
	implements IOrderTicketController, IPropertyChangeListener, Messages
{

    private final MarketDataFeedTracker marketDataTracker;

    protected IMarketDataFeedToken<?> primaryMarketDataToken;

    private final T orderTicketModel;

    protected final FIXMessageFactory messageFactory;

    /**
     * Create a new OrderTicketController.  Sets up a MarketDataFeedTracker
     * to track the market data feed service.
     * And hooks up a change listener for the Symbol property of the 
     * OrderTicketModel
     * 
     * @param orderTicketModel
     */
    public OrderTicketController(T orderTicketModel) {
        if (orderTicketModel == null){
            throw new NullPointerException();
        }
        this.orderTicketModel = orderTicketModel;

        marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
                                                      .getBundleContext());
        marketDataTracker.open();

        clear();

        PhotonPlugin plugin = PhotonPlugin.getDefault();
        ScopedPreferenceStore preferenceStore = plugin.getPreferenceStore();
        preferenceStore.addPropertyChangeListener(this);
        updateCustomFields(preferenceStore.getString(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));
        messageFactory = plugin.getFIXVersion().getMessageFactory();
        // subscribe to changes in the model (the model will be updated when the symbol changes)
        orderTicketModel.getPublisher().subscribe(new ISubscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return inData instanceof OrderTicketModel.OrderTicketPublication &&
                ((OrderTicketModel.OrderTicketPublication)inData).getType().equals(OrderTicketModel.OrderTicketPublication.Type.SYMBOL_CHANGE); 
            }
            @Override
            public void publishTo(Object inData)
            {
                String newSymbol = ((OrderTicketModel.OrderTicketPublication)inData).getSymbolFragment();
                if (PhotonPlugin.getMainConsoleLogger().isDebugEnabled()){
                    PhotonPlugin.getMainConsoleLogger().debug(String.format("Subscribing to new symbol: %s", //$NON-NLS-1$
                                                                            newSymbol));
                }
                listenMarketData(newSymbol);
            }
        });
    }
    /**
     * Cancel all market data feed subscriptions.  Catches and logs any exceptions.
     */
    protected void unlistenMarketData() {
        MarketDataFeedService<?> service = marketDataTracker.getMarketDataFeedService();
        if (service != null) {
            try {
                doUnlistenMarketData(service);
            } catch (CoreException e) {
                PhotonPlugin.getMainConsoleLogger().warn(CANNOT_UNSUBSCRIBE.getText());
            }
        }
    }	
    /**
     * Do the work of unsubscribing from the given MarketDataFeedService
     * throwing exceptions if necessary
     * 
     * @param service the service to unsubscribe from
     * @throws CoreException if there is a problem unsubscribing
     */
    protected void doUnlistenMarketData(MarketDataFeedService<?> service) throws CoreException {
        if (primaryMarketDataToken != null) {
            primaryMarketDataToken.cancel();

            primaryMarketDataToken = null;
        }
    }

    /**
     * Subscribe for market data for the given symbol.
     * 
     * @param symbol the symbol for which to subscribe for market data
     */
    public void listenMarketData(String symbol) {
        unlistenMarketData();
        if (symbol != null && !"".equals(symbol.trim())) { //$NON-NLS-1$
            try {
                MarketDataFeedService<?> service = getMarketDataTracker()
                .getMarketDataFeedService();

                if (service != null){
                    doListenMarketData(service, new MSymbol(symbol));
                }
            } catch (CoreException e) {
				PhotonPlugin.getMainConsoleLogger().error(CANNOT_SUBSCRIBE_TO_MARKET_DATA.getText(symbol));
            }
        }
    }

    /**
     * Does the work of subscribing for market data based on the given symbol,
     * and MarketDataFeedService
     * 
     * @param service the service from which to subscribe
     * @param symbol the symbol for which to describe
     * @throws CoreException if there is a problem subscribing
     */
    protected void doListenMarketData(MarketDataFeedService<?> service, MSymbol symbol) 
        throws CoreException 
    {
        Message subscriptionMessage = MarketDataUtils.newSubscribeLevel2(symbol);
        primaryMarketDataToken = service.execute(subscriptionMessage, new ISubscriber() {
            public boolean isInteresting(Object arg0) {
                return true;
            }
            public void publishTo(Object obj) {
                Message message;
                if (obj instanceof HasFIXMessage){
                    message = ((HasFIXMessage)obj).getMessage();
                } else {
                    message = (Message) obj;
                }
                doOnPrimaryQuote(message);
            }
        });
    }

    /**
     * Subclasses should implement this method to handle market data messages
     * received for this order ticket.
     * 
     * Messages will usually be {@link MsgType#MARKET_DATA_INCREMENTAL_REFRESH},
     * {@link MsgType#MARKET_DATA_SNAPSHOT_FULL_REFRESH}, {@link MsgType#DERIVATIVE_SECURITY_LIST},
     * or other market data related message.
     * 
     * @param message the market data message
     */
    protected abstract void doOnPrimaryQuote(Message message);


    /**
     * Get the market data tracker.
     * @return the market data tracker.
     */
    public MarketDataFeedTracker getMarketDataTracker() {
        return marketDataTracker;
    }

    /**
     * Get the order ticket model for this order ticket.
     */
    public T getOrderTicketModel() {
        return orderTicketModel;
    }

    /**
     * Remove this as a market data listener, and close the connection
     * to the {@link MarketDataFeedTracker}.
     * 
     * Remove this as a listener to preference store events.
     */
    public void dispose() {
        marketDataTracker.close();
        // don't dispose of system colors
        PhotonPlugin.getDefault().getPreferenceStore()
        .removePropertyChangeListener(this);
    }

    /**
     * Handle a property change event.  If the property change is the 
     * {@link CustomOrderFieldPage#CUSTOM_FIELDS_PREFERENCE} preference,
     * call {@link #updateCustomFields(String)}
     * 
     * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        final String property = event.getProperty();
        final String valueString = event.getNewValue().toString();
        if (CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE.equals(property)) {
            updateCustomFields(valueString);
        }
    }

    /**
     * Loop through the custom fields as represented in the preferenceString
     * and synchronize the entries with the list of {@link CustomField}s in
     * the order ticket model.
     * 
     * @param preferenceString string representing the custom fields as a preference entry
     */
    public void updateCustomFields(String preferenceString) 
    {
        WritableList customFieldsList = getOrderTicketModel().getCustomFieldsList();
        customFieldsList.clear();
        if (preferenceString.contains("=")){ //$NON-NLS-1$
            String [] pieces = preferenceString.split("&"); //$NON-NLS-1$
            for (String piece : pieces) {
                try {
                    customFieldsList.add(CustomField.fromString(piece));
                } catch (Throwable ex){
					PhotonPlugin.getMainConsoleLogger().warn(CANNOT_READ_CUSTOM_FIELD.getText(piece),
					                                         ex);
                }
            }
        }
    }

    /**
     * Get the order message associated with this order ticket.
     * @return the order message
     */
    public Message getOrderMessage() {
        return orderTicketModel.getOrderMessage();
    }


    /**
     * Set the order message associated with this order ticket.
     * @param the new order message
     */
    public void setOrderMessage(Message order) {
        orderTicketModel.setOrderMessage(order);
    }

    /**
     * Get the current market data subscription for this ticket.
     * 
     * @return the ISubscription for market data
     */
    public IMarketDataFeedToken<?> getPrimaryMarketDataToken() {
        return primaryMarketDataToken;
    }
}

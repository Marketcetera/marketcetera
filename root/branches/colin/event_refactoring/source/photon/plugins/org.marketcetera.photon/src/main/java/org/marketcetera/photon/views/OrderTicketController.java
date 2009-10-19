package org.marketcetera.photon.views;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.quickfix.FIXMessageFactory;

import quickfix.Message;

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
@ClassVersion("$Id$")
public abstract class OrderTicketController <T extends OrderTicketModel>
	implements IOrderTicketController, IPropertyChangeListener, Messages
{

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

        clear();

        PhotonPlugin plugin = PhotonPlugin.getDefault();
        ScopedPreferenceStore preferenceStore = plugin.getPreferenceStore();
        preferenceStore.addPropertyChangeListener(this);
        updateCustomFields(preferenceStore.getString(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));
        messageFactory = plugin.getFIXVersion().getMessageFactory();
    }

    /**
     * Get the order ticket model for this order ticket.
     */
    public T getOrderTicketModel() {
        return orderTicketModel;
    }

    @Override
    public void dispose() {
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
     * @param order the new order message
     */
    public void setOrderMessage(Message order) {
        orderTicketModel.setOrderMessage(order);
    }
    
    @Override
    public void setBrokerId(String id) {
    	orderTicketModel.setBrokerId(id);
    }
}

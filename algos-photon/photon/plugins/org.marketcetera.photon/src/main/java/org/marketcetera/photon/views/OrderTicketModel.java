package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.marketcetera.algo.BrokerAlgo;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.algo.BrokerAlgoTagSpec;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.BrokerManager.Broker;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.photon.ui.databinding.NewOrReplaceOrderObservable;
import org.marketcetera.trade.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;

/* $License$ */

/**
 * The abstract superclass for model objects that represent order tickets.
 * 
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @since 0.6.0
 */
@ClassVersion("$Id$")
public abstract class OrderTicketModel {

    protected static final Object BLANK = new NullSentinel(""); //$NON-NLS-1$
    private final NewOrReplaceOrderObservable mOrderObservable = new NewOrReplaceOrderObservable();
    private final ITypedObservableValue<BrokerID> mBrokerId;
    private final ITypedObservableValue<Side> mSide;
    private final ITypedObservableValue<BigDecimal> mQuantity;
    private final ITypedObservableValue<BigDecimal> mDisplayQuantity;
    private final ITypedObservableValue<BigDecimal> mPrice;
    private final ITypedObservableValue<TimeInForce> mTimeInForce;
    private final ITypedObservableValue<String> mAccount;
    private final ITypedObservableValue<OrderType> mOrderType;
    private final ITypedObservableValue<Boolean> mIsLimitOrder;
    private final ITypedObservableValue<BrokerAlgo> mBrokerAlgo;
    private final WritableList mCustomFieldsList = new WritableList();
    private final WritableList mAlgoTagsList = new WritableList();
    private Broker selectedBroker;
    private BrokerAlgo selectedAlgo;
    private final IObservableList validAlgos = new WritableList(new SyncRealm(),
                                                                Lists.newArrayList(BLANK),
                                                                BrokerAlgo.class);
    private final IObservableList unmodifiableValidAlgos = Observables.unmodifiableObservableList(validAlgos);

    /**
     * Constructor.
     */
    public OrderTicketModel() {
        mSide = mOrderObservable.observeSide();
        mQuantity = mOrderObservable.observeQuantity();
        mDisplayQuantity = mOrderObservable.observeDisplayQuantity();
        mOrderType = mOrderObservable.observeOrderType();
        mPrice = mOrderObservable.observePrice();
        mTimeInForce = mOrderObservable.observeTimeInForce();
        mAccount = mOrderObservable.observeAccount();
        mBrokerId = mOrderObservable.observeBrokerId();
        mBrokerAlgo = mOrderObservable.observeBrokerAlgo();

        mIsLimitOrder = TypedObservableValueDecorator.decorate(
                new ComputedValue(Boolean.class) {
                    @Override
                    protected Object calculate() {
                        return mOrderType.getValue() == OrderType.Limit;
                    }
                }, true, Boolean.class);

        mIsLimitOrder.addValueChangeListener(new IValueChangeListener() {
            @Override
            public void handleValueChange(ValueChangeEvent event) {
                if (!mIsLimitOrder.getTypedValue()) {
                    mPrice.setValue(null);
                }
            }
        });
        // this listener watches the selection of the broker ID combo and updates the list of available broker algos
        mBrokerId.addValueChangeListener(new IValueChangeListener() {
            @Override
            public void handleValueChange(ValueChangeEvent inEvent)
            {
                if(getSelectedBroker() != null) {
                    Set<BrokerAlgoSpec> algos = getSelectedBroker().getAlgos();
                    validAlgos.clear();
                    // add a blank value at the top that allows you to unselect an algo
                    validAlgos.add(BLANK);
                    if(algos != null) {
                        for(BrokerAlgoSpec algo : algos) {
                            Set<BrokerAlgoTag> algoTags = new TreeSet<BrokerAlgoTag>();
                            for(BrokerAlgoTagSpec tagSpec : algo.getAlgoTagSpecs()) {
                                algoTags.add(new BrokerAlgoTag(tagSpec));
                            }
                            validAlgos.add(new BrokerAlgo(algo,
                                                          algoTags));
                        }
                    }
                }
            }
        });
        // this listener watches the selection of the algo and populates the algo tag table
        mBrokerAlgo.addValueChangeListener(new IValueChangeListener() {
            @Override
            public void handleValueChange(ValueChangeEvent inEvent)
            {
                updateAlgoTags();
            }
        });
    }
    public void updateAlgoTags()
    {
        // find selected algo and populate the algo table
        mAlgoTagsList.clear();
        if(selectedAlgo != null && selectedAlgo.getAlgoTags() != null) {
            for(BrokerAlgoTag tag : selectedAlgo.getAlgoTags()) {
                mAlgoTagsList.add(tag);
            }
        }
    }
    /**
     * Get the selectedBroker value.
     *
     * @return a <code>Broker</code> value
     */
    public Broker getSelectedBroker()
    {
        return selectedBroker;
    }
    /**
     * Sets the selectedBroker value.
     *
     * @param inSelectedBroker a <code>Broker</code> value
     */
    public void setSelectedBroker(Broker inSelectedBroker)
    {
        selectedBroker = inSelectedBroker;
    }
    /**
     * Get the selectedAlgo value.
     *
     * @return a <code>BrokerAlgo</code> value
     */
    public BrokerAlgo getSelectedAlgo()
    {
        return selectedAlgo;
    }
    /**
     * Sets the selectedAlgo value.
     *
     * @param inSelectedAlgo a <code>BrokerAlgo</code> value
     */
    public void setSelectedAlgo(BrokerAlgo inSelectedAlgo)
    {
        selectedAlgo = inSelectedAlgo;
    }
    /**
     * Returns an observable that tracks the current order.
     * 
     * @return the order observable
     */
    public final NewOrReplaceOrderObservable getOrderObservable() {
        return mOrderObservable;
    }

    /**
     * Returns the broker of the ticket being edited.
     * 
     * @return the broker observable
     */
    public final ITypedObservableValue<BrokerID> getBrokerId() {
        return mBrokerId;
    }
    /**
     * Get the brokerAlgo value.
     *
     * @return an <code>ITypedObservableValue<BrokerAlgo></code> value
     */
    public ITypedObservableValue<BrokerAlgo> getBrokerAlgo()
    {
        return mBrokerAlgo;
    }
    /**
     * Returns an observable that tracks the side of the current order.
     * 
     * @return the side observable
     */
    public final ITypedObservableValue<Side> getSide() {
        return mSide;
    }

    /**
     * Returns an observable that tracks the quantity of the current order.
     * 
     * @return the quantity observable
     */
    public final ITypedObservableValue<BigDecimal> getQuantity() {
        return mQuantity;
    }
    
    /**
     * Returns an observable that tracks the display quantity of the current order.
     * 
     * @return the quantity observable
     */
    public final ITypedObservableValue<BigDecimal> getDisplayQuantity() {
        return mDisplayQuantity;
    }

    /**
     * Returns an observable that tracks the symbol of the current order.
     * 
     * @return the symbol observable
     */
    public abstract ITypedObservableValue<String> getSymbol();

    /**
     * Returns an observable that tracks the order type of the current order.
     * 
     * @return the order type observable
     */
    public final ITypedObservableValue<OrderType> getOrderType() {
        return mOrderType;
    }

    /**
     * Returns an observable that tracks whether the order is a limit order.
     * 
     * @return the limit order type observable
     */
    public final ITypedObservableValue<Boolean> isLimitOrder() {
        return mIsLimitOrder;
    }

    /**
     * Returns an observable that tracks the price of the current order.
     * 
     * @return the price observable
     */
    public final ITypedObservableValue<BigDecimal> getPrice() {
        return mPrice;
    }

    /**
     * Returns an observable that tracks the time in force of the current order.
     * 
     * @return the time in force observable
     */
    public final ITypedObservableValue<TimeInForce> getTimeInForce() {
        return mTimeInForce;
    }

    /**
     * Returns an observable that tracks the account of the current order.
     * 
     * @return the account observable
     */
    public final ITypedObservableValue<String> getAccount() {
        return mAccount;
    }

    /**
     * Clear the existing order message and replace it with a new empty one.
     */
    public final void clearOrderMessage() {
        mOrderObservable.setValue(createNewOrder());
    }

    /**
     * Creates a new empty order.
     * 
     * @return the new order
     */
    protected OrderSingle createNewOrder() {
        NewOrReplaceOrder currentOrder = getOrderObservable().getTypedValue();
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setTimeInForce(org.marketcetera.trade.TimeInForce.Day);
        if (currentOrder != null) {
            // save broker selection
            order.setBrokerID(currentOrder.getBrokerID());
        }
        return order;
    }

    /**
     * The list that should store a collection of {@link CustomField} objects.
     * These custom fields are presented to the user, and each can be activated
     * for inclusion into all messages generated by this order ticket.
     * 
     * Modify this list directly to add and remove items.
     * 
     * @return the list of custom fields
     */
    public final WritableList getCustomFieldsList() {
        return mCustomFieldsList;
    }
    /**
     * 
     *
     *
     * @return
     */
    public final WritableList getAlgoTagsList()
    {
        return mAlgoTagsList;
    }
    /**
     * This method is responsible for "completing" the order message prior to
     * sending it.
     */
    public void completeMessage() {
        addCustomFields();
    }

    /**
     * Loops through the list of custom fields and adds the enabled fields to
     * the message.
     */
    private void addCustomFields() {
        NewOrReplaceOrder order = mOrderObservable.getTypedValue();
        Map<String, String> map = Maps.newHashMap();
        for (Object customFieldObject : mCustomFieldsList) {
            CustomField customField = (CustomField) customFieldObject;
            if (customField.isEnabled()) {
                String key = customField.getKeyString();
                String value = customField.getValueString();
                map.put(key, value);
            }
        }
        if (!map.isEmpty()) {
            order.setCustomFields(map);
        }
    }

    /**
     * Get the valid values for the side.
     * 
     * @return the valid sides
     */
    public Object[] getValidSideValues() {
        return EnumSet.complementOf(
                EnumSet.of(Side.Unknown, Side.SellShortExempt)).toArray();
    }

    /**
     * Get the valid values for the order type.
     * 
     * @return the valid order types
     */
    public Object[] getValidOrderTypeValues() {
        return EnumSet.complementOf(EnumSet.of(OrderType.Unknown)).toArray();
    }

    /**
     * Get the valid values for the broker.
     * 
     * @return the valid brokers
     */
    public IObservableList getValidBrokers() {
        return BrokerManager.getCurrent().getAvailableBrokers();
    }
    public IObservableList getValidAlgos()
    {
        return unmodifiableValidAlgos;
    }
    /**
     * Get the valid values for the time in force.
     * 
     * @return the valid time in force values
     */
    public Object[] getValidTimeInForceValues() {
        return ObjectArrays.concat(BLANK, EnumSet.complementOf(
                EnumSet.of(TimeInForce.Unknown)).toArray());
    }

    /**
     * An object that can be used in place of null. It has a {@link #toString()}
     * value for display purposes, but it corresponds to a null model value.
     */
    @ClassVersion("$Id$")
    static class NullSentinel {
        private final String mString;

        /**
         * Constructor.
         * 
         * @param string
         *            the value for {@link #toString()}
         */
        public NullSentinel(String string) {
            mString = string;
        }

        @Override
        public String toString() {
            return mString;
        }
    }
    @ClassVersion("$Id$")
    private final class SyncRealm
            extends Realm
    {
        @Override
        public boolean isCurrent() {
            return true;
        }

        @Override
        protected void syncExec(Runnable runnable) {
            synchronized(OrderTicketModel.this) {
                super.syncExec(runnable);
            }
        }
    }
}

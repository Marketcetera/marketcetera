package org.marketcetera.photon.ui.databinding;

import java.math.BigDecimal;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderCapacity;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Observes a {@link NewOrReplaceOrder} object and provides methods for
 * observing its details.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class NewOrReplaceOrderObservable extends
        TypedObservableValue<NewOrReplaceOrder> {

    /**
     * Constructor.
     */
    public NewOrReplaceOrderObservable() {
        super(NewOrReplaceOrder.class);
    }

    private NewOrReplaceOrder mOrder;

    @Override
    protected NewOrReplaceOrder doGetValue() {
        return mOrder;
    }

    @Override
    protected void doSetTypedValue(NewOrReplaceOrder value) {
        /*
         * Even if the order has not changed, we fire the change anyway to
         * provide a mechanism to update detail value listeners. This is
         * necessary because NewOrReplaceOrder impl's are mutable but don't
         * provide change notifications.
         */
        NewOrReplaceOrder oldValue = mOrder;
        mOrder = (NewOrReplaceOrder) value;
        fireValueChange(Diffs.createValueDiff(oldValue, mOrder));
    }

    /**
     * Provides a detail observable value for the side field on the underlying
     * order. The created observable will be disposed with this object.
     * 
     * @return an observable that tracks the order side
     */
    public ITypedObservableValue<Side> observeSide() {
        return observeDetail("side", Side.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the quantity field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order quantity
     */
    public ITypedObservableValue<BigDecimal> observeQuantity() {
        return observeDetail("quantity", BigDecimal.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the instrument field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order instrument
     */
    public ITypedObservableValue<Instrument> observeInstrument() {
        return observeDetail("instrument", Instrument.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the order type field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order type
     */
    public ITypedObservableValue<OrderType> observeOrderType() {
        return observeDetail("orderType", OrderType.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the price field on the underlying
     * order. The created observable will be disposed with this object.
     * 
     * @return an observable that tracks the order price
     */
    public ITypedObservableValue<BigDecimal> observePrice() {
        return observeDetail("price", BigDecimal.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the account field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order account
     */
    public ITypedObservableValue<String> observeAccount() {
        return observeDetail("account", String.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the time in force field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order time in force
     */
    public ITypedObservableValue<TimeInForce> observeTimeInForce() {
        return observeDetail("timeInForce", TimeInForce.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the order capacity field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order capacity
     */
    public ITypedObservableValue<OrderCapacity> observeOrderCapacity() {
        return observeDetail("orderCapacity", OrderCapacity.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the position effect field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order position effect
     */
    public ITypedObservableValue<PositionEffect> observePositionEffect() {
        return observeDetail("positionEffect", PositionEffect.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the broker id field on the
     * underlying order. The created observable will be disposed with this
     * object.
     * 
     * @return an observable that tracks the order broker id
     */
    public ITypedObservableValue<BrokerID> observeBrokerId() {
        return observeDetail("brokerID", BrokerID.class); //$NON-NLS-1$
    }

    /**
     * Provides a detail observable value for the given field on the underlying
     * order. The created observable will be disposed with this object.
     * 
     * @param field
     *            the field to observe
     * @return an observable that tracks the value of the field
     */
    private <T> ITypedObservableValue<T> observeDetail(final String field,
            Class<T> clazz) {
        return TypedObservableValueDecorator.decorate(MasterDetailObservables
                .detailValue(this, new IObservableFactory() {
                    @Override
                    public IObservable createObservable(Object target) {
                        NewOrReplaceOrder order = (NewOrReplaceOrder) target;
                        return PojoObservables.observeValue(order, field);
                    }
                }, clazz), true, clazz);
    }
}

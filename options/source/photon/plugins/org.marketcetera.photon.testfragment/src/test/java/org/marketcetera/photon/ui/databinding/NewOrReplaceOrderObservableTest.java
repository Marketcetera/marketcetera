package org.marketcetera.photon.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.photon.ui.databinding.NewOrReplaceOrderObservable;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderCapacity;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;

/* $License$ */

/**
 * Tests {@link NewOrReplaceOrderObservable}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class NewOrReplaceOrderObservableTest {
    
    private abstract class TestTemplate<T> {
        
        protected TestTemplate(T value1, T value2, T value3) throws Exception {
            NewOrReplaceOrder order = Factory.getInstance().createOrderSingle();
            set(order, value1);
            NewOrReplaceOrderObservable orderObservable = new NewOrReplaceOrderObservable();
            orderObservable.setValue(order);
            ITypedObservableValue<T> detail = observeDetail(orderObservable);
            assertThat(detail.getTypedValue(), is(value1));
            order = Factory.getInstance().createOrderSingle();
            set(order, value2);
            orderObservable.setValue(order);
            assertThat(detail.getTypedValue(), is(value2));
            detail.setValue(value3);
            assertThat(get(order), is(value3));
        }
        
        protected abstract void set(NewOrReplaceOrder order, T value);
        
        protected abstract T get(NewOrReplaceOrder order);
        
        protected abstract ITypedObservableValue<T> observeDetail(NewOrReplaceOrderObservable master);
    }

    @Test
    @UI
    public void testObserveSide() throws Exception {
        new TestTemplate<Side>(Side.Buy, Side.Sell, Side.SellShort) {
            @Override
            protected void set(NewOrReplaceOrder order, Side value) {
                order.setSide(value);
            }
            @Override
            protected ITypedObservableValue<Side> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observeSide();
            }
            @Override
            protected Side get(NewOrReplaceOrder order) {
                return order.getSide();
            }
        };
    }

    @Test
    @UI
    public void testObserveQuantity() throws Exception {
        new TestTemplate<BigDecimal>(new BigDecimal("123"), new BigDecimal("321"), new BigDecimal("5.5")) {
            @Override
            protected void set(NewOrReplaceOrder order, BigDecimal value) {
                order.setQuantity(value);
            }
            @Override
            protected ITypedObservableValue<BigDecimal> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observeQuantity();
            }
            @Override
            protected BigDecimal get(NewOrReplaceOrder order) {
                return order.getQuantity();
            }
        };
    }

    @Test
    @UI
    public void testObserveInstrument() throws Exception {
        new TestTemplate<Instrument>(new Equity("ABC"), new Option("ABC", "200910", BigDecimal.ONE, OptionType.Call),new Equity("YBM")) {
            @Override
            protected void set(NewOrReplaceOrder order, Instrument value) {
                order.setInstrument(value);
            }
            @Override
            protected ITypedObservableValue<Instrument> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observeInstrument();
            }
            @Override
            protected Instrument get(NewOrReplaceOrder order) {
                return order.getInstrument();
            }
        };
    }

    @Test
    @UI
    public void testObservePrice() throws Exception {
        new TestTemplate<BigDecimal>(new BigDecimal("123"), new BigDecimal("321"), new BigDecimal("5.5")) {
            @Override
            protected void set(NewOrReplaceOrder order, BigDecimal value) {
                order.setPrice(value);
            }
            @Override
            protected ITypedObservableValue<BigDecimal> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observePrice();
            }
            @Override
            protected BigDecimal get(NewOrReplaceOrder order) {
                return order.getPrice();
            }
        };
    }

    @Test
    @UI
    public void testObserveTimeInForce() throws Exception {
        new TestTemplate<TimeInForce>(TimeInForce.Day, TimeInForce.FillOrKill, TimeInForce.GoodTillCancel) {
            @Override
            protected void set(NewOrReplaceOrder order, TimeInForce value) {
                order.setTimeInForce(value);
            }
            @Override
            protected ITypedObservableValue<TimeInForce> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observeTimeInForce();
            }
            @Override
            protected TimeInForce get(NewOrReplaceOrder order) {
                return order.getTimeInForce();
            }
        };
    }

    @Test
    @UI
    public void testObserveAccount() throws Exception {
        new TestTemplate<String>("acc", "a2", "123") {
            @Override
            protected void set(NewOrReplaceOrder order, String value) {
                order.setAccount(value);
            }
            @Override
            protected ITypedObservableValue<String> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observeAccount();
            }
            @Override
            protected String get(NewOrReplaceOrder order) {
                return order.getAccount();
            }
        };
    }

    @Test
    @UI
    public void testObserveBrokerId() throws Exception {
        new TestTemplate<BrokerID>(new BrokerID("ABC"), new BrokerID("XYZ"), new BrokerID("123")) {
            @Override
            protected void set(NewOrReplaceOrder order, BrokerID value) {
                order.setBrokerID(value);
            }
            @Override
            protected ITypedObservableValue<BrokerID> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observeBrokerId();
            }
            @Override
            protected BrokerID get(NewOrReplaceOrder order) {
                return order.getBrokerID();
            }
        };
    }

    @Test
    @UI
    public void testObserveOrderCapacity() throws Exception {
        new TestTemplate<OrderCapacity>(OrderCapacity.Agency, OrderCapacity.Individual, OrderCapacity.Principal) {
            @Override
            protected void set(NewOrReplaceOrder order, OrderCapacity value) {
                order.setOrderCapacity(value);
            }
            @Override
            protected ITypedObservableValue<OrderCapacity> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observeOrderCapacity();
            }
            @Override
            protected OrderCapacity get(NewOrReplaceOrder order) {
                return order.getOrderCapacity();
            }
        };
    }

    @Test
    @UI
    public void testObservePositionEffect() throws Exception {
        new TestTemplate<PositionEffect>(PositionEffect.Close, PositionEffect.Close, PositionEffect.Open) {
            @Override
            protected void set(NewOrReplaceOrder order, PositionEffect value) {
                order.setPositionEffect(value);
            }
            @Override
            protected ITypedObservableValue<PositionEffect> observeDetail(
                    NewOrReplaceOrderObservable master) {
                return master.observePositionEffect();
            }
            @Override
            protected PositionEffect get(NewOrReplaceOrder order) {
                return order.getPositionEffect();
            }
        };
    }
}

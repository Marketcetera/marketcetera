package org.marketcetera.core.trade;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.OrderSingleSuggestion}
 *
 * @version $Id: OrderSingleSuggestionTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class OrderSingleSuggestionTest extends TypesTestBase {
    /**
     * Verifies default attributes of objects returned via
     * {@link org.marketcetera.core.trade.Factory#createOrderSingleSuggestion()}
     */
    @Test
    public void pojoDefaults() {
        OrderSingleSuggestion suggest = sFactory.createOrderSingleSuggestion();
        assertSuggestionValues(suggest, null, null);
        assertNull(suggest.getOrder());
        //Verify toString() doesn't fail.
        suggest.toString();

        assertNotSame(suggest, sFactory.createOrderSingleSuggestion());
    }

    /**
     * Verifies setters of objects returned via
     * {@link org.marketcetera.core.trade.Factory#createOrderSingleSuggestion()}
     */
    @Test
    public void pojoSetters() {
        OrderSingleSuggestion suggest = sFactory.createOrderSingleSuggestion();
        checkSetters(suggest);
    }

    private void checkSetters(OrderSingleSuggestion inSuggest) {
        checkSuggestionSetters(inSuggest);

        inSuggest.setOrder(null);
        assertNull(inSuggest.getOrder());
        final OrderSingle order = sFactory.createOrderSingle();
        order.setAccount("acc");
        Map<String, String> custom = new HashMap<String, String>();
        custom.put("343","what?");
        custom.put("737","no");
        order.setCustomFields(custom);
        order.setBrokerID(new BrokerID("bro"));
        order.setOrderCapacity(OrderCapacity.Agency);
        order.setOrderID(new OrderID("orde"));
        order.setOrderType(OrderType.Limit);
        order.setPositionEffect(PositionEffect.Open);
        order.setPrice(new BigDecimal("983.32"));
        order.setQuantity(new BigDecimal("3.365"));
        order.setSide(Side.Sell);
        order.setInstrument(new Equity("IBM"));
        order.setTimeInForce(TimeInForce.AtTheClose);
        OrderSingle clone = order.clone();
        inSuggest.setOrder(order);
        assertNotSame(order, inSuggest.getOrder());
        assertOrderSingleEquals(order, inSuggest.getOrder());
        //Verify toString() doesn't fail
        inSuggest.toString();
        
        //Verify updating the order that was set doesn't change suggestion's copy
        OrderSingleTest.check(order);
        assertOrderSingleEquals(clone, inSuggest.getOrder());
        //Verify updating returned order doesn't change suggestion's copy
        OrderSingleTest.check(inSuggest.getOrder());
        assertOrderSingleEquals(clone, inSuggest.getOrder());
        inSuggest.setOrder(null);
        assertNull(inSuggest.getOrder());
    }

}

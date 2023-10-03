package org.marketcetera.trade;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests {@link OrderSingleSuggestion}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderSingleSuggestionTest
        extends SuggestionTestBase
{
    /**
     * Verifies default attributes of objects returned via
     * {@link org.marketcetera.trade.SuggestionFactory#createOrderSingleSuggestion()}
     */
    @Test
    public void pojoDefaults() {
        OrderSingleSuggestion suggest = suggestionFactory.createOrderSingleSuggestion();
        assertSuggestionValues(suggest, null, null);
        assertNull(suggest.getOrder());
        //Verify toString() doesn't fail.
        suggest.toString();

        assertNotSame(suggest, suggestionFactory.createOrderSingleSuggestion());
    }

    /**
     * Verifies setters of objects returned via
     * {@link org.marketcetera.trade.SuggestionFactory#createOrderSingleSuggestion()}
     */
    @Test
    public void pojoSetters() {
        OrderSingleSuggestion suggest = suggestionFactory.createOrderSingleSuggestion();
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

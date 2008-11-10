package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link OrderSingleSuggestion}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderSingleSuggestionTest extends TypesTestBase {
    /**
     * Verifies default attributes of objects returned via
     * {@link org.marketcetera.trade.Factory#createOrderSingleSuggestion()}
     */
    @Test
    public void pojoDefaults() {
        OrderSingleSuggestion suggest = sFactory.createOrderSingleSuggestion();
        assertOrderValues(suggest, null, null);
        assertOrderBaseValues(suggest, null, null, null, null, null, null);
        assertNROrderValues(suggest,null, null, null);
        assertSuggestionValues(suggest, null, null);

        assertNotSame(suggest, sFactory.createOrderSingleSuggestion());
    }

    /**
     * Verifies setters of objects returned via
     * {@link org.marketcetera.trade.Factory#createOrderSingleSuggestion()}
     */
    @Test
    public void pojoSetters() {
        OrderSingleSuggestion suggest = sFactory.createOrderSingleSuggestion();
        checkSetters(suggest);
    }

    private void checkSetters(OrderSingleSuggestion inSuggest) {
        checkOrderSetters(inSuggest);
        checkOrderBaseSetters(inSuggest);
        checkNRSetters(inSuggest);
        checkSuggestionSetters(inSuggest);
    }

}
package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link QuoteBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class QuoteBeanTest
        extends MarketDataBeanTest
{
    /**
     * Tests {@link QuoteBean#getAction()} and {@link QuoteBean#setAction(QuoteAction)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void action()
            throws Exception
    {
       QuoteBean bean = (QuoteBean)constructBean();
       assertNull(bean.getAction());
       QuoteAction action = QuoteAction.CHANGE;
       bean.setAction(action);
       assertEquals(action,
                    bean.getAction());
    }
    /**
     * Tests {@link MarketDataBean#hashCode()} and {@link MarketDataBean#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        QuoteBean bean1 = (QuoteBean)constructBean();
        QuoteBean bean2 = (QuoteBean)constructBean();
        QuoteBean bean3 = (QuoteBean)constructBean();
        assertNull(bean1.getAction());
        assertNull(bean2.getAction());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // test action
        assertNull(bean1.getAction());
        bean3.setAction(QuoteAction.DELETE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setAction(bean1.getAction());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.MarketDataBeanTest#constructBean()
     */
    @Override
    protected MarketDataBean constructBean()
    {
        return new QuoteBean();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.MarketDataBeanTest#doAdditionalValidationTest(org.marketcetera.event.beans.MarketDataBean)
     */
    @Override
    protected void doAdditionalValidationTest(MarketDataBean inBean)
            throws Exception
    {
        // do MarketDataBean-level validation tests
        super.doAdditionalValidationTest(inBean);
        final QuoteBean quote = (QuoteBean)inBean;
        assertNull(quote.getAction());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_QUOTE_ACTION.getText()) {
            protected void run()
                    throws Exception
            {
                quote.validate();
            }
        };
        quote.setAction(QuoteAction.ADD);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#doAdditionalSetDefaultsTest(org.marketcetera.event.beans.EventBean)
     */
    @Override
    protected void doAdditionalSetDefaultsTest(MarketDataBean inBean)
            throws Exception
    {
        // perform MarketDataBean-level defaults test
        super.doAdditionalSetDefaultsTest(inBean);
        QuoteBean quote = (QuoteBean)inBean;
        quote.setAction(null);
        assertNull(quote.getAction());
        quote.setDefaults();
        assertEquals(QuoteAction.ADD,
                     quote.getAction());
        quote.setAction(QuoteAction.DELETE);
        quote.setDefaults();
        assertEquals(QuoteAction.DELETE,
                     quote.getAction());
    }
}

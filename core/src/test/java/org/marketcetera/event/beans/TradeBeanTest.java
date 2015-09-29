package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/* $License$ */

/**
 * Tests {@link TradeBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeBeanTest
        extends MarketDataBeanTest
{
    /**
     * Tests {@link MarketDataBean#copy(MarketDataBean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void copy()
            throws Exception
    {
        MarketDataBeanTest.doCopyTest(new TradeBean());
        doCopyTest(new TradeBean());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.MarketDataBeanTest#constructBean()
     */
    @Override
    protected TradeBean constructBean()
    {
        return new TradeBean();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.MarketDataBeanTest#doAdditionalValidationTest(org.marketcetera.event.beans.MarketDataBean)
     */
    @Override
    protected void doAdditionalValidationTest(MarketDataBean inBean)
            throws Exception
    {
        super.doAdditionalValidationTest(inBean);
        final TradeBean trade = (TradeBean)inBean;
        assertNull(trade.getTradeCondition());
        trade.validate();
        trade.setTradeCondition("trade-condition");
    }
    /**
     * Tests {@link TradeBean#copy(TradeBean)}.
     *
     * @param inBean a <code>TradeBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void doCopyTest(TradeBean inBean)
            throws Exception
    {
        verifyTradeBean(inBean,
                        null);
        TradeBean newBean = TradeBean.copy(inBean);
        verifyTradeBean(newBean,
                        null);
        String tradeCondition = "trade-condition1,trade-condition2";
        inBean.setTradeCondition(tradeCondition);
        verifyTradeBean(inBean,
                        tradeCondition);
        newBean = TradeBean.copy(inBean);
        verifyTradeBean(newBean,
                        tradeCondition);
    }
    /**
     * Verifies that the given <code>TradeBean</code> contains the given attributes.
     *
     * @param inBean a <code>TradeBean</code> value
     * @param inExpectedTradeCondition a <code>String</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void verifyTradeBean(TradeBean inBean,
                                String inExpectedTradeCondition)
            throws Exception
    {
        assertEquals(inExpectedTradeCondition,
                     inBean.getTradeCondition());
    }
}

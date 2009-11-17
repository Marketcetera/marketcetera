package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link MarketDataBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class MarketDataBeanTest
        extends AbstractEventBeanTestBase<MarketDataBean>
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
        EventBeanTest.doCopyTest(new MarketDataBean());
        doCopyTest(new MarketDataBean());
    }
    /**
     * Tests {@link E#getInstrument()} and {@link E#setInstrument(org.marketcetera.trade.Instrument)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void instrument()
            throws Exception
    {
        MarketDataBean bean = constructBean();
        assertNull(bean.getInstrument());
        assertNull(bean.getInstrumentAsString());
        Equity equity = new Equity("METC");
        Option option = new Option("METC",
                                   "201001",
                                   BigDecimal.TEN,
                                   OptionType.Put);
        bean.setInstrument(equity);
        assertEquals(equity,
                     bean.getInstrument());
        assertEquals(equity.getSymbol(),
                     bean.getInstrumentAsString());
        bean.setInstrument(option);
        assertEquals(option,
                     bean.getInstrument());
        assertEquals(option.getSymbol(),
                     bean.getInstrumentAsString());
    }
    /**
     * Tests {@link MarketDataBean#hashCode()} and {@link MarketDataBean#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Override
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        MarketDataBean bean1 = constructBean();
        MarketDataBean bean2 = constructBean();
        MarketDataBean bean3 = constructBean();
        assertNull(bean1.getExchange());
        assertNull(bean2.getExchange());
        assertNull(bean1.getExchangeTimestamp());
        assertNull(bean2.getExchangeTimestamp());
        assertNull(bean1.getInstrument());
        assertNull(bean2.getInstrument());
        assertNull(bean1.getPrice());
        assertNull(bean2.getPrice());
        assertNull(bean1.getSize());
        assertNull(bean2.getSize());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // test exchange
        // set bean3 to non-null
        assertNull(bean1.getExchange());
        bean3.setExchange("test");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setExchange(bean1.getExchange());
        // test exchangeTimestamp
        // set bean3 to non-null
        assertNull(bean1.getExchangeTimestamp());
        bean3.setExchangeTimestamp(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setExchangeTimestamp(bean1.getExchangeTimestamp());
        // test instrument
        // set bean3 to non-null
        assertNull(bean1.getInstrument());
        bean3.setInstrument(new Equity("METC"));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setInstrument(bean1.getInstrument());
        // test price
        // set bean3 to non-null
        assertNull(bean1.getPrice());
        bean3.setPrice(BigDecimal.ZERO);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setPrice(bean1.getPrice());
        // test size
        // set bean3 to non-null
        assertNull(bean1.getSize());
        bean3.setSize(BigDecimal.TEN);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#doAdditionalValidationTest(org.marketcetera.event.beans.EventBean)
     */
    @Override
    protected void doAdditionalValidationTest(final MarketDataBean inBean)
            throws Exception
    {
        assertNull(inBean.getInstrument());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_INSTRUMENT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setInstrument(new Equity("METC"));
        assertNull(inBean.getPrice());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_PRICE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setPrice(BigDecimal.ONE);
        assertNull(inBean.getSize());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_SIZE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setSize(BigDecimal.ZERO);
        assertNull(inBean.getExchange());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setExchange("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setExchange("Q");
        assertNull(inBean.getExchangeTimestamp());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE_TIMESTAMP.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setExchangeTimestamp("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE_TIMESTAMP.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                inBean.validate();
            }
        };
        inBean.setExchangeTimestamp(DateUtils.dateToString(new Date()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#constructBean()
     */
    @Override
    protected MarketDataBean constructBean()
    {
        return new MarketDataBean();
    }
    /**
     * Tests {@link MarketDataBean#copy(MarketDataBean)}.
     *
     * @param inBean a <code>MarketDataBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void doCopyTest(MarketDataBean inBean)
            throws Exception
    {
        verifyMarketDataBean(inBean,
                             null,
                             null,
                             null,
                             null,
                             null);
        MarketDataBean newBean = MarketDataBean.copy(inBean);
        verifyMarketDataBean(newBean,
                             null,
                             null,
                             null,
                             null,
                             null);
        String exchange = "test exchange";
        String exchangeTimestamp = DateUtils.dateToString(new Date());
        Instrument instrument = new Equity("GOOG");
        BigDecimal price = BigDecimal.ONE;
        BigDecimal size = BigDecimal.TEN;
        inBean.setExchange(exchange);
        inBean.setExchangeTimestamp(exchangeTimestamp);
        inBean.setInstrument(instrument);
        inBean.setPrice(price);
        inBean.setSize(size);
        verifyMarketDataBean(inBean,
                             exchange,
                             exchangeTimestamp,
                             instrument,
                             price,
                             size);
        newBean = MarketDataBean.copy(inBean);
        verifyMarketDataBean(newBean,
                             exchange,
                             exchangeTimestamp,
                             instrument,
                             price,
                             size);
    }
    /**
     * Verifies that the given <code>MarketDataBean</code> contains the given attributes.
     *
     * @param inBean a <code>MarketDataBean</code> value
     * @param inExpectedExchange a <code>String</code> value
     * @param inExpectedExchangeTimestamp a <code>String</code> value
     * @param inExpectedInstrument an <code>Instrument</code> value
     * @param inExpectedPrice a <code>BigDecimal</code> value
     * @param inExpectedSize a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void verifyMarketDataBean(MarketDataBean inBean,
                                     String inExpectedExchange,
                                     String inExpectedExchangeTimestamp,
                                     Instrument inExpectedInstrument,
                                     BigDecimal inExpectedPrice,
                                     BigDecimal inExpectedSize)
            throws Exception
    {
        assertEquals(inExpectedExchange,
                     inBean.getExchange());
        assertEquals(inExpectedExchangeTimestamp,
                     inBean.getExchangeTimestamp());
        assertEquals(inExpectedInstrument,
                     inBean.getInstrument());
        assertEquals(inExpectedPrice,
                     inBean.getPrice());
        assertEquals(inExpectedSize,
                     inBean.getSize());
    }
}

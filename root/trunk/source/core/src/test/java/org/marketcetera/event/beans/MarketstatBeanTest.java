package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 * Tests {@link MarketstatBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class MarketstatBeanTest
        extends AbstractEventBeanTestBase<MarketstatBean>
{
    /**
     * Tests {@link MarketstatBean#copy(MarketstatBean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void copy()
            throws Exception
    {
        EventBeanTest.doCopyTest(new MarketstatBean());
        doCopyTest(new MarketstatBean());
    }
    /**
     * Tests {@link MarketstatBean#getOpen()} and {@link MarketstatBean#setOpen(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void open()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getOpen());
        bean.setOpen(null);
        assertNull(bean.getOpen());
        BigDecimal open = new BigDecimal("-10");
        bean.setOpen(open);
        assertEquals(open,
                     bean.getOpen());
        open = BigDecimal.ZERO;
        bean.setOpen(open);
        assertEquals(open,
                     bean.getOpen());
        open = BigDecimal.TEN;
        bean.setOpen(open);
        assertEquals(open,
                     bean.getOpen());
    }
    /**
     * Tests {@link MarketstatBean#getHigh()} and {@link MarketstatBean#setHigh(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void high()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getHigh());
        bean.setHigh(null);
        assertNull(bean.getHigh());
        BigDecimal high = new BigDecimal("-10");
        bean.setHigh(high);
        assertEquals(high,
                     bean.getHigh());
        high = BigDecimal.ZERO;
        bean.setHigh(high);
        assertEquals(high,
                     bean.getHigh());
        high = BigDecimal.TEN;
        bean.setHigh(high);
        assertEquals(high,
                     bean.getHigh());
    }
    /**
     * Tests {@link MarketstatBean#getLow()} and {@link MarketstatBean#setLow(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void low()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getLow());
        bean.setLow(null);
        assertNull(bean.getLow());
        BigDecimal low = new BigDecimal("-10");
        bean.setLow(low);
        assertEquals(low,
                     bean.getLow());
        low = BigDecimal.ZERO;
        bean.setLow(low);
        assertEquals(low,
                     bean.getLow());
        low = BigDecimal.TEN;
        bean.setLow(low);
        assertEquals(low,
                     bean.getLow());
    }
    /**
     * Tests {@link MarketstatBean#getClose()} and {@link MarketstatBean#setClose(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void close()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getClose());
        bean.setClose(null);
        assertNull(bean.getClose());
        BigDecimal close = new BigDecimal("-10");
        bean.setClose(close);
        assertEquals(close,
                     bean.getClose());
        close = BigDecimal.ZERO;
        bean.setClose(close);
        assertEquals(close,
                     bean.getClose());
        close = BigDecimal.TEN;
        bean.setClose(close);
        assertEquals(close,
                     bean.getClose());
    }
    /**
     * Tests {@link MarketstatBean#getPreviousClose()} and {@link MarketstatBean#setPreviousClose(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void previousClose()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getPreviousClose());
        bean.setPreviousClose(null);
        assertNull(bean.getPreviousClose());
        BigDecimal previousClose = new BigDecimal("-10");
        bean.setPreviousClose(previousClose);
        assertEquals(previousClose,
                     bean.getPreviousClose());
        previousClose = BigDecimal.ZERO;
        bean.setPreviousClose(previousClose);
        assertEquals(previousClose,
                     bean.getPreviousClose());
        previousClose = BigDecimal.TEN;
        bean.setPreviousClose(previousClose);
        assertEquals(previousClose,
                     bean.getPreviousClose());
    }
    /**
     * Tests {@link MarketstatBean#getVolume()} and {@link MarketstatBean#setVolume(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void volume()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getVolume());
        bean.setVolume(null);
        assertNull(bean.getVolume());
        BigDecimal volume = new BigDecimal("-10");
        bean.setVolume(volume);
        assertEquals(volume,
                     bean.getVolume());
        volume = BigDecimal.ZERO;
        bean.setVolume(volume);
        assertEquals(volume,
                     bean.getVolume());
        volume = BigDecimal.TEN;
        bean.setVolume(volume);
        assertEquals(volume,
                     bean.getVolume());
    }
    /**
     * Tests {@link MarketstatBean#getCloseDate()} and {@link MarketstatBean#setCloseDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void closeDate()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getCloseDate());
        bean.setCloseDate(null);
        assertNull(bean.getCloseDate());
        bean.setCloseDate("");
        assertEquals("",
                     bean.getCloseDate());
        String closeDate = DateUtils.dateToString(new Date());
        bean.setCloseDate(closeDate);
        assertEquals(closeDate,
                     bean.getCloseDate());
    }
    /**
     * Tests {@link MarketstatBean#getPreviousCloseDate()} and {@link MarketstatBean#setPreviousCloseDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void previousCloseDate()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getPreviousCloseDate());
        bean.setPreviousCloseDate(null);
        assertNull(bean.getPreviousCloseDate());
        bean.setPreviousCloseDate("");
        assertEquals("",
                     bean.getPreviousCloseDate());
        String previousCloseDate = DateUtils.dateToString(new Date());
        bean.setPreviousCloseDate(previousCloseDate);
        assertEquals(previousCloseDate,
                     bean.getPreviousCloseDate());
    }
    /**
     * Tests {@link MarketstatBean#getTradeHighTime()} and {@link MarketstatBean#setTradeHighTime(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void tradeHighTime()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getTradeHighTime());
        bean.setTradeHighTime(null);
        assertNull(bean.getTradeHighTime());
        bean.setTradeHighTime("");
        assertEquals("",
                     bean.getTradeHighTime());
        String tradeHighTime = DateUtils.dateToString(new Date());
        bean.setTradeHighTime(tradeHighTime);
        assertEquals(tradeHighTime,
                     bean.getTradeHighTime());
    }
    /**
     * Tests {@link MarketstatBean#getTradeLowTime()} and {@link MarketstatBean#setTradeLowTime(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void tradeLowTime()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getTradeLowTime());
        bean.setTradeLowTime(null);
        assertNull(bean.getTradeLowTime());
        bean.setTradeLowTime("");
        assertEquals("",
                     bean.getTradeLowTime());
        String tradeLowTime = DateUtils.dateToString(new Date());
        bean.setTradeLowTime(tradeLowTime);
        assertEquals(tradeLowTime,
                     bean.getTradeLowTime());
    }
    /**
     * Tests {@link MarketstatBean#getOpenExchange()} and {@link MarketstatBean#setOpenExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void openExchange()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getOpenExchange());
        bean.setOpenExchange(null);
        assertNull(bean.getOpenExchange());
        bean.setOpenExchange("");
        assertEquals("",
                     bean.getOpenExchange());
        String openExchange = "test exchange";
        bean.setOpenExchange(openExchange);
        assertEquals(openExchange,
                     bean.getOpenExchange());
    }
    /**
     * Tests {@link MarketstatBean#getHighExchange()} and {@link MarketstatBean#setHighExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void highExchange()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getHighExchange());
        bean.setHighExchange(null);
        assertNull(bean.getHighExchange());
        bean.setHighExchange("");
        assertEquals("",
                     bean.getHighExchange());
        String highExchange = "test exchange";
        bean.setHighExchange(highExchange);
        assertEquals(highExchange,
                     bean.getHighExchange());
    }
    /**
     * Tests {@link MarketstatBean#getLowExchange()} and {@link MarketstatBean#setLowExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void lowExchange()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getLowExchange());
        bean.setLowExchange(null);
        assertNull(bean.getLowExchange());
        bean.setLowExchange("");
        assertEquals("",
                     bean.getLowExchange());
        String lowExchange = "test exchange";
        bean.setLowExchange(lowExchange);
        assertEquals(lowExchange,
                     bean.getLowExchange());
    }
    /**
     * Tests {@link MarketstatBean#getCloseExchange()} and {@link MarketstatBean#setCloseExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void closeExchange()
            throws Exception
    {
        MarketstatBean bean = constructBean();
        assertNull(bean.getCloseExchange());
        bean.setCloseExchange(null);
        assertNull(bean.getCloseExchange());
        bean.setCloseExchange("");
        assertEquals("",
                     bean.getCloseExchange());
        String closeExchange = "test exchange";
        bean.setCloseExchange(closeExchange);
        assertEquals(closeExchange,
                     bean.getCloseExchange());
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
        MarketstatBean bean = constructBean();
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
     * Tests {@link MarketstatBean#hashCode()} and {@link MarketstatBean#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Override
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        MarketstatBean bean1 = constructBean();
        MarketstatBean bean2 = constructBean();
        MarketstatBean bean3 = constructBean();
        // verify that null attributes are still equal (mostly this is that equals/hashcode doesn't NPE with null attributes)
        assertNull(bean1.getClose());
        assertNull(bean2.getClose());
        assertNull(bean1.getCloseDate());
        assertNull(bean2.getCloseDate());
        assertNull(bean1.getCloseExchange());
        assertNull(bean2.getCloseExchange());
        assertNull(bean1.getHigh());
        assertNull(bean2.getHigh());
        assertNull(bean1.getHighExchange());
        assertNull(bean2.getHighExchange());
        assertNull(bean1.getInstrument());
        assertNull(bean2.getInstrument());
        assertNull(bean1.getLow());
        assertNull(bean2.getLow());
        assertNull(bean1.getLowExchange());
        assertNull(bean2.getLowExchange());
        assertNull(bean1.getOpen());
        assertNull(bean2.getOpen());
        assertNull(bean1.getOpenExchange());
        assertNull(bean2.getOpenExchange());
        assertNull(bean1.getPreviousClose());
        assertNull(bean2.getPreviousClose());
        assertNull(bean1.getPreviousCloseDate());
        assertNull(bean2.getPreviousCloseDate());
        assertNull(bean1.getTradeHighTime());
        assertNull(bean2.getTradeHighTime());
        assertNull(bean1.getTradeLowTime());
        assertNull(bean2.getTradeLowTime());
        assertNull(bean1.getVolume());
        assertNull(bean2.getVolume());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // differs by a superclass attribute
        bean1.setMessageId(1);
        bean2.setMessageId(bean1.getMessageId());
        assertFalse(bean1.getMessageId() == bean3.getMessageId());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setMessageId(bean1.getMessageId());
        assertEquals(bean1.getTimestamp(),
                     bean3.getTimestamp());
        // test close
        // set bean3 to non-null
        assertNull(bean1.getClose());
        bean3.setClose(BigDecimal.TEN);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setClose(bean1.getClose());
        // test closeDate
        // set bean3 to non-null
        assertNull(bean1.getCloseDate());
        bean3.setCloseDate(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setCloseDate(bean1.getCloseDate());
        // test closeExchange
        // set bean3 to non-null
        assertNull(bean1.getCloseExchange());
        bean3.setCloseExchange("test exchange");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setCloseExchange(bean1.getCloseExchange());
        // test closePrice
        // set bean3 to non-null
        assertNull(bean1.getClose());
        bean3.setClose(BigDecimal.ONE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setClose(bean1.getClose());
        // test highExchange
        // set bean3 to non-null
        assertNull(bean1.getHighExchange());
        bean3.setHighExchange("test exchange");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setHighExchange(bean1.getHighExchange());
        // test highPrice
        // set bean3 to non-null
        assertNull(bean1.getHigh());
        bean3.setHigh(BigDecimal.ONE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setHigh(bean1.getHigh());
        // test instrument
        // set bean3 to non-null
        assertNull(bean1.getInstrument());
        bean3.setInstrument(new Equity("METC"));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setInstrument(bean1.getInstrument());
        // test lowExchange
        // set bean3 to non-null
        assertNull(bean1.getLowExchange());
        bean3.setLowExchange("test exchange");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setLowExchange(bean1.getLowExchange());
        // test lowPrice
        // set bean3 to non-null
        assertNull(bean1.getLow());
        bean3.setLow(BigDecimal.ONE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setLow(bean1.getLow());
        // test openExchange
        // set bean3 to non-null
        assertNull(bean1.getOpenExchange());
        bean3.setOpenExchange("test exchange");
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setOpenExchange(bean1.getOpenExchange());
        // test openPrice
        // set bean3 to non-null
        assertNull(bean1.getOpen());
        bean3.setOpen(BigDecimal.ONE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setOpen(bean1.getOpen());
        // test previousCloseDate
        // set bean3 to non-null
        assertNull(bean1.getPreviousCloseDate());
        bean3.setPreviousCloseDate(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setPreviousCloseDate(bean1.getPreviousCloseDate());
        // test previousClosePrice
        // set bean3 to non-null
        assertNull(bean1.getPreviousClose());
        bean3.setPreviousClose(BigDecimal.ONE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setPreviousClose(bean1.getPreviousClose());
        // test tradeHighTime
        // set bean3 to non-null
        assertNull(bean1.getTradeHighTime());
        bean3.setTradeHighTime(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setTradeHighTime(bean1.getTradeHighTime());
        // test tradeLowTime
        // set bean3 to non-null
        assertNull(bean1.getTradeLowTime());
        bean3.setTradeLowTime(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setTradeLowTime(bean1.getTradeLowTime());
        // test volume
        // set bean3 to non-null
        assertNull(bean1.getVolume());
        bean3.setVolume(BigDecimal.ONE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setVolume(bean1.getVolume());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#doAdditionalValidationTest(org.marketcetera.event.beans.EventBean)
     */
    @Override
    protected void doAdditionalValidationTest(final MarketstatBean inBean)
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
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.AbstractEventBeanTestBase#constructBean()
     */
    @Override
    protected MarketstatBean constructBean()
    {
        return new MarketstatBean();
    }
    /**
     * Tests {@link MarketstatBean#copy(MarketstatBean)}.
     *
     * @param inBean an <code>MarketstatBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void doCopyTest(MarketstatBean inBean)
            throws Exception
    {
        verifyMarketstatBean(inBean,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null);
        MarketstatBean newBean = MarketstatBean.copy(inBean);
        verifyMarketstatBean(newBean,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null,
                             null);
        long useThisTimestamp = System.currentTimeMillis();
        long oneDay = 1000l * 60l * 60l * 24l;
        BigDecimal close = BigDecimal.ONE;
        String closeDate = DateUtils.dateToString(new Date(useThisTimestamp));
        String closeExchange = "close exchange";
        BigDecimal high = close.add(BigDecimal.ONE);
        String highExchange = "high exchange";
        Instrument instrument = new Equity("METC");
        BigDecimal low = high.add(BigDecimal.ONE);
        BigDecimal open = low.add(BigDecimal.ONE);
        String openExchange = "open exchange";
        BigDecimal previousClose = open.add(BigDecimal.ONE);
        String previousCloseDate = DateUtils.dateToString(new Date(useThisTimestamp + (oneDay * 1)));
        String tradeHighTime = DateUtils.dateToString(new Date(useThisTimestamp + (oneDay * 2)));
        String tradeLowTime =  DateUtils.dateToString(new Date(useThisTimestamp + (oneDay * 3)));
        BigDecimal volume = open.add(BigDecimal.ONE);
        inBean.setClose(close);
        inBean.setCloseDate(closeDate);
        inBean.setCloseExchange(closeExchange);
        inBean.setHigh(high);
        inBean.setHighExchange(highExchange);
        inBean.setInstrument(instrument);
        inBean.setLow(low);
        inBean.setOpen(open);
        inBean.setOpenExchange(openExchange);
        inBean.setPreviousClose(previousClose);
        inBean.setPreviousCloseDate(previousCloseDate);
        inBean.setTradeHighTime(tradeHighTime);
        inBean.setTradeLowTime(tradeLowTime);
        inBean.setVolume(volume);
        verifyMarketstatBean(inBean,
                             close,
                             closeDate,
                             closeExchange,
                             high,
                             highExchange,
                             instrument,
                             low,
                             open,
                             openExchange,
                             previousClose,
                             previousCloseDate,
                             tradeHighTime,
                             tradeLowTime,
                             volume);
        newBean = MarketstatBean.copy(inBean);
        verifyMarketstatBean(newBean,
                             close,
                             closeDate,
                             closeExchange,
                             high,
                             highExchange,
                             instrument,
                             low,
                             open,
                             openExchange,
                             previousClose,
                             previousCloseDate,
                             tradeHighTime,
                             tradeLowTime,
                             volume);
    }
    /**
     * Verifies that the given <code>MarketstatBean</code> contains the given attributes.
     *
     * @param inBean an <code>MarketstatBean</code> value
     * @param inExpectedClose a <code>BigDecimal</code> value
     * @param inExpectedCloseDate a <code>String</code> value
     * @param inExpectedCloseExchange a <code>String</code> value
     * @param inExpectedHigh a <code>BigDecimal</code> value
     * @param inExpectedHighExchange a <code>String</code> value
     * @param inExpectedInstrument an <code>Instrument</code> value
     * @param inExpectedLow a <code>BigDecimal</code> value
     * @param inExpectedOpen a <code>BigDecimal</code> value
     * @param inExpectedOpenExchange a <code>String</code> value
     * @param inExpectedPreviousClose a <code>BigDecimal</code> value
     * @param inExpectedPreviousCloseDate a <code>String</code> value
     * @param inExpectedTradeHighTime a <code>String</code> value
     * @param inExpectedTradeLowTime a <code>String</code> value
     * @param inExpectedVolume a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void verifyMarketstatBean(MarketstatBean inBean,
                                     BigDecimal inExpectedClose,
                                     String inExpectedCloseDate,
                                     String inExpectedCloseExchange,
                                     BigDecimal inExpectedHigh,
                                     String inExpectedHighExchange,
                                     Instrument inExpectedInstrument,
                                     BigDecimal inExpectedLow,
                                     BigDecimal inExpectedOpen,
                                     String inExpectedOpenExchange,
                                     BigDecimal inExpectedPreviousClose,
                                     String inExpectedPreviousCloseDate,
                                     String inExpectedTradeHighTime,
                                     String inExpectedTradeLowTime,
                                     BigDecimal inExpectedVolume)
            throws Exception
    {
        assertEquals(inExpectedClose,
                     inBean.getClose());
        assertEquals(inExpectedCloseDate,
                     inBean.getCloseDate());
        assertEquals(inExpectedCloseExchange,
                     inBean.getCloseExchange());
        assertEquals(inExpectedHigh,
                     inBean.getHigh());
        assertEquals(inExpectedHighExchange,
                     inBean.getHighExchange());
        assertEquals(inExpectedInstrument,
                     inBean.getInstrument());
        assertEquals(inExpectedLow,
                     inBean.getLow());
        assertEquals(inExpectedOpen,
                     inBean.getOpen());
        assertEquals(inExpectedOpenExchange,
                     inBean.getOpenExchange());
        assertEquals(inExpectedPreviousClose,
                     inBean.getPreviousClose());
        assertEquals(inExpectedPreviousCloseDate,
                     inBean.getPreviousCloseDate());
        assertEquals(inExpectedTradeHighTime,
                     inBean.getTradeHighTime());
        assertEquals(inExpectedTradeLowTime,
                     inBean.getTradeLowTime());
        assertEquals(inExpectedVolume,
                     inBean.getVolume());
    }
}

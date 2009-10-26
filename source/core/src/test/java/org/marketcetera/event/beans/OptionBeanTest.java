package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Messages;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link OptionBean}.
 * 
 * <p>Note that this class intentionally does not extend {@link AbstractEventBeanTestBase}
 * because {@link OptionBean} does not extend {@link EventBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OptionBeanTest
        implements Messages
{
    /**
     * Tests {@link OptionBean#copy(OptionBean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void copy()
            throws Exception
    {
        doCopyTest(new OptionBean());
    }
    /**
     * Tests {@link OptionBean#getOptionBeanFromEvent(org.marketcetera.event.OptionEvent)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getOptionBeanFromEvent()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                OptionBean.getOptionBeanFromEvent(null);
            }
        };
        ExpirationType expirationType = ExpirationType.AMERICAN;
        boolean hasDeliverable = true;
        int multiplier = Integer.MIN_VALUE;
        Instrument underlyingInstrument = new Equity("METC");
        final QuoteEventBuilder<BidEvent> builder = QuoteEventBuilder.bidEvent(option);
        builder.hasDeliverable(hasDeliverable)
               .withExpirationType(expirationType)
               .withExchange("test exchange")
               .withInstrument(option)
               .withMultiplier(multiplier)
               .withPrice(BigDecimal.ONE)
               .withQuoteDate(DateUtils.dateToString(new Date()))
               .withSize(BigDecimal.TEN)
               .withUnderlyingInstrument(underlyingInstrument);
        BidEvent bid = builder.create();
        assertTrue(bid instanceof OptionEvent);
        OptionBean bean = OptionBean.getOptionBeanFromEvent((OptionEvent)bid);
        verifyOptionBean(bean,
                         expirationType,
                         hasDeliverable,
                         option,
                         multiplier,
                         underlyingInstrument);
        // vary some inputs
        // multiplier
        multiplier = 0;
        builder.withMultiplier(multiplier);
        bid = builder.create();
        bean = OptionBean.getOptionBeanFromEvent((OptionEvent)bid);
        verifyOptionBean(bean,
                         expirationType,
                         hasDeliverable,
                         option,
                         multiplier,
                         underlyingInstrument);
        multiplier = Integer.MAX_VALUE;
        builder.withMultiplier(multiplier);
        bid = builder.create();
        bean = OptionBean.getOptionBeanFromEvent((OptionEvent)bid);
        verifyOptionBean(bean,
                         expirationType,
                         hasDeliverable,
                         option,
                         multiplier,
                         underlyingInstrument);
        // hasDeliverable
        hasDeliverable = false;
        builder.hasDeliverable(hasDeliverable);
        bid = builder.create();
        bean = OptionBean.getOptionBeanFromEvent((OptionEvent)bid);
        verifyOptionBean(bean,
                         expirationType,
                         hasDeliverable,
                         option,
                         multiplier,
                         underlyingInstrument);
        // underlyingInstrument
        underlyingInstrument = new Option("MSFT",
                                          "20100319",
                                          BigDecimal.ONE,
                                          OptionType.Call);
        builder.withUnderlyingInstrument(underlyingInstrument);
        bid = builder.create();
        bean = OptionBean.getOptionBeanFromEvent((OptionEvent)bid);
        verifyOptionBean(bean,
                         expirationType,
                         hasDeliverable,
                         option,
                         multiplier,
                         underlyingInstrument);
        // prove the last three test-cases are untestable
        builder.withExpirationType(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXPIRATION_TYPE.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        builder.withExpirationType(expirationType)
               .withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(null){
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        builder.withInstrument(option)
               .withUnderlyingInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_UNDERLYING_INSTRUMENT.getText()){
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
    }
    /**
     * Tests {@link OptionBean#getInstrument()} and {@link OptionBean#setInstrument(org.marketcetera.trade.Instrument)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void instrument()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertNull(bean.getInstrument());
        bean.setInstrument(option);
        assertEquals(option,
                     bean.getInstrument());
    }
    /**
     * Tests {@link OptionBean#getUnderlyingInstrument()} and {@link OptionBean#setUnderlyingInstrument(org.marketcetera.trade.Equity)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void underlyingInstrument()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertNull(bean.getUnderlyingInstrument());
        Equity equity = new Equity("METC");
        bean.setUnderlyingInstrument(equity);
        assertEquals(equity,
                     bean.getUnderlyingInstrument());
    }
    /**
     * Tests {@link OptionBean#getExpirationType()} and {@link OptionBean#setExpirationType(ExpirationType)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void expirationType()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertNull(bean.getExpirationType());
        ExpirationType expirationType = ExpirationType.AMERICAN;
        bean.setExpirationType(expirationType);
        assertEquals(expirationType,
                     bean.getExpirationType());
    }
    /**
     * Tests {@link OptionBean#getMultiplier()} and {@link OptionBean#setMultiplier(int)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void multiplier()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertEquals(0,
                     bean.getMultiplier());
        int multiplier = Integer.MIN_VALUE;
        bean.setMultiplier(multiplier);
        assertEquals(multiplier,
                     bean.getMultiplier());
        multiplier = 0;
        bean.setMultiplier(multiplier);
        assertEquals(multiplier,
                     bean.getMultiplier());
        multiplier = Integer.MAX_VALUE;
        bean.setMultiplier(multiplier);
        assertEquals(multiplier,
                     bean.getMultiplier());
    }
    /**
     * Tests {@link OptionBean#hasDeliverable()} and {@link OptionBean#setHasDeliverable(boolean)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hasDeliverable()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertEquals(false,
                     bean.hasDeliverable());
        boolean hasDeliverable = false;
        bean.setHasDeliverable(hasDeliverable);
        assertEquals(hasDeliverable,
                     bean.hasDeliverable());
        hasDeliverable = true;
        bean.setHasDeliverable(hasDeliverable);
        assertEquals(hasDeliverable,
                     bean.hasDeliverable());
    }
    /**
     * Tests {@link OptionBean#validate()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validate()
            throws Exception
    {
        final OptionBean bean = new OptionBean();
        assertNotNull(bean.toString());
        assertNull(bean.getInstrument());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_INSTRUMENT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setInstrument(new Option("METC",
                                      "201001",
                                      BigDecimal.TEN,
                                      OptionType.Put));
        assertNotNull(bean.toString());
        assertNull(bean.getUnderlyingInstrument());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_UNDERLYING_INSTRUMENT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setUnderlyingInstrument(new Equity("METC"));
        assertNull(bean.getExpirationType());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXPIRATION_TYPE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setExpirationType(ExpirationType.EUROPEAN);
    }
    /**
     * Tests {@link OptionBean#hashCode()} and {@link OptionBean#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        OptionBean bean1 = new OptionBean();
        OptionBean bean2 = new OptionBean();
        OptionBean bean3 = new OptionBean();
        assertNull(bean1.getExpirationType());
        assertNull(bean2.getExpirationType());
        assertEquals(false,
                     bean1.hasDeliverable());
        assertEquals(false,
                     bean2.hasDeliverable());
        assertNull(bean1.getInstrument());
        assertNull(bean2.getInstrument());
        assertEquals(0,
                     bean1.getMultiplier());
        assertEquals(0,
                     bean2.getMultiplier());
        assertNull(bean1.getUnderlyingInstrument());
        assertNull(bean2.getUnderlyingInstrument());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // test expirationType
        // set bean3 to non-null
        assertNull(bean1.getExpirationType());
        bean3.setExpirationType(ExpirationType.AMERICAN);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setExpirationType(bean1.getExpirationType());
        // test instrument
        assertNull(bean1.getInstrument());
        bean3.setInstrument(new Option("METC",
                                       "201001",
                                       BigDecimal.TEN,
                                       OptionType.Put));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setInstrument(bean1.getInstrument());
        // test hasDeliverable
        assertFalse(bean1.hasDeliverable());
        bean3.setHasDeliverable(true);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setHasDeliverable(bean1.hasDeliverable());
        // test multiplier
        assertEquals(0,
                     bean1.getMultiplier());
        bean3.setMultiplier(1);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setMultiplier(bean1.getMultiplier());
        // test underlyingInstrument
        // set bean3 to non-null
        assertNull(bean1.getUnderlyingInstrument());
        bean3.setUnderlyingInstrument(new Equity("METC"));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
    }
    /**
     * Tests {@link OptionBean#copy(OptionBean)}.
     *
     * @param inBean an <code>OptionBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void doCopyTest(OptionBean inBean)
            throws Exception
    {
        verifyOptionBean(inBean,
                         null,
                         false,
                         null,
                         0,
                         null);
        OptionBean newBean = OptionBean.copy(inBean);
        verifyOptionBean(newBean,
                         null,
                         false,
                         null,
                         0,
                         null);
        ExpirationType expirationType = ExpirationType.AMERICAN;
        boolean hasDeliverable = true;
        int multiplier = 10;
        Instrument underlyingInstrument = new Equity("METC");
        inBean.setExpirationType(expirationType);
        inBean.setHasDeliverable(hasDeliverable);
        inBean.setInstrument(option);
        inBean.setMultiplier(multiplier);
        inBean.setUnderlyingInstrument(underlyingInstrument);
        verifyOptionBean(inBean,
                         expirationType,
                         hasDeliverable,
                         option,
                         multiplier,
                         underlyingInstrument);
        newBean = OptionBean.copy(inBean);
        verifyOptionBean(newBean,
                         expirationType,
                         hasDeliverable,
                         option,
                         multiplier,
                         underlyingInstrument);
    }
    /**
     * Verifies that the given <code>OptionBean</code> contains the given attributes.
     *
     * @param inBean an <code>OptionBean</code> value
     * @param inExpectedExpirationType an <code>ExpirationType</code> value
     * @param inExpectedHasDeliverable a <code>boolean</code> value
     * @param inExpectedInstrument an <code>Option</code> value
     * @param inExpectedMultiplier an <code>int</code> value
     * @param inExpectedUnderlyingInstrument an <code>Instrument</code> value
     * @throws Exception if an unexpected error occurs
     */
    static void verifyOptionBean(OptionBean inBean,
                                 ExpirationType inExpectedExpirationType,
                                 boolean inExpectedHasDeliverable,
                                 Option inExpectedInstrument,
                                 int inExpectedMultiplier,
                                 Instrument inExpectedUnderlyingInstrument)
            throws Exception
    {
        assertEquals(inExpectedExpirationType,
                     inBean.getExpirationType());
        assertEquals(inExpectedHasDeliverable,
                     inBean.hasDeliverable());
        assertEquals(inExpectedInstrument,
                     inBean.getInstrument());
        assertEquals(inExpectedMultiplier,
                     inBean.getMultiplier());
        assertEquals(inExpectedUnderlyingInstrument,
                     inBean.getUnderlyingInstrument());
    }
    /**
     * test option
     */
    private final static Option option = new Option("METC",
                                                    "201001",
                                                    BigDecimal.TEN,
                                                    OptionType.Put);
}

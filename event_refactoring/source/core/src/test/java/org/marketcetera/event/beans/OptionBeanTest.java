package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.Messages;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
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
        Option option = new Option("METC",
                                   "201001",
                                   BigDecimal.TEN,
                                   OptionType.Put);
        bean.setInstrument(option);
        assertEquals(option,
                     bean.getInstrument());
    }
    /**
     * Tests {@link OptionBean#getUnderlyingEquity()} and {@link OptionBean#setUnderlyingEquity(org.marketcetera.trade.Equity)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void underlyingEquity()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertNull(bean.getUnderlyingEquity());
        Equity equity = new Equity("METC");
        bean.setUnderlyingEquity(equity);
        assertEquals(equity,
                     bean.getUnderlyingEquity());
    }
    /**
     * Tests {@link OptionBean#getExpiry()} and {@link OptionBean#setExpiry(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void expiry()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertNull(bean.getExpiry());
        String expiry = DateUtils.dateToString(new Date());
        bean.setExpiry(expiry);
        assertEquals(expiry,
                     bean.getExpiry());
    }
    /**
     * Tests {@link OptionBean#getStrike()} and {@link OptionBean#setStrike(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void strike()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertNull(bean.getStrike());
        bean.setStrike(null);
        assertNull(bean.getStrike());
        BigDecimal strike = BigDecimal.TEN;
        bean.setStrike(strike);
        assertEquals(strike,
                     bean.getStrike());
    }
    /**
     * Tests {@link OptionBean#getOptionType()} and {@link OptionBean#setOptionType(OptionType)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void optionType()
            throws Exception
    {
        OptionBean bean = new OptionBean();
        assertNull(bean.getOptionType());
        OptionType optionType = OptionType.Call;
        bean.setOptionType(optionType);
        assertEquals(optionType,
                     bean.getOptionType());
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
        assertNull(bean.getUnderlyingEquity());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_UNDERLYING_EQUITY.getText()) {
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setUnderlyingEquity(new Equity("METC"));
        assertNull(bean.getExpiry());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXPIRY.getText()) {
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setExpiry("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXPIRY.getText()) {
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setExpiry(DateUtils.dateToString(new Date()));
        assertNull(bean.getStrike());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_STRIKE.getText()) {
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setStrike(BigDecimal.TEN);
        assertNull(bean.getOptionType());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_OPTION_TYPE.getText()) {
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setOptionType(OptionType.Call);
        assertNull(bean.getExpirationType());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXPIRATION_TYPE.getText()) {
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
        assertNull(bean1.getExpiry());
        assertNull(bean2.getExpiry());
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
        assertNull(bean1.getOptionType());
        assertNull(bean2.getOptionType());
        assertNull(bean1.getStrike());
        assertNull(bean2.getStrike());
        assertNull(bean1.getUnderlyingEquity());
        assertNull(bean2.getUnderlyingEquity());
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
        // test expiry
        // set bean3 to non-null
        assertNull(bean1.getExpiry());
        bean3.setExpiry(DateUtils.dateToString(new Date()));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setExpiry(bean1.getExpiry());
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
        // test optionType
        assertNull(bean1.getOptionType());
        bean3.setOptionType(OptionType.Put);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setOptionType(bean1.getOptionType());
        // test strike
        assertNull(bean1.getStrike());
        bean3.setStrike(BigDecimal.ONE);
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
        bean3.setStrike(bean1.getStrike());
        // test underlyingEquity
        // set bean3 to non-null
        assertNull(bean1.getUnderlyingEquity());
        bean3.setUnderlyingEquity(new Equity("METC"));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
    }
}

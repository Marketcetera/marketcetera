package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.event.Messages;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link InstrumentBean}.
 * 
 * <p>Note that this class intentionally does not extend {@link AbstractEventBeanTestBase}
 * because {@link InstrumentBean} does not extend {@link EventBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class InstrumentBeanTest
        implements Messages
{
    /**
     * Tests {@link InstrumentBean#getInstrument()} and {@link InstrumentBean#setInstrument(org.marketcetera.trade.Instrument)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void instrument()
            throws Exception
    {
        InstrumentBean bean = new InstrumentBean();
        assertNull(bean.getInstrument());
        Equity equity = new Equity("METC");
        Option option = new Option("METC",
                                   "201001",
                                   BigDecimal.TEN,
                                   OptionType.Put);
        bean.setInstrument(equity);
        assertEquals(equity,
                     bean.getInstrument());
        bean.setInstrument(option);
        assertEquals(option,
                     bean.getInstrument());
    }
    /**
     * Tests {@link InstrumentBean#validate()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validate()
            throws Exception
    {
        final InstrumentBean bean = new InstrumentBean();
        assertNotNull(bean.toString());
        assertNull(bean.getInstrument());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_INSTRUMENT.getText()) {
            protected void run()
                    throws Exception
            {
                bean.validate();
            }
        };
        bean.setInstrument(new Equity("METC"));
        assertNotNull(bean.toString());
        bean.validate();
    }
    /**
     * Tests {@link InstrumentBean#hashCode()} and {@link InstrumentBean#equals(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        // test empty bean equality (and inequality with an object of a different class and null)
        // beans 1 & 2 will always be the same, bean 3 will always be different
        InstrumentBean bean1 = new InstrumentBean();
        InstrumentBean bean2 = new InstrumentBean();
        InstrumentBean bean3 = new InstrumentBean();
        assertNull(bean1.getInstrument());
        assertNull(bean2.getInstrument());
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      this,
                                      null);
        // test instrument
        // set bean3 to non-null
        assertNull(bean1.getInstrument());
        bean3.setInstrument(new Equity("METC"));
        EqualityAssert.assertEquality(bean1,
                                      bean2,
                                      bean3);
    }
}

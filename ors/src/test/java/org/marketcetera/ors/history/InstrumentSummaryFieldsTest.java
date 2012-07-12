package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

/* $License$ */
/**
 * Tests {@link InstrumentSummaryFields}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class InstrumentSummaryFieldsTest {
    @Test
    public void equity() {
        Instrument equity = new Equity("equity");
        InstrumentSummaryFields fields = InstrumentSummaryFields.SELECTOR.forInstrument(equity);
        assertNull(fields.getOptionType(equity));
        assertNull(fields.getExpiry(equity));
        assertNull(fields.getStrikePrice(equity));
    }

    @Test
    public void options() {
        Option option = new Option("option", "20101010", BigDecimal.TEN, OptionType.Put);
        InstrumentSummaryFields fields = InstrumentSummaryFields.SELECTOR.forInstrument(option);
        assertEquals(option.getType(), fields.getOptionType(option));
        assertEquals(option.getExpiry(), fields.getExpiry(option));
        assertEquals(option.getStrikePrice(), fields.getStrikePrice(option));
    }

    @Test
    public void unknown() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                InstrumentSummaryFields.SELECTOR.forInstrument(new Instrument() {
                    @Override
                    public String getSymbol() {
                        return null;
                    }

                    @Override
                    public SecurityType getSecurityType() {
                        return null;
                    }
                });
            }
        };
    }
}

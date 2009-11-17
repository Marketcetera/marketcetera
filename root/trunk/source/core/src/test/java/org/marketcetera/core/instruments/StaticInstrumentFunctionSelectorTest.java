package org.marketcetera.core.instruments;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertThat;
import org.marketcetera.trade.*;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.core.LoggerConfiguration;
import static org.hamcrest.Matchers.instanceOf;

import java.math.BigDecimal;

/* $License$ */
/**
 * Tests {@link StaticInstrumentFunctionSelector}.
 * <p>
 * Utilizes the existing usage of the selector in {@link InstrumentToMessage}
 * to test the class.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
public class StaticInstrumentFunctionSelectorTest {

    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }

    @Test
    public void forInstrument() throws Exception {
        final StaticInstrumentFunctionSelector<InstrumentToMessage> selector = InstrumentToMessage.SELECTOR;
        new ExpectedFailure<IllegalArgumentException>("instrument"){
            @Override
            protected void run() throws Exception {
                selector.forInstrument(null);
            }
        };
        assertThat(selector.forInstrument(new Equity("blue")),
                instanceOf(EquityToMessage.class));
        assertThat(selector.forInstrument(
                new Option("blue", "20091010", BigDecimal.TEN, OptionType.Call)),
                instanceOf(OptionToMessage.class));
        new ExpectedFailure<IllegalArgumentException>(
                Messages.NO_HANDLER_FOR_INSTRUMENT.getText(
                        UnknownInstrument.class.getName(),
                        InstrumentToMessage.class.getName())){
            @Override
            protected void run() throws Exception {
                selector.forInstrument(new UnknownInstrument());
            }
        };
    }
    @Test
    public void constructor() throws Exception {
        new ExpectedFailure<IllegalArgumentException>("class"){
            @Override
            protected void run() throws Exception {
                new StaticInstrumentFunctionSelector(null);
            }
        };
    }

    /**
     * An unknown instrument class for testing.
     */
    private static class UnknownInstrument extends Instrument {

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public SecurityType getSecurityType() {
            return null;
        }

        private static final long serialVersionUID = 1L;
    }
}

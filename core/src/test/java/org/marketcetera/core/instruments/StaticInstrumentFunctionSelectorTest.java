package org.marketcetera.core.instruments;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.*;

/* $License$ */
/**
 * Tests {@link StaticInstrumentFunctionSelector}.
 * <p>
 * Utilizes the existing usage of the selector in {@link InstrumentToMessage}
 * to test the class.
 *
 * @version $Id$
 * @author anshul@marketcetera.com
 * @since 2.0.0
 */
public class StaticInstrumentFunctionSelectorTest {

    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }

    @Test
    @SuppressWarnings("rawtypes")
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
        assertThat(selector.forInstrument(new ConvertibleBond("US013817AT86")),
                   instanceOf(ConvertibleBondToMessage.class));
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
    @SuppressWarnings("rawtypes")
    public void constructor() throws Exception {
        new ExpectedFailure<IllegalArgumentException>("class"){
            @Override
            @SuppressWarnings("unchecked")
            protected void run() throws Exception {
                new StaticInstrumentFunctionSelector(null);
            }
        };
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testHandlers() throws Exception {
        StaticInstrumentFunctionSelector<InstrumentToMessage> selector = InstrumentToMessage.SELECTOR;
        Map<Class<?>, InstrumentToMessage> handlers = selector.getHandlers();

        assertEquals("Should load 5 handlers", 5, selector.getHandlers().size());
        assertThat(handlers.get(Equity.class), instanceOf(EquityToMessage.class));
        assertThat(handlers.get(Option.class), instanceOf(OptionToMessage.class));
        assertThat(handlers.get(Future.class), instanceOf(FutureToMessage.class));
        assertThat(handlers.get(Currency.class), instanceOf(CurrencyToMessage.class));
        assertThat(handlers.get(ConvertibleBond.class),
                   instanceOf(ConvertibleBondToMessage.class));
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

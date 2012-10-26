package org.marketcetera.core.instruments;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.api.systemmodel.instruments.FutureExpirationMonth;
import org.marketcetera.api.systemmodel.instruments.Instrument;
import org.marketcetera.api.systemmodel.instruments.OptionType;
import org.marketcetera.api.systemmodel.instruments.SecurityType;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.trade.*;

/* $License$ */
/**
 * Tests {@link StaticInstrumentFunctionSelector}.
 * <p>
 * Utilizes the existing usage of the selector in {@link InstrumentToMessage}
 * to test the class.
 *
 * @version $Id: StaticInstrumentFunctionSelectorTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class StaticInstrumentFunctionSelectorTest {

    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }

    @Test
    public void forInstrument() throws Exception {
        @SuppressWarnings("rawtypes")
        final StaticInstrumentFunctionSelector<InstrumentToMessage> selector = InstrumentToMessage.SELECTOR;
        new ExpectedFailure<IllegalArgumentException>("instrument"){
            @Override
            protected void run() throws Exception {
                selector.forInstrument(null);
            }
        };
        assertThat(selector.forInstrument(new EquityImpl("blue")),
                instanceOf(EquityToMessage.class));
        assertThat(selector.forInstrument(
                new OptionImpl("blue", "20091010", BigDecimal.TEN, OptionType.Call)),
                instanceOf(OptionToMessage.class));
        assertThat(selector.forInstrument(new FutureImpl("blue",
                                                     FutureExpirationMonth.APRIL,
                                                     2012)),
                   instanceOf(FutureToMessage.class));
        assertThat(selector.forInstrument(new ConvertibleBondImpl("yellow")),
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
    public void constructor() throws Exception {
        new ExpectedFailure<IllegalArgumentException>("class"){
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            protected void run() throws Exception {
                new StaticInstrumentFunctionSelector(null);
            }
        };
    }

    @Test
    public void testHandlers() throws Exception {
        @SuppressWarnings("rawtypes")
        StaticInstrumentFunctionSelector<InstrumentToMessage> selector = InstrumentToMessage.SELECTOR;
        @SuppressWarnings("rawtypes")
        Map<Class<?>, InstrumentToMessage> handlers = selector.getHandlers();

        assertEquals("Should load 4 handlers",
                     4,
                     selector.getHandlers().size());
        assertThat(handlers.get(EquityImpl.class), instanceOf(EquityToMessage.class));
        assertThat(handlers.get(OptionImpl.class), instanceOf(OptionToMessage.class));
        assertThat(handlers.get(FutureImpl.class), instanceOf(FutureToMessage.class));
        assertThat(handlers.get(ConvertibleBondImpl.class),
                   instanceOf(ConvertibleBondToMessage.class));
    }

    /**
     * An unknown instrument class for testing.
     */
    private static class UnknownInstrument extends AbstractInstrumentImpl implements Instrument {

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

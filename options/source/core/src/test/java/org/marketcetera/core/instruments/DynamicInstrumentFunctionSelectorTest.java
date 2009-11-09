package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.instanceOf;
import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.SecurityType;

import java.math.BigDecimal;
import java.util.List;

/* $License$ */
/**
 * Tests {@link DynamicInstrumentFunctionSelector}.
 * <p>
 * Utilizes the existing usage of the selector in {@link InstrumentFromMessage}
 * to test the class.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DynamicInstrumentFunctionSelectorTest {

    @BeforeClass
    public static void logSetup() throws Exception {
        LoggerConfiguration.logSetup();
        FIXDataDictionaryManager.initialize(FIXVersion.FIX42,
                FIXVersion.FIX42.getDataDictionaryURL());
    }

    /**
     * Tests {@link DynamicInstrumentFunctionSelector#forValue(Object)}
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void forValue() throws Exception {
        final DynamicInstrumentFunctionSelector<Message, InstrumentFromMessage> selector =
                InstrumentFromMessage.SELECTOR;
        //null value
        new ExpectedFailure<IllegalArgumentException>("value"){
            @Override
            protected void run() throws Exception {
                selector.forValue(null);
            }
        };

        //equity
        Instrument instrument = new Equity("YBM");
        Message msg = createExecReport(instrument);
        assertThat(selector.forValue(msg), instanceOf(EquityFromMessage.class));

        //option
        instrument = new Option("YBM", "20101010", BigDecimal.TEN, OptionType.Call);
        msg = createExecReport(instrument);
        assertThat(selector.forValue(msg), instanceOf(OptionFromMessage.class));
        
        //unknown type
        msg.setField(new SecurityType(SecurityType.BANK_NOTES));
        final Message message = msg;
        new ExpectedFailure<IllegalArgumentException>(
                Messages.NO_HANDLER_FOR_VALUE.getText(message,
                        InstrumentFromMessage.class.getName())){
            @Override
            protected void run() throws Exception {
                selector.forValue(message);
            }
        };
    }

    /**
     * Tests {@link DynamicInstrumentFunctionSelector#DynamicInstrumentFunctionSelector(Class)}.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void constructor() throws Exception {
        new ExpectedFailure<IllegalArgumentException>("class"){
            @Override
            protected void run() throws Exception {
                new DynamicInstrumentFunctionSelector(null);
            }
        };
    }

    /**
     * Tests {@link DynamicInstrumentFunctionSelector#getHandlers()}.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void getHandlers() throws Exception {
        final DynamicInstrumentFunctionSelector<Message, InstrumentFromMessage> selector =
                InstrumentFromMessage.SELECTOR;
        List<InstrumentFromMessage> handlers = selector.getHandlers();
        assertEquals(2, handlers.size());
        assertThat(handlers.get(0), instanceOf(EquityFromMessage.class));
        assertThat(handlers.get(1), instanceOf(OptionFromMessage.class));
    }

    /**
     * Creates an exec report for testing.
     *
     * @param inInstrument the instrument for the report.
     *
     * @return the exec report.
     *
     * @throws Exception if there were unexpected errors.
     */
    private Message createExecReport(Instrument inInstrument)
            throws Exception  {
        return FIXVersion.FIX42.getMessageFactory().newExecutionReport("o1",
                "o2", "o3", OrdStatus.NEW, Side.BUY, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, inInstrument, "");
    }
}

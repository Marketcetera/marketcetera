package org.marketcetera.core.instruments;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldMap;
import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.SecurityType;
import quickfix.field.Side;

/* $License$ */
/**
 * Tests {@link DynamicInstrumentFunctionSelector}.
 * <p>
 * Utilizes the existing usage of the selector in {@link InstrumentFromMessage}
 * to test the class.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class DynamicInstrumentFunctionSelectorTest {

    @BeforeClass
    public static void logSetup() throws Exception {
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
        final DynamicInstrumentFunctionSelector<FieldMap,InstrumentFromMessage> selector = InstrumentFromMessage.SELECTOR;
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
        // currency
        instrument = new Currency("USD","INR","","");
        msg = createExecReport(instrument);
        assertThat(selector.forValue(msg),
                   instanceOf(CurrencyFromMessage.class));
        // convertible bond
        instrument = new ConvertibleBond("US013817AT86");
        msg = createExecReport(instrument);
        assertThat(selector.forValue(msg),
                   instanceOf(ConvertibleBondFromMessage.class));
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
            @SuppressWarnings({ "rawtypes", "unchecked" })
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
        final DynamicInstrumentFunctionSelector<FieldMap,InstrumentFromMessage> selector = InstrumentFromMessage.SELECTOR;
        List<InstrumentFromMessage> handlers = selector.getHandlers();
        assertEquals(5, handlers.size());
        assertThat(handlers.get(0), instanceOf(CurrencyFromMessage.class));
        assertThat(handlers.get(1), instanceOf(EquityFromMessage.class));
        assertThat(handlers.get(2), instanceOf(OptionFromMessage.class));
        assertThat(handlers.get(3), instanceOf(FutureFromMessage.class));
        assertThat(handlers.get(4), instanceOf(ConvertibleBondFromMessage.class));
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
                BigDecimal.ONE, inInstrument, "", "text");
    }
}

package org.marketcetera.photon.views;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link InstrumentToMemento} and {@link InstrumentFromMemento}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class InstrumentMementoSerializerTest {

    @Test
    public void testSerialization() {
        test(new Equity("IBM"));
        test(new Equity("234xyz"));
        test(new Option("YHOO", "20101010", new BigDecimal("1.1"),
                OptionType.Put));
        test(new Option("YHOO", "201010", new BigDecimal("45"), OptionType.Call));
        test(new Option("MSFT", "201010", new BigDecimal("45.0000001"),
                OptionType.Call));
        test(new Option("MSFT", "201010", new BigDecimal("0045.100000"),
                OptionType.Put));
    }
    
    @Test
    public void testNulls() throws Exception {
        final IMemento memento = XMLMemento.createWriteRoot("ABC");
        new ExpectedNullArgumentFailure("instrument") {
            @Override
            protected void run() throws Exception {
                InstrumentToMemento.save(null, memento);
            }
        };
        new ExpectedNullArgumentFailure("memento") {
            @Override
            protected void run() throws Exception {
                InstrumentToMemento.save(new Equity("a"), null);
            }
        };
        new ExpectedNullArgumentFailure("memento") {
            @Override
            protected void run() throws Exception {
                InstrumentFromMemento.restore(null);
            }
        };
    }

    private void test(Instrument instrument) {
        IMemento memento = XMLMemento.createWriteRoot("ABC");
        InstrumentToMemento.save(instrument, memento);
        assertThat(InstrumentFromMemento.restore(memento), is(instrument));
    }
}

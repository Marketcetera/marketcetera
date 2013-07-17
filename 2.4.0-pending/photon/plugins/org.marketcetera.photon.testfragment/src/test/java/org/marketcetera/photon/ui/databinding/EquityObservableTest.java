package org.marketcetera.photon.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link EquityObservable}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(SimpleUIRunner.class)
public class EquityObservableTest {

    @Test
    @UI
    public void testObserveSymbol() throws Exception {
        ITypedObservableValue<Instrument> instrument = TypedObservableValueDecorator.create(Instrument.class);
        instrument.setValue(new Equity("ABC"));
        EquityObservable equityObservable = new EquityObservable(instrument);
        ITypedObservableValue<String> symbol = equityObservable.observeSymbol();
        assertThat(symbol.getTypedValue(), is("ABC"));
        instrument.setValue(new Equity("IBM"));
        assertThat(symbol.getTypedValue(), is("IBM"));
        instrument.setValue(new Option("x", "y", BigDecimal.ONE, OptionType.Call));
        assertThat(symbol.getTypedValue(), nullValue());
        instrument.setValue(new Equity("YHOO"));
        assertThat(symbol.getTypedValue(), is("YHOO"));
        instrument.setValue(null);
        assertThat(symbol.getTypedValue(), nullValue());
        symbol.setValue("METC");
        assertThat(instrument.getTypedValue(), is((Instrument) new Equity("METC")));
        symbol.setValue("  \t");
        assertThat(instrument.getTypedValue(), nullValue());
        symbol.setValue("IBM");
        assertThat(instrument.getTypedValue(), is((Instrument) new Equity("IBM")));
        symbol.setValue("");
        assertThat(instrument.getTypedValue(), nullValue());
        symbol.setValue("YHOO");
        assertThat(instrument.getTypedValue(), is((Instrument) new Equity("YHOO")));
        symbol.setValue(null);
        assertThat(instrument.getTypedValue(), nullValue());
        instrument.dispose();
        assertTrue(symbol.isDisposed());
    }
}

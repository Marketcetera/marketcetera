package org.marketcetera.photon.ui.databinding;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.photon.ui.databinding.EquityObservable;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Tests {@link EquityObservable}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
        instrument.setValue(null);
        assertThat(symbol.getTypedValue(), nullValue());
        symbol.setValue("METC");
        assertThat(instrument.getTypedValue(), is((Instrument) new Equity("METC")));
        symbol.setValue(null);
        assertThat(instrument.getTypedValue(), nullValue());
        instrument.dispose();
        assertTrue(symbol.isDisposed());
    }
}

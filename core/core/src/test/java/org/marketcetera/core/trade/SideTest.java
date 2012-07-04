package org.marketcetera.core.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;
import org.marketcetera.core.attributes.ClassVersion;

import static org.marketcetera.core.trade.Side.*;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.Side}
 *
 * @author anshul@marketcetera.com
 * @version $Id: SideTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: SideTest.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public class SideTest extends FIXCharEnumTestBase <Side>{
    @Override
    protected Side getInstanceForFIXValue(Character inFIXValue) {
        return Side.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Character getFIXValue(Side e) {
        return e.getFIXValue();
    }

    @Override
    protected Side unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<Side> getValues() {
        return Arrays.asList(values());
    }

    @Override
    protected List<Pair<Side,Character>> knownValues()
    {
        List<Pair<Side,Character>> values = new ArrayList<Pair<Side,Character>>();
        values.add(new Pair<Side,Character>(Buy,
                                            quickfix.field.Side.BUY));
        values.add(new Pair<Side,Character>(Sell,
                                            quickfix.field.Side.SELL));
        values.add(new Pair<Side,Character>(SellShort,
                                            quickfix.field.Side.SELL_SHORT));
        values.add(new Pair<Side,Character>(SellShortExempt,
                                            quickfix.field.Side.SELL_SHORT_EXEMPT));
        return values;
    }
}
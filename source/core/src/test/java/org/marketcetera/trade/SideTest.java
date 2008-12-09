package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.Side.*;
import org.marketcetera.core.Pair;
import java.util.Arrays;
import java.util.List;

/* $License$ */
/**
 * Tests {@link Side}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    protected List<Pair<Side, Character>> knownValues() {
        return Arrays.asList(
                new Pair<Side, Character>(Buy, quickfix.field.Side.BUY),
                new Pair<Side, Character>(Sell, quickfix.field.Side.SELL),
                new Pair<Side, Character>(SellShort, quickfix.field.Side.SELL_SHORT),
                new Pair<Side, Character>(SellShortExempt, quickfix.field.Side.SELL_SHORT_EXEMPT)
        );
    }
}
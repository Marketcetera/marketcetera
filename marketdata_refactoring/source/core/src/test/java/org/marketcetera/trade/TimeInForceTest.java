package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.TimeInForce.*;
import org.marketcetera.core.Pair;
import java.util.Arrays;
import java.util.List;

/* $License$ */
/**
 * Tests {@link TimeInForce}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class TimeInForceTest extends FIXCharEnumTestBase <TimeInForce>{
    @Override
    protected TimeInForce getInstanceForFIXValue(Character inFIXValue) {
        return TimeInForce.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Character getFIXValue(TimeInForce e) {
        return e.getFIXValue();
    }

    @Override
    protected TimeInForce unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<TimeInForce> getValues() {
        return Arrays.asList(values());
    }

    @Override
    protected List<Pair<TimeInForce, Character>> knownValues() {
        return Arrays.asList(
                new Pair<TimeInForce, Character>(Day, quickfix.field.TimeInForce.DAY),
                new Pair<TimeInForce, Character>(GoodTillCancel, quickfix.field.TimeInForce.GOOD_TILL_CANCEL),
                new Pair<TimeInForce, Character>(AtTheOpening, quickfix.field.TimeInForce.AT_THE_OPENING),
                new Pair<TimeInForce, Character>(ImmediateOrCancel, quickfix.field.TimeInForce.IMMEDIATE_OR_CANCEL),
                new Pair<TimeInForce, Character>(FillOrKill, quickfix.field.TimeInForce.FILL_OR_KILL),
                new Pair<TimeInForce, Character>(AtTheClose, quickfix.field.TimeInForce.AT_THE_CLOSE)
        );
    }
}
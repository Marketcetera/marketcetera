package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.OrderType.*;
import org.marketcetera.core.Pair;
import java.util.Arrays;
import java.util.List;

import quickfix.field.OrdType;

/* $License$ */
/**
 * Tests {@link OrderType}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderTypeTest extends FIXCharEnumTestBase <OrderType>{
    @Override
    protected OrderType getInstanceForFIXValue(Character inFIXValue) {
        return OrderType.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Character getFIXValue(OrderType e) {
        return e.getFIXValue();
    }

    @Override
    protected OrderType unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<OrderType> getValues() {
        return Arrays.asList(values());
    }

    @Override
    protected List<Pair<OrderType,Character>> knownValues() {
        return Arrays.asList(
                new Pair<OrderType, Character>(Market, OrdType.MARKET),
                new Pair<OrderType, Character>(Limit, OrdType.LIMIT)
        );
    }
}
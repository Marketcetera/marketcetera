package org.marketcetera.core.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;
import quickfix.field.OrdType;

import static org.marketcetera.core.trade.OrderType.*;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.OrderType}
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderTypeTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
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
    protected List<Pair<OrderType,Character>> knownValues()
    {
        List<Pair<OrderType,Character>> values = new ArrayList<Pair<OrderType,Character>>();
        values.add(new Pair<OrderType, Character>(Market, OrdType.MARKET));
        values.add(new Pair<OrderType, Character>(Limit, OrdType.LIMIT));
        return values;
    }
}
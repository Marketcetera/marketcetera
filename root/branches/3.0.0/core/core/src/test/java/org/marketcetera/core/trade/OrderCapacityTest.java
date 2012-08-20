package org.marketcetera.core.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;

import static org.marketcetera.core.trade.OrderCapacity.*;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.OrderCapacity}
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderCapacityTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class OrderCapacityTest extends FIXCharEnumTestBase<OrderCapacity> {

    @Override
    protected OrderCapacity getInstanceForFIXValue(Character inFIXValue) {
        return OrderCapacity.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Character getFIXValue(OrderCapacity e) {
        return e.getFIXValue();
    }

    @Override
    protected OrderCapacity unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<OrderCapacity> getValues() {
        return Arrays.asList(values());
    }
    @Override
    protected List<Pair<OrderCapacity,Character>> knownValues()
    {
        List<Pair<OrderCapacity,Character>> values = new ArrayList<Pair<OrderCapacity,Character>>();
        values.add(new Pair<OrderCapacity, Character>(Agency, quickfix.field.OrderCapacity.AGENCY));
        values.add(new Pair<OrderCapacity, Character>(Proprietary, quickfix.field.OrderCapacity.PROPRIETARY));
        values.add(new Pair<OrderCapacity, Character>(Individual, quickfix.field.OrderCapacity.INDIVIDUAL));
        values.add(new Pair<OrderCapacity, Character>(Principal, quickfix.field.OrderCapacity.PRINCIPAL));
        values.add(new Pair<OrderCapacity, Character>(RisklessPrincipal, quickfix.field.OrderCapacity.RISKLESS_PRINCIPAL));
        values.add(new Pair<OrderCapacity, Character>(AgentOtherMember, quickfix.field.OrderCapacity.AGENT_FOR_OTHER_MEMBER));
        return values;
    }
}
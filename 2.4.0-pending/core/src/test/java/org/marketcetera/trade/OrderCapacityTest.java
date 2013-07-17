package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.OrderCapacity.*;
import org.marketcetera.core.Pair;
import java.util.Arrays;
import java.util.List;

/* $License$ */
/**
 * Tests {@link OrderCapacity}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    protected List<Pair<OrderCapacity,Character>> knownValues() {
        return Arrays.asList(
                new Pair<OrderCapacity, Character>(Agency, quickfix.field.OrderCapacity.AGENCY),
                new Pair<OrderCapacity, Character>(Proprietary, quickfix.field.OrderCapacity.PROPRIETARY),
                new Pair<OrderCapacity, Character>(Individual, quickfix.field.OrderCapacity.INDIVIDUAL),
                new Pair<OrderCapacity, Character>(Principal, quickfix.field.OrderCapacity.PRINCIPAL),
                new Pair<OrderCapacity, Character>(RisklessPrincipal, quickfix.field.OrderCapacity.RISKLESS_PRINCIPAL),
                new Pair<OrderCapacity, Character>(AgentOtherMember, quickfix.field.OrderCapacity.AGENT_FOR_OTHER_MEMBER)
        );
    }
}
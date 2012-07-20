package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/* $License$ */
/**
 * Designates the capacity of the firm placing the order.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public enum OrderCapacity {
    /**
     * Sentinel value for OrderCapacity that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE),
    Agency(quickfix.field.OrderCapacity.AGENCY),
    Proprietary(quickfix.field.OrderCapacity.PROPRIETARY),
    Individual(quickfix.field.OrderCapacity.INDIVIDUAL),
    Principal(quickfix.field.OrderCapacity.PRINCIPAL),
    RisklessPrincipal(quickfix.field.OrderCapacity.RISKLESS_PRINCIPAL),
    AgentOtherMember(quickfix.field.OrderCapacity.AGENT_FOR_OTHER_MEMBER)
    ;

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    private OrderCapacity(char inFIXValue) {
        mFIXValue = inFIXValue;
    }
    /**
     * Gets the OrderCapacity instance.
     *
     * @param inValue the FIX char value.
     *
     * @return the Side instance.
     */
    static OrderCapacity getInstanceForFIXValue(char inValue) {
        OrderCapacity s = mFIXValueMap.get(inValue);
        return s == null
                ? Unknown
                : s;
    }

    /**
     * The FIX char value for this instance.
     *
     * @return the FIX char value for this instance.
     */
    char getFIXValue() {
        return mFIXValue;
    }
    
    private final char mFIXValue;
    private static final Map<Character, OrderCapacity> mFIXValueMap;
    static {
        Map<Character, OrderCapacity> table = new HashMap<Character, OrderCapacity>();
        for(OrderCapacity s:values()) {
            table.put(s.getFIXValue(),s);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}

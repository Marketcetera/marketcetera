package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/* $License$ */
/**
 * An enumeration used to identify if an option is a Put or a Call option.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum OptionType {
    /**
     * Sentinel value for OptionType that the system is not currently
     * aware of.
     */
    Unknown(Integer.MIN_VALUE),
    /**
     * Indicates that an option is a Put option.
     */
    Put(quickfix.field.PutOrCall.PUT),
    /**
     * Indicates that an option is a Call option.
     */
    Call(quickfix.field.PutOrCall.CALL);

    /**
     * The FIX char value for this instance.
     *
     * @return the FIX char value for this instance.
     */
    public int getFIXValue() {
        return mFIXValue;
    }

    /**
     * Gets the OptionType instance.
     *
     * @param inValue the FIX int value.
     * 
     * @return the OptionType instance.
     */
    public static OptionType getInstanceForFIXValue(int inValue) {
        OptionType s = mFIXValueMap.get(inValue);
        return s == null
                ? Unknown
                : s;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX int value for this instance.
     */
    private OptionType(int inFIXValue) {
        mFIXValue = inFIXValue;
    }

    private final int mFIXValue;
    private static final Map<Integer, OptionType> mFIXValueMap;

    static {
        Map<Integer, OptionType> table = new HashMap<Integer, OptionType>();
        for (OptionType s : OptionType.values()) {
            table.put(s.getFIXValue(), s);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}

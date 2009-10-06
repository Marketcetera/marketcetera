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
public enum PutOrCall {
    /**
     * Sentinel value for PutOrCall that the system is not currently
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
     * Gets the PutOrCall instance.
     *
     * @param inValue the FIX int value.
     * 
     * @return the PutOrCall instance.
     */
    static PutOrCall getInstanceForFIXValue(int inValue) {
        PutOrCall s = mFIXValueMap.get(inValue);
        return s == null
                ? Unknown
                : s;
    }

    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX int value for this instance.
     */
    private PutOrCall(int inFIXValue) {
        mFIXValue = inFIXValue;
    }

    private final int mFIXValue;
    private static final Map<Integer, PutOrCall> mFIXValueMap;

    static {
        Map<Integer, PutOrCall> table = new HashMap<Integer, PutOrCall>();
        for (PutOrCall s : PutOrCall.values()) {
            table.put(s.getFIXValue(), s);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}

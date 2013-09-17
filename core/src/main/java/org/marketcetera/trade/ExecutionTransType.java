package org.marketcetera.trade;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.util.misc.ClassVersion;

import quickfix.field.ExecTransType;

/* $License$ */
/**
 * Specific execution report status.
 *
 * @author anshul@marketcetera.com
 * @version $Id: ExecutionTransType.java 16613 2013-07-03 19:28:31Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: ExecutionTransType.java 16613 2013-07-03 19:28:31Z colin $") //$NON-NLS-1$
public enum ExecutionTransType {
    /**
     * Sentinel value for Execution Transaction Type that the system is not currently
     * aware of.
     */
    Unknown(Character.MIN_VALUE),
    New(ExecTransType.NEW),
    Cancel(ExecTransType.CANCEL),
    Correct(ExecTransType.CORRECT),
    Status(ExecTransType.STATUS);
    
    /**
     * The FIX char value for this instance.
     *
     * @return FIX char value for this instance.
     */
    public char getFIXValue() {
        return mFIXValue;
    }

    /**
     * Returns the ExecutionTransType instance given the FIX char value.
     *
     * @param inValue the FIX char value.
     *
     * @return the ExecutionTransType instance.
     */
    public static ExecutionTransType getInstanceForFIXValue(char inValue) {
        ExecutionTransType type = mFIXValueMap.get(inValue);
        return type == null
                ? Unknown
                : type;
    }
    /**
     * Creates an instance.
     *
     * @param inFIXValue the FIX char value for this instance.
     */
    private ExecutionTransType(char inFIXValue) {
        mFIXValue = inFIXValue;
    }
    /**
     * underlying FIX value
     */
    private final char mFIXValue;
    /**
     * all values by FIX value
     */
    private static final Map<Character, ExecutionTransType> mFIXValueMap;
    static {
        //Initialize the lookup table.
        Map<Character,ExecutionTransType> table = new HashMap<Character, ExecutionTransType>();
        for(ExecutionTransType et:values()) {
            table.put(et.getFIXValue(), et);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}

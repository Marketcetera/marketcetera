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
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum OptionType
        implements HasCFICode
{
    /**
     * Sentinel value for OptionType that the system is not currently
     * aware of.
     */
    Unknown(Integer.MIN_VALUE,
            'X'),
    /**
     * Indicates that an option is a Put option.
     */
    Put(quickfix.field.PutOrCall.PUT,
        'P'),
    /**
     * Indicates that an option is a Call option.
     */
    Call(quickfix.field.PutOrCall.CALL,
         'C');

    /**
     * The FIX char value for this instance.
     *
     * @return the FIX char value for this instance.
     */
    public int getFIXValue() {
        return mFIXValue;
    }
    /**
     * Get the cfiCode value.
     *
     * @return a <code>char</code> value
     */
    public char getCfiCode()
    {
        return cfiCode;
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
    private OptionType(int inFIXValue,
                       char inCfiCode)
    {
        mFIXValue = inFIXValue;
        cfiCode = inCfiCode;
    }

    private final int mFIXValue;
    private final char cfiCode;
    private static final Map<Integer, OptionType> mFIXValueMap;

    static {
        Map<Integer, OptionType> table = new HashMap<Integer, OptionType>();
        for (OptionType s : OptionType.values()) {
            table.put(s.getFIXValue(), s);
        }
        mFIXValueMap = Collections.unmodifiableMap(table);
    }
}

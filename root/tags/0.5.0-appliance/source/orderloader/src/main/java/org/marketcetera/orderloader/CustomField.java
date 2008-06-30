package org.marketcetera.orderloader;

import org.marketcetera.core.ClassVersion;
import quickfix.Field;

import java.math.BigDecimal;

/**
 * Represents "custom" fields (ie non-predetermined fields) that can show up in
 * the list of orders
 * These can appear only as integers, and their value are treated as either
 * ints, doubles or strings (catch-all)
 * Ex: Price,OrderQty,1324,Account
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class CustomField extends Field
{
    public CustomField(int i, Object inObject)
    {
        super(i, inObject);
    }

    /** Custom field interprets the message the following way:
     * 1. if it parses as an int, return an int
     * 2. if it parses as a {@link BigDecimal}, return a {@link BigDecimal}
     * 3. else, return a String
     * @param inValue Field value we are interpreting
     * @return Int, Double or String
     */
    public Object parseMessageValue(String inValue)
    {
        try {
            return Integer.valueOf(inValue);
        } catch(NumberFormatException ex) {
            try {
                return new BigDecimal(inValue);
            } catch(NumberFormatException ex2) {
                return inValue;
            }
        }
    }

    @Override
    public String toString()
    {
        return ""+getTag();
    }

    @Override
    public boolean equals(Object inObject)
    {
        if(!(inObject instanceof CustomField)) return super.equals(inObject);

        return getTag() == ((CustomField)inObject).getTag();
    }

    /**
     * Fields are treated as custom when the header name is not "standard" and
     * the name can be parsed as an int.
     * @param inName
     * @return Custom field for the passed in key
     * @throws OrderParsingException
     */
    public static CustomField getCustomField(String inName)
        throws OrderParsingException
    {
        try {
            int fieldKey = Integer.parseInt(inName);
            return new CustomField(fieldKey, null);
        } catch(NumberFormatException nex) {
            throw new OrderParsingException(inName);
        }
    }
}

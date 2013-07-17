package org.marketcetera.orderloader.fix;

import java.math.BigDecimal;

import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.orderloader.OrderParsingException;
import org.marketcetera.orderloader.Messages;

import quickfix.Field;

/* $License */

/**
 * Represents "custom" fields (ie non-predetermined fields) that can show up in
 * the list of orders
 * These can appear only as integers, and their value are treated as either
 * ints, doubles or strings (catch-all)
 * Ex: Price,OrderQty,1324,Account
 *
 * @author Toli Kuznets
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class CustomField<T>
    extends Field<T>
{
    private static final long serialVersionUID = 7712839170687733751L;
    /**
     * Create a new CustomField instance.
     *
     * @param i an <code>int</code> value
     * @param inObject a <code>T</code> value
     */
    public CustomField(int i,
                       T inObject)
    {
        super(i, 
              inObject);
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
            return Integer.valueOf(inValue); //non-i18n
        } catch(NumberFormatException ex) {
            try {
                return new BigDecimal(inValue); //non-i18n
            } catch(NumberFormatException ex2) {
                return inValue;
            }
        }
    }
    @Override
    public String toString()
    {
        return String.format("%d", //$NON-NLS-1$
                             getTag());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + getTag();
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final CustomField<?> other = (CustomField<?>) obj;
        if (getTag() != other.getTag())
            return false;
        return true;
    }
    /**
     * Fields are treated as custom when the header name is not "standard" and
     * the name can be parsed as an int.
     * @param inName
     * @return Custom field for the passed in key
     * @throws OrderParsingException
     */
    public static CustomField<?> getCustomField(String inName)
        throws OrderParsingException
    {
        try {
            int fieldKey = Integer.parseInt(inName); //non-i18n
            return new CustomField<Integer>(fieldKey, 
                                            null);
        } catch(NumberFormatException nex) {
            throw new OrderParsingException(new I18NBoundMessage1P(Messages.ERROR_PARSING_NUMBER_FORMAT, inName));
        }
    }
}

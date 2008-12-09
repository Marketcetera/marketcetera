package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.orderloader.OrderParsingException;

import java.util.EnumSet;
import java.util.Set;

/**
 * Extracts enum value from an order row.
*
* @author anshul@marketcetera.com
* @version $Id$
* @since 1.0.0
*/
@ClassVersion("$Id$")
abstract class EnumProcessor<T extends Enum<T>>
        extends IndexedProcessor {
    /**
     * Creates an instance.
     *
     * @param inClass the enum class handled by this instance.
     * @param inDisallowedValue the enum value that is not allowed as a
     * valid value.
     * @param inErrorMessage the error message to display when the specified
     * enum value is not valid.
     * @param inIndex the column index of the index value in the order row.
     */
    protected EnumProcessor(Class<T> inClass, T inDisallowedValue,
                            I18NMessage2P inErrorMessage, int inIndex) {
        super(inIndex);
        mClass = inClass;
        mErrorMessage = inErrorMessage;
        mValidValues = EnumSet.allOf(inClass);
        mValidValues.remove(inDisallowedValue);
    }

    /**
     * Extracts the enum value from the specified order row.
     *
     * @param inRow the order row.
     *
     * @return the enum value from the supplied row.
     *
     * @throws OrderParsingException if the value found in the row is not
     * a valid enum value.
     */
    protected T getEnumValue(String [] inRow) throws OrderParsingException {
        String value = getValue(inRow);
        if(value == null || value.isEmpty()) {
            return null;
        }
        try {
            T t = Enum.valueOf(mClass, getValue(inRow));
            if(!mValidValues.contains(t)) {
                throw new OrderParsingException(new I18NBoundMessage2P(
                        mErrorMessage, value, mValidValues.toString()));
            }
            return t;
        } catch (IllegalArgumentException e) {
            throw new OrderParsingException(e, new I18NBoundMessage2P(
                    mErrorMessage, value, mValidValues.toString()));
        }
    }
    private final Set<T> mValidValues;
    private final I18NMessage2P mErrorMessage;
    private final Class<T> mClass;
}

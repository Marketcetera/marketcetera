package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.orderloader.OrderParsingException;

import java.math.BigDecimal;

/**
 * Processes a BigDecimal value from an order row.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id")
abstract class BigDecimalProcessor extends IndexedProcessor {
    /**
     * Creates an instance.
     *
     * @param inMessage the error message to display if the field value is
     * not a valid decimal value.
     * @param inIndex the column index for the decimal value.
     */
    public BigDecimalProcessor(I18NMessage1P inMessage, int inIndex) {
        super(inIndex);
        mMessage = inMessage;
    }

    /**
     * Gets the decimal value of the field from the order row.
     *
     * @param inRow the order row.
     *
     * @return the decimal value extracted from the row.
     *
     * @throws OrderParsingException if the supplied value was incorrect.
     */
    protected final BigDecimal getDecimalValue(String []inRow)
            throws OrderParsingException {
        String value = getValue(inRow);
        try {
            if (value != null && !(value.isEmpty())) {
                return new BigDecimal(value);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            throw new OrderParsingException(e,
                    new I18NBoundMessage1P(mMessage, value));
        }
    }
    private final I18NMessage1P mMessage;
}

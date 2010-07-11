package org.marketcetera.client.userlimit;

import java.math.BigDecimal;
import java.util.Properties;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Util;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;

/* $License$ */

/**
 * Manages the data for a single symbol in the risk manager.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id$")
public class SymbolData
{
    /**
     * Create a new SymbolData instance.
     *
     * @param inSymbol a <code>String</code> value
     * @param inMaximumPosition a <code>BigDecimal</code> value
     * @param inMaximumTradeVale a <code>BigDecimal</code> value
     * @param inMaximumDeviationFromLast a <code>BigDecimal</code> value
     * @param inMaximumDeviationFromMid a <code>BigDecimal</code> value
     * @throws IllegalArgumentException if any of the passed values fail validation
     */
    public SymbolData(String inSymbol,
                      BigDecimal inMaximumPosition,
                      BigDecimal inMaximumTradeValue,
                      BigDecimal inMaximumDeviationFromLast,
                      BigDecimal inMaximumDeviationFromMid)
    {
        symbol = inSymbol;
        maximumPosition = inMaximumPosition;
        maximumTradeValue = inMaximumTradeValue;
        maximumDeviationFromLast = inMaximumDeviationFromLast;
        maximumDeviationFromMid = inMaximumDeviationFromMid;
        validate();
    }
    /**
     * Create a new SymbolData instance.
     *
     * @param inConsolidatedData a <code>String</code> value containing the instance data in the format
     *  returned by {@link #toConsolidatedFormat()}
     * @throws IllegalArgumentException if the attributes contained in the consolidated data string are invalid
     */
    public SymbolData(String inConsolidatedData)
    {
        Properties attributes = Util.propertiesFromString(inConsolidatedData);
        symbol = attributes.getProperty(SYMBOL_KEY);
        maximumPosition = convertToBigDecimal(attributes.getProperty(MAX_POS_KEY),
                                              Messages.NULL_MAX_POSITION,
                                              Messages.INVALID_MAX_POSITION);
        maximumTradeValue = convertToBigDecimal(attributes.getProperty(MAX_VALUE_KEY),
                                                Messages.NULL_MAX_VALUE,
                                                Messages.INVALID_MAX_VALUE);
        maximumDeviationFromLast = convertToBigDecimal(attributes.getProperty(MAX_LAST_KEY),
                                                       Messages.NULL_MAX_DEVIATION_FROM_LAST,
                                                       Messages.INVALID_MAX_DEVIATION_FROM_LAST);
        maximumDeviationFromMid = convertToBigDecimal(attributes.getProperty(MAX_MID_KEY),
                                                      Messages.NULL_MAX_DEVIATION_FROM_MID,
                                                      Messages.INVALID_MAX_DEVIATION_FROM_MID);
        validate();
    }
    /**
     * Renders the instance data in consolidated format.
     *
     * @return a <code>String</code> value
     */
    public String toConsolidatedFormat()
    {
        Properties attributes = new Properties();
        attributes.setProperty(SYMBOL_KEY,
                               getSymbol());
        attributes.setProperty(MAX_POS_KEY,
                               getMaximumPosition().toPlainString());
        attributes.setProperty(MAX_VALUE_KEY,
                               getMaximumTradeValue().toPlainString());
        attributes.setProperty(MAX_LAST_KEY,
                               getMaximumDeviationFromLast().toPlainString());
        attributes.setProperty(MAX_MID_KEY,
                               getMaximumDeviationFromMid().toPlainString());
        return Util.propertiesToString(attributes);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("[symbol=%s, maxPos=%s, maxVal=%s, maxDevLast=%s, maxDevMid=%s]",
                             symbol,
                             maximumPosition,
                             maximumTradeValue,
                             maximumDeviationFromLast,
                             maximumDeviationFromMid);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SymbolData)) {
            return false;
        }
        SymbolData other = (SymbolData) obj;
        if (symbol == null) {
            if (other.symbol != null) {
                return false;
            }
        } else if (!symbol.equals(other.symbol)) {
            return false;
        }
        return true;
    }
    /**
     * Get the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol()
    {
        return symbol;
    }
    /**
     * Get the maximumPosition value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMaximumPosition()
    {
        return maximumPosition;
    }
    /**
     * Get the maximumTradeValue value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMaximumTradeValue()
    {
        return maximumTradeValue;
    }
    /**
     * Get the maximumDeviationFromLast value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMaximumDeviationFromLast()
    {
        return maximumDeviationFromLast;
    }
    /**
     * Get the maximumDeviationFromMid value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getMaximumDeviationFromMid()
    {
        return maximumDeviationFromMid;
    }
    /**
     * Attempts to render the given <code>String</code> as a <code>BigDecimal</code>.
     *
     * @param inValue a <code>String</code> value containing the value to convert
     * @param inNullMessage an <code>I18NMessage0P</code> value containing the message to return if the given
     *   value is <code>null</code> or empty
     * @param inInvalidMessage an <code>I18NMessage1P</code> value containing the message to return if the given
     *   value does not a <code>BigDecimal</code> make
     * @return a <code>BigDecimal</code> value
     * @throws IllegalArgumentException if the value could not be converted to a <code>BigDecimal</code>
     */
    private BigDecimal convertToBigDecimal(String inValue,
                                           I18NMessage0P inNullMessage,
                                           I18NMessage1P inInvalidMessage)
    {
        if(inValue == null ||
           inValue.isEmpty()) {
            throw new IllegalArgumentException(inNullMessage.getText());
        }
        try {
            return new BigDecimal(inValue);
        } catch (Exception e) {
            throw new IllegalArgumentException(inInvalidMessage.getText(inValue));
        }
    }
    /**
     * Validates that the instance variables are acceptable.
     *
     * @throws IllegalArgumentException if any instance value is out of acceptable range
     */
    private void validate()
    {
        Validate.notNull(symbol,
                         Messages.NULL_SYMBOL.getText());
        Validate.notEmpty(symbol,
                          Messages.NULL_SYMBOL.getText());
        Validate.notNull(maximumPosition,
                         Messages.NULL_MAX_POSITION.getText());
        Validate.notNull(maximumTradeValue,
                         Messages.NULL_MAX_VALUE.getText());
        Validate.notNull(maximumDeviationFromLast,
                         Messages.NULL_MAX_DEVIATION_FROM_LAST.getText());
        Validate.notNull(maximumDeviationFromMid,
                         Messages.NULL_MAX_DEVIATION_FROM_MID.getText());
    }
    /**
     * key/value attribute used in consolidated format for the symbol
     */
    private static final String SYMBOL_KEY = "symbol";
    /**
     * key/value attribute used in consolidated format for the maximum position
     */
    private static final String MAX_POS_KEY = "maxpos";
    /**
     * key/value attribute used in consolidated format for the maximum value
     */
    private static final String MAX_VALUE_KEY = "maxval";
    /**
     * key/value attribute used in consolidated format for the maximum deviation from the last price
     */
    private static final String MAX_LAST_KEY = "maxlast";
    /**
     * key/value attribute used in consolidated format for the maximum deviation from the mid-point between the last bid/ask
     */
    private static final String MAX_MID_KEY = "maxmid";
    /**
     * the symbol for which data is stored
     */
    private final String symbol;
    /**
     * the maximum position allowable for this symbol
     */
    private final BigDecimal maximumPosition;
    /**
     * the maximum trade value allowable for this symbol
     */
    private final BigDecimal maximumTradeValue;
    /**
     * the maximum deviation from the last quote allowable for this symbol
     */
    private final BigDecimal maximumDeviationFromLast;
    /**
     * the maximum deviation from the mid-point between the best bid/offer allowble for this symbol
     */
    private final BigDecimal maximumDeviationFromMid;
}

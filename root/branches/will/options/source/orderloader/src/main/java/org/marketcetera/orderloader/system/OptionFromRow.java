package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.trade.*;
import org.marketcetera.orderloader.OrderParsingException;
import org.marketcetera.orderloader.Messages;

import java.math.BigDecimal;

/* $License$ */
/**
 * Implements extraction of an equity instrument from a row.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OptionFromRow extends InstrumentFromRow {
    @Override
    protected boolean canProcess(String inHeader, int inIndex) {
        boolean isHandled = false;
        if(FIELD_EXPIRY.equals(inHeader)) {
            mExpiryIndex = inIndex;
            isHandled = true;
        } else if(FIELD_OPTION_TYPE.equals(inHeader)) {
            mOpTypeProcessor = new OptionTypeProcessor(inIndex);
            isHandled = true;
        } else if(FIELD_STRIKE_PRICE.equals(inHeader)) {
            mStrikeProcessor = new BigDecimalProcessor(
                    Messages.INVALID_STRIKE_PRICE_VALUE, inIndex) {
                @Override
                public void apply(String[] inRow, OrderSingle inOrder) throws OrderParsingException {
                    //do nothing.
                }
            };
            isHandled = true;
        }
        return isHandled;
    }

    @Override
    protected boolean isHandled(Row inValue) {
        SecurityType secType = null;
        try {
            secType = getSecurityType(inValue.getRow());
        } catch (OrderParsingException ignore) {
        }
        return secType != null && SecurityType.Option.equals(secType);
    }

    @Override
    protected Instrument extract(Row inRow) throws OrderParsingException {
        String[] row = inRow.getRow();
        String symbol = getSymbol(row);
        checkEmptyField(FIELD_SYMBOL, symbol == null || symbol.trim().isEmpty()); 

        String expiry = mExpiryIndex >=0
                ? row[mExpiryIndex]
                : null;
        checkEmptyField(FIELD_EXPIRY, expiry == null || expiry.trim().isEmpty());

        OptionType opType = mOpTypeProcessor == null
                ? null
                : mOpTypeProcessor.getEnumValue(row);
        checkEmptyField(FIELD_OPTION_TYPE, opType == null);

        BigDecimal strikePrice = mStrikeProcessor == null
                ? null
                : mStrikeProcessor.getDecimalValue(row);
        checkEmptyField(FIELD_STRIKE_PRICE, strikePrice == null);
        return new Option(symbol,expiry, strikePrice, opType);
    }

    /**
     * Throws an exception for the field header if the check fails.
     *
     * @param inFieldHeader the field header name to include in the error message.
     * @param inCheckFailed if the field validation check failed.
     *
     * @throws OrderParsingException if <code>inCheckFailed</code> is true.
     */
    private static void checkEmptyField(String inFieldHeader, boolean inCheckFailed)
            throws OrderParsingException {
        if(inCheckFailed) {
            throw new OrderParsingException(new I18NBoundMessage1P(
                    Messages.MISSING_OPTION_FIELD, inFieldHeader));
        }
    }
    /**
     * The option expiry field header name.
     */
    public static final String FIELD_EXPIRY = "Expiry";  //$NON-NLS-1$
    /**
     * The option strike price field header name
     */
    public static final String FIELD_STRIKE_PRICE = "StrikePrice";  //$NON-NLS-1$
    /**
     * The option type field header name
     */
    public static final String FIELD_OPTION_TYPE = "OptionType";  //$NON-NLS-1$
    private OptionTypeProcessor mOpTypeProcessor = null;
    private BigDecimalProcessor mStrikeProcessor = null;
    private int mExpiryIndex = -1;
}

package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.instruments.DynamicInstrumentHandler;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.orderloader.OrderParsingException;

/* $License$ */
/**
 * Abstracts out creation of an instrument from a row of fields.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class InstrumentFromRow extends DynamicInstrumentHandler<Row> {
    /**
     * Returns true if this handler can handle the field corresponding
     * to the supplied header.
     * <p>
     * Subclasses override this method to setup any state needed to handle
     * the headers that they care about.
     *
     * @param inHeader the header value.
     * @param inIndex the header index.
     *
     * @return if the handler can process the field, false otherwise.
     */
    protected abstract boolean canProcess(String inHeader, int inIndex);

    /**
     * Returns the symbol value for the supplied row.
     *
     * @param inRow the row
     *
     * @return the symbol value.
     */
    protected final String getSymbol(String []inRow) {
        return inRow[mSymbolIdx];
    }

    /**
     * Returns the security type value from the supplied row, if available.
     *
     * @param inRow the row
     *
     * @return the security type value.
     *
     * @throws OrderParsingException if there were errors parsing the
     * security type value.
     */
    protected final SecurityType getSecurityType(String[] inRow)
            throws OrderParsingException {
        return mSecurityProcessor != null
                ? mSecurityProcessor.getEnumValue(inRow)
                : null;
    }

    /**
     * This method is invoked by the {@link InstrumentProcessor} to allow
     * the handler to process the supplied header.
     *
     * @param inHeader the header value.
     * @param inIndex the header index.
     *
     * @return if the handler can process this header value. If true, further
     * processing of this header is stopped. Note that returning true from
     * this method doesn't prevent other instances of this class (or subclass)
     * from processing the header. 
     */
    final boolean processHeader(String inHeader, int inIndex) {
        boolean isHandled = false;
        if(FIELD_SYMBOL.equals(inHeader)) {
            setSymbolIdx(inIndex);
            isHandled = true;
        } else if(FIELD_SECURITY_TYPE.equals(inHeader)) {
            setSecurityTypeProcessor(inIndex);
            isHandled = true;
        } else {
            if(canProcess(inHeader, inIndex)) {
                isHandled = true;
            }
        }
        return isHandled;
    }

    /**
     * Extracts the instrument value from the supplied row.
     *
     * @param inRow the row
     *
     * @return the extracted Instrument value.
     *
     * @throws OrderParsingException if there were errors extracting
     * the instrument value.
     */
    protected abstract Instrument extract(Row inRow) throws OrderParsingException;
    /**
     * Sets the column index at which the symbol value is located.
     *
     * @param inSymbolIdx the column index for the symbol.
     */
    private void setSymbolIdx(int inSymbolIdx) {
        mSymbolIdx = inSymbolIdx;
    }

    /**
     * Sets the security type index at which the security type value is
     * located.
     *
     * @param inSecurityTypeIdx the column index for security type value.
     */
    private void setSecurityTypeProcessor(int inSecurityTypeIdx) {
        mSecurityProcessor = new SecurityTypeProcessor(inSecurityTypeIdx);
    }

    /**
     * The Symbol field header name.
     */
    public static final String FIELD_SYMBOL = "Symbol";  //$NON-NLS-1$
    /**
     * The Security Type field header name.
     */
    public static final String FIELD_SECURITY_TYPE = "SecurityType";  //$NON-NLS-1$

    private SecurityTypeProcessor mSecurityProcessor = null;
    private int mSymbolIdx;
}

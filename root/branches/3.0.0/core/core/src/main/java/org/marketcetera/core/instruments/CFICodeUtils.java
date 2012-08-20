package org.marketcetera.core.instruments;

import org.marketcetera.core.trade.Future;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.Option;
import org.marketcetera.core.trade.OptionType;

/* $License$ */
/**
 * A class that provides means to process and interpret CFICode as defined by
 * <a href="http://www.iso.org/iso/catalogue_detail?csnumber=32835">ISO 10962</a>
 * standard.
 *
 * @author anshul@marketcetera.com
 * @version $Id: CFICodeUtils.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
class CFICodeUtils {
    /**
     * Returns true if the specified CFI code string specifies an Option
     * instrument.
     *
     * @param inCFICode the CFI code value.
     *
     * @return if the CFI code value specifies an option instrument.
     */
    static boolean isOption(String inCFICode) {
        return (inCFICode != null && inCFICode.startsWith("O"));  //$NON-NLS-1$
    }
    /**
     * Returns true if the specified CFI code string specifies a Future
     * instrument.
     *
     * @param inCFICode a <code>String</code> value containing the CFI code value.
     * @return a <code>boolean</code> value indicating if the CFI code value specifies a future instrument
     */
    static boolean isFuture(String inCFICode)
    {
        return (inCFICode != null &&
                inCFICode.startsWith("F"));  //$NON-NLS-1$
    }
    /**
     * Returns the option type value from the CFI code string. 
     *
     * @param inCFICode the CFI code value.
     *
     * @return the Option Type value, if it could be extracted from the CFI code,
     * null otherwise.
     */
    static OptionType getOptionType(String inCFICode) {
        if(inCFICode != null && inCFICode.length() >= 2 &&
                inCFICode.charAt(0) == 'O') {  //$NON-NLS-1$
            switch(inCFICode.charAt(1)) {
                case 'C':  //$NON-NLS-1$
                    return OptionType.Call;
                case 'P':  //$NON-NLS-1$
                    return OptionType.Put;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the option CFI code for the supplied option type.
     *
     * @param inType the option type.
     *
     * @return the option CFI code. null if the option type is unknown.
     */
    static String getOptionCFICode(OptionType inType) {
        switch(inType) {
            case Call:
                return "OCXXXX";  //$NON-NLS-1$
            case Put:
                return "OPXXXX";  //$NON-NLS-1$
            default:
                return null;
        }
    }
    /**
     * Gets the CFI code for the given <code>Option</code>.
     *
     * @param inInstrument an <code>Option</code> value
     * @return a <code>String</code> value
     */
    static String getCFICode(Option inInstrument)
    {
        StringBuffer code = new StringBuffer();
        code.append("O"); // indicates option //$NON-NLS-1$
        code.append(inInstrument.getType().getCfiCode()); // put or call
        code.append("X"); // exercise style is not captured in our instrument //$NON-NLS-1$
        code.append("X"); // underlying asset type not captured //$NON-NLS-1$
        code.append("X"); // delivery //$NON-NLS-1$
        code.append("X"); // standard/non-standard //$NON-NLS-1$
        return code.toString();
    }
    /**
     * Gets the CFI code for the given <code>Future</code>.
     *
     * @param inInstrument a <code>Future</code> value
     * @return a <code>String</code> value
     */
    static String getCFICode(Future inInstrument)
    {
        StringBuffer code = new StringBuffer();
        code.append("F"); // indicates future //$NON-NLS-1$
        code.append("X"); // financial or commodity //$NON-NLS-1$
        code.append("X"); // underlying asset type //$NON-NLS-1$
        code.append("X"); // delivery //$NON-NLS-1$
        code.append("X"); // standard/non-standard //$NON-NLS-1$
        code.append("X"); // not used //$NON-NLS-1$
        return code.toString();
    }
    /**
     * Gets the CFI code for the given <code>Instrument</code> value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>String</code> value
     */
    static String getCFICode(Instrument inInstrument)
    {
        if(inInstrument instanceof Option) {
            return getCFICode((Option)inInstrument);
        }
        if(inInstrument instanceof Future) {
            return getCFICode((Future)inInstrument);
        }
        throw new UnsupportedOperationException();
    }
}

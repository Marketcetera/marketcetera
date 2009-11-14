package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.OptionType;

/* $License$ */
/**
 * A class that provides means to process and interpret CFICode as defined by
 * <a href="http://www.iso.org/iso/catalogue_detail?csnumber=32835">ISO 10962</a>
 * standard.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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
}

package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage3P;
import static org.marketcetera.persist.Messages.UNSUPPORTED_CHARACTER;

/* $License$ */
/**
 * Provides MySQL specific implementation of DB Vendor.
 * MySQL supports Version 3 of unicode specification, which
 * does not include supplementary characters.
 * The implementation of this class helps ensure that supplementary
 * characters are not included in the strings that are sent to the database.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MySQLDBVendor extends DBVendor {
    /**
     * Creates an instance
     *
     * @throws PersistSetupException if there was an error
     */
    private MySQLDBVendor() throws PersistSetupException {
        super();
    }
    @Override
    void validateText(CharSequence s) throws ValidationException {
        // mysql version dependency: this section of code depends on
        // mysql version specific behavior and may need to be updated
        // whenever mysql version is updated
        for(int i = 0; i < s.length(); i ++) {
            final char c = s.charAt(i);
            // Supplementary characters are not supported
            if(Character.isHighSurrogate(s.charAt(i)) || Character.isLowSurrogate(c)) {
                // get the substring with at most 5 chars before the problem character
                int startIdx = Math.max(i - 5, 0);
                throw new ValidationException(
                        new I18NBoundMessage3P(UNSUPPORTED_CHARACTER,
                                c, i, s.subSequence(startIdx,i).toString()));
            }
        }
    }
}

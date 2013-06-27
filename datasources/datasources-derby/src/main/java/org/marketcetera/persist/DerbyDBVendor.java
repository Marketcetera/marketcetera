package org.marketcetera.persist;

import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.persist.Messages.UNSUPPORTED_CHARACTER;

/* $License$ */
/**
 * Provides Derby specific implementation of DB Vendor. Derby supports Version 2
 * of unicode specification, which does not include supplementary characters.
 * The implementation of this class helps ensure that supplementary characters
 * are not included in the strings that are sent to the database.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: MySQLDBVendor.java 16154 2012-07-14 16:34:05Z colin $")
public class DerbyDBVendor
        extends DBVendor
{
    /**
     * Creates an instance
     * 
     * @throws PersistSetupException if there was an error
     */
    private DerbyDBVendor()
            throws PersistSetupException
    {
        super();
    }
    @Override
    void validateText(CharSequence s)
            throws ValidationException
    {
        // derby version dependency: this section of code depends on
        // derby version specific behavior and may need to be updated
        // whenever derby version is updated
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            // Supplementary characters are not supported
            if (Character.isHighSurrogate(s.charAt(i)) || Character.isLowSurrogate(c)) {
                // get the substring with at most 5 chars before the problem character
                int startIdx = Math.max(i - 5,
                                        0);
                throw new ValidationException(new I18NBoundMessage3P(UNSUPPORTED_CHARACTER,
                                                                     c,
                                                                     i,
                                                                     s.subSequence(startIdx,
                                                                                   i).toString()));
            }
        }
    }
}

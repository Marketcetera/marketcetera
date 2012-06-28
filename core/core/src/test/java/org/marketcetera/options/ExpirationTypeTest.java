package org.marketcetera.options;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.options.ExpirationType.AMERICAN;
import static org.marketcetera.options.ExpirationType.EUROPEAN;
import static org.marketcetera.options.ExpirationType.UNKNOWN;

import org.junit.Test;

/* $License$ */

/**
 * Tests {@link ExpirationType}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExpirationTypeTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class ExpirationTypeTest
{
    /**
     * Tests {@link ExpirationType#getExpirationTypeForChar(char)}.
     *
     * @throws Exception
     */
    @Test
    public void getByChar()
            throws Exception
    {
        assertEquals(UNKNOWN,
                     ExpirationType.getExpirationTypeForChar(' '));
        assertEquals(UNKNOWN,
                     ExpirationType.getExpirationTypeForChar('X'));
        assertEquals(UNKNOWN,
                     ExpirationType.getExpirationTypeForChar(' '));
        assertEquals(UNKNOWN,
                     ExpirationType.getExpirationTypeForChar('a'));
        assertEquals(UNKNOWN,
                     ExpirationType.getExpirationTypeForChar('e'));
        assertEquals(AMERICAN,
                     ExpirationType.getExpirationTypeForChar('A'));
        assertEquals(EUROPEAN,
                     ExpirationType.getExpirationTypeForChar('E'));
    }
}

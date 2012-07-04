package org.marketcetera.core.options;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.core.options.ExpirationType.*;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.options.ExpirationType}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExpirationTypeTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class ExpirationTypeTest
{
    /**
     * Tests {@link org.marketcetera.core.options.ExpirationType#getExpirationTypeForChar(char)}.
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

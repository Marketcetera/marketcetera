package org.marketcetera.core.instruments;

import org.junit.Test;
import org.marketcetera.core.util.l10n.MessageComparator;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/* $License$ */

/**
 * Tests {@link Messages}.
 *
 * @version $Id: MessagesTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class MessagesTest extends TestCaseBase {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator = new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(), comparator.isMatch());
    }
}
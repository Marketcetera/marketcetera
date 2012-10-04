package org.marketcetera.marketdata.csv;

import org.marketcetera.core.util.l10n.MessageComparator;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Verify that all the i18n messages are specified correctly
 * @version $Id: MessagesTest.java 16063 2012-01-31 18:21:55Z colin $
 */

public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
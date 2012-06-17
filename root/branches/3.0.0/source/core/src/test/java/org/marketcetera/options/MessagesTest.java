package org.marketcetera.options;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */

/**
 * Tests that all messages in this package are mapped correctly
 *
 * @author anshul@marketcetera.com
 * @version $Id: MessagesTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
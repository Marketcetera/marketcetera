package org.marketcetera.persist;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */

/**
 * Tests that all persist messages are mapped correctly
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
    @Test
    public void testMessagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(
                org.marketcetera.persist.example.Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

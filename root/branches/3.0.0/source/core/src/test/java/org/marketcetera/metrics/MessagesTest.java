package org.marketcetera.metrics;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */
/**
 * Verifies that all the messages are correctly specified.
 *
 * @author anshul@marketcetera.com
 */
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
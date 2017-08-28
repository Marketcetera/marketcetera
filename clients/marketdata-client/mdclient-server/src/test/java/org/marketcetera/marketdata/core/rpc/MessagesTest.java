package org.marketcetera.marketdata.core.rpc;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.core.Messages;
import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Verifies that all the messages are correctly specified.
 *
 * @author anshul@marketcetera.com
 * @version $Id: MessagesTest.java 17242 2016-09-02 16:46:48Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: MessagesTest.java 17242 2016-09-02 16:46:48Z colin $")
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

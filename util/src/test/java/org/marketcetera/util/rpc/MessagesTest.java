package org.marketcetera.util.rpc;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.rpc.Messages;
import org.marketcetera.util.l10n.MessageComparator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/* $License$ */
/**
 * Verifies that all the messages are correctly specified.
 *
 * @author anshul@marketcetera.com
 * @version $Id: MessagesTest.java 16901 2014-05-11 16:14:11Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: MessagesTest.java 16901 2014-05-11 16:14:11Z colin $")
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

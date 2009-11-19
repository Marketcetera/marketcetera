package org.marketcetera.options;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.l10n.MessageComparator;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/* $License$ */

/**
 * Tests that all messages in this package are mapped correctly
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
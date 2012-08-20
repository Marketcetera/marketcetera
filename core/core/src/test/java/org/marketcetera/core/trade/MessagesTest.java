package org.marketcetera.core.trade;

import org.junit.Test;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.util.l10n.MessageComparator;

import static org.junit.Assert.assertTrue;

/* $License$ */
/**
 * Verifies that all the messages are correctly specified.
 *
 * @author anshul@marketcetera.com
 * @version $Id: MessagesTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: MessagesTest.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
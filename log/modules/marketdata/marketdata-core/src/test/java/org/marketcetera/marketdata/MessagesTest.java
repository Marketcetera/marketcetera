package org.marketcetera.marketdata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */

/**
 * @since 0.6.0
 * @version $Id: MessagesTest.java 82306 2012-02-29 23:18:25Z colin $
 */
public class MessagesTest
{
    @Test
    public void messagesMatch()
        throws Exception
    {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

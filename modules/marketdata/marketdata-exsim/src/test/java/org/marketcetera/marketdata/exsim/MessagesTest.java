package org.marketcetera.marketdata.exsim;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */

/**
 * Tests package messages.
 * 
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id: MessagesTest.java 16154 2012-07-14 16:34:05Z colin $
 */
public class MessagesTest
{
    /**
     * Test that the package messages are set up properly.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void messagesMatch()
            throws Exception
    {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

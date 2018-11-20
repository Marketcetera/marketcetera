package org.marketcetera.trade.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */

/**
 * Tests messages for the keytools package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MessagesTest.java 16191 2012-06-27 00:13:01Z colin $
 * @since 2.1.4
 */
public class MessagesTest
{
    /**
     * Tests messages.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void messagesMatch()
            throws Exception
    {
        MessageComparator comparator = new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),
                   comparator.isMatch());
    }
}

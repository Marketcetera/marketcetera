package org.marketcetera.photon.marketdata;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */

/**
 * Tests the message file of Photon market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MessagesTest.java 3002 2008-08-08 23:37:40Z klim $
 * @since $Release$
 */
public class MessagesTest
{
    @Test
    public void messagesMatch()
        throws Exception
    {
        MessageComparator comparator = new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),
                   comparator.isMatch());
    }
}

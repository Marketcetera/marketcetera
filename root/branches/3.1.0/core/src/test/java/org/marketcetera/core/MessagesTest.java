package org.marketcetera.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.test.TestCaseBase;

/* $License$ */

/**
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id: MessagesTest.java 82384 2012-07-20 19:09:59Z colin $
 */
public class MessagesTest
    extends TestCaseBase
{
    @Test
    public void messagesMatch()
        throws Exception
    {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

package org.marketcetera.util.ws.stateless;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: MessagesTest.java 82384 2012-07-20 19:09:59Z colin $
 */

/* $License$ */

public class MessagesTest
    extends TestCaseBase
{
    @Test
    public void messagesMatch()
        throws Exception
    {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());

        comparator=new MessageComparator(TestMessages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

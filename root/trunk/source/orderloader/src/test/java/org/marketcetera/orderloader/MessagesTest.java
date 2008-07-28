package org.marketcetera.orderloader;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.util.l10n.MessageComparator;

/**
 * @author toli
 * @version $Id: OrderLoaderMessageKeyTest.java 9257 2008-05-29 11:35:48Z tlerios $
 */

public class MessagesTest extends TestCase {
    public MessagesTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(MessagesTest.class);
    }
    public void testInstantiateAll() 
        throws Exception 
    {
        MessageComparator comparator = new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),
                   comparator.isMatch());
    }
}

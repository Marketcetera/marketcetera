package org.marketcetera.marketdata.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.l10n.MessageComparator;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Verify that all the i18n messages are specified correctly
 * @author toli kuznets
 * @version $Id: MessagesTest.java 4348 2009-09-24 02:33:11Z toli $
 */

@ClassVersion("$Id: MessagesTest.java 4348 2009-09-24 02:33:11Z toli $")
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
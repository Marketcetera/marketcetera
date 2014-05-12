package org.marketcetera.orderloader;


import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.misc.ClassVersion;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

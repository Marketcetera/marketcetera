package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.l10n.MessageInfo;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/* $License$ */
/**
 * Verifies that all the messages are correctly specified.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MessagesTest {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        MessageInfo.getList(comparator.getExtraSrcInfo()).store(System.out,"");
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

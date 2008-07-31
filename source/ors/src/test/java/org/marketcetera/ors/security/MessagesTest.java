package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.except.I18NException;
import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests all the messages.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MessagesTest {
    @Test
    public void messagesMatch() throws I18NException {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}

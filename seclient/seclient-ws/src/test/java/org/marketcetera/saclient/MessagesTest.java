package org.marketcetera.saclient;

import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/* $License$ */

/**
 * Tests for internationalized messages in this package.
 *
 * @author anshul@marketcetera.com
 * @since 2.0.0
 * @version $Id: MessagesTest.java 16154 2012-07-14 16:34:05Z colin $
 */
public class MessagesTest extends TestCaseBase {
    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator=new MessageComparator(Messages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
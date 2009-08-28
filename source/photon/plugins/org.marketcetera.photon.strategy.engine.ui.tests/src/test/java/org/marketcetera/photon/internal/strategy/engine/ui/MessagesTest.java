package org.marketcetera.photon.internal.strategy.engine.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.photon.commons.ComposedContainerClassInfo;
import org.marketcetera.photon.commons.ui.LocalizedLabelMessageInfoProvider;
import org.marketcetera.util.l10n.MessageComparator;

/* $License$ */

/**
 * Tests {@link Messages}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class MessagesTest {

    @Test
    public void messagesMatch() throws Exception {
        MessageComparator comparator = new MessageComparator(
                new ComposedContainerClassInfo(Messages.class,
                        new LocalizedLabelMessageInfoProvider(Messages.class)));
        assertTrue(comparator.getDifferences(), comparator.isMatch());
    }
}
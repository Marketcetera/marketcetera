package org.marketcetera.photon.commons.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.util.l10n.MessageInfo;
import org.marketcetera.util.log.I18NMessage0P;

/* $License$ */

/**
 * Tests {@link LocalizedLabelContainerClassInfo}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: LocalizedLabelMessageInfoProviderTest.java 10713 2009-08-30
 *          09:08:28Z tlerios $
 * @since 2.0.0
 */
public class LocalizedLabelContainerClassInfoTest extends PhotonTestBase {

    @Test
    public void testGetMessageInfo() throws Exception {
        List<MessageInfo> info = new LocalizedLabelContainerClassInfo(
                Messages.class).getMessageInfo();
        assertThat(info.size(), is(5));
        assertInfo(info.get(0), "m.msg");
        assertInfo(info.get(1), "ll.label");
        assertInfo(info.get(2), "ll.tooltip");
        assertInfo(info.get(3), "ll2.label");
        assertInfo(info.get(4), "ll2.tooltip");
    }

    private void assertInfo(MessageInfo info, String label) {
        assertThat(info.getKey(), is(label));
        assertThat(info.getParamCount(), is(0));
    }

    public static class Messages {
        static Object obj = new Object();
        Object obj2 = new Object();
        static I18NMessage0P M;
        static LocalizedLabel LL;
        static LocalizedLabel LL2;

        static {
            ReflectiveMessages.init(Messages.class);
        }
    }
}

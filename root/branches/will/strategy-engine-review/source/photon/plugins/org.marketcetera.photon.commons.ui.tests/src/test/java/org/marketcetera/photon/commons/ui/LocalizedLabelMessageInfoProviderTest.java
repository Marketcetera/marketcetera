package org.marketcetera.photon.commons.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.util.l10n.MessageInfo;

/* $License$ */

/**
 * Tests {@link LocalizedLabelMessageInfoProvider}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class LocalizedLabelMessageInfoProviderTest extends PhotonTestBase {

    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("clazz") {
            @Override
            protected void run() throws Exception {
                new LocalizedLabelMessageInfoProvider(null);
            }
        };
    }

    @Test
    public void testGetMessageInfo() throws Exception {
        List<MessageInfo> info = new LocalizedLabelMessageInfoProvider(
                Messages.class).getMessageInfo();
        assertThat(info.size(), is(4));
        assertInfo(info.get(0), "ll.label");
        assertInfo(info.get(1), "ll.tooltip");
        assertInfo(info.get(2), "ll2.label");
        assertInfo(info.get(3), "ll2.tooltip");
    }

    private void assertInfo(MessageInfo info, String label) {
        assertThat(info.getKey(), is(label));
        assertThat(info.getParamCount(), is(0));
    }

    public interface Messages {
        Object obj = new Object();
        LocalizedLabel LL = new LocalizedLabel("lbl", "tltp");
        LocalizedLabel LL2 = new LocalizedLabel("lbl2", "tltp2");
    }

}

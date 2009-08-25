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
        List<MessageInfo> info = new LocalizedLabelMessageInfoProvider(Messages.class).getMessageInfo();
        assertThat(info.size(), is(4));
        assertThat(info.get(0).getKey(), is("ll.label"));
        assertThat(info.get(1).getKey(), is("ll.tooltip"));
        assertThat(info.get(2).getKey(), is("ll2.label"));
        assertThat(info.get(3).getKey(), is("ll2.tooltip"));        
    }
    
    public interface Messages {
        LocalizedLabel LL = new LocalizedLabel("lbl", "tltp");
        LocalizedLabel LL2 = new LocalizedLabel("lbl2", "tltp2");
    }

}

package org.marketcetera.photon.commons;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedEmptyFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullElementFailure;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.util.l10n.MessageInfo;
import org.marketcetera.util.l10n.MessageInfoProvider;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Tests {@link ComposedContainerClassInfo}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class ComposedContainerClassInfoTest extends PhotonTestBase {

    @Test
    public void testValidation() throws Exception {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run() throws Exception {
                new ComposedContainerClassInfo(null);
            }
        };
        new ExpectedEmptyFailure("additionalProviders") {
            @Override
            protected void run() throws Exception {
                new ComposedContainerClassInfo(Messages.class);
            }
        };
        new ExpectedNullArgumentFailure("additionalProviders") {
            @Override
            protected void run() throws Exception {
                new ComposedContainerClassInfo(Messages.class,
                        (MessageInfoProvider[]) null);
            }
        };
        new ExpectedNullElementFailure("additionalProviders") {
            @Override
            protected void run() throws Exception {
                new ComposedContainerClassInfo(Messages.class,
                        (MessageInfoProvider) null);
            }
        };
    }

    @Test
    public void testAdditionalProviders() throws Exception {
        final MessageInfo info = new MessageInfo("abc", 0) {}; 
        List<MessageInfo> list = new ComposedContainerClassInfo(Messages.class,
                new MessageInfoProvider() {
                    @Override
                    public List<MessageInfo> getMessageInfo() {
                        return Collections.singletonList(info);
                    }
                }).getMessageInfo();
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getKey(), is("message.msg"));
        assertThat(list.get(1).getKey(), is("abc"));
    }

    public static interface Messages {
        I18NMessageProvider PROVIDER = new I18NMessageProvider("bogus");
        I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
        I18NMessage0P MESSAGE = new I18NMessage0P(LOGGER, "message");
    }
}

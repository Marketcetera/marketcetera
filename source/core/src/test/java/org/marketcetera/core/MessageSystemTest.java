package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;
import org.apache.commons.i18n.MessageManager;

import java.util.*;

/**
 * Tests the loading of the localization messages file and that messages with arguments work
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class MessageSystemTest extends TestCase {
    public MessageSystemTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(MessageSystemTest.class);
    }

    public void testLoadMessage() throws Exception {
        MyApp app = new MyApp();
        String msg = app.getMessage("test1", new Object[0]); //$NON-NLS-1$
        assertEquals("This is test1", msg); //$NON-NLS-1$

        assertEquals("Test2 has parameter abc followed by 123.", app.getMessage("test2", new String[] {"abc", "123"})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    public void testMultiPartKey() throws Exception {
        MyApp app = new MyApp();
        assertEquals("multi.part.key", "This key has a lot of parts to it.", app.getMessage("key.with.dots", new Object[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private class MyApp extends ApplicationBase
    {
        protected List<MessageBundleInfo> getLocalMessageBundles() {
            LinkedList<MessageBundleInfo> bundles = new LinkedList<MessageBundleInfo>();
            bundles.add(new MessageBundleInfo("test", "test_messages")); //$NON-NLS-1$ //$NON-NLS-2$
            return bundles;
        }

        public String getMessage(String key, Object[] args)
        {
            return MessageManager.getText(key, "error", args, Locale.getDefault()); //$NON-NLS-1$
        }

        protected void registerMBean(boolean fExitOnFail) {
            // do nothing
        }
    }
}

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

@ClassVersion("$Id$")
public class MessageSystemTest extends TestCase {
    public MessageSystemTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(MessageSystemTest.class);
    }

    public void testLoadMessage() throws Exception {
        MyApp app = new MyApp(new Properties());
        app.init();
        String msg = app.getMessage("test1", new Object[0]);
        assertEquals("This is test1", msg);

        assertEquals("Test2 has parameter abc followed by 123.", app.getMessage("test2", new String[] {"abc", "123"}));
    }

    public void testMultiPartKey() throws Exception {
        MyApp app = new MyApp(new Properties());
        app.init();
        assertEquals("multi.part.key", "This key has a lot of parts to it.", app.getMessage("key.with.dots", new Object[0]));
    }

    /** Iterate over all the message keys and make sure they can be instantiated and that they all exist in the messages file */
    public void testInstantiateAll() throws Exception {
        for(MessageKey key : MessageKey.values()) {
            String localized = key.getLocalizedMessage();
            assertNotNull(key.toString(), localized);
            assertTrue(key.toString(), localized.length() > 0);
        }
    }

    private class MyApp extends ApplicationBase
    {
        public MyApp(Properties inProps) {
            super(inProps);
        }

        protected void addLocalMessageBundles(List<MessageBundleInfo> bundles) {
            bundles.add(new MessageBundleInfo("test", "test_messages"));
        }

        public String getMessage(String key, Object[] args)
        {
            return MessageManager.getText(key, "error", args, Locale.getDefault());
        }

        protected void registerMBean(boolean fExitOnFail) {
            // do nothing
        }
    }
}

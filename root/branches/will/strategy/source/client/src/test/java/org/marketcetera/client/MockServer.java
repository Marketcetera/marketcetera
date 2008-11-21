package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/* $License$ */
/**
 * A mock server for testing the Client.
 * The server can be connected to at the URL {@link #URL}.
 * Any username / password can be used to connect to the server as long
 * as the username and password are identical.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MockServer {
    public static void main(String[] args)
            throws InterruptedException, FIXFieldConverterNotAvailable {
        LoggerConfiguration.logSetup();
        FIXVersionTestSuite.initializeFIXDataDictionaryManager(
                FIXVersionTestSuite.ALL_FIX_VERSIONS);
        MockServer ms = new MockServer();
        synchronized(ms) {
            ms.wait();
        }
        ms.close();
    }
    public MockServer() {
        mContext = new ClassPathXmlApplicationContext("mock_server.xml");
        mContext.registerShutdownHook();
        mHandler = (MockMessageHandler) mContext.getBean("messageHandler",
                MockMessageHandler.class);
        mContext.start();
    }
    public void close() {
        mContext.close();
    }

    public MockMessageHandler getHandler() {
        return mHandler;
    }

    /**
     * The URL for the broker hosted by this server.
     * Do note that this URL is changed, the mock_server.xml & broker.xml
     * files need to be updated as well.
     */
    public static final String URL = "tcp://localhost:61616";
    private final ClassPathXmlApplicationContext mContext;
    private MockMessageHandler mHandler;
}

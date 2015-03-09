package org.marketcetera.orderloader;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.orderloader.Messages.ERROR_MISSING_FILE;
import static org.marketcetera.orderloader.Messages.ERROR_TOO_MANY_ARGUMENTS;
import static org.marketcetera.orderloader.Messages.LINE_SUMMARY;
import static org.marketcetera.orderloader.Messages.LOG_APP_COPYRIGHT;
import static org.marketcetera.orderloader.Messages.ORDER_SUMMARY;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Test;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.util.file.CopyCharsUtils;

/* $License$ */
/**
 * Tests {@link OrderLoaderMain}
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class MainTest
{
    /**
     * Tests running the loader if the input file is missing
     *
     * @throws Exception if an unexpected error occurs.
     */
    @Test
    public void inputFileMissing()
            throws Exception
    {
        MockMain main = run();
        main.assertUsage();
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains(ERROR_MISSING_FILE.getText()));
    }
    /**
     * Tests running the loader with too many arguments.
     *
     * @throws Exception if an unexpected error occurs.
     */
    @Test
    public void tooManyArgs()
            throws Exception
    {
        MockMain main = run("file",
                            "extra");
        main.assertUsage();
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains(ERROR_TOO_MANY_ARGUMENTS.getText()));
    }
    /**
     * Tests running the loader with an incorrect mode specified.
     *
     * @throws Exception if an unexpected error occurs.
     */
    @Test
    public void incorrectModeSyntax()
            throws Exception
    {
        MockMain main = run("-m");
        main.assertUsage();
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains("option: m"));
    }
    /**
     * Tests running the loader with an incorrect broker specified.
     *
     * @throws Exception if an unexpected error occurs.
     */
    @Test
    public void incorrectBrokerSyntax()
            throws Exception
    {
        MockMain main = run("-b");
        main.assertUsage();
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains("option: b"));
    }
    /**
     * Tests running the loader with an incorrect username.
     *
     * @throws Exception if an unexpected error occurs.
     */
    @Test
    public void incorrectUsernameSyntax()
            throws Exception
    {
        MockMain main = run("-u");
        main.assertUsage();
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains("option: u"));
    }
    /**
     * Tests running the loader with incorrect password syntax.
     *
     * @throws Exception if an unexpected error occurs.
     */
    @Test
    public void incorrectPasswordSyntax()
            throws Exception
    {
        MockMain main = run("-p");
        main.assertUsage();
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains("option: p"));
    }
    /**
     * Tests successful run with the system FIX dictionary.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void systemOrder()
            throws Exception
    {
        File tmpFile = File.createTempFile("orders",
                                           ".csv");
        tmpFile.deleteOnExit();
        CopyCharsUtils.copy(OrderParserTest.arrayToLines(OrderLoaderTest.SYSTEM_ORDER_EXAMPLE).toCharArray(),
                            tmpFile.getAbsolutePath());
        MockMain main = run(tmpFile.getAbsolutePath());
        //Verify Client Parameter values
        ClientParameters parameters = main.getClientParameters();
        assertEquals("127.0.0.1",
                     parameters.getHostname());
        assertNull(parameters.getIDPrefix());
        assertArrayEquals("admin".toCharArray(),
                          parameters.getPassword());
        assertEquals(9000,
                     parameters.getPort());
        assertEquals("tcp://localhost:61616",
                     parameters.getURL());
        assertEquals("admin",
                     parameters.getUsername());
        //Verify that the order processor was closed/done
        assertTrue(main.getOrderProcessor().isDoneInvoked());
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains(LINE_SUMMARY.getText(28,
                                                        6,
                                                        6)));
        assertTrue(output,
                   output.contains(ORDER_SUMMARY.getText(5,
                                                         10)));
        //Verify copyright message was printed
        assertTrue(output,
                   output.contains(LOG_APP_COPYRIGHT.getText()));
        tmpFile.delete();
    }
    /**
     * Tests running the orderloader with an invalid header.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void invalidHeader()
            throws Exception
    {
        File tmpFile = File.createTempFile("orders",".csv");
        tmpFile.deleteOnExit();
        CopyCharsUtils.copy(OrderParserTest.arrayToLines(
                "Side,Symbol,Price,OrderType,What?").toCharArray(),
                tmpFile.getAbsolutePath());
        run(tmpFile.getAbsolutePath());
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains(Messages.INVALID_CUSTOM_HEADER.getText("What?",
                                                                          4)));
    }
    /**
     * Tests running the loader while being unable to connect to the server.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void failConnectServer()
            throws Exception
    {
        MockMain main = create(new String[] { "don'tmatter" } );
        main.setFailCreateProcessor(true);
        main.start();
        String output = mOutput.toString();
        assertTrue(output,
                   output.contains(TEST_FAILURE));
    }
    /**
     * 
     *
     *
     * @param inArgs a <code>String[]</code> value
     * @return a <code>MockMain</code> value
     */
    private MockMain run(String... inArgs)
    {
        MockMain main = create(inArgs);
        main.start();
        return main;
    }
    /**
     * 
     *
     *
     * @param inArgs
     * @return
     */
    private MockMain create(String...inArgs)
    {
        MockMain main = new MockMain(inArgs);
        main.setClientWsHost("127.0.0.1");
        main.setClientWsPort("9000");
        main.setClientURL("tcp://localhost:61616");
        main.setClientUsername("admin");
        main.setClientPassword("admin".toCharArray());
        mOutput = new ByteArrayOutputStream();
        main.setMsgStream(new PrintStream(mOutput));
        return main;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    public class MockMain
            extends OrderLoaderMain
    {
        /**
         * Create a new MockMain instance.
         *
         * @param inArgs a <code>String[]</code> value
         */
        public MockMain(String...inArgs)
        {
            args = inArgs;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.orderloader.OrderLoaderMain#getArgs()
         */
        @Override
        protected String[] getArgs()
        {
            return args;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.orderloader.OrderLoaderMain#createProcessor(org.marketcetera.client.ClientParameters)
         */
        @Override
        protected OrderProcessor createProcessor(ClientParameters inParameters)
                throws Exception
        {
            if(mFailCreateProcessor) {
                throw new IllegalArgumentException(TEST_FAILURE);
            }
            mClientParameters = inParameters;
            mOrderProcessor = new MockOrderProcessor();
            return mOrderProcessor;
        }
        /**
         * 
         *
         *
         * @return
         */
        public ClientParameters getClientParameters()
        {
            return mClientParameters;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.orderloader.OrderLoaderMain#displaySummary(org.marketcetera.orderloader.OrderLoader)
         */
        @Override
        protected void displaySummary(OrderLoader inLoader)
        {
            super.displaySummary(inLoader);
            loader = inLoader;
        }
        /**
         * 
         *
         *
         * @param inFailCreateProcessor
         */
        public void setFailCreateProcessor(boolean inFailCreateProcessor)
        {
            mFailCreateProcessor = inFailCreateProcessor;
        }
        /**
         * 
         *
         *
         * @return
         */
        public MockOrderProcessor getOrderProcessor()
        {
            return mOrderProcessor;
        }
        /**
         * Get the loader value.
         *
         * @return an <code>OrderLoader</code> value
         */
        public OrderLoader getLoader()
        {
            return loader;
        }
        /**
         * Sets the loader value.
         *
         * @param inLoader a <code>OrderLoader</code> value
         */
        public void setLoader(OrderLoader inLoader)
        {
            loader = inLoader;
        }
        /**
         * 
         *
         *
         */
        public void assertNoErrors()
        {
            assertNotNull(loader);
            assertTrue(loader.getFailedOrders().isEmpty());
            assertEquals(0,
                         loader.getNumFailed());
        }
        /**
         * 
         *
         *
         */
        public void assertErrors()
        {
            assertNotNull(loader);
            assertFalse(loader.getFailedOrders().isEmpty());
            assertTrue(loader.getNumFailed() > 0);
        }
        /**
         * 
         *
         *
         */
        public void assertUsage()
        {
            String output = new String(mOutput.toByteArray());
            assertTrue(output,
                       output.contains("Usage"));
            assertNull(loader);
            assertTrue(output,
                       output.contains(LOG_APP_COPYRIGHT.getText()));
        }
        /**
         * 
         */
        private boolean mFailCreateProcessor = false;
        /**
         * 
         */
        private ClientParameters mClientParameters;
        /**
         * 
         */
        public MockOrderProcessor mOrderProcessor;
        /**
         * 
         */
        private OrderLoader loader;
        /**
         * 
         */
        private String[] args;
    }
    /**
     * 
     */
    private static final String TEST_FAILURE = "TEST FAILURE";
    /**
     * 
     */
    public ByteArrayOutputStream mOutput;
}

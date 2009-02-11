package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.core.LoggerConfiguration;
import static org.marketcetera.orderloader.Messages.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;

/* $License$ */
/**
 * Tests {@link Main}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class MainTest {
    @BeforeClass
    public static void setupLogger() {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void inputFileMissing() {
        MockMain main = run();
        assertEquals(Main.EXIT_CODE_USAGE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains(ERROR_MISSING_FILE.getText()));
    }
    @Test
    public void tooManyArgs() {
        MockMain main = run("file","extra");
        assertEquals(Main.EXIT_CODE_USAGE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains(ERROR_TOO_MANY_ARGUMENTS.getText()));
    }
    @Test
    public void incorrectModeSyntax() {
        MockMain main = run("-m");
        assertEquals(Main.EXIT_CODE_USAGE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains("option:m"));
    }
    @Test
    public void incorrectBrokerSyntax() {
        MockMain main = run("-b");
        assertEquals(Main.EXIT_CODE_USAGE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains("option:b"));
    }
    @Test
    public void incorrectUsernameSyntax() {
        MockMain main = run("-u");
        assertEquals(Main.EXIT_CODE_USAGE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains("option:u"));
        //verify that the copyright message is printed even when there's failure.
        assertTrue(output, output.contains(LOG_APP_COPYRIGHT.getText()));
    }
    @Test
    public void incorrectPasswordSyntax() {
        MockMain main = run("-p");
        assertEquals(Main.EXIT_CODE_USAGE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains("option:p"));
    }
    @Test
    public void systemOrder() throws Exception {
        File tmpFile = File.createTempFile("orders",".csv");
        tmpFile.deleteOnExit();
        CopyCharsUtils.copy(OrderParserTest.arrayToLines(
                OrderLoaderTest.SYSTEM_ORDER_EXAMPLE).toCharArray(),
                tmpFile.getAbsolutePath());
        MockMain main = run(tmpFile.getAbsolutePath());
        assertEquals(Main.EXIT_CODE_SUCCESS, main.getExitCode());
        //Verify Client Parameter values
        ClientParameters parameters = main.getClientParameters();
        assertEquals("127.0.0.1", parameters.getHostname());
        assertEquals("", parameters.getIDPrefix());
        assertArrayEquals("admin".toCharArray(), parameters.getPassword());
        assertEquals(9000, parameters.getPort());
        assertEquals("tcp://localhost:61616", parameters.getURL());
        assertEquals("admin", parameters.getUsername());
        //Verify that the order processor was closed/done
        assertTrue(main.getOrderProcessor().isDoneInvoked());

        String output = mOutput.toString();
        assertTrue(output, output.contains(LINE_SUMMARY.getText(22, 5, 5)));
        assertTrue(output, output.contains(ORDER_SUMMARY.getText(4, 7)));
        //Verify copyright message was printed
        assertTrue(output, output.contains(LOG_APP_COPYRIGHT.getText()));
        tmpFile.delete();
    }
    @Test
    public void invalidHeader() throws Exception {
        File tmpFile = File.createTempFile("orders",".csv");
        tmpFile.deleteOnExit();
        CopyCharsUtils.copy(OrderParserTest.arrayToLines(
                "Side,Symbol,Price,OrderType,What?").toCharArray(),
                tmpFile.getAbsolutePath());
        MockMain main = run(tmpFile.getAbsolutePath());
        assertEquals(Main.EXIT_CODE_FAILURE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains(Messages.INVALID_CUSTOM_HEADER.getText("What?", 4)));
    }
    @Test
    public void failConnectServer() throws Exception {
        MockMain main = create();
        main.setFailCreateProcessor(true);
        Main.run(new String[]{"don'tmatter"}, main);
        assertEquals(Main.EXIT_CODE_FAILURE, main.getExitCode());
        String output = mOutput.toString();
        assertTrue(output, output.contains(TEST_FAILURE));
    }
    private MockMain run(String... inArgs) {
        MockMain main = create();
        Main.run(inArgs, main);
        return main;
    }
    private MockMain create() {
        MockMain main = new MockMain();
        mOutput = new ByteArrayOutputStream();
        main.setMsgStream(new PrintStream(mOutput));
        return main;
    }
    public static class MockMain extends Main {
        @Override
        protected void exit(int inCode) {
            mExitCode = inCode;
        }

        @Override
        protected OrderProcessor createProcessor(ClientParameters inParameters)
                throws Exception {
            if(mFailCreateProcessor) {
                throw new IllegalArgumentException(TEST_FAILURE);
            }
            mClientParameters = inParameters;
            mOrderProcessor = new MockOrderProcessor();
            return mOrderProcessor;
        }

        public int getExitCode() {
            return mExitCode;
        }

        public ClientParameters getClientParameters() {
            return mClientParameters;
        }

        public void setFailCreateProcessor(boolean inFailCreateProcessor) {
            mFailCreateProcessor = inFailCreateProcessor;
        }

        public MockOrderProcessor getOrderProcessor() {
            return mOrderProcessor;
        }

        private int mExitCode = -1;
        private boolean mFailCreateProcessor = false;
        private ClientParameters mClientParameters;
        public MockOrderProcessor mOrderProcessor;
    }

    private static final String TEST_FAILURE = "TEST FAILURE";
    public ByteArrayOutputStream mOutput;
}

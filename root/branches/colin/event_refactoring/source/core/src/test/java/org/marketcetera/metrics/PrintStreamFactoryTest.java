package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* $License$ */
/**
 * Tests {@link PrintStreamFactory} subclasses, {@link FileStreamFactory} &
 * {@link StdErrFactory}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PrintStreamFactoryTest {
    @BeforeClass
    public static void setup() {
        LoggerConfiguration.logSetup();
    }

    /**
     * Tests the {@link StdErrFactory}
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void stderrFactory() throws Exception {
        PrintStream stderr = System.err;
        MyStream stream = new MyStream();
        System.setErr(stream);
        try {
            PrintStream createdStream = StdErrFactory.INSTANCE.getStream("blue");
            assertSame(stream, createdStream);
            assertFalse(stream.isCloseCalled());
            assertFalse(stream.isFlushCalled());
            StdErrFactory.INSTANCE.done(createdStream);
            assertFalse(stream.isCloseCalled());
            assertTrue(stream.isFlushCalled());
        } finally {
            System.setErr(stderr);
        }

    }

    /**
     * Tests the {@link FileStreamFactory}.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void fileStreamFactory() throws Exception {
        final PrintStream stream = FileStreamFactory.INSTANCE.getStream("name");
        stream.print("test");
        FileStreamFactory.INSTANCE.done(stream);
        assertFalse(stream.checkError());
        //try writing
        stream.write(12);
        //the failure of write() above results in setting of the error flag.
        assertTrue(stream.checkError());
    }

    /**
     * A test stream that writes output into a byte array.
     * The class is used to verify whether {@link java.io.PrintStream#close()}
     * & {@link java.io.PrintStream#flush()} operations are being correctly
     * invoked.
     */
    private static class MyStream extends PrintStream {
        /**
         * Creates an instance.
         *
         */
        public MyStream() {
            super(new ByteArrayOutputStream());
        }

        @Override
        public void close() {
            super.close();
            mCloseCalled = true;
        }

        @Override
        public void flush() {
            super.flush();
            mFlushCalled = true;
        }

        /**
         *
         * @return if the {@link #close()} was invoked.
         */
        public boolean isCloseCalled() {
            return mCloseCalled;
        }

        /**
         *
         * @return if the {@link #flush()} was invoked.
         */
        public boolean isFlushCalled() {
            return mFlushCalled;
        }

        private boolean mCloseCalled;
        private boolean mFlushCalled;
    }
}

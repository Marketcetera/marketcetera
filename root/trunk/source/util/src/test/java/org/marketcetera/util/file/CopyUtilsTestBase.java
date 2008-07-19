package org.marketcetera.util.file;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.marketcetera.util.test.TestCaseBase;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class CopyUtilsTestBase
    extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"copy_utils"+File.separator;
    protected static final String TEST_INPUT_FILE=
        TEST_ROOT+"input_file";
    protected static final String TEST_OUTPUT_FILE=
        TEST_ROOT+"output_file";
    protected static final String TEST_NONEXISTENT_FILE=
        TEST_ROOT+"nonexistent"+File.separator+"nonexistent";

    protected static final class CloseSetReader
        extends Reader
    {
        private boolean mClosed;

        boolean getClosed()
        {
            return mClosed;
        }

        @Override
        public int read
            (char[] cbuf,
             int off,
             int len) 
        {
            return -1;
        }

        @Override
        public void close()
        {
            mClosed=true;
        }
    }

    protected static final class CloseSetWriter
        extends Writer
    {
        private boolean mClosed;

        boolean getClosed()
        {
            return mClosed;
        }

        @Override
        public void write
            (char[] cbuf,
             int off,
             int len) {}

        @Override
        public void flush() {}
       
        @Override
        public void close()
        {
            mClosed=true;
        }
    }

    protected static final class CloseSetInputStream
        extends InputStream
    {
        private boolean mClosed;

        boolean getClosed()
        {
            return mClosed;
        }

        @Override
        public int read()
        {
            return -1;
        }

        @Override
        public void close()
        { 
            mClosed=true;
        }
    }

    protected static final class CloseSetOutputStream
        extends OutputStream
    {
        private boolean mClosed;

        boolean getClosed()
        {
            return mClosed;
        }

        @Override
        public void write
            (int b) {}

        @Override
        public void flush() {}
       
        @Override
        public void close()
        {
            mClosed=true;
        }
    }
}

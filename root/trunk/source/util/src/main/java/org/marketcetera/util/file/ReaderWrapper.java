package org.marketcetera.util.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.marketcetera.core.ClassVersion;

import static org.marketcetera.util.file.SpecialNames.*;

/**
 * A wrapped reader. It may wrap a regular file, the standard input
 * stream, or any other {@link Reader} instance. This wrapper is
 * intended to wrap {@link Reader} instances for use with {@link
 * CloseableRegistry}, hence such instances should not be closed
 * directly, i.e. without going through the wrapper's {@link #close()}
 * method.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ReaderWrapper
    implements Closeable
{

    // INSTANCE DATA.

    private Reader mReader;
    private boolean mSkipClose;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapped reader that wraps the regular file with
     * the given name, or the standard input stream (if the name is
     * {@link SpecialNames#STANDARD_INPUT}).
     *
     * @param name The file name.
     *
     * @throws FileNotFoundException Thrown if the name represents a
     * regular file, and it cannot be opened for reading.
     * @throws IOException Thrown if the name represents the standard
     * input stream, and a reader cannot be built for it.
     */

    public ReaderWrapper
        (String name)
        throws FileNotFoundException,
               IOException
    {
        if (name.equals(STANDARD_INPUT)) {
            mReader=new InputStreamReader(System.in);
            mSkipClose=true;
            return;
        }
        mReader=new FileReader(name);
    }

    /**
     * Creates a new wrapped reader that wraps the given regular file.
     *
     * @param file The file.
     *
     * @throws FileNotFoundException Thrown if the file cannot be
     * opened for reading.
     */

    public ReaderWrapper
        (File file)
        throws FileNotFoundException
    {
        mReader=new FileReader(file);
    }

    /**
     * Creates a new wrapped reader that wraps the given reader. The
     * reader may or may not be closed when {@link #close()} is
     * called depending on the given flag.
     *
     * @param reader The reader.
     * @param skipClose True if the underlying reader should not be
     * closed.
     */

    public ReaderWrapper
        (Reader reader,
         boolean skipClose)
    {
        mReader=reader;
        mSkipClose=skipClose;
    }

    /**
     * Creates a new wrapped reader that wraps the given reader. The
     * underlying reader will be closed when {@link #close()} is
     * called; hence the given reader should not wrap the standard
     * input stream.
     *
     * @param reader The reader.
     */

    public ReaderWrapper
        (Reader reader)
    {
        this(reader,false);
    }


    // Closeable.

    @Override
    public void close()
        throws IOException
    {
        if ((getReader()==null) || getSkipClose()) {
            return;
        }
        getReader().close();

        // Prevent future closures per {@link Closeable}.

        mReader=null;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's underlying reader.
     *
     * @return The reader.
     */

    public Reader getReader()
    {
        return mReader;
    }

    /**
     * Returns true if the receiver's underlying reader will not be
     * closed when {@link #close()} is called.
     *
     * @return True if so.
     */

    public boolean getSkipClose()
    {
        return mSkipClose;
    }
}

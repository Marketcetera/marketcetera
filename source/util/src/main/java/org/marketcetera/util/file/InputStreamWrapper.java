package org.marketcetera.util.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.marketcetera.util.misc.ClassVersion;

import static org.marketcetera.util.file.SpecialNames.*;

/**
 * A wrapped input stream. It may wrap a regular file, the standard
 * input stream, or any other {@link InputStream} instance. This
 * wrapper is intended to wrap {@link InputStream} instances for use
 * with {@link CloseableRegistry}, hence such instances should not be
 * closed directly, that is, without going through the wrapper's
 * {@link #close()} method.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class InputStreamWrapper
    implements Closeable
{

    // INSTANCE DATA.

    private InputStream mStream;
    private boolean mSkipClose;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapped stream that wraps the regular file with
     * the given name, or the standard input stream (if the name is
     * {@link SpecialNames#STANDARD_INPUT}).
     *
     * @param name The file name.
     *
     * @throws FileNotFoundException Thrown if the name represents a
     * regular file, and it cannot be opened for reading.
     */

    public InputStreamWrapper
        (String name)
        throws FileNotFoundException
    {
        if (name.equals(STANDARD_INPUT)) {
            mStream=System.in;
            mSkipClose=true;
            return;
        }
        mStream=new FileInputStream(name);
    }

    /**
     * Creates a new wrapped stream that wraps the given regular file.
     *
     * @param file The file.
     *
     * @throws FileNotFoundException Thrown if the file cannot be
     * opened for reading.
     */

    public InputStreamWrapper
        (File file)
        throws FileNotFoundException
    {
        mStream=new FileInputStream(file);
    }

    /**
     * Creates a new wrapped stream that wraps the given stream. The
     * stream may or may not be closed when {@link #close()} is called
     * depending on the given flag.
     *
     * @param stream The stream.
     * @param skipClose True if the underlying stream should not be
     * closed.
     */

    public InputStreamWrapper
        (InputStream stream,
         boolean skipClose)
    {
        mStream=stream;
        mSkipClose=skipClose;
    }

    /**
     * Creates a new wrapped stream that wraps the given stream. The
     * underlying stream will be closed when {@link #close()} is
     * called; hence the given stream should not wrap (or be) the
     * standard input stream.
     *
     * @param stream The stream.
     */

    public InputStreamWrapper
        (InputStream stream)
    {
        this(stream,false);
    }


    // Closeable.

    @Override
    public void close()
        throws IOException
    {
        if ((getStream()==null) || getSkipClose()) {
            return;
        }
        getStream().close();

        // Prevent future closures per {@link Closeable}.

        mStream=null;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's underlying stream.
     *
     * @return The stream.
     */

    public InputStream getStream()
    {
        return mStream;
    }

    /**
     * Returns true if the receiver's underlying stream will not be
     * closed when {@link #close()} is called.
     *
     * @return True if so.
     */

    public boolean getSkipClose()
    {
        return mSkipClose;
    }
}

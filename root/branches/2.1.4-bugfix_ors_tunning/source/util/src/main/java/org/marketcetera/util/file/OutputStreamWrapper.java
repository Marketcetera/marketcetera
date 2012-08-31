package org.marketcetera.util.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.marketcetera.util.misc.ClassVersion;

import static org.marketcetera.util.file.SpecialNames.*;

/**
 * A wrapped output stream. It may wrap a regular file, the standard
 * output or error stream, or any other {@link OutputStream}
 * instance. This wrapper is intended to wrap {@link OutputStream}
 * instances for use with {@link CloseableRegistry}, hence such
 * instances should not be closed directly, that is, without going
 * through the wrapper's {@link #close()} method.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class OutputStreamWrapper
    implements Closeable
{

    // INSTANCE DATA.

    private OutputStream mStream;
    private boolean mSkipClose;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapped stream that wraps:
     *
     * <ul>
     * <li>the regular file with the given name (data is appended to the
     * file if the name is prefixed by {@link SpecialNames#PREFIX_APPEND}),
     * or</li>
     * <li>the standard output stream (if the name is {@link
     * SpecialNames#STANDARD_OUTPUT}), or</li>
     * <li>the standard error stream (if the name is {@link
     * SpecialNames#STANDARD_ERROR}).</li>
     * </ul>
     *
     * @param name The file name.
     *
     * @throws FileNotFoundException Thrown if the name represents a
     * regular file, and it cannot be opened for writing.
     */

    public OutputStreamWrapper
        (String name)
        throws FileNotFoundException
    {
        if (name.equals(STANDARD_OUTPUT)) {
            mStream=System.out;
            mSkipClose=true;
            return;
        }
        if (name.equals(STANDARD_ERROR)) {
            mStream=System.err;
            mSkipClose=true;
            return;
        }
        if (!name.startsWith(PREFIX_APPEND)) {
            mStream=new FileOutputStream(name);
            return;
        }
        mStream=new FileOutputStream
            (name.substring(PREFIX_APPEND.length()),true);
    }

    /**
     * Creates a new wrapped stream that wraps the given regular file.
     *
     * @param file The file.
     *
     * @throws FileNotFoundException Thrown if the file cannot be
     * opened for writing.
     */

    public OutputStreamWrapper
        (File file)
        throws FileNotFoundException
    {
        mStream=new FileOutputStream(file);
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

    public OutputStreamWrapper
        (OutputStream stream,
         boolean skipClose)
    {
        mStream=stream;
        mSkipClose=skipClose;
    }

    /**
     * Creates a new wrapped stream that wraps the given stream. The
     * underlying stream will be closed when {@link #close()} is
     * called; hence the given stream should not wrap (or be) the
     * standard output or error stream.
     *
     * @param stream The stream.
     */

    public OutputStreamWrapper
        (OutputStream stream)
    {
        this(stream,false);
    }


    // Closeable.

    @Override
    public void close()
        throws IOException
    {
        if (getStream()==null) {
            return;
        }

        // All streams (closeable or not) are flushed.

        getStream().flush();
        if (getSkipClose()) {
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

    public OutputStream getStream()
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

package org.marketcetera.util.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.marketcetera.core.ClassVersion;

import static org.marketcetera.util.file.SpecialNames.*;

/**
 * A wrapped writer. It may wrap a regular file, the standard output
 * or error stream, or any other {@link Writer} instance. This wrapper
 * is intended to wrap {@link Writer} instances for use with {@link
 * CloseableRegistry}, hence such instances should not be closed
 * directly, i.e. without going through the wrapper's {@link #close()}
 * method.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class WriterWrapper
    implements Closeable
{

    // INSTANCE DATA.

    private Writer mWriter;
    private boolean mSkipClose;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapped writer that wraps:
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
     * @throws IOException Thrown if a writer cannot be built for the
     * standard output or error.
     *
     * @throws FileNotFoundException Thrown if the name represents a
     * regular file, and it cannot be opened for writing.
     */

    public WriterWrapper
        (String name)
        throws FileNotFoundException,
               IOException
    {
        if (name.equals(STANDARD_OUTPUT)) {
            mWriter=new OutputStreamWriter(System.out);
            mSkipClose=true;
            return;
        }
        if (name.equals(STANDARD_ERROR)) {
            mWriter=new OutputStreamWriter(System.err);
            mSkipClose=true;
            return;
        }
        if (!name.startsWith(PREFIX_APPEND)) {
            mWriter=new FileWriter(name);
            return;
        }
        mWriter=new FileWriter(name.substring(PREFIX_APPEND.length()),true);
    }

    /**
     * Creates a new wrapped writer that wraps the given regular file.
     *
     * @param file The file.
     *
     * @throws IOException Thrown if the file cannot be opened for
     * writing.
     */

    public WriterWrapper
        (File file)
        throws IOException
    {
        mWriter=new FileWriter(file);
    }

    /**
     * Creates a new wrapped writer that wraps the given writer. The
     * writer may or may not be closed when {@link #close()} is called
     * depending on the given flag.
     *
     * @param writer The writer.
     * @param skipClose True if the underlying writer should not be
     * closed.
     */

    public WriterWrapper
        (Writer writer,
         boolean skipClose)
    {
        mWriter=writer;
        mSkipClose=skipClose;
    }

    /**
     * Creates a new wrapped writer that wraps the given writer. The
     * underlying writer will be closed when {@link #close()} is
     * called; hence the given writer should not wrap the standard
     * output or error stream.
     *
     * @param writer The writer.
     */

    public WriterWrapper
        (Writer writer)
    {
        this(writer,false);
    }


    // Closeable.

    @Override
    public void close()
        throws IOException
    {
        if (getWriter()==null) {
            return;
        }

        // All writers (closeable or not) are flushed.

        getWriter().flush();
        if (getSkipClose()) {
            return;
        }
        getWriter().close();

        // Prevent future closures per {@link Closeable}.

        mWriter=null;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's underlying writer.
     *
     * @return The writer.
     */

    public Writer getWriter()
    {
        return mWriter;
    }

    /**
     * Returns true if the receiver's underlying writer will not be
     * closed when {@link #close()} is called.
     *
     * @return True if so.
     */

    public boolean getSkipClose()
    {
        return mSkipClose;
    }
}

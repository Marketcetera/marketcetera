package org.marketcetera.util.file;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Utilities for copying textual data, in the default JVM charset. The
 * participating media include files, arrays, readers/writers, or some
 * combinations thereof.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class CopyCharsUtils
{

    // CLASS METHODS.

    /**
     * Copies a character stream from the given source to the given
     * sink.
     *
     * @param in The character source, as interpreted by {@link
     * ReaderWrapper#ReaderWrapper(Reader,boolean)}.
     * @param inSkipClose True if the source reader should not be
     * closed.
     * @param out The character sink, as interpreted by {@link
     * WriterWrapper#WriterWrapper(Writer,boolean)}.
     * @param outSkipClose True if the sink writer should not be
     * closed.
     *
     * @return The number of characters copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copy
        (Reader in,
         boolean inSkipClose,
         Writer out,
         boolean outSkipClose)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            ReaderWrapper inW=new ReaderWrapper(in,inSkipClose);
            registry.register(inW);
            WriterWrapper outW=new WriterWrapper(out,outSkipClose);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getReader(),outW.getWriter());
        } catch (IOException ex) {
            throw ExceptUtils.wrap(ex,Messages.CANNOT_COPY_CSTREAMS);
        } finally {
            registry.close();
        }
    }

    /**
     * Copies a character stream from one given location to another.
     *
     * @param in The name of the character source, as interpreted by
     * {@link ReaderWrapper#ReaderWrapper(String)}.
     * @param out The name of the character sink, as interpreted by
     * {@link WriterWrapper#WriterWrapper(String)}.
     *
     * @return The number of characters copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copy
        (String in,
         String out)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            ReaderWrapper inW=new ReaderWrapper(in);
            registry.register(inW);
            WriterWrapper outW=new WriterWrapper(out);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getReader(),outW.getWriter());
        } catch (IOException ex) {
            throw ExceptUtils.wrap(ex,new I18NBoundMessage2P
                                   (Messages.CANNOT_COPY_FILES,in,out));
        } finally {
            registry.close();
        }
    }

    /**
     * Copies a character stream from the given source to the given
     * location.
     *
     * @param in The character source, as interpreted by {@link
     * ReaderWrapper#ReaderWrapper(Reader,boolean)}.
     * @param skipClose True if the source reader should not be
     * closed.
     * @param out The name of the character sink, as interpreted by
     * {@link WriterWrapper#WriterWrapper(String)}.
     *
     * @return The number of characters copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copy
        (Reader in,
         boolean skipClose,
         String out)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            ReaderWrapper inW=new ReaderWrapper(in,skipClose);
            registry.register(inW);
            WriterWrapper outW=new WriterWrapper(out);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getReader(),outW.getWriter());
        } catch (IOException ex) {
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_READER,out));
        } finally {
            registry.close();
        }
    }

    /**
     * Copies a character stream from the given location to the given
     * sink.
     *
     * @param in The name of the character source, as interpreted by {@link
     * ReaderWrapper#ReaderWrapper(String)}.
     * @param out The character sink, as interpreted by {@link
     * WriterWrapper#WriterWrapper(Writer,boolean)}.
     * @param skipClose True if the sink writer should not be closed.
     *
     * @return The number of characters copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copy
        (String in,
         Writer out,
         boolean skipClose)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            ReaderWrapper inW=new ReaderWrapper(in);
            registry.register(inW);
            WriterWrapper outW=new WriterWrapper(out,skipClose);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getReader(),outW.getWriter());
        } catch (IOException ex) {
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_WRITER,in));
        } finally {
            registry.close();
        }
    }

    /**
     * Copies the character stream at the given location into memory,
     * returning a character array.
     *
     * @param name The name of the character source, as interpreted by
     * {@link ReaderWrapper#ReaderWrapper(String)}.
     *
     * @return The array.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static char[] copy
        (String name)
        throws I18NException
    {
        try {
            ReaderWrapper inW=new ReaderWrapper(name);
            try {
                return IOUtils.toCharArray(inW.getReader());
            } finally {
                inW.close();
            }
        } catch (IOException ex) {
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_MEMORY_DST,name));
        }
    }

    /**
     * Copies an in-memory character array into the given location.
     *
     * @param data The array.
     * @param name The name of the character sink, as interpreted by
     * {@link WriterWrapper#WriterWrapper(String)}.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static void copy
        (char[] data,
         String name)
        throws I18NException
    {
        try {
            WriterWrapper outW=new WriterWrapper(name);
            try {
                outW.getWriter().write(data);
            } finally {
                outW.close();
            }
        } catch (IOException ex) {
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_MEMORY_SRC,name));
        }
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private CopyCharsUtils() {}
}

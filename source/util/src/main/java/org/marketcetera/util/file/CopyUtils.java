package org.marketcetera.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NException;

/**
 * Utilities for copying data. The participating media include files,
 * arrays, streams, or some combinations thereof.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class CopyUtils
{

    // CLASS METHODS.

    /**
     * Copies a byte stream from one given location to another.
     *
     * @param in The name of the byte source, as interpreted by {@link
     * InputStreamWrapper#InputStreamWrapper(String)}.
     * @param out The name of the byte sink, as interpreted by {@link
     * OutputStreamWrapper#OutputStreamWrapper(String)}.
     *
     * @return The number of bytes copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copyBytes
        (String in,
         String out)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            InputStreamWrapper inW=new InputStreamWrapper(in);
            registry.register(inW);
            OutputStreamWrapper outW=new OutputStreamWrapper(out);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getStream(),outW.getStream());
        } catch (IOException ex) {
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_FILES,in,out);
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

    public static long copyChars
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
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_FILES,in,out);
        } finally {
            registry.close();
        }
    }

    /**
     * Copies the given byte stream to the given location.
     *
     * @param in The byte source, as interpreted by {@link
     * InputStreamWrapper#InputStreamWrapper(InputStream,boolean)}.
     * @param skipClose True if the source stream should not be
     * closed.
     * @param out The name of the byte sink, as interpreted by {@link
     * OutputStreamWrapper#OutputStreamWrapper(String)}.
     *
     * @return The number of bytes copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copyBytes
        (InputStream in,
         boolean skipClose,
         String out)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            InputStreamWrapper inW=new InputStreamWrapper(in,skipClose);
            registry.register(inW);
            OutputStreamWrapper outW=new OutputStreamWrapper(out);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getStream(),outW.getStream());
        } catch (IOException ex) {
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_ISTREAM,in,out);
        } finally {
            registry.close();
        }
    }

    /**
     * Copies the given character stream to the given location.
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

    public static long copyChars
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
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_READER,in,out);
        } finally {
            registry.close();
        }
    }

    /**
     * Copies a byte stream from the given location to the given sink.
     *
     * @param in The name of the byte source, as interpreted by {@link
     * InputStreamWrapper#InputStreamWrapper(String)}.
     * @param out The byte sink, as interpreted by {@link
     * OutputStreamWrapper#OutputStreamWrapper(OutputStream,boolean)}.
     * @param skipClose True if the sink stream should not be closed.
     *
     * @return The number of bytes copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copyBytes
        (String in,
         OutputStream out,
         boolean skipClose)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            InputStreamWrapper inW=new InputStreamWrapper(in);
            registry.register(inW);
            OutputStreamWrapper outW=new OutputStreamWrapper(out,skipClose);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getStream(),outW.getStream());
        } catch (IOException ex) {
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_OSTREAM,in,out);
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

    public static long copyChars
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
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_WRITER,in,out);
        } finally {
            registry.close();
        }
    }

    /**
     * Copies the byte stream at the given named location into memory,
     * returning a byte array.
     *
     * @param name The name of the byte source, as interpreted by
     * {@link InputStreamWrapper#InputStreamWrapper(String)}.
     *
     * @return The array.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static byte[] copyBytes
        (String name)
        throws I18NException
    {
        try {
            InputStreamWrapper inW=new InputStreamWrapper(name);
            try {
                return IOUtils.toByteArray(inW.getStream());
            } finally {
                inW.close();
            }
        } catch (IOException ex) {
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_MEMORY_DST,name);
        }
    }

    /**
     * Copies the character stream at the given named location into
     * memory, returning a character array.
     *
     * @param name The name of the character source, as interpreted by
     * {@link ReaderWrapper#ReaderWrapper(String)}.

     * @return The array.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static char[] copyChars
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
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_MEMORY_DST,name);
        }
    }

    /**
     * Copies an in-memory byte array into the given named location.
     *
     * @param data The array.
     * @param name The name of the byte sink, as interpreted by {@link
     * OutputStreamWrapper#OutputStreamWrapper(String)}.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static void copyBytes
        (byte[] data,
         String name)
        throws I18NException
    {
        try {
            OutputStreamWrapper outW=new OutputStreamWrapper(name);
            try {
                outW.getStream().write(data);
            } finally {
                outW.close();
            }
        } catch (IOException ex) {
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_MEMORY_SRC,name);
        }
    }

    /**
     * Copies an in-memory character array into the given named
     * location.
     *
     * @param data The array.
     * @param name The name of the character sink, as interpreted by
     * {@link WriterWrapper#WriterWrapper(String)}.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static void copyChars
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
            throw ExceptUtils.wrap
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_MEMORY_SRC,name);
        }
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private CopyUtils() {}
}

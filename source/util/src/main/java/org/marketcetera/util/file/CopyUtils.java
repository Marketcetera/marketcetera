package org.marketcetera.util.file;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.except.I18NException;

/**
 * Utilities for copying data. The participating media include files,
 * arrays, or a combination thereof.
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
            throw new I18NException
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
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static void copyChars
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
            IOUtils.copyLarge(inW.getReader(),outW.getWriter());
        } catch (IOException ex) {
            throw new I18NException
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_FILES,in,out);
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
            throw new I18NException
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
            throw new I18NException
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
            throw new I18NException
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
            throw new I18NException
                (ex,Messages.PROVIDER,Messages.CANNOT_COPY_MEMORY_SRC,name);
        }
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private CopyUtils() {}
}

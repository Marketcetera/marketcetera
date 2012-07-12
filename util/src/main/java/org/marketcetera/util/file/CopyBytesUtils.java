package org.marketcetera.util.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Utilities for copying binary data. The participating media include
 * files, arrays, streams, or some combinations thereof.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class CopyBytesUtils
{

    // CLASS METHODS.

    /**
     * Copies a byte stream from the given source to the given sink.
     *
     * @param in The byte source, as interpreted by {@link
     * InputStreamWrapper#InputStreamWrapper(InputStream,boolean)}.
     * @param inSkipClose True if the source stream should not be
     * closed.
     * @param out The byte sink, as interpreted by {@link
     * OutputStreamWrapper#OutputStreamWrapper(OutputStream,boolean)}.
     * @param outSkipClose True if the sink stream should not be
     * closed.
     *
     * @return The number of bytes copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copy
        (InputStream in,
         boolean inSkipClose,
         OutputStream out,
         boolean outSkipClose)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            InputStreamWrapper inW=new InputStreamWrapper(in,inSkipClose);
            registry.register(inW);
            OutputStreamWrapper outW=new OutputStreamWrapper(out,outSkipClose);
            registry.register(outW);
            return IOUtils.copyLarge(inW.getStream(),outW.getStream());
        } catch (IOException ex) {
            throw ExceptUtils.wrap(ex,Messages.CANNOT_COPY_STREAMS);
        } finally {
            registry.close();
        }
    }

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

    public static long copy
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
            throw ExceptUtils.wrap(ex,new I18NBoundMessage2P
                                   (Messages.CANNOT_COPY_FILES,in,out));
        } finally {
            registry.close();
        }
    }

    /**
     * Copies a byte stream from the given source to the given
     * location.
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

    public static long copy
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
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_ISTREAM,out));
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

    public static long copy
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
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_OSTREAM,in));
        } finally {
            registry.close();
        }
    }

    /**
     * Copies a byte stream from the given location into memory,
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

    public static byte[] copy
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
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_MEMORY_DST,name));
        }
    }

    /**
     * Copies an in-memory byte array into the given location.
     *
     * @param data The array.
     * @param name The name of the byte sink, as interpreted by {@link
     * OutputStreamWrapper#OutputStreamWrapper(String)}.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static void copy
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
            throw ExceptUtils.wrap(ex,new I18NBoundMessage1P
                                   (Messages.CANNOT_COPY_MEMORY_SRC,name));
        }
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private CopyBytesUtils() {}
}

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
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.unicode.SignatureCharset;
import org.marketcetera.util.unicode.UnicodeInputStreamReader;

/**
 * Utilities for copying textual data, with Unicode BOM support. The
 * participating media include files, arrays, readers/writers, or some
 * combinations thereof.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class CopyCharsUnicodeUtils
{

    // CLASS METHODS.

    /**
     * Copies a character stream from one given location (and
     * interpreted using the given strategy) to another, attempting to
     * retain signature/charset information.
     *
     * @param in The name of the character source, as interpreted by
     * {@link ReaderWrapper#ReaderWrapper(String,DecodingStrategy)}.
     * @param decodingStrategy The decoding strategy. It may be null
     * to use the default JVM charset.
     * @param out The name of the character sink, as interpreted by
     * {@link WriterWrapper#WriterWrapper(String,SignatureCharset)}.
     *
     * @return The number of characters copied.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static long copy
        (String in,
         DecodingStrategy decodingStrategy,
         String out)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            ReaderWrapper inW=new ReaderWrapper(in,decodingStrategy);
            registry.register(inW);
            Reader reader=inW.getReader();
            SignatureCharset sc=null;
            if (reader instanceof UnicodeInputStreamReader) {
                sc=((UnicodeInputStreamReader)reader).getSignatureCharset();
            }
            WriterWrapper outW=new WriterWrapper(out,sc);
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
     * location, attempting to retain signature/charset information.
     *
     * @param in The character source, as interpreted by {@link
     * ReaderWrapper#ReaderWrapper(Reader,boolean)}.
     * @param skipClose True if the source reader should not be
     * closed.
     * @param out The name of the character sink, as interpreted by
     * {@link WriterWrapper#WriterWrapper(String,SignatureCharset)}.
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
            SignatureCharset sc=null;
            if (in instanceof UnicodeInputStreamReader) {
                sc=((UnicodeInputStreamReader)in).getSignatureCharset();
            }
            WriterWrapper outW=new WriterWrapper(out,sc);
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
     * Copies a character stream from the given location (and
     * interpreted using the given strategy) to the given sink.
     *
     * @param in The name of the character source, as interpreted by {@link
     * ReaderWrapper#ReaderWrapper(String,DecodingStrategy)}.
     * @param decodingStrategy The decoding strategy. It may be null
     * to use the default JVM charset.
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
         DecodingStrategy decodingStrategy,
         Writer out,
         boolean skipClose)
        throws I18NException
    {
        CloseableRegistry registry=new CloseableRegistry();
        try {
            ReaderWrapper inW=new ReaderWrapper(in,decodingStrategy);
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
     * Copies the character stream at the given location (and
     * interpreted using the given strategy) into memory, returning a
     * character array.
     *
     * @param name The name of the character source, as interpreted by
     * {@link ReaderWrapper#ReaderWrapper(String,DecodingStrategy)}.
     * @param decodingStrategy The decoding strategy. It may be null
     * to use the default JVM charset.
     *
     * @return The array.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static char[] copy
        (String name,
         DecodingStrategy decodingStrategy)
        throws I18NException
    {
        try {
            ReaderWrapper inW=new ReaderWrapper(name,decodingStrategy);
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
     * Copies an in-memory character array into the given location and
     * using the given signature/charset.
     *
     * @param data The array.
     * @param name The name of the character sink, as interpreted by
     * {@link WriterWrapper#WriterWrapper(String,SignatureCharset)}.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     *
     * @throws I18NException Thrown if there is a data read/write
     * error.
     */

    public static void copy
        (char[] data,
         String name,
         SignatureCharset requestedSignatureCharset)
        throws I18NException
    {
        try {
            WriterWrapper outW=new WriterWrapper
                (name,requestedSignatureCharset);
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

    private CopyCharsUnicodeUtils() {}
}

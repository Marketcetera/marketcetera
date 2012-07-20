package org.marketcetera.util.unicode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A variation of {@link OutputStreamWriter} that is BOM-aware. It can
 * operate in any of the following modes:
 *
 * <ul>
 *
 * <li>As a standard output stream writer that uses the default JVM
 * charset.</li>
 *
 * <li>A writer that uses a specific charset and (optionally) injects
 * a specific signature in the header of the output stream.</li>
 *
 * <li>A writer that uses the signature/charset which a supplied
 * reader had used to decode a byte stream. This is useful when a file
 * is modified, and the original signature/charset must be
 * preserved.</li>
 *
 * </ul>
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class UnicodeOutputStreamWriter
    extends Writer
{

    // INSTANCE DATA.

    private OutputStream mStream;
    private OutputStreamWriter mWriter;
    private SignatureCharset mRequestedSignatureCharset;
    private SignatureCharset mSignatureCharset;
    private boolean mWriteSignature=true;


    // CONSTRUCTORS.

    /**
     * Creates a new writer over the given stream that uses the
     * default JVM charset (and no signature is injected).
     *
     * @param stream The stream.
     */

    public UnicodeOutputStreamWriter
        (OutputStream stream)
    {
        super(stream);
        mStream=stream;
    }

    /**
     * Creates a new writer over the given stream that normally
     * injects the given signature, and uses its associated charset
     * for encoding. However, if the charset in the given
     * signature/charset pair is not supported by the JVM, the default
     * JVM charset is used instead (and no signature is
     * injected). Signature injection is also skipped if the given
     * flag is set to false (even if the signature/charset pair
     * has a signature).
     *
     * @param stream The stream.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     * @param writeSignature True if a signature (if any) should be
     * written.
     */

    protected UnicodeOutputStreamWriter
        (OutputStream stream,
         SignatureCharset requestedSignatureCharset,
         boolean writeSignature)
    {
        this(stream);
        mRequestedSignatureCharset=requestedSignatureCharset;
        mWriteSignature=writeSignature;
    }

    /**
     * Creates a new writer over the given stream that normally
     * injects the given signature, and uses its associated charset
     * for encoding. However, if the charset in the given
     * signature/charset pair is not supported by the JVM, the default
     * JVM charset is used instead (and no signature is injected).
     *
     * @param stream The stream.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     */

    public UnicodeOutputStreamWriter
        (OutputStream stream,
         SignatureCharset requestedSignatureCharset)
    {
        this(stream,requestedSignatureCharset,true);
    }

    /**
     * Creates a new writer over the given stream that uses the actual
     * signature/charset of the given reader. If the reader is not a
     * valid instance of a {@link UnicodeInputStreamReader}, or if it
     * used the default JVM charset, then the writer uses the default
     * JVM charset (and no signature is injected). Signature injection
     * is also skipped if the given flag is set to false (even if the
     * given reader's signature/charset pair has a signature).
     *
     * @param stream The stream.
     * @param reader The reader.
     * @param writeSignature True if a signature (if any) should be
     * written.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    protected UnicodeOutputStreamWriter
        (OutputStream stream,
         Reader reader,
         boolean writeSignature)
        throws IOException
    {
        this(stream);
        if (reader instanceof UnicodeInputStreamReader) {
            mRequestedSignatureCharset=
                ((UnicodeInputStreamReader)reader).getSignatureCharset();
            mWriteSignature=writeSignature;
        }
    }

    /**
     * Creates a new writer over the given stream that uses the actual
     * signature/charset of the given reader. If the reader is not a
     * valid instance of a {@link UnicodeInputStreamReader}, or if it
     * used the default JVM charset, then the writer uses the default
     * JVM charset (and no signature is injected).
     *
     * @param stream The stream.
     * @param reader The reader.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    public UnicodeOutputStreamWriter
        (OutputStream stream,
         Reader reader)
        throws IOException
    {
        this(stream,reader,true);
    }


    // Writer.

    @Override
    public void write
        (int c)
        throws IOException
    {
        synchronized (lock) {
            init();
            mWriter.write(c);
        }
    }

    @Override
    public void write
        (char[] cbuf)
        throws IOException
    {
        synchronized (lock) {
            init();
            mWriter.write(cbuf);
        }
    }

    @Override
    public void write
        (char cbuf[],
         int off,
         int len)
        throws IOException
    {
        synchronized (lock) {
            init();
            mWriter.write(cbuf,off,len);
        }
    }

    @Override
    public void write
        (String str)
        throws IOException
    {
        synchronized (lock) {
            init();
            mWriter.write(str);
        }
    }

    @Override
    public void write
        (String str,
         int off,
         int len)
        throws IOException
    {
        synchronized (lock) {
            init();
            mWriter.write(str,off,len);
        }
    }

    @Override
    public Writer append
        (CharSequence csq)
        throws IOException
    {
        synchronized (lock) {
            init();
            return mWriter.append(csq);
        }
    }

    @Override
    public Writer append
        (CharSequence csq,
         int start,
         int end)
        throws IOException
    {
        synchronized (lock) {
            init();
            return mWriter.append(csq,start,end);
        }
    }

    @Override
    public Writer append
        (char c)
        throws IOException
    {
        synchronized (lock) {
            init();
            return mWriter.append(c);
        }
    }

    @Override
    public void flush()
        throws IOException
    {
        synchronized (lock) {
            init();
            mWriter.flush();
        }
    }

    @Override
    public void close()
        throws IOException
    {
        synchronized (lock) {
            if (mStream==null) {
                return;
            }
            if (mWriter!=null) {
                mWriter.close();
            }
            mStream.close();
            mStream=null;
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's requested signature/charset. This pair
     * may have either been requested directly or by requesting that a
     * reader's actual signature/charset be matched.
     *
     * @return The requested signature/charset, which may be null if
     * none was requested.
     */

    public SignatureCharset getRequestedSignatureCharset()
    {
        return mRequestedSignatureCharset;
    }

    /**
     * Returns the receiver's actual signature/charset (that is, the
     * one in use to encode the stream).
     *
     * @return The signature/charset, which may be null if the default
     * JVM charset is used.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    public SignatureCharset getSignatureCharset()
        throws IOException
    {
        synchronized (lock) {
            init();
            return mSignatureCharset;
        }
    }
    
    /**
     * Initializes the receiver.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */

    private void init()
        throws IOException
    {
        if (mStream==null) {
            throw new IOException(Messages.STREAM_CLOSED.getText());
        }
        if (mWriter!=null) {
            return;
        }
        mSignatureCharset=getRequestedSignatureCharset();
        if ((mSignatureCharset!=null) && (!mSignatureCharset.isSupported())) {
            mSignatureCharset=null;
        }
        if (mSignatureCharset!=null) {
            if (mWriteSignature) {
                mStream.write(mSignatureCharset.getSignature().getMark());
            }
            mWriter=new OutputStreamWriter
                (mStream,mSignatureCharset.getCharset().getCharset());
        } else {
            mWriter=new OutputStreamWriter(mStream);
        }
    }
}

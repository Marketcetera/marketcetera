package org.marketcetera.util.unicode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A variation of {@link InputStreamReader} that is BOM-aware. It can
 * operate in any of the following modes:
 *
 * <ul>
 *
 * <li>As a standard input stream reader that uses the default JVM
 * charset.</li>
 *
 * <li>A reader that uses a specific charset and assumes a specific
 * signature is present in the input stream (and skips it without
 * confirming that it's actually present and valid).</li>
 *
 * <li>A reader that looks for a signature match among several
 * candidates, and thus automatically determines the charset.</li>
 *
 * </ul>
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class UnicodeInputStreamReader
    extends Reader
{

    // INSTANCE DATA.

    private PushbackInputStream mStream;
    private InputStreamReader mReader;
    private DecodingStrategy mDecodingStrategy;
    private SignatureCharset mRequestedSignatureCharset;
    private SignatureCharset mSignatureCharset;


    // CONSTRUCTORS.

    /**
     * Creates a new reader over the given stream that uses the
     * default JVM charset.
     *
     * @param stream The stream.
     */

    public UnicodeInputStreamReader
        (InputStream stream)
    {
        super(stream);
        mStream=new PushbackInputStream(stream,Signature.getLongestLength());
    }

    /**
     * Creates a new reader over the given stream that normally
     * assumes the given signature is present and its associated
     * charset should be used. However, if the charset in the given
     * signature/charset pair is not supported by the JVM, the default
     * JVM charset is used instead.
     *
     * @param stream The stream.
     * @param requestedSignatureCharset The signature/charset. It may
     * be null to use the default JVM charset.
     */

    public UnicodeInputStreamReader
        (InputStream stream,
         SignatureCharset requestedSignatureCharset)
    {
        this(stream);
        mRequestedSignatureCharset=requestedSignatureCharset;
    }

    /**
     * Creates a new reader over the given stream that normally uses
     * the charset associated with a matching signature among those
     * of the given decoding strategy. However, if no signature
     * matches, or if the charset of the matching signature is not
     * supported by the JVM, the default JVM charset is used instead.
     *
     * @param stream The stream.
     * @param decodingStrategy The decoding strategy. It may be null
     * to use the default JVM charset.
     */

    public UnicodeInputStreamReader
        (InputStream stream,
         DecodingStrategy decodingStrategy)
    {
        this(stream);
        mDecodingStrategy=decodingStrategy;
    }


    // Reader.

    @Override
    public int read
        (CharBuffer target)
        throws IOException
    {
        synchronized (lock) {
            init();
            return mReader.read(target);
        }
    }

    @Override
    public int read()
        throws IOException
    {
        synchronized (lock) {
            init();
            return mReader.read();
        }
    }

    @Override
    public int read
        (char[] cbuf)
        throws IOException
    {
        synchronized (lock) {
            init();
            return mReader.read(cbuf);
        }
    }

    @Override
    public int read
        (char[] cbuf,
         int off,
         int len)
        throws IOException
    {
        synchronized (lock) {
            init();
            return mReader.read(cbuf,off,len);
        }
    }

    @Override
    public long skip
        (long n)
        throws IOException
    {
        synchronized (lock) {
            init();
            return mReader.skip(n);
        }
    }

    @Override
    public boolean ready() 
        throws IOException
    {
        synchronized (lock) {
            if (mStream==null) {
                throw new IOException(Messages.STREAM_CLOSED.getText());
            }
            if (mReader==null) {
                return false;
            }
            return mReader.ready();
        }
    }

    @Override
    public boolean markSupported()
    {
        try {
            init();
        } catch (IOException ex) {
            Messages.STREAM_ACCESS_ERROR.error(this,ex);
            return false;
        }
        return mReader.markSupported();
    }

    @Override
    public void mark
        (int readAheadLimit)
        throws IOException
    {
        synchronized (lock) {
            init();
            mReader.mark(readAheadLimit);
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
            if (mReader!=null) {
                mReader.close();
            }
            mStream.close();
            mStream=null;
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's decoding strategy.
     *
     * @return The strategy, which may be null if none was specified.
     */

    public DecodingStrategy getDecodingStrategy()
    {
        return mDecodingStrategy;
    }

    /**
     * Returns the receiver's requested signature/charset.
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
     * one in use to decode the stream).
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
        if (mReader!=null) {
            return;
        }
        if (getDecodingStrategy()==null) {
            mSignatureCharset=getRequestedSignatureCharset();
        } else {
            byte[] consumed=new byte[Signature.getLongestLength()];
            int count=mStream.read(consumed);
            if (count==-1) {
                count=0;
            }
            byte[] header=Arrays.copyOf(consumed,count);
            mSignatureCharset=getDecodingStrategy().getPrefixMatch(header);
            mStream.unread(header);
        }
        if ((mSignatureCharset!=null) && (!mSignatureCharset.isSupported())) {
            mSignatureCharset=null;
        }
        if (mSignatureCharset!=null) {
            int len=mSignatureCharset.getSignature().getLength();
            long left=len;
            long skipped=1;
            while ((left>0) && (skipped>0)) {
                skipped=mStream.skip(left);
                left-=skipped;
            }
            mReader=new InputStreamReader
                (mStream,mSignatureCharset.getCharset().getCharset());
        } else {
            mReader=new InputStreamReader(mStream);
        }
    }
}

package org.marketcetera.util.unicode;

import java.nio.charset.Charset;
import org.apache.commons.lang.CharEncoding;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A thin wrapper around {@link Charset} for the Unicode charsets.
 *
 * <h4>Notes:</h4>
 *
 * <p>Java inserts a BOM when a string is encoded into a byte array
 * with the generic (neither LE or BE) UTF-16 charset. Java does not
 * do that for the generic UTF-32, or UTF-8. As a result, the generic
 * UTF-16 and UTF-32 are avoided by other classes in this package,
 * opting instead for the more consistent LE/BE variants that never
 * insert a BOM (and, upon decoding, ignore one if present).</p>
 *
 * <p>Since Java 5, strings are internally stored in UTF-16, not
 * UCS-2, which means that they can represent code points above
 * 0xFFFF.</p>
 *
 * <p>UTF-32 encodings may not be supported on all platforms.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum UnicodeCharset
{
    UTF8(CharEncoding.UTF_8),
    UTF16BE(CharEncoding.UTF_16BE),
    UTF16LE(CharEncoding.UTF_16LE),
    UTF16(CharEncoding.UTF_16),
    UTF32BE("UTF-32BE"), //$NON-NLS-1$
    UTF32LE("UTF-32LE"), //$NON-NLS-1$
    UTF32("UTF-32"); //$NON-NLS-1$


    // INSTANCE DATA.

    private final String mName;
    private final Charset mCharset;


    // CONSTRUCTORS.

    /**
     * Creates a new charset with the given name.
     *
     * @param name The charset name as understood by {@link
     * Charset.forName(String)}.
     */

    UnicodeCharset(String name)
    {
        mName=name;
        Charset charset=null;
        try {
            charset=Charset.forName(getName());
        } catch (IllegalArgumentException ex) {
            Messages.UNKNOWN_CHARSET.warn(this,ex,getName());
        }
        mCharset=charset;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    public String getName()
    {
        return mName;
    }

    /**
     * Returns the receiver's wrapped Java charset.
     *
     * @return The charset. It is null if the JVM does not support
     * this charset.
     */

    public Charset getCharset()
    {
        return mCharset;
    }

    /**
     * Checks whether the JVM supports the receiver's charset.
     *
     * @return True if so.
     */

    public boolean isSupported()
    {
        return (getCharset()!=null);
    }

    /**
     * Asserts that the JVM supports the receiver's charset.
     *
     * @throws I18NException Thrown if it does not.
     */

    public void assertSupported()
        throws I18NException
    {
        if (!isSupported()) {
            throw new I18NException
                (new I18NBoundMessage1P
                 (Messages.UNKNOWN_CHARSET,getName()));
        }
    }

    /**
     * Decodes the given portion of the given byte array using the
     * receiver's charset, and returns the result.
     *
     * @param data The byte array, which may be null.
     * @param offset The starting point for decoding.
     * @param length The number of bytes to decode.
     *
     * @return The decoded string; it is null if the given byte array
     * is null.
     *
     * @throws I18NException Thrown if the receiver is not a supported
     * JVM charset.
     */

    public String decode
        (byte[] data,
         int offset,
         int length)
        throws I18NException
    {
        if (data==null) {
            return null;
        }
        assertSupported();
        return new String(data,offset,length,getCharset());
    }

    /**
     * Decodes the given byte array using the receiver's charset, and
     * returns the result.
     *
     * @param data The byte array, which may be null.
     *
     * @return The decoded string; it is null if the given byte array
     * is null.
     *
     * @throws I18NException Thrown if the receiver is not a supported
     * JVM charset.
     */

    public String decode
        (byte[] data)
        throws I18NException
    {
        if (data==null) {
            return null;
        }
        assertSupported();
        return new String(data,getCharset());
    }

    /**
     * Encodes the given string using the receiver's charset, and
     * returns the result.
     *
     * @param data The string, which may be null.
     *
     * @return The encoded byte array; it is null if the given string
     * is null.
     *
     * @throws I18NException Thrown if the receiver is not a supported
     * JVM charset.
     */

    public byte[] encode
        (String data)
        throws I18NException
    {
        if (data==null) {
            return null;
        }
        assertSupported();
        return data.getBytes(getCharset());
    }
}

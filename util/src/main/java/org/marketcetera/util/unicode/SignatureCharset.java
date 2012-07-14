package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A byte stream signature ({@link Signature}) coupled with a charset
 * ({@link UnicodeCharset}) that may follow the signature (aka a
 * signature/charset pair). Observe that, in the absence of a
 * signature ({@link Signature#NONE}), many different charsets are
 * allowed. Instances of this class enforce signature removal upon
 * decoding and insertion upon charset; there is no attempt made to
 * identify matching signatures, as done by {@link Serialization}.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum SignatureCharset
{
    NONE_UTF8(Signature.NONE,UnicodeCharset.UTF8),
    NONE_UTF16BE(Signature.NONE,UnicodeCharset.UTF16BE),
    NONE_UTF16LE(Signature.NONE,UnicodeCharset.UTF16LE),
    NONE_UTF32BE(Signature.NONE,UnicodeCharset.UTF32BE),
    NONE_UTF32LE(Signature.NONE,UnicodeCharset.UTF32LE),
    UTF8_UTF8(Signature.UTF8,UnicodeCharset.UTF8),
    UTF16BE_UTF16BE(Signature.UTF16BE,UnicodeCharset.UTF16BE),
    UTF16LE_UTF16LE(Signature.UTF16LE,UnicodeCharset.UTF16LE),
    UTF32BE_UTF32BE(Signature.UTF32BE,UnicodeCharset.UTF32BE),
    UTF32LE_UTF32LE(Signature.UTF32LE,UnicodeCharset.UTF32LE);


    // INSTANCE DATA.

    private final Signature mSignature;
    private final UnicodeCharset mCharset;


    // CONSTRUCTORS.

    /**
     * Creates a new signature/charset pair with the given signature
     * and charset.
     *
     * @param signature The signature.
     * @param charset The charset.
     */

    SignatureCharset
        (Signature signature,
         UnicodeCharset charset)
    {
        mSignature=signature;
        mCharset=charset;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's signature.
     *
     * @return The signature.
     */

    public Signature getSignature()
    {
        return mSignature;
    }

    /**
     * Returns the receiver's charset.
     *
     * @return The charset.
     */

    public UnicodeCharset getCharset()
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
        return getCharset().isSupported();
    }

    /**
     * Asserts that the JVM supports the receiver's charset.
     *
     * @throws I18NException Thrown if it does not.
     */

    public void assertSupported()
        throws I18NException
    {
        getCharset().assertSupported();
    }

    /**
     * Decodes the given byte array using the receiver's charset, and
     * returns the result. The receiver's signature BOM is assumed to
     * be present in the header of the byte array, and is removed
     * (without a check performed to confirm its presence).
     *
     * @param data The byte array, which may be null.
     *
     * @return The decoded string; it is null if the given byte array
     * is null.
     *
     * @throws I18NException Thrown if the receiver's charset is not a
     * supported JVM charset.
     */

    public String decode
        (byte[] data)
        throws I18NException
    {
        if (data==null) {
            return null;
        }
        assertSupported();
        int len=getSignature().getLength();
        return getCharset().decode(data,len,data.length-len);
    }

    /**
     * Encodes the given string using the receiver's charset, and
     * returns the result. The receiver's signature BOM is
     * <em>always</em> prepended to the header of the returned byte
     * array.
     *
     * @param data The string, which may be null.
     *
     * @return The encoded byte array; it is null if the given string
     * is null.
     *
     * @throws I18NException Thrown if the receiver's charset is not a
     * supported JVM charset.
     */

    public byte[] encode
        (String data)
        throws I18NException
    {
        if (data==null) {
            return null;
        }
        assertSupported();
        return ArrayUtils.addAll
            (getSignature().getMark(),getCharset().encode(data));
    }
}

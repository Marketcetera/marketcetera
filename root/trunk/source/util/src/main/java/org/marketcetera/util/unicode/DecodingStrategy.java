package org.marketcetera.util.unicode;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A list of one or more {@link Serialization} instances. This list
 * (indirectly) defines a sequence of signatures that we attempt to
 * match (in order) against a byte array header, and the associated
 * charset we should use if a match is found.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum DecodingStrategy
{
    UTF8_DEFAULT(new Serialization[]
        {Serialization.UTF8N,
         Serialization.UTF8,
         Serialization.UTF16,
         Serialization.UTF32}),
    UTF16_DEFAULT(new Serialization[]
        {Serialization.UTF16,
         Serialization.UTF8,
         Serialization.UTF32}),
    UTF32_DEFAULT(new Serialization[]
        {Serialization.UTF32,
         Serialization.UTF8,
         Serialization.UTF16}),
    SIG_REQ(new Serialization[]
        {Serialization.UTF8,
         Serialization.UTF16BE_REQ,
         Serialization.UTF16LE_REQ,
         Serialization.UTF32BE_REQ,
         Serialization.UTF32LE_REQ});


    // INSTANCE DATA.

    private final Serialization[] mSerializations;


    // CONSTRUCTORS.

    /**
     * Creates a new charset with the given serializations.
     *
     * @param serializations The serializations.
     */

    DecodingStrategy
        (Serialization[] serializations)
    {
        mSerializations=serializations;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's serializations.
     *
     * @return The serializations.
     */

    public Serialization[] getSerializations()
    {
        return mSerializations;
    }

    /**
     * Checks whether any of the signatures among the
     * signature/charset pairs within the receiver's serializations
     * matches the header of the given byte array, and returns the
     * matching pair.
     *
     * @param data The byte array.
     *
     * @return The matching signature/charset pair, or null if no
     * serialization contains a match. If more than one
     * signature/charset pair is a match, the one with the longest
     * signature is returned; and if there is more than one with the
     * same length, the first such match is returned.
     */

    public SignatureCharset getPrefixMatch
        (byte[] data)
    {
        return Serialization.getPrefixMatch(getSerializations(),data);
    }

    /**
     * Decodes the given byte array using the charset paired to a
     * signature (among the signature/charset pairs within the
     * receiver's serializations) that matches the array header, and
     * returns the result.
     *
     * @param data The byte array, which may be null.
     *
     * @return The decoded string; it is null if the given byte array
     * is null.
     *
     * @throws I18NException Thrown if no match can be found, or if
     * the JVM does not support the charset of the matching
     * signature/charset pair.
     */

    public String decode
        (byte[] data)
        throws I18NException
    {
        if (data==null) {
            return null;
        }
        SignatureCharset sc=getPrefixMatch(data);
        if (sc==null) {
            throw new I18NException(Messages.NO_SIGNATURE_MATCHES);
        }
        return sc.decode(data);
    }
}

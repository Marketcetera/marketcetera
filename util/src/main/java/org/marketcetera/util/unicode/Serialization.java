package org.marketcetera.util.unicode;

import java.util.EnumSet;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A list of signature/charset pairs. This list defines a sequence of
 * signatures that we attempt to match (in order) against a byte array
 * header, and the associated charset we use if a match is found. Most
 * instances of this class correspond to the entries in Table 2 of <a
 * href="http://www.icu-project.org/docs/papers/forms_of_unicode/">this
 * reference document</a>.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum Serialization
{
    UTF8(SignatureCharset.UTF8_UTF8),
    UTF8N(SignatureCharset.NONE_UTF8),
    UTF16(new SignatureCharset[]
        {SignatureCharset.NONE_UTF16BE,
         SignatureCharset.UTF16BE_UTF16BE,
         SignatureCharset.UTF16LE_UTF16LE}),
    UTF16BE(SignatureCharset.NONE_UTF16BE),
    UTF16LE(SignatureCharset.NONE_UTF16LE),
    UTF32(new SignatureCharset[]
        {SignatureCharset.NONE_UTF32BE,
         SignatureCharset.UTF32BE_UTF32BE,
         SignatureCharset.UTF32LE_UTF32LE}),
    UTF32BE(SignatureCharset.NONE_UTF32BE),
    UTF32LE(SignatureCharset.NONE_UTF32LE),

    UTF16BE_REQ(SignatureCharset.UTF16BE_UTF16BE),
    UTF16LE_REQ(SignatureCharset.UTF16LE_UTF16LE),
    UTF32BE_REQ(SignatureCharset.UTF32BE_UTF32BE),
    UTF32LE_REQ(SignatureCharset.UTF32LE_UTF32LE);


    // INSTANCE DATA.

    private final SignatureCharset[] mSignatureCharsets;


    // CONSTRUCTORS.

    /**
     * Creates a new serialization with only the given
     * signature/charset pair.
     *
     * @param signatureCharset The pair.
     */

    Serialization
        (SignatureCharset signatureCharset)
    {
        mSignatureCharsets=new SignatureCharset[] {signatureCharset};
    }

    /**
     * Creates a new serialization with the given signature/charset
     * pairs.
     *
     * @param signatureCharsets The pairs.
     */

    Serialization
        (SignatureCharset[] signatureCharsets)
    {
        mSignatureCharsets=signatureCharsets;
    }


    // CLASS METHODS.

    /**
     * Checks whether any of the signatures among the pairs of the
     * given serializations matches the header of the given byte
     * array, and returns the matching signature/charset pair.
     *
     * @param candidates The serializations.
     * @param data The byte array.
     *
     * @return The matching signature/charset pair, or null if no
     * candidate contains a matching pair. If more than one
     * signature/charset pair is a match, the one with the longest
     * signature is returned; and if there is more than one with the
     * same length, the first such match is returned.
     */

    public static SignatureCharset getPrefixMatch
        (Serialization[] candidates,
         byte[] data)
    {
        EnumSet<Signature> signatures=EnumSet.noneOf(Signature.class);
        for (Serialization serialization:candidates) {
            for (SignatureCharset sc:serialization.getSignatureCharsets()) {
                signatures.add(sc.getSignature());
            }
        }
        Signature match=Signature.getPrefixMatch
            (signatures.toArray(Signature.EMPTY_ARRAY),data);
        for (Serialization serialization:candidates) {
            for (SignatureCharset sc:serialization.getSignatureCharsets()) {
                if (sc.getSignature()==match) {
                    return sc;
                }
            }
        }
        return null;
    }

    /**
     * Decodes the given byte array using the charset paired to a
     * signature (among the pairs of the given serializations) that
     * matches the array header, and returns the result.
     *
     * @param candidates The serializations.
     * @param data The byte array, which may be null.
     *
     * @return The decoded string; it is null if the given byte array
     * is null.
     *
     * @throws I18NException Thrown if no match can be found, or if
     * the JVM does not support the charset of the matching
     * signature/charset pair.
     */

    public static String decode
        (Serialization[] candidates,
         byte[] data)
        throws I18NException
    {
        if (data==null) {
            return null;
        }
        SignatureCharset sc=getPrefixMatch(candidates,data);
        if (sc==null) {
            throw new I18NException(Messages.NO_SIGNATURE_MATCHES);
        }
        return sc.decode(data);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's signature/charset pairs.
     *
     * @return The pairs.
     */

    public SignatureCharset[] getSignatureCharsets()
    {
        return mSignatureCharsets;
    }
}

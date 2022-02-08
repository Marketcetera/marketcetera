package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A byte stream signature. It appears in the beginning (header) of a
 * byte stream, and identifies the charset necessary to interpret the
 * remaining bytes as text.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum Signature
{
    NONE(ArrayUtils.EMPTY_BYTE_ARRAY),
    UTF8(new byte[] {(byte)0xEF,(byte)0xBB,(byte)0xBF}),
    UTF16BE(new byte[] {(byte)0xFE,(byte)0xFF}),
    UTF16LE(new byte[] {(byte)0xFF,(byte)0xFE}),
    UTF32BE(new byte[] {(byte)0x00,(byte)0x00,(byte)0xFE,(byte)0xFF}),
    UTF32LE(new byte[] {(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00});


    // CLASS DATA.

    public static final Signature[] EMPTY_ARRAY=new Signature[0];


    // INSTANCE DATA.

    private final byte[] mMark;


    // CONSTRUCTORS.

    /**
     * Creates a new signature with the given BOM.
     *
     * @param mark The BOM.
     */

    Signature
        (byte[] mark)
    {
        mMark=mark;
    }


    // CLASS METHODS.

    /**
     * Returns the maximum length of any signature BOM.
     *
     * @return The length.
     */

    public static int getLongestLength()
    {
		int max=-1;
        for (Signature signature:values()) {
            int len=signature.getLength();
            if (len>max) {
                max=len;
            }
        }
        return max;
    }

    /**
     * Checks whether any of the given signatures matches the header
     * of the given byte array.
     *
     * @param candidates The signatures.
     * @param data The byte array.
     *
     * @return The matching signature, or null if no candidate is a
     * match. If more than one candidate is a match, the one with the
     * longest signature is returned; and if there is more than one
     * with the same length, the first such match is returned.
     */

    public static Signature getPrefixMatch
        (Signature[] candidates,
         byte[] data)
    {
		int max=-1;
        Signature result=null;
        for (Signature signature:candidates) {
            if (signature.prefixMatch(data)) {
                int len=signature.getLength();
                if (len>max) {
                    result=signature;
                    max=len;
                }
            }
        }
        return result;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's BOM.
     *
     * @return The BOM.
     */

    public byte[] getMark()
    {
        return mMark;
    }

    /**
     * Returns the receiver's BOM length.
     *
     * @return The BOM length.
     */

    public int getLength()
    {
        return getMark().length;
    }

    /**
     * Checks whether the receiver's BOM matches the header of the
     * given byte array.
     *
     * @param data The byte array.
     *
     * @return True if so.
     */

    public boolean prefixMatch
        (byte[] data)
    {
        if (data.length<getLength()) {
            return false;
        }
        for (int i=0;i<getLength();i++) {
            if (data[i]!=getMark()[i]) {
                return false;
            }
        }
        return true;
    }
}

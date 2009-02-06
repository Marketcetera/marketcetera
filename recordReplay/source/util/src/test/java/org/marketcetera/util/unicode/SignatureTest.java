package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.unicode.Signature.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class SignatureTest
    extends TestCaseBase
{
    private static void single
        (Signature[] allCandidates,
         Signature signature,
         int length)
    {
        assertNotNull(signature.getMark());
        assertEquals(length,signature.getLength());

        byte[] data=signature.getMark();
        assertTrue(signature.prefixMatch(data));
        Signature[] candidates=new Signature[] {signature};
        assertEquals(signature,getPrefixMatch(candidates,data));
        assertEquals(signature,getPrefixMatch(allCandidates,data));

        data=ArrayUtils.addAll(data,new byte[] {(byte)0x01,(byte)0x02});
        assertTrue(signature.prefixMatch(data));
        assertEquals(signature,getPrefixMatch(candidates,data));
        assertEquals(signature,getPrefixMatch(allCandidates,data));

        data[0]=(byte)0x01;
        assertEquals((length==0),signature.prefixMatch(data));
        assertEquals((length==0)?signature:null,getPrefixMatch
                     (candidates,data));
        assertEquals(NONE,getPrefixMatch(allCandidates,data));

        data=new byte[] {(byte)0x01};
        assertEquals((length==0),signature.prefixMatch(data));
        assertEquals((length==0)?signature:null,getPrefixMatch
                     (candidates,data));
        assertEquals(NONE,getPrefixMatch(allCandidates,data));

        data=new byte[] {(byte)0x00,(byte)0x00};
        assertEquals((length==0),signature.prefixMatch(data));
        assertEquals((length==0)?signature:null,getPrefixMatch
                     (candidates,data));
        assertEquals(NONE,getPrefixMatch(allCandidates,data));
    }

    private static void multi
        (Signature signature,
         int length)
    {
        single(new Signature[] {
                NONE,UTF8,UTF16BE,UTF16LE,UTF32BE,UTF32LE},signature,length);
        single(new Signature[] {
                UTF8,UTF16BE,UTF16LE,UTF32BE,UTF32LE,NONE},signature,length);
        single(new Signature[] {
                UTF16LE,UTF32LE,UTF16BE,UTF32BE,UTF8,NONE},signature,length);
    }


    @Test
    public void all()
    {
        multi(NONE,0);
        multi(UTF8,3);
        multi(UTF16BE,2);
        multi(UTF16LE,2);
        multi(UTF32BE,4);
        multi(UTF32LE,4);
    }

    @Test
    public void multiMatch()
    {
        byte[] data=UTF8.getMark();
        assertNull(getPrefixMatch(Signature.EMPTY_ARRAY,data));
        assertNull(getPrefixMatch(new Signature[]
            {UTF16BE,UTF16LE,UTF32BE,UTF32LE},data));
        assertEquals(NONE,getPrefixMatch(new Signature[]
            {NONE,UTF16BE,UTF16LE,UTF32BE,UTF32LE},data));
    }

    @Test
    public void longestSignature()
    {
        assertEquals(4,Signature.getLongestLength());
    }
}

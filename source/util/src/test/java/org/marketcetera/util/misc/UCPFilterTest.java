package org.marketcetera.util.misc;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.unicode.UnicodeCharset;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class UCPFilterTest
    extends TestCaseBase
{
    @Test
    public void all()
    {
        assertTrue(UCPFilter.VALID.isAcceptable(' '));
        assertFalse(UCPFilter.VALID.isAcceptable(0xD800));

        assertTrue(UCPFilter.CHAR.isAcceptable(' '));
        assertTrue(UCPFilter.CHAR.isAcceptable(0xFFFF));
        assertFalse(UCPFilter.CHAR.isAcceptable(0x10000));

        assertTrue(UCPFilter.DIGIT.isAcceptable('1'));
        assertFalse(UCPFilter.DIGIT.isAcceptable('a'));

        assertTrue(UCPFilter.LETTER.isAcceptable('a'));
        assertFalse(UCPFilter.LETTER.isAcceptable('1'));

        assertTrue(UCPFilter.ALNUM.isAcceptable('a'));
        assertTrue(UCPFilter.ALNUM.isAcceptable('1'));
        assertFalse(UCPFilter.ALNUM.isAcceptable(' '));

        UCPFilter dc=UCPFilter.getDefaultCharset();
        assertSame(dc,UCPFilter.getDefaultCharset());
        assertTrue(dc.isAcceptable('a'));
        assertFalse(dc.isAcceptable(0xD800));

        UCPFilter fs=UCPFilter.getFileSystemCharset();
        assertSame(fs,UCPFilter.getFileSystemCharset());
        assertTrue(fs.isAcceptable('a'));
        assertFalse(fs.isAcceptable(0xD800));

        UCPFilter utf8=UCPFilter.forCharset(UnicodeCharset.UTF8.getCharset());
        assertSame(utf8,UCPFilter.forCharset(UnicodeCharset.UTF8.getCharset()));
        assertTrue(utf8.isAcceptable('a'));
        assertFalse(utf8.isAcceptable(0xD800));
    }
}

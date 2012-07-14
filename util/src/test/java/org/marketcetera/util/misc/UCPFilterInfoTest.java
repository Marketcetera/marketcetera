package org.marketcetera.util.misc;

import java.util.Arrays;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class UCPFilterInfoTest
    extends TestCaseBase
{
    private static final UCPFilter FILTER=
        new UCPFilter()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return ((ucp>=0x100) && (ucp<=0x102));
            }
        };

    @Test
    public void all()
    {
        UCPFilterInfo info=UCPFilterInfo.getInfo(FILTER);
        assertSame(info,UCPFilterInfo.getInfo(FILTER));
        assertArrayEquals(new int[] {0x100,0x101,0x102},info.getUCPs());

        info=UCPFilterInfo.getInfo(UCPFilter.DIGIT);
        assertSame(info,UCPFilterInfo.getInfo(UCPFilter.DIGIT));
        assertTrue(Arrays.binarySearch(info.getUCPs(),'1')>=0);
        assertTrue(Arrays.binarySearch(info.getUCPs(),'2')>=0);
        assertTrue(Arrays.binarySearch(info.getUCPs(),'a')<0);

        assertEquals
            (UCPFilterInfo.getInfo(UCPFilter.ALNUM).getUCPs().length,
             UCPFilterInfo.getInfo(UCPFilter.DIGIT).getUCPs().length+
             UCPFilterInfo.getInfo(UCPFilter.LETTER).getUCPs().length);
    }
}

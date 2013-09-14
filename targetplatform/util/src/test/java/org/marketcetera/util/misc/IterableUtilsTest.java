package org.marketcetera.util.misc;

import java.util.LinkedList;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class IterableUtilsTest
    extends TestCaseBase
{
    @Test
    public void basics()
    {
        LinkedList<Integer> list=new LinkedList<Integer>();
        assertEquals(0,IterableUtils.size(list));
        assertArrayEquals(ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY,
                          IterableUtils.toArray(list));

        list.add(1);
        list.add(2);
        assertEquals(2,IterableUtils.size(list));
        assertArrayEquals(new Integer[] {1,2},
                          IterableUtils.toArray(list));
    }
}

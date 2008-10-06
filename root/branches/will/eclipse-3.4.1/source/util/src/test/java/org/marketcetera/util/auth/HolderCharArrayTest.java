package org.marketcetera.util.auth;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class HolderCharArrayTest
    extends HolderTestBase
{
    private static final String TEST_VALUE_STR=
        "abc";
    private static final char[] TEST_VALUE=
        TEST_VALUE_STR.toCharArray();
    private static final char[] NUL_VALUE=
        StringUtils.repeat("\0",TEST_VALUE.length).toCharArray();


    private static void getAsString
        (HolderCharArray holder)
    {
        assertNull(holder.getValueAsString());

        holder.setValue(TEST_VALUE.clone());
        assertEquals(TEST_VALUE_STR,holder.getValueAsString());

        holder.setValue(null);
        assertNull(holder.getValueAsString());
    }

    private static void clear
        (HolderCharArray holder)
    {
        char[] valueCopy=TEST_VALUE.clone();
        holder.setValue(valueCopy);
        holder.clear();
        assertNull(holder.getValue());
        assertNull(holder.getValueAsString());
        assertArrayEquals(NUL_VALUE,valueCopy);
        holder.clear();
        assertNull(holder.getValue());
        assertNull(holder.getValueAsString());

        valueCopy=TEST_VALUE.clone();
        holder.setValue(valueCopy);
        holder.setValue(null);
        assertArrayEquals(NUL_VALUE,valueCopy);
        holder.clear();
        assertNull(holder.getValue());
        assertNull(holder.getValueAsString());

        valueCopy=TEST_VALUE.clone();
        holder.setValue(valueCopy);
        holder.setValue(ArrayUtils.EMPTY_CHAR_ARRAY);
        assertArrayEquals(NUL_VALUE,valueCopy);
        holder.clear();
        assertNull(holder.getValue());
        assertNull(holder.getValueAsString());
    }


    @Test
    public void basics()
    {
        char[] testValueClone=TEST_VALUE.clone();

        HolderCharArray holder=new HolderCharArray();
        simpleNoMessage(holder,TEST_VALUE.clone());
        clear(holder);
        getAsString(new HolderCharArray());

        holder=new HolderCharArray(TestMessages.TEST_MESSAGE);
        simpleWithMessage(holder,TEST_VALUE.clone());
        clear(holder);
        getAsString(new HolderCharArray(TestMessages.TEST_MESSAGE));        

        assertArrayEquals(testValueClone,TEST_VALUE);
    }
}

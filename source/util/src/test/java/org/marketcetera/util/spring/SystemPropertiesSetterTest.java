package org.marketcetera.util.spring;

import java.util.HashMap;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class SystemPropertiesSetterTest
    extends TestCaseBase
{
    private static final String TEST_PREFIX_NAME=
        "testName";
    private static final String TEST_PREFIX_VALUE=
        "testValue";

    private static final String TEST_PROP_A=
        "propA";
    private static final String TEST_PROP_A_NAME=
        TEST_PREFIX_NAME+"."+TEST_PROP_A;
    private static final String TEST_PROP_A_VALUE=
        TEST_PREFIX_VALUE+"."+TEST_PROP_A;

    private static final String TEST_PROP_B=
        "propB";
    private static final String TEST_PROP_B_NAME=
        TEST_PREFIX_NAME+"."+TEST_PROP_B;
    private static final String TEST_PROP_B_VALUE=
        TEST_PREFIX_VALUE+"."+TEST_PROP_B;


    @Test
    public void set()
    {
        SystemPropertiesSetter setter=new SystemPropertiesSetter();
        HashMap<String,String> map=new HashMap<String,String>();
        map.put(TEST_PROP_A_NAME,TEST_PROP_A_VALUE);
        map.put(TEST_PROP_B_NAME,TEST_PROP_B_VALUE);
        setter.setMap(map);
        assertEquals(TEST_PROP_A_VALUE,System.getProperty(TEST_PROP_A_NAME));
        assertEquals(TEST_PROP_B_VALUE,System.getProperty(TEST_PROP_B_NAME));

        map=new HashMap<String,String>();
        map.put(TEST_PROP_A_NAME,TEST_PROP_B_VALUE);
        setter.setMap(map);
        assertEquals(TEST_PROP_B_VALUE,System.getProperty(TEST_PROP_A_NAME));
        assertEquals(TEST_PROP_B_VALUE,System.getProperty(TEST_PROP_B_NAME));

        map=new HashMap<String,String>();
        map.put(TEST_PROP_A_NAME,null);
        setter.setMap(map);
        assertNull(System.getProperty(TEST_PROP_A_NAME));
    }
}

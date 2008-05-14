package org.marketcetera.util.spring;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.context.support.StaticApplicationContext;

import static org.junit.Assert.*;

public class SpringUtilsTest
	extends TestCaseBase
{
    private static final String TEST_NAME=
        "testName";
    private static final String TEST_VALUE=
        "testValue";


    @Test
    public void setValue()
    {
		StaticApplicationContext context=new StaticApplicationContext();
        SpringUtils.addStringBean(context,TEST_NAME,TEST_VALUE);
        assertEquals(TEST_VALUE,context.getBean(TEST_NAME));
    }
}

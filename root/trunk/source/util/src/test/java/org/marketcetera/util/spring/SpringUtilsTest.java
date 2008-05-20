package org.marketcetera.util.spring;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.Assert.*;

public class SpringUtilsTest
	extends TestCaseBase
{
    private static final String TEST_NAME_BEAN=
        "testNameBean";
    private static final String TEST_VALUE_BEAN=
        "testValueBean";
    private static final String TEST_NAME_PROP=
        "testNameProp";
    private static final String TEST_VALUE_PROP=
        "testValueProp";
    private static final String TEST_NAME_CONFIGURER=
        "testConfigurer";
    private static final String TEST_NAME_RESOURCE_BEAN=
        "testResource";
    private static final String TEST_RESOURCES_FILE=
        "spring.properties";


    @Test
    public void stringBean()
    {
		GenericApplicationContext context=new GenericApplicationContext();
        SpringUtils.addStringBean(context,TEST_NAME_BEAN,TEST_VALUE_BEAN);
        assertEquals(TEST_VALUE_BEAN,context.getBean(TEST_NAME_BEAN));
    }

    @Test
    public void propertiesConfigurer()
    {
		GenericApplicationContext context=new GenericApplicationContext();
        SpringUtils.addStringBean
            (context,TEST_NAME_RESOURCE_BEAN,"classpath:"+TEST_RESOURCES_FILE);
        SpringUtils.addPropertiesConfigurer
            (context,TEST_NAME_CONFIGURER,TEST_NAME_RESOURCE_BEAN);
        SpringUtils.addStringBean
            (context,TEST_NAME_BEAN,"${"+TEST_NAME_PROP+"}");
        context.refresh();
        assertEquals(TEST_VALUE_PROP,context.getBean(TEST_NAME_BEAN));
    }
}

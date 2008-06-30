package org.marketcetera.util.spring;

import java.io.File;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class SpringUtilsTest
    extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"spring"+File.separator;
    private static final String TEST_NAME_BEAN=
        "testNameBean";
    private static final String TEST_VALUE=
        "testValue";
    private static final String TEST_NAME_PROP=
        "testNameProp";
    private static final String TEST_NAME_PROP_BLANK=
        "testNamePropBlank";
    private static final String TEST_VALUE_PROP=
        "testValueProp";
    private static final String TEST_VALUE_PROP_OVERRIDE=
        "testValuePropOverride";
    private static final String TEST_CONFIGURER_BEAN=
        "testConfigurer";
    private static final String TEST_SPRING_FILE=
        TEST_ROOT+"spring.xml";
    private static final String TEST_PROPERTIES_FILE_BEAN=
        "propertiesFile";
    private static final String TEST_PROPERTIES_FILES_BEAN=
        "propertiesFiles";


    @Test
    public void stringBean()
    {
        GenericApplicationContext context=new GenericApplicationContext();
        SpringUtils.addStringBean(context,TEST_NAME_BEAN,TEST_VALUE);
        assertEquals(TEST_VALUE,context.getBean(TEST_NAME_BEAN));
    }

    @Test
    public void propertiesConfigurerStringSet()
    {
        GenericApplicationContext context=
            new GenericApplicationContext
            (new FileSystemXmlApplicationContext(TEST_SPRING_FILE));
        SpringUtils.addPropertiesConfigurer
            (context,TEST_CONFIGURER_BEAN,TEST_PROPERTIES_FILE_BEAN);
        SpringUtils.addStringBean
            (context,TEST_NAME_BEAN,"${"+TEST_NAME_PROP+"}");
        context.refresh();
        assertEquals(TEST_VALUE_PROP,context.getBean(TEST_NAME_BEAN));
    }

    @Test
    public void propertiesConfigurerStringUnset()
    {
        GenericApplicationContext context=
            new GenericApplicationContext
            (new FileSystemXmlApplicationContext(TEST_SPRING_FILE));
        SpringUtils.addPropertiesConfigurer
            (context,TEST_CONFIGURER_BEAN,TEST_PROPERTIES_FILE_BEAN);
        SpringUtils.addStringBean
            (context,TEST_NAME_BEAN,"${"+TEST_NAME_PROP_BLANK+"}");
        context.refresh();
        assertEquals(StringUtils.EMPTY,context.getBean(TEST_NAME_BEAN));
    }

    @Test
    public void propertiesConfigurerList()
    {
        GenericApplicationContext context=
            new GenericApplicationContext
            (new FileSystemXmlApplicationContext(TEST_SPRING_FILE));
        SpringUtils.addPropertiesConfigurer
            (context,TEST_CONFIGURER_BEAN,TEST_PROPERTIES_FILES_BEAN);
        SpringUtils.addStringBean
            (context,TEST_NAME_BEAN,"${"+TEST_NAME_PROP+"}");
        context.refresh();
        assertEquals(TEST_VALUE_PROP_OVERRIDE,context.getBean(TEST_NAME_BEAN));
    }
}

package org.marketcetera.util.auth;

import java.io.File;
import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage;
import org.springframework.context.support.GenericApplicationContext;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class SetterContextSpringTest
    extends SetterContextTestBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"auth"+File.separator;
    private static final String TEST_SPRING=
        "Spring framework";
    private static final String TEST_PROPERTY_NAME=
        "propertyName";
    private static final String TEST_CONFIG_LOCATION=
        "configLocation";
    private static final String TEST_PROPERTIES_FILES=
        "propertiesFiles";

    private static class TestSetter<T extends Holder<?>>
        extends SpringSetter<T>
    {
        public TestSetter
            (T holder,
             I18NBoundMessage usage,
             String propertyName)
        {
            super(holder,usage,propertyName);
        }

        public void setValue
            (GenericApplicationContext context) {}
    }

    private static <T> void setter
        (SpringSetter<Holder<T>> setter,
         Holder<T> holder)
    {
        simpleSetter(setter,holder);
        assertEquals(TEST_PROPERTY_NAME,setter.getPropertyName());
    }


    @Test
    public void setterBasics()
        throws Exception
    {
        setter(new TestSetter<Holder<String>>
               (TEST_STRING_HOLDER,TestMessages.TEST_USAGE,
                TEST_PROPERTY_NAME),
               TEST_STRING_HOLDER);
        setter(new SpringSetterString
               (TEST_STRING_HOLDER,TestMessages.TEST_USAGE,
                TEST_PROPERTY_NAME),
               TEST_STRING_HOLDER);
        setter(new SpringSetterCharArray
               (TEST_CHAR_ARRAY_HOLDER,TestMessages.TEST_USAGE,
                TEST_PROPERTY_NAME),
               TEST_CHAR_ARRAY_HOLDER);
    }

    @Test
    public void contextBasics()
        throws Exception
    {
        Holder<String> holder=new Holder<String>();
        SpringSetter<Holder<String>> setter=
            new TestSetter<Holder<String>>
               (holder,TestMessages.TEST_USAGE,TEST_PROPERTY_NAME);

        simpleContextWithName
            (new SpringContext(true,null,null),
             setter,true,true,Messages.SPRING_NAME,TEST_SPRING);
        simpleContextWithName
            (new SpringContext(false,null,null),
             setter,false,true,Messages.SPRING_NAME,TEST_SPRING);
        simpleContextWithName
            (new SpringContext(TestMessages.TEST_CONTEXT,true,null,null),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new SpringContext(TestMessages.TEST_CONTEXT,false,null,null),
             setter,false,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);

        holder.setValue("x");
        simpleContextWithName
            (new SpringContext(true,null,null),
             setter,true,true,Messages.SPRING_NAME,TEST_SPRING);
        simpleContextWithName
            (new SpringContext(false,null,null),
             setter,false,false,Messages.SPRING_NAME,TEST_SPRING);
        simpleContextWithName
            (new SpringContext(TestMessages.TEST_CONTEXT,true,null,null),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new SpringContext(TestMessages.TEST_CONTEXT,false,null,null),
             setter,false,false,TestMessages.TEST_CONTEXT,TEST_CONTEXT);

        SpringContext context=new SpringContext
            (true,TEST_CONFIG_LOCATION,TEST_PROPERTIES_FILES);
        assertEquals
            (TEST_CONFIG_LOCATION,context.getConfigLocation());
        assertEquals
            (TEST_PROPERTIES_FILES,context.getPropertiesFilesBean());
    }

    @Test
    public void setValues()
        throws Exception
    {
        Holder<String> userHolder=
            new Holder<String>(Messages.NO_USER);
        HolderCharArray passwordHolder=
            new HolderCharArray(Messages.NO_PASSWORD);

        SpringSetterString userSetter=new SpringSetterString
            (userHolder,new I18NBoundMessage1P
             (Messages.USER_SPRING_USAGE,"metc.amq.user"),
             "metc.amq.user");
        SpringSetterCharArray passwordSetter=new SpringSetterCharArray
            (passwordHolder,new I18NBoundMessage1P
             (Messages.PASSWORD_SPRING_USAGE,"metc.amq.password"),
             "metc.amq.password");

        SpringContext context=new SpringContext
            (false,TEST_ROOT+"auth_none.xml",TEST_PROPERTIES_FILES);
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertNull(userHolder.getValue());
        assertNull(passwordHolder.getValueAsString());

        context=new SpringContext
            (false,TEST_ROOT+"auth_pwd.xml",TEST_PROPERTIES_FILES);
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertNull(userHolder.getValue());
        assertEquals("tp2",passwordHolder.getValueAsString());

        context=new SpringContext
            (false,TEST_ROOT+"auth_both.xml",TEST_PROPERTIES_FILES);
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertEquals("tu3",userHolder.getValue());
        assertEquals("tp2",passwordHolder.getValueAsString());

        context=new SpringContext
            (false,TEST_ROOT+"auth_user.xml",TEST_PROPERTIES_FILES);
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertEquals("tu3",userHolder.getValue());
        assertEquals("tp2",passwordHolder.getValueAsString());

        context=new SpringContext
            (true,TEST_ROOT+"auth_blank.xml",TEST_PROPERTIES_FILES);
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertEquals("tu3",userHolder.getValue());
        assertEquals("tp2",passwordHolder.getValueAsString());

        context=new SpringContext
            (TestMessages.TEST_CONTEXT,true,
             TEST_ROOT+"auth_both.xml",TEST_PROPERTIES_FILES);
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertEquals("tu3",userHolder.getValue());
        assertEquals("tp3",passwordHolder.getValueAsString());
    }
}

package org.marketcetera.util.auth;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class SetterContextCliTest
    extends SetterContextTestBase
{
    private static final String TEST_CLI=
        "Command-line options";
    private static final String TEST_SHORT_FORM=
        "short";
    private static final String TEST_LONG_FORM=
        "long";
    private static final String[] TEST_ARGS=
        new String[] {"a","b","c"};

    private static class TestSetter<T extends Holder<?>>
        extends CliSetter<T>
    {
        public TestSetter
            (T holder,
             I18NBoundMessage usage,
             String shortForm,
             String longForm,
             I18NBoundMessage description)
        {
            super(holder,usage,shortForm,longForm,description);
        }

        public void setValue
            (CommandLine commandLine) {}
    }

    private static <T> void setter
        (CliSetter<Holder<T>> setter,
         Holder<T> holder)
    {
        simpleSetter(setter,holder);
        assertEquals(TEST_SHORT_FORM,setter.getShortForm());
        assertEquals(TEST_LONG_FORM,setter.getLongForm());
        assertEquals(TestMessages.TEST_DESCRIPTION,setter.getDescription());
    }


    @Test
    public void setterBasics()
        throws Exception
    {
        setter(new TestSetter<Holder<String>>
               (TEST_STRING_HOLDER,TestMessages.TEST_USAGE,
                TEST_SHORT_FORM,TEST_LONG_FORM,TestMessages.TEST_DESCRIPTION),
               TEST_STRING_HOLDER);
        setter(new CliSetterString
               (TEST_STRING_HOLDER,TestMessages.TEST_USAGE,
                TEST_SHORT_FORM,TEST_LONG_FORM,TestMessages.TEST_DESCRIPTION),
               TEST_STRING_HOLDER);
        setter(new CliSetterCharArray
               (TEST_CHAR_ARRAY_HOLDER,TestMessages.TEST_USAGE,
                TEST_SHORT_FORM,TEST_LONG_FORM,TestMessages.TEST_DESCRIPTION),
               TEST_CHAR_ARRAY_HOLDER);
    }

    @Test
    public void contextBasics()
        throws Exception
    {
        Holder<String> holder=new Holder<String>();
        CliSetter<Holder<String>> setter=
            new TestSetter<Holder<String>>
               (holder,TestMessages.TEST_USAGE,
                TEST_SHORT_FORM,TEST_LONG_FORM,TestMessages.TEST_DESCRIPTION);

        simpleContextWithName
            (new CliContext(true,ArrayUtils.EMPTY_STRING_ARRAY),
             setter,true,true,Messages.CLI_NAME,TEST_CLI);
        simpleContextWithName
            (new CliContext(false,ArrayUtils.EMPTY_STRING_ARRAY),
             setter,false,true,Messages.CLI_NAME,TEST_CLI);
        simpleContextWithName
            (new CliContext(TestMessages.TEST_CONTEXT,true,
                            ArrayUtils.EMPTY_STRING_ARRAY),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new CliContext(TestMessages.TEST_CONTEXT,false,
                            ArrayUtils.EMPTY_STRING_ARRAY),
             setter,false,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);

        holder.setValue("x");
        simpleContextWithName
            (new CliContext(true,ArrayUtils.EMPTY_STRING_ARRAY),
             setter,true,true,Messages.CLI_NAME,TEST_CLI);
        simpleContextWithName
            (new CliContext(false,ArrayUtils.EMPTY_STRING_ARRAY),
             setter,false,false,Messages.CLI_NAME,TEST_CLI);
        simpleContextWithName
            (new CliContext(TestMessages.TEST_CONTEXT,true,
                            ArrayUtils.EMPTY_STRING_ARRAY),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new CliContext(TestMessages.TEST_CONTEXT,false,
                            ArrayUtils.EMPTY_STRING_ARRAY),
             setter,false,false,TestMessages.TEST_CONTEXT,TEST_CONTEXT);

        CliContext context=new CliContext(true,TEST_ARGS);
        assertArrayEquals(TEST_ARGS,context.getArgs());
    }

    @Test
    public void setValues()
        throws Exception
    {
        Holder<String> userHolder=
            new Holder<String>(Messages.NO_USER);
        HolderCharArray passwordHolder=
            new HolderCharArray(Messages.NO_PASSWORD);

        CliSetterString userSetter=new CliSetterString
            (userHolder,new I18NBoundMessage2P
             (Messages.USER_CLI_USAGE,"u","username"),
             "u","username",Messages.USER_DESCRIPTION);
        CliSetterCharArray passwordSetter=new CliSetterCharArray
            (passwordHolder,new I18NBoundMessage2P
             (Messages.PASSWORD_CLI_USAGE,"p","password"),
             "p","password",Messages.PASSWORD_DESCRIPTION);

        CliContext context=new CliContext
            (false,ArrayUtils.EMPTY_STRING_ARRAY);
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY,
                          context.getCommandLine().getArgs());
        assertNull(userHolder.getValue());
        assertNull(passwordHolder.getValueAsString());

        context=new CliContext
            (false,new String[] {"-p","firstp"});
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY,
                          context.getCommandLine().getArgs());
        assertNull(userHolder.getValue());
        assertEquals("firstp",passwordHolder.getValueAsString());

        context=new CliContext
            (false,new String[] {"-u","firstu","-p","secondp"});
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY,
                          context.getCommandLine().getArgs());
        assertEquals("firstu",userHolder.getValue());
        assertEquals("firstp",passwordHolder.getValueAsString());

        context=new CliContext
            (false,new String[] {"-u","secondu"});
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY,
                          context.getCommandLine().getArgs());
        assertEquals("firstu",userHolder.getValue());
        assertEquals("firstp",passwordHolder.getValueAsString());

        context=new CliContext
            (true,new String[] {"-u",StringUtils.EMPTY,"-p",StringUtils.EMPTY});
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY,
                          context.getCommandLine().getArgs());
        assertEquals("firstu",userHolder.getValue());
        assertEquals("firstp",passwordHolder.getValueAsString());

        context=new CliContext
            (TestMessages.TEST_CONTEXT,true,
             new String[] {"-u","thirdu","-p","thirdp","extra"});
        context.add(userSetter);
        context.add(passwordSetter);
        context.setValues();
        assertArrayEquals(new String[] {"extra"},
                          context.getCommandLine().getArgs());
        assertEquals("thirdu",userHolder.getValue());
        assertEquals("thirdp",passwordHolder.getValueAsString());
    }

    @Test
    public void parsingError()
        throws Exception
    {
        CliContext context=new CliContext(true,new String[] {"-x"});
        try {
            context.setValues();
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.PARSING_FAILED,
                 ex.getI18NBoundMessage().getMessage());
        }
    }
}

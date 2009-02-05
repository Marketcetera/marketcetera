package org.marketcetera.util.auth;

import java.io.Console;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class SetterContextConsoleTest
    extends SetterContextTestBase
{
    private static final String TEST_CONSOLE=
        "Console terminal";

    private static class TestSetter<T extends Holder<?>>
        extends ConsoleSetter<T>
    {
        public TestSetter
            (T holder,
             I18NBoundMessage usage,
             I18NBoundMessage prompt)
        {
            super(holder,usage,prompt);
        }

        public void setValue
            (Console console) {}
    }

    private static <T> void setter
        (ConsoleSetter<Holder<T>> setter,
         Holder<T> holder)
    {
        simpleSetter(setter,holder);
        assertEquals(TestMessages.TEST_PROMPT,setter.getPrompt());
    }


    @Test
    public void setterBasics()
        throws Exception
    {
        setter(new TestSetter<Holder<String>>
               (TEST_STRING_HOLDER,TestMessages.TEST_USAGE,
                TestMessages.TEST_PROMPT),
               TEST_STRING_HOLDER);
        setter(new ConsoleSetterString
               (TEST_STRING_HOLDER,TestMessages.TEST_USAGE,
                TestMessages.TEST_PROMPT),
               TEST_STRING_HOLDER);
        setter(new ConsoleSetterCharArray
               (TEST_CHAR_ARRAY_HOLDER,TestMessages.TEST_USAGE,
                TestMessages.TEST_PROMPT),
               TEST_CHAR_ARRAY_HOLDER);
    }

    @Test
    public void contextBasics()
        throws Exception
    {
        Holder<String> holder=new Holder<String>();
        ConsoleSetter<Holder<String>> setter=
            new TestSetter<Holder<String>>
               (holder,TestMessages.TEST_USAGE,TestMessages.TEST_PROMPT);

        simpleContextWithName
            (new ConsoleContext(true),
             setter,true,true,Messages.CONSOLE_NAME,TEST_CONSOLE);
        simpleContextWithName
            (new ConsoleContext(false),
             setter,false,true,Messages.CONSOLE_NAME,TEST_CONSOLE);
        simpleContextWithName
            (new ConsoleContext(TestMessages.TEST_CONTEXT,true),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new ConsoleContext(TestMessages.TEST_CONTEXT,false),
             setter,false,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);

        holder.setValue("x");
        simpleContextWithName
            (new ConsoleContext(true),
             setter,true,true,Messages.CONSOLE_NAME,TEST_CONSOLE);
        simpleContextWithName
            (new ConsoleContext(false),
             setter,false,false,Messages.CONSOLE_NAME,TEST_CONSOLE);
        simpleContextWithName
            (new ConsoleContext(TestMessages.TEST_CONTEXT,true),
             setter,true,true,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
        simpleContextWithName
            (new ConsoleContext(TestMessages.TEST_CONTEXT,false),
             setter,false,false,TestMessages.TEST_CONTEXT,TEST_CONTEXT);
    }

    @Test
    public void noConsole()
        throws Exception
    {
        ConsoleContext context=new ConsoleContext(true);
        try {
            context.setValues();
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CONSOLE_UNAVAILABLE,
                 ex.getI18NBoundMessage().getMessage());
        }
    }


    /*
     * Run via -Psetter.context exec:java. Will go through three
     * passes of entering username and password, and showing the
     * results. The second pass will be skipped for data entered in
     * the first pass. To skip entering data in a pass, press
     * Control+D (Linux), or Control+Z and then Enter (Windows), or
     * leave blank and then Enter (all OS); such omitted entries will
     * be shown as 'null' (or the earlier entry will be retained and
     * show again). Run four tests where you enter in the first pass
     * neither username or password, either, or both. Also run four
     * test where, after entering values for both username and
     * password during the first two passes, you enter in the third
     * pass neither username and password, either, or both.
     */

    public static void main(String[] args)
        throws Exception
    {
        Holder<String> userHolder=
            new Holder<String>(Messages.NO_USER);
        HolderCharArray passwordHolder=
            new HolderCharArray(Messages.NO_PASSWORD);

        ConsoleSetterString userSetter=new ConsoleSetterString
            (userHolder,
             Messages.USER_CONSOLE_USAGE,Messages.USER_PROMPT);
        ConsoleSetterCharArray passwordSetter=new ConsoleSetterCharArray
            (passwordHolder,
             Messages.PASSWORD_CONSOLE_USAGE,Messages.PASSWORD_PROMPT);

        ConsoleContext context=new ConsoleContext(false);
        context.add(userSetter);
        context.add(passwordSetter);
        context.printUsage(System.err);

        System.err.println("First pass");
        context.setValues();
        System.err.println(userHolder.getValue());
        System.err.println(passwordHolder.getValueAsString());

        System.err.println("Second pass");
        context.setValues();
        System.err.println(userHolder.getValue());
        System.err.println(passwordHolder.getValueAsString());

        context=new ConsoleContext(TestMessages.TEST_CONTEXT,true);
        context.add(userSetter);
        context.add(passwordSetter);

        System.err.println("Third pass");
        context.setValues();
        System.err.println(userHolder.getValue());
        System.err.println(passwordHolder.getValueAsString());
    }
}

package org.marketcetera.util.auth;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.commons.lang.SystemUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.IterableUtils;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public class SetterContextTestBase
    extends TestCaseBase
{
    protected static final Holder<String> TEST_STRING_HOLDER=
        new Holder<String>();
    protected static final Holder<char[]> TEST_CHAR_ARRAY_HOLDER=
        new Holder<char[]>();

    protected static final String TEST_CONTEXT=
        "Test context";
    protected static final String TEST_USAGE=
        "Test usage";

    protected static final String TEST_ANONYMOUS=
        "(anonymous context)";
    protected static final String TEST_OVERRIDES=
        "(overriding context)";


    protected static <T extends Holder<?>> void simpleSetter
        (Setter<T> setter,
         T holder)
    {
        assertEquals(holder,setter.getHolder());
        assertEquals(TestMessages.TEST_USAGE,setter.getUsage());
    }


    private static <T extends Setter<?>> void setContext
        (Context<T> context,
         T setter,
         boolean override,
         boolean shouldProcess,
         String name)
    {
        String usage=name;
        if (override) {
            usage+=" "+TEST_OVERRIDES;
        }
        usage+=SystemUtils.LINE_SEPARATOR;

        assertArrayEquals(new Object[0],
                          IterableUtils.toArray(context.getSetters()));

        ByteArrayOutputStream outputStream;
        CloseableRegistry r=new CloseableRegistry();
        try {
            outputStream=new ByteArrayOutputStream();
            r.register(outputStream);
            PrintStream printStream=new PrintStream(outputStream);
            r.register(printStream);
            context.printUsage(printStream);
        } finally {
            r.close();
        }
        assertEquals(usage,new String(outputStream.toByteArray()));

        context.add(setter);
        assertArrayEquals(new Object[] {setter},
                          IterableUtils.toArray(context.getSetters()));

        r=new CloseableRegistry();
        try {
            outputStream=new ByteArrayOutputStream();
            r.register(outputStream);
            PrintStream printStream=new PrintStream(outputStream);
            r.register(printStream);
            context.printUsage(printStream);
        } finally {
            r.close();
        }
        usage+=" "+TEST_USAGE+SystemUtils.LINE_SEPARATOR;
        assertEquals(usage,new String(outputStream.toByteArray()));

        assertEquals(override,context.getOverride());
        assertEquals(shouldProcess,context.shouldProcess(setter));
    }

    protected static <T extends Setter<?>> void simpleContextNoName
        (Context<T> context,
         T setter,
         boolean override,
         boolean shouldProcess)
    {
        assertNull(context.getName());
        setContext(context,setter,override,shouldProcess,TEST_ANONYMOUS);
    }

    protected static <T extends Setter<?>> void simpleContextWithName
        (Context<T> context,
         T setter,
         boolean override,
         boolean shouldProcess,
         I18NBoundMessage nameBound,
         String nameStr)
    {
        assertEquals(nameBound,context.getName());
        setContext(context,setter,override,shouldProcess,nameStr);
    }


    @Before
    public void setupSetterContextTestBase()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }
}

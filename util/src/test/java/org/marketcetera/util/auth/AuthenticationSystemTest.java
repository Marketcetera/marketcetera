package org.marketcetera.util.auth;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class AuthenticationSystemTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        AuthenticationSystem.class.getName();
    private static final String TEST_VALUE=
        "x";
    private static final String TEST_MESSAGE=
        "Test message";
    private static final String TEST_LOCATION=
        TEST_CATEGORY;


    private static class MyHolder
        extends Holder<String>
    {
        private int mTipCount;
        private int mCount;

        MyHolder
            (int tipCount)
        {
            mTipCount=tipCount;
            mCount=0;
        }

        MyHolder
            (int tipCount,
             I18NBoundMessage message)
        {
            super(message);
            mTipCount=tipCount;
            mCount=0;
        }

        void increaseAndSet()
        {
            mCount++;
            if (mCount>=mTipCount) {
                setValue(TEST_VALUE);
            }
        }
    }

    private static class MySetter
        extends Setter<MyHolder>
    {
        private int mCalledCount;

        MySetter
            (MyHolder holder)
        {
            super(holder,null);
        }

        int getCalledCount()
        {
            return mCalledCount;
        }

        void setValue()
        {
            mCalledCount++;
            getHolder().increaseAndSet();
        }
    }

    private static class MyContext
        extends Context<MySetter>
    {
        private String mUsage;
        private int mCalledCount;

        public MyContext
            (boolean overrides,
             String usage)
        {
            super(overrides);
            mUsage=usage;
        }

        int getCalledCount()
        {
            return mCalledCount;
        }

        @Override
        public void printUsage
            (PrintStream stream)
        {
            stream.print(mUsage);
        }

        @Override
        public void setValues()
        {
            mCalledCount++;
            for (MySetter setter:getSetters()) {
                if (setter.getHolder().isSet()) {
                    continue;
                }
                setter.setValue();
            }
        }
    }

    private static void usage
        (AuthenticationSystem system,
         String usage)
    {
        ByteArrayOutputStream outputStream;
        CloseableRegistry r=new CloseableRegistry();
        try {
            outputStream=new ByteArrayOutputStream();
            r.register(outputStream);
            PrintStream printStream=new PrintStream(outputStream);
            r.register(printStream);
            system.printUsage(printStream);
        } finally {
            r.close();
        }
        assertEquals(usage,new String(outputStream.toByteArray()));
    }


    @Before
    public void setupAuthenticationSystemTest()
    {
        setLevel(TEST_CATEGORY,Level.ERROR);
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    @Test
    public void oneContext()
        throws Exception
    {
        MyHolder holder1=new MyHolder(1);
        assertFalse(holder1.isSet());
        MyHolder holder2=new MyHolder(2,TestMessages.TEST_MESSAGE);
        assertFalse(holder2.isSet());

        MyContext context1=new MyContext(false,"1");
        MySetter setter11=new MySetter(holder1);
        context1.add(setter11);
        MySetter setter12=new MySetter(holder2);
        context1.add(setter12);

        AuthenticationSystem system=new AuthenticationSystem();
        system.add(context1);
        usage(system,
              "1"+SystemUtils.LINE_SEPARATOR);
        system.setValues();

        assertEquals(1,context1.getCalledCount());
        assertEquals(1,setter11.getCalledCount());
        assertEquals(1,setter12.getCalledCount());
 
        assertTrue(holder1.isSet());
        assertFalse(holder2.isSet());

        assertEquals(TEST_VALUE,holder1.getValue());

        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test
    public void twoContexts()
        throws Exception
    {
        MyHolder holder1=new MyHolder(1);
        assertFalse(holder1.isSet());
        MyHolder holder2=new MyHolder(2,TestMessages.TEST_MESSAGE);
        assertFalse(holder2.isSet());

        MyContext context1=new MyContext(false,"1");
        MySetter setter11=new MySetter(holder1);
        context1.add(setter11);
        MySetter setter12=new MySetter(holder2);
        context1.add(setter12);

        MyContext context2=new MyContext(false,"2");
        MySetter setter21=new MySetter(holder1);
        context2.add(setter21);
        MySetter setter22=new MySetter(holder2);
        context2.add(setter22);

        AuthenticationSystem system=new AuthenticationSystem();
        system.add(context1);
        system.add(context2);
        usage(system,
              "1"+SystemUtils.LINE_SEPARATOR+
              "2"+SystemUtils.LINE_SEPARATOR);
        system.setValues();
 
        assertEquals(1,context1.getCalledCount());
        assertEquals(1,setter11.getCalledCount());
        assertEquals(1,setter12.getCalledCount());

        assertEquals(1,context2.getCalledCount());
        assertEquals(0,setter21.getCalledCount());
        assertEquals(1,setter22.getCalledCount());
 
        assertTrue(holder1.isSet());
        assertTrue(holder2.isSet());

        assertEquals(TEST_VALUE,holder1.getValue());
        assertEquals(TEST_VALUE,holder2.getValue());

        assertNoEvents();
    }

    @Test
    public void threeContexts()
        throws Exception
    {
        MyHolder holder1=new MyHolder(1);
        assertFalse(holder1.isSet());
        MyHolder holder2=new MyHolder(2,TestMessages.TEST_MESSAGE);
        assertFalse(holder2.isSet());

        MyContext context1=new MyContext(false,"1");
        MySetter setter11=new MySetter(holder1);
        context1.add(setter11);
        MySetter setter12=new MySetter(holder2);
        context1.add(setter12);

        MyContext context2=new MyContext(false,"2");
        MySetter setter21=new MySetter(holder1);
        context2.add(setter21);
        MySetter setter22=new MySetter(holder2);
        context2.add(setter22);

        MyContext context3=new MyContext(false,"3");
        MySetter setter31=new MySetter(holder1);
        context3.add(setter31);
        MySetter setter32=new MySetter(holder2);
        context3.add(setter32);

        AuthenticationSystem system=new AuthenticationSystem();
        system.add(context1);
        system.add(context2);
        system.add(context3);
        usage(system,
              "1"+SystemUtils.LINE_SEPARATOR+
              "2"+SystemUtils.LINE_SEPARATOR+
              "3"+SystemUtils.LINE_SEPARATOR);
        system.setValues();
 
        assertEquals(1,context1.getCalledCount());
        assertEquals(1,setter11.getCalledCount());
        assertEquals(1,setter12.getCalledCount());

        assertEquals(1,context2.getCalledCount());
        assertEquals(0,setter21.getCalledCount());
        assertEquals(1,setter22.getCalledCount());

        assertEquals(0,context3.getCalledCount());
        assertEquals(0,setter31.getCalledCount());
        assertEquals(0,setter32.getCalledCount());
 
        assertTrue(holder1.isSet());
        assertTrue(holder2.isSet());

        assertEquals(TEST_VALUE,holder1.getValue());
        assertEquals(TEST_VALUE,holder2.getValue());

        assertNoEvents();
    }
}

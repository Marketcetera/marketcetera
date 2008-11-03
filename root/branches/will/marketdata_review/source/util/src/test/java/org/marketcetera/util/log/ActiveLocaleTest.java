package org.marketcetera.util.log;

import java.util.Locale;
import java.util.concurrent.Callable;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class ActiveLocaleTest
    extends TestCaseBase
{
    private static final Locale DEFAULT_LOCALE=
        Locale.getDefault();


    private static class LocaleHolder
    {
        private Locale mBeforeActiveLocale;
        private Locale mAfterActiveLocale;

        Locale getBeforeActiveLocale()
        {
            return mBeforeActiveLocale;
        }

        Locale getAfterActiveLocale()
        {
            return mAfterActiveLocale;
        }

        protected void setBeforeActiveLocale()
        {
            mBeforeActiveLocale=ActiveLocale.getLocale();
        }

        protected void setAfterActiveLocale()
        {
            mAfterActiveLocale=ActiveLocale.getLocale();
        }
    }

    private static final class RunnableBase
        extends LocaleHolder
        implements Runnable
    {
        private Locale mSetLocale;

        RunnableBase
            (Locale setLocale)
        {
            mSetLocale=setLocale;
        }

        @Override
        public void run()
        {
            setBeforeActiveLocale();
            ActiveLocale.setThreadLocale(mSetLocale);
            setAfterActiveLocale();
        }
    }

    private static final class CallableBase
        extends LocaleHolder
        implements Callable<Integer>
    {
        private Locale mSetLocale;

        CallableBase
            (Locale setLocale)
        {
            mSetLocale=setLocale;
        }

        @Override
        public Integer call()
        {
            setBeforeActiveLocale();
            ActiveLocale.setThreadLocale(mSetLocale);
            setAfterActiveLocale();
            return 1;
        }
    }

    private static final class RunnableProxy
        extends LocaleHolder
        implements Runnable
    {
        private Locale mSelfSetLocale;
        private Locale mRunnableBaseSetLocale;
        private RunnableBase mRunnableBase;

        RunnableProxy
            (Locale selfSetLocale,
             Locale runnableBaseSetLocale)
        {
            mSelfSetLocale=selfSetLocale;
            mRunnableBaseSetLocale=runnableBaseSetLocale;
        }

        RunnableBase getRunnableBase()
        {
            return mRunnableBase;
        }

        @Override
        public void run()
        {
            setBeforeActiveLocale();
            mRunnableBase=new RunnableBase(mRunnableBaseSetLocale);
            ActiveLocale.runWithLocale(getRunnableBase(),mSelfSetLocale);
            setAfterActiveLocale();
        }
    }

    private static final class CallableProxy
        extends LocaleHolder
        implements Callable<Integer>
    {
        private Locale mSelfSetLocale;
        private Locale mCallableBaseSetLocale;
        private CallableBase mCallableBase;

        CallableProxy
            (Locale selfSetLocale,
             Locale callableBaseSetLocale)
        {
            mSelfSetLocale=selfSetLocale;
            mCallableBaseSetLocale=callableBaseSetLocale;
        }

        CallableBase getCallableBase()
        {
            return mCallableBase;
        }

        @Override
        public Integer call()
            throws Exception
        {
            setBeforeActiveLocale();
            mCallableBase=new CallableBase(mCallableBaseSetLocale);
            int result=
                ActiveLocale.runWithLocale(getCallableBase(),mSelfSetLocale);
            setAfterActiveLocale();
            return result+2;
        }
    }

    private static class LocaleHolderThread
        extends Thread
    {
        private Locale mBeforeActiveLocale;
        private Locale mAfterActiveLocale;

        Locale getBeforeActiveLocale()
        {
            return mBeforeActiveLocale;
        }

        Locale getAfterActiveLocale()
        {
            return mAfterActiveLocale;
        }

        protected void setBeforeActiveLocale()
        {
            mBeforeActiveLocale=ActiveLocale.getLocale();
        }

        protected void setAfterActiveLocale()
        {
            mAfterActiveLocale=ActiveLocale.getLocale();
        }
    }

    private static final class SimpleChildThread
        extends LocaleHolderThread
    {
        private Locale mSetLocale;

        SimpleChildThread
            (Locale setLocale)
        {
            mSetLocale=setLocale;
        }

        @Override
        public void run()
        {
            setBeforeActiveLocale();
            ActiveLocale.setThreadLocale(mSetLocale);
            setAfterActiveLocale();
        }
    }

    private static final class SimpleChildThreadProxy
        extends LocaleHolder
        implements Runnable
    {
        private Locale mSetLocale;
        private SimpleChildThread mSimpleChildThread;

        SimpleChildThreadProxy
            (Locale setLocale)
        {
            mSetLocale=setLocale;
        }

        SimpleChildThread getSimpleChildThread()
        {
            return mSimpleChildThread;
        }

        @Override
        public void run()
        {
            setBeforeActiveLocale();
            mSimpleChildThread=new SimpleChildThread(mSetLocale);
            getSimpleChildThread().start();
            try {
                getSimpleChildThread().join();
            } catch (InterruptedException ex) {
                fail();
            }
            setAfterActiveLocale();
        }
    }

    private static final class ComplexChildThread
        extends LocaleHolderThread
    {
        private Locale mSelfSetLocale;
        private Locale mRunnableBaseSetLocale;
        private RunnableBase mRunnableBase;

        ComplexChildThread
            (Locale selfSetLocale,
             Locale runnableBaseSetLocale)
        {
            mSelfSetLocale=selfSetLocale;
            mRunnableBaseSetLocale=runnableBaseSetLocale;
        }

        RunnableBase getRunnableBase()
        {
            return mRunnableBase;
        }

        @Override
        public void run()
        {
            setBeforeActiveLocale();
            mRunnableBase=new RunnableBase(mRunnableBaseSetLocale);
            ActiveLocale.runWithLocale(getRunnableBase(),mSelfSetLocale);
            setAfterActiveLocale();
        }
    }

    private static final class ComplexChildThreadProxy
        extends LocaleHolder
        implements Runnable
    {
        private Locale mSelfSetLocale;
        private Locale mRunnableBaseSetLocale;
        private ComplexChildThread mComplexChildThread;

        ComplexChildThreadProxy
            (Locale selfSetLocale,
             Locale runnableBaseSetLocale)
        {
            mSelfSetLocale=selfSetLocale;
            mRunnableBaseSetLocale=runnableBaseSetLocale;
        }

        ComplexChildThread getComplexChildThread()
        {
            return mComplexChildThread;
        }

        @Override
        public void run()
        {
            setBeforeActiveLocale();
            mComplexChildThread=new ComplexChildThread
                (mSelfSetLocale,mRunnableBaseSetLocale);
            getComplexChildThread().start();
            try {
                getComplexChildThread().join();
            } catch (InterruptedException ex) {
                fail();
            }
            setAfterActiveLocale();
        }
    }


    private static void checkBasics
        (Locale processLocale,
         Locale activeLocale)
    {
        assertEquals(processLocale,ActiveLocale.getProcessLocale());
        assertEquals(activeLocale,ActiveLocale.getLocale());
    }

    private static void checkRunnableSingle
        (Locale processLocale,
         Locale activeLocale,
         Locale blockSetLocale,
         Locale blockGetLocale,
         Locale proxySetLocale,
         Locale runnableGetBeforeLocale,
         Locale runnableSetLocale,
         Locale runnableGetAfterLocale)
    {
        RunnableProxy p=new RunnableProxy(proxySetLocale,runnableSetLocale);
        ActiveLocale.runWithLocale(p,blockSetLocale);
        checkBasics(processLocale,activeLocale);
        assertEquals(blockGetLocale,p.getBeforeActiveLocale());
        assertEquals(blockGetLocale,p.getAfterActiveLocale());
        assertEquals(runnableGetBeforeLocale,
                     p.getRunnableBase().getBeforeActiveLocale());
        assertEquals(runnableGetAfterLocale,
                     p.getRunnableBase().getAfterActiveLocale());
    }

    private static void checkRunnableAll
        (Locale processLocale,
         Locale activeLocale)
    {
        checkRunnableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            Locale.JAPANESE,Locale.JAPANESE,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkRunnableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            Locale.JAPANESE,Locale.JAPANESE,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkRunnableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            null,Locale.KOREAN,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkRunnableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            Locale.JAPANESE,Locale.JAPANESE,
                            null,Locale.KOREAN);
        checkRunnableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            null,Locale.KOREAN,
                            null,Locale.KOREAN);
        checkRunnableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            Locale.JAPANESE,Locale.JAPANESE,
                            null,activeLocale);
        checkRunnableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            null,activeLocale,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkRunnableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            null,activeLocale,
                            null,activeLocale);
    }

    private static void checkCallableSingle
        (Locale processLocale,
         Locale activeLocale,
         Locale blockSetLocale,
         Locale blockGetLocale,
         Locale proxySetLocale,
         Locale callableGetBeforeLocale,
         Locale callableSetLocale,
         Locale callableGetAfterLocale)
        throws Exception        
    {
        CallableProxy p=new CallableProxy(proxySetLocale,callableSetLocale);
        assertEquals(3,ActiveLocale.runWithLocale(p,blockSetLocale).intValue());
        checkBasics(processLocale,activeLocale);
        assertEquals(blockGetLocale,p.getBeforeActiveLocale());
        assertEquals(blockGetLocale,p.getAfterActiveLocale());
        assertEquals(callableGetBeforeLocale,
                     p.getCallableBase().getBeforeActiveLocale());
        assertEquals(callableGetAfterLocale,
                     p.getCallableBase().getAfterActiveLocale());
    }

    private static void checkCallableAll
        (Locale processLocale,
         Locale activeLocale)
        throws Exception
    {
        checkCallableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            Locale.JAPANESE,Locale.JAPANESE,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkCallableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            Locale.JAPANESE,Locale.JAPANESE,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkCallableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            null,Locale.KOREAN,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkCallableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            Locale.JAPANESE,Locale.JAPANESE,
                            null,Locale.KOREAN);
        checkCallableSingle(processLocale,activeLocale,
                            Locale.KOREAN,Locale.KOREAN,
                            null,Locale.KOREAN,
                            null,Locale.KOREAN);
        checkCallableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            Locale.JAPANESE,Locale.JAPANESE,
                            null,activeLocale);
        checkCallableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            null,activeLocale,
                            Locale.ITALIAN,Locale.ITALIAN);
        checkCallableSingle(processLocale,activeLocale,
                            null,activeLocale,
                            null,activeLocale,
                            null,activeLocale);
    }

    private static void checkSimpleChildSingle
        (Locale processLocale,
         Locale activeLocale,
         Locale setLocale,
         Locale getLocale)
        throws Exception
    {
        SimpleChildThread t=new SimpleChildThread(setLocale);
        t.start();
        t.join();
        checkBasics(processLocale,activeLocale);
        assertEquals(activeLocale,t.getBeforeActiveLocale());
        assertEquals(getLocale,t.getAfterActiveLocale());
    }

    private static void checkSimpleChildAll
        (Locale processLocale,
         Locale activeLocale)
        throws Exception
    {
        Locale threadLocale=
            ((processLocale==null)?DEFAULT_LOCALE:processLocale);
        checkSimpleChildSingle(processLocale,activeLocale,
                               Locale.ITALIAN,Locale.ITALIAN);
        checkSimpleChildSingle(processLocale,activeLocale,
                               null,threadLocale);
    }

    private static void checkSimpleChildFromStackSingle
        (Locale processLocale,
         Locale activeLocale,
         Locale blockSetLocale,
         Locale blockGetLocale,
         Locale childSetLocale,
         Locale childGetAfterLocale)
    {
        SimpleChildThreadProxy p=new SimpleChildThreadProxy(childSetLocale);
        ActiveLocale.runWithLocale(p,blockSetLocale);
        checkBasics(processLocale,activeLocale);
        assertEquals(blockGetLocale,p.getBeforeActiveLocale());
        assertEquals(blockGetLocale,p.getAfterActiveLocale());
        assertEquals(blockGetLocale,
                     p.getSimpleChildThread().getBeforeActiveLocale());
        assertEquals(childGetAfterLocale,
                     p.getSimpleChildThread().getAfterActiveLocale());
    }

    private static void checkSimpleChildFromStackAll
        (Locale processLocale,
         Locale activeLocale)
    {
        Locale threadLocale=
            ((processLocale==null)?DEFAULT_LOCALE:processLocale);
        checkSimpleChildFromStackSingle
            (processLocale,activeLocale,
             Locale.KOREAN,Locale.KOREAN,
             Locale.ITALIAN,Locale.ITALIAN);
        checkSimpleChildFromStackSingle
            (processLocale,activeLocale,
             null,activeLocale,
             Locale.ITALIAN,Locale.ITALIAN);
        checkSimpleChildFromStackSingle
            (processLocale,activeLocale,
             Locale.KOREAN,Locale.KOREAN,
             null,threadLocale);
        checkSimpleChildFromStackSingle
            (processLocale,activeLocale,
             null,activeLocale,
             null,threadLocale);
    }

    private static void checkComplexChildSingle
        (Locale processLocale,
         Locale activeLocale,
         Locale blockSetLocale,
         Locale blockGetLocale,
         Locale proxySetLocale,
         Locale runnableGetBeforeLocale,
         Locale runnableSetLocale,
         Locale runnableGetAfterLocale)
    {
        ComplexChildThreadProxy p=new ComplexChildThreadProxy
            (proxySetLocale,runnableSetLocale);
        ActiveLocale.runWithLocale(p,blockSetLocale);
        checkBasics(processLocale,activeLocale);
        assertEquals(blockGetLocale,p.getBeforeActiveLocale());
        assertEquals(blockGetLocale,p.getAfterActiveLocale());
        assertEquals(blockGetLocale,
                     p.getComplexChildThread().getBeforeActiveLocale());
        assertEquals(blockGetLocale,
                     p.getComplexChildThread().getAfterActiveLocale());
        assertEquals(runnableGetBeforeLocale,
                     p.getComplexChildThread().getRunnableBase().
                     getBeforeActiveLocale());
        assertEquals(runnableGetAfterLocale,
                     p.getComplexChildThread().getRunnableBase().
                     getAfterActiveLocale());
    }

    private static void checkComplexChildAll
        (Locale processLocale,
         Locale activeLocale)
    {
        checkComplexChildSingle(processLocale,activeLocale,
                                Locale.KOREAN,Locale.KOREAN,
                                Locale.JAPANESE,Locale.JAPANESE,
                                Locale.ITALIAN,Locale.ITALIAN);
        checkComplexChildSingle(processLocale,activeLocale,
                                null,activeLocale,
                                Locale.JAPANESE,Locale.JAPANESE,
                                Locale.ITALIAN,Locale.ITALIAN);
        checkComplexChildSingle(processLocale,activeLocale,
                                Locale.KOREAN,Locale.KOREAN,
                                null,Locale.KOREAN,
                                Locale.ITALIAN,Locale.ITALIAN);
        checkComplexChildSingle(processLocale,activeLocale,
                                Locale.KOREAN,Locale.KOREAN,
                                Locale.JAPANESE,Locale.JAPANESE,
                                null,Locale.KOREAN);
        checkComplexChildSingle(processLocale,activeLocale,
                                null,activeLocale,
                                null,activeLocale,
                                Locale.ITALIAN,Locale.ITALIAN);
        checkComplexChildSingle(processLocale,activeLocale,
                                Locale.KOREAN,Locale.KOREAN,
                                null,Locale.KOREAN,
                                null,Locale.KOREAN);
        checkComplexChildSingle(processLocale,activeLocale,
                                null,activeLocale,
                                Locale.JAPANESE,Locale.JAPANESE,
                                null,activeLocale);
        checkComplexChildSingle(processLocale,activeLocale,
                                null,activeLocale,
                                null,activeLocale,
                                null,activeLocale);
    }


    @Before
    public void setupActiveLocaleTest()
    {
        ActiveLocale.clear();
    }


    @Test
    public void basics()
    {
        checkBasics(null,DEFAULT_LOCALE);
        ActiveLocale.setProcessLocale(Locale.FRENCH);
        checkBasics(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.setThreadLocale(Locale.GERMAN);
        checkBasics(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.setProcessLocale(null);
        checkBasics(null,Locale.GERMAN);
        ActiveLocale.setThreadLocale(null);
        checkBasics(null,DEFAULT_LOCALE);

        ActiveLocale.setProcessLocale(Locale.FRENCH);
        checkBasics(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.pushLocale(Locale.GERMAN);
        checkBasics(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.pushLocale(Locale.KOREAN);
        checkBasics(Locale.FRENCH,Locale.KOREAN);
        ActiveLocale.pushLocale(Locale.JAPANESE);
        checkBasics(Locale.FRENCH,Locale.JAPANESE);
        ActiveLocale.setThreadLocale(Locale.ITALIAN);
        checkBasics(Locale.FRENCH,Locale.ITALIAN);
        ActiveLocale.setThreadLocale(null);
        checkBasics(Locale.FRENCH,Locale.KOREAN);
        ActiveLocale.popLocale();
        checkBasics(Locale.FRENCH,Locale.KOREAN);
        ActiveLocale.popLocale();
        checkBasics(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.popLocale();
        checkBasics(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.setProcessLocale(null);
        checkBasics(null,DEFAULT_LOCALE);
    }

    @Test
    public void runnable()
    {
        checkRunnableAll(null,DEFAULT_LOCALE);
        ActiveLocale.setProcessLocale(Locale.FRENCH);
        checkRunnableAll(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.setThreadLocale(Locale.GERMAN);
        checkRunnableAll(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.setProcessLocale(null);
        checkRunnableAll(null,Locale.GERMAN);
    }

    @Test
    public void callable()
        throws Exception
    {
        checkCallableAll(null,DEFAULT_LOCALE);
        ActiveLocale.setProcessLocale(Locale.FRENCH);
        checkCallableAll(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.setThreadLocale(Locale.GERMAN);
        checkCallableAll(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.setProcessLocale(null);
        checkCallableAll(null,Locale.GERMAN);
    }

    @Test
    public void simpleChild()
        throws Exception
    {
        checkSimpleChildAll(null,DEFAULT_LOCALE);
        ActiveLocale.setProcessLocale(Locale.FRENCH);
        checkSimpleChildAll(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.setThreadLocale(Locale.GERMAN);
        checkSimpleChildAll(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.setProcessLocale(null);
        checkSimpleChildAll(null,Locale.GERMAN);
    }

    @Test
    public void simpleChildFromStack()
    {
        checkSimpleChildFromStackAll(null,DEFAULT_LOCALE);
        ActiveLocale.setProcessLocale(Locale.FRENCH);
        checkSimpleChildFromStackAll(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.setThreadLocale(Locale.GERMAN);
        checkSimpleChildFromStackAll(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.setProcessLocale(null);
        checkSimpleChildFromStackAll(null,Locale.GERMAN);
    }

    @Test
    public void complexChild()
    {
        checkComplexChildAll(null,DEFAULT_LOCALE);
        ActiveLocale.setProcessLocale(Locale.FRENCH);
        checkComplexChildAll(Locale.FRENCH,Locale.FRENCH);
        ActiveLocale.setThreadLocale(Locale.GERMAN);
        checkComplexChildAll(Locale.FRENCH,Locale.GERMAN);
        ActiveLocale.setProcessLocale(null);
        checkComplexChildAll(null,Locale.GERMAN);
    }
}

package org.marketcetera.util.except;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class I18NRuntimeExceptionTest
    extends I18NThrowableTestBase
{
    private final static I18NThrowable[] ALL=new I18NThrowable[] {
        new I18NRuntimeException(),
        new I18NRuntimeException
        (new ArrayStoreException()),
        new I18NRuntimeException
        (new ArrayStoreException(TEST_MSG_1)),
        new I18NRuntimeException
        (new I18NRuntimeException
         (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM))),
        new I18NRuntimeException
        (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NRuntimeException
        (new ArrayStoreException(),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NRuntimeException
        (new ArrayStoreException(TEST_MSG_2),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NRuntimeException
        (new I18NRuntimeException(TestMessages.BOT_EXCEPTION),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NError(),
        null
    };


    @Test
    public void empty()
    {
        empty(new RuntimeException(),new I18NRuntimeException(),ALL,0);
    }

    @Test
    public void causeWithoutMessage()
    {
        ArrayStoreException nested=new ArrayStoreException();
        causeWithoutMessage
            (nested,new RuntimeException(nested),
             new I18NRuntimeException(nested),ALL,1);
    }

    @Test
    public void causeWithMessage()
    {
        ArrayStoreException nested=
            new ArrayStoreException(TEST_MSG_1);
        causeWithMessage
            (nested,new RuntimeException(nested),
             new I18NRuntimeException(nested),ALL,2);
    }

    @Test
    public void causeWithI18NMessage()
    {
        I18NRuntimeException nested=new I18NRuntimeException
            (new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        causeWithI18NMessage
            (nested,new RuntimeException(nested),
             new I18NRuntimeException(nested),ALL,3);
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new RuntimeException(TEST_MSG_1),
             new I18NRuntimeException
             (new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,4);
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        ArrayStoreException nested=new ArrayStoreException();
        myMessageAndCauseWithoutMessage
            (nested,new RuntimeException(TEST_MSG_1,nested),
             new I18NRuntimeException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,5);
    }

    @Test
    public void myMessageAndCauseWithMessage()
    {
        ArrayStoreException nested=
            new ArrayStoreException(TEST_MSG_2);
        myMessageAndCauseWithMessage
            (nested,new RuntimeException(TEST_MSG_1,nested),
             new I18NRuntimeException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,6);
    }

    @Test
    public void myMessageAndCauseWithI18NMessage()
    {
        I18NRuntimeException nested=new I18NRuntimeException
            (TestMessages.BOT_EXCEPTION);
        myMessageAndCauseWithI18NMessage
            (nested,new RuntimeException(TEST_MSG_1,nested),
             new I18NRuntimeException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,7);
    }

    @Test
    public void nesting()
    {
        I18NRuntimeException exBot=new I18NRuntimeException
            (TestMessages.BOT_EXCEPTION);
        I18NRuntimeException exMid=new I18NRuntimeException
            (exBot,new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        I18NRuntimeException exTop=new I18NRuntimeException
            (exMid,TestMessages.TOP_EXCEPTION);
        nesting(exBot,exMid,exTop);
    }
}

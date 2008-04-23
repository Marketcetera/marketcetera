package org.marketcetera.util.except;

import org.junit.Test;

public class I18NRuntimeExceptionTest
	extends I18NThrowableTestBase
{
    @Test
    public void empty()
    {
        empty(new RuntimeException(),new I18NRuntimeException());
    }

    @Test
    public void causeWithoutMessage()
    {
        ArrayStoreException nested=new ArrayStoreException();
        causeWithoutMessage
            (nested,new RuntimeException(nested),
             new I18NRuntimeException(nested));
    }

    @Test
    public void causeWithMessage()
    {
        ArrayStoreException nested=
            new ArrayStoreException(TEST_MSG_1);
        causeWithMessage
            (nested,new RuntimeException(nested),
             new I18NRuntimeException(nested));
    }

    @Test
    public void causeWithI18NMessage()
    {
        I18NRuntimeException nested=new I18NRuntimeException
            (TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        causeWithI18NMessage
            (nested,new RuntimeException(nested),
             new I18NRuntimeException(nested));
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new RuntimeException(TEST_MSG_1),
             new I18NRuntimeException
             (TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        ArrayStoreException nested=new ArrayStoreException();
        myMessageAndCauseWithoutMessage
            (nested,new RuntimeException(TEST_MSG_1,nested),
             new I18NRuntimeException
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithMessage()
    {
        ArrayStoreException nested=
            new ArrayStoreException(TEST_MSG_2);
        myMessageAndCauseWithMessage
            (nested,new RuntimeException(TEST_MSG_1,nested),
             new I18NRuntimeException
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithI18NMessage()
    {
        I18NRuntimeException nested=new I18NRuntimeException
            (TestMessages.PROVIDER,TestMessages.BOT_EXCEPTION);
        myMessageAndCauseWithI18NMessage
            (nested,new RuntimeException(TEST_MSG_1,nested),
             new I18NRuntimeException
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void nesting()
    {
        I18NRuntimeException exBot=new I18NRuntimeException
            (TestMessages.PROVIDER,TestMessages.BOT_EXCEPTION);
        I18NRuntimeException exMid=new I18NRuntimeException
            (exBot,TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,
             MID_MSG_PARAM);
        I18NRuntimeException exTop=new I18NRuntimeException
            (exMid,TestMessages.PROVIDER,TestMessages.TOP_EXCEPTION);
        nesting(exBot,exMid,exTop);
    }
}

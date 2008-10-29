package org.marketcetera.util.except;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class I18NExceptionTest
    extends I18NThrowableTestBase
{
    private final static I18NThrowable[] ALL=new I18NThrowable[] {
        new I18NException(),
        new I18NException
        (new CloneNotSupportedException()),
        new I18NException
        (new CloneNotSupportedException(TEST_MSG_1)),
        new I18NException
        (new I18NException
         (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM))),
        new I18NException
        (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NException
        (new CloneNotSupportedException(),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NException
        (new CloneNotSupportedException(TEST_MSG_2),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NException
        (new I18NException(TestMessages.BOT_EXCEPTION),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NError(),
        null
    };


    @Test
    public void empty()
    {
        empty(new Exception(),new I18NException(),ALL,0);
    }

    @Test
    public void causeWithoutMessage()
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        causeWithoutMessage
            (nested,new Exception(nested),new I18NException(nested),ALL,1);
    }

    @Test
    public void causeWithMessage()
    {
        CloneNotSupportedException nested=
            new CloneNotSupportedException(TEST_MSG_1);
        causeWithMessage
            (nested,new Exception(nested),new I18NException(nested),ALL,2);
    }

    @Test
    public void causeWithI18NMessage()
    {
        I18NException nested=new I18NException
            (new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        causeWithI18NMessage
            (nested,new Exception(nested),new I18NException(nested),ALL,3);
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new Exception(TEST_MSG_1),
             new I18NException
             (new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,4);
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        myMessageAndCauseWithoutMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,5);
    }

    @Test
    public void myMessageAndCauseWithMessage()
    {
        CloneNotSupportedException nested=
            new CloneNotSupportedException(TEST_MSG_2);
        myMessageAndCauseWithMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,6);
    }

    @Test
    public void myMessageAndCauseWithI18NMessage()
    {
        I18NException nested=new I18NException
            (TestMessages.BOT_EXCEPTION);
        myMessageAndCauseWithI18NMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,7);
    }

    @Test
    public void nesting()
    {
        I18NException exBot=new I18NException
            (TestMessages.BOT_EXCEPTION);
        I18NException exMid=new I18NException
            (exBot,new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        I18NException exTop=new I18NException
            (exMid,TestMessages.TOP_EXCEPTION);
        nesting(exBot,exMid,exTop);
    }
}

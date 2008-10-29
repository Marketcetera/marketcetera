package org.marketcetera.util.except;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class I18NErrorTest
    extends I18NThrowableTestBase
{
    private final static I18NThrowable[] ALL=new I18NThrowable[] {
        new I18NError(),
        new I18NError
        (new AssertionError()),
        new I18NError
        (new AssertionError(TEST_MSG_1)),
        new I18NError
        (new I18NError
         (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM))),
        new I18NError
        (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NError
        (new AssertionError(),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NError
        (new AssertionError(TEST_MSG_2),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NError
        (new I18NError(TestMessages.BOT_EXCEPTION),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NException(),
        null
    };


    @Test
    public void empty()
    {
        empty(new Error(),new I18NError(),ALL,0);
    }

    @Test
    public void causeWithoutMessage()
    {
        AssertionError nested=new AssertionError();
        causeWithoutMessage
            (nested,new Error(nested),new I18NError(nested),ALL,1);
    }

    @Test
    public void causeWithMessage()
    {
        AssertionError nested=new AssertionError(TEST_MSG_1);
        causeWithMessage
            (nested,new Error(nested),new I18NError(nested),ALL,2);
    }

    @Test
    public void causeWithI18NMessage()
    {
        I18NError nested=new I18NError
            (new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        causeWithI18NMessage
            (nested,new Error(nested),new I18NError(nested),ALL,3);
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new Error(TEST_MSG_1),
             new I18NError
             (new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,4);
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        AssertionError nested=new AssertionError();
        myMessageAndCauseWithoutMessage
            (nested,new Error(TEST_MSG_1,nested),
             new I18NError
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,5);
    }

    @Test
    public void myMessageAndCauseWithMessage()
    {
        AssertionError nested=new AssertionError(TEST_MSG_2);
        myMessageAndCauseWithMessage
            (nested,new Error(TEST_MSG_1,nested),
             new I18NError
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,6);
    }

    @Test
    public void myMessageAndCauseWithI18NMessage()
    {
        I18NError nested=new I18NError
            (TestMessages.BOT_EXCEPTION);
        myMessageAndCauseWithI18NMessage
            (nested,new Error(TEST_MSG_1,nested),
             new I18NError
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,7);
    }

    @Test
    public void nesting()
    {
        I18NError exBot=new I18NError
            (TestMessages.BOT_EXCEPTION);
        I18NError exMid=new I18NError
            (exBot,new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        I18NError exTop=new I18NError
            (exMid,TestMessages.TOP_EXCEPTION);
        nesting(exBot,exMid,exTop);
    }
}

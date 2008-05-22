package org.marketcetera.util.except;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;

public class I18NExceptionTest
    extends I18NThrowableTestBase
{
    @Test
    public void empty()
    {
        empty(new Exception(),new I18NException());
    }

    @Test
    public void causeWithoutMessage()
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        causeWithoutMessage
            (nested,new Exception(nested),new I18NException(nested));
    }

    @Test
    public void causeWithMessage()
    {
        CloneNotSupportedException nested=
            new CloneNotSupportedException(TEST_MSG_1);
        causeWithMessage
            (nested,new Exception(nested),new I18NException(nested));
    }

    @Test
    public void causeWithI18NMessage()
    {
        I18NException nested=new I18NException
            (new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        causeWithI18NMessage
            (nested,new Exception(nested),new I18NException(nested));
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new Exception(TEST_MSG_1),
             new I18NException
             (new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        myMessageAndCauseWithoutMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
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
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
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
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
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

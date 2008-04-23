package org.marketcetera.util.log;

import org.junit.Test;

import static org.junit.Assert.*;

public class LogUtilsTest
{
    @Test
    public void listText()
    {
        assertEquals("()",LogUtils.getListText());
        assertEquals("([null])",LogUtils.getListText(null));
        assertEquals("('a')",LogUtils.getListText('a'));
        assertEquals("('a','b')",LogUtils.getListText('a',"b"));
        assertEquals("('a',[null])",LogUtils.getListText('a',null));
    }

    @Test
    public void simpleMessage()
    {
        assertEquals
            ("provider 'log_test'; id 'log'; entry 'msg'; "+
             "parameters ()",
             LogUtils.getSimpleMessage
             (TestMessages.PROVIDER,TestMessages.LOG_MSG));
        assertEquals
            ("provider 'log_test'; id 'log'; entry 'msg'; "+
             "parameters ([null])",
             LogUtils.getSimpleMessage
             (TestMessages.PROVIDER,TestMessages.LOG_MSG,null));
        assertEquals
            ("provider 'log_test'; id 'log'; entry 'msg'; "+
             "parameters ('a',[null])",
             LogUtils.getSimpleMessage
             (TestMessages.PROVIDER,TestMessages.LOG_MSG,"a",null));
    }
}

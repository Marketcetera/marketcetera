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
        I18NMessageProvider provider=new I18NMessageProvider("nonexistent_prv");
        assertEquals
            ("provider 'nonexistent_prv'; id 'log'; entry 'msg'; "+
             "parameters ()",
             LogUtils.getSimpleMessage(provider,TestMessages.LOG_MSG));
        assertEquals
            ("provider 'nonexistent_prv'; id 'log'; entry 'msg'; "+
             "parameters ([null])",
             LogUtils.getSimpleMessage(provider,TestMessages.LOG_MSG,null));
        assertEquals
            ("provider 'nonexistent_prv'; id 'log'; entry 'msg'; "+
             "parameters ('a',[null])",
             LogUtils.getSimpleMessage(provider,TestMessages.LOG_MSG,"a",null));

        assertEquals
            ("provider 'log_test'; id 'log'; entry 'msg'; "+
             "parameters ()",
             LogUtils.getSimpleMessage(TestMessages.LOG_MSG));
        assertEquals
            ("provider 'log_test'; id 'log'; entry 'msg'; "+
             "parameters ([null])",
             LogUtils.getSimpleMessage(TestMessages.LOG_MSG,null));
        assertEquals
            ("provider 'log_test'; id 'log'; entry 'msg'; "+
             "parameters ('a',[null])",
             LogUtils.getSimpleMessage(TestMessages.LOG_MSG,"a",null));
    }
}

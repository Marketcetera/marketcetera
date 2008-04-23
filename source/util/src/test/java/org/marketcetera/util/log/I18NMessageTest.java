package org.marketcetera.util.log;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class I18NMessageTest
	extends TestCaseBase
{
    private static final String TEST_MSG_ID="Test message ID";
    private static final String TEST_ENTRY_ID="Test entry ID";


    @Test
    public void message()
    {
        I18NMessage m=new I18NMessage(TEST_MSG_ID,TEST_ENTRY_ID);
        assertEquals(TEST_MSG_ID,m.getMessageId());
        assertEquals(TEST_ENTRY_ID,m.getEntryId());

        m=new I18NMessage(TEST_MSG_ID);
        assertEquals(TEST_MSG_ID,m.getMessageId());
        assertEquals(I18NMessage.UNKNOWN_ENTRY_ID,m.getEntryId());
    }
}

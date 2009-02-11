package org.marketcetera.util.l10n;

import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class MessageInfoTestBase
    extends TestCaseBase
{
    protected static final String TEST_KEY=
        "testKey";
    protected static final String TEST_KEY_D=
        "testKeyD";
    protected static final int TEST_PC=
        1;
    protected static final int TEST_PC_D=
        2;
    protected static final String TEST_TEXT=
        "testText";
    protected static final String TEST_TEXT_D=
        "testTextD";
    protected static final I18NMessage TEST_MESSAGE=
        TestMessages.M0_MSG;
    protected static final I18NMessage TEST_MESSAGE_D=
        TestMessages.M1_MSG;

    protected static final PropertyMessageInfo TEST_PROPERTY_INFO=
        new PropertyMessageInfo(TEST_KEY,TEST_PC,TEST_TEXT);
    protected static final PropertyMessageInfo TEST_PROPERTY_INFO_SAME=
        new PropertyMessageInfo(TEST_KEY,TEST_PC,TEST_TEXT);
    protected static final PropertyMessageInfo TEST_PROPERTY_INFO_KD=
        new PropertyMessageInfo(TEST_KEY_D,TEST_PC,TEST_TEXT);
    protected static final PropertyMessageInfo TEST_PROPERTY_INFO_PCD=
        new PropertyMessageInfo(TEST_KEY,TEST_PC_D,TEST_TEXT);
    protected static final PropertyMessageInfo TEST_PROPERTY_INFO_TD=
        new PropertyMessageInfo(TEST_KEY,TEST_PC,TEST_TEXT_D);

    protected static final I18NMessageInfo TEST_I18N_INFO=
        new I18NMessageInfo(TEST_KEY,TEST_PC,TEST_MESSAGE);
    protected static final I18NMessageInfo TEST_I18N_INFO_SAME=
        new I18NMessageInfo(TEST_KEY,TEST_PC,TEST_MESSAGE);
    protected static final I18NMessageInfo TEST_I18N_INFO_KD=
        new I18NMessageInfo(TEST_KEY_D,TEST_PC,TEST_MESSAGE);
    protected static final I18NMessageInfo TEST_I18N_INFO_PCD=
        new I18NMessageInfo(TEST_KEY,TEST_PC_D,TEST_MESSAGE);
    protected static final I18NMessageInfo TEST_I18N_INFO_MD=
        new I18NMessageInfo(TEST_KEY,TEST_PC,TEST_MESSAGE_D);

    protected static void retention
        (MessageInfo info)
    {
        assertEquals(TEST_KEY,info.getKey());
        assertEquals(TEST_PC,info.getParamCount());
    }
}

package org.marketcetera.ors.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.ors.filters.FieldRemoverMessageModifier;
import org.marketcetera.ors.filters.Messages;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.MsgType;

/* $License$ */

/**
 * Tests {@link FieldRemoverMessageModifier}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FieldRemoverMessageModifierTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        FIXDataDictionary dataDictionary = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        if(dataDictionary == null) {
            FIXDataDictionaryManager.initialize(fixVersion, 
                                                fixVersion.getDataDictionaryURL());
            dataDictionary = FIXDataDictionaryManager.getFIXDataDictionary(fixVersion);
        }
    }
    /**
     * Tests the {@link FieldRemoverMessageModifier} constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testConstructor()
            throws Exception
    {
        FieldRemoverMessageModifier modifier = new FieldRemoverMessageModifier();
        verifyModifier(modifier,
                       null,
                       -1);
        modifier = new FieldRemoverMessageModifier(null);
        verifyModifier(modifier,
                       null,
                       -1);
        new ExpectedFailure<IllegalArgumentException>(Messages.NON_CONFORMING_FIELD_SPECIFICATION.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                new FieldRemoverMessageModifier("this-won't-conform");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NON_CONFORMING_FIELD_SPECIFICATION.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                new FieldRemoverMessageModifier("   this-won't-conform   ");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NON_CONFORMING_FIELD_SPECIFICATION.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                new FieldRemoverMessageModifier("123456(A)");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NON_CONFORMING_FIELD_SPECIFICATION.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                new FieldRemoverMessageModifier("12345(ABCD)");
            }
        };
        modifier = new FieldRemoverMessageModifier("256(A)");
        verifyModifier(modifier,
                       "A",
                       256);
        modifier = new FieldRemoverMessageModifier("25678(ABC)");
        verifyModifier(modifier,
                       "ABC",
                       25678);
        modifier = new FieldRemoverMessageModifier("123(*)");
        verifyModifier(modifier,
                       "*",
                       123);
        modifier = new FieldRemoverMessageModifier("     25678(ABC)     ");
        verifyModifier(modifier,
                       "ABC",
                       25678);
    }
    /**
     * Tests {@link FieldRemoverMessageModifier#modifyMessage(quickfix.Message, org.marketcetera.ors.history.ReportHistoryServices, org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testMessageMatching()
            throws Exception
    {
        Message message = fixVersion.getMessageFactory().createMessage(MsgType.ORDER_SINGLE);
        doCompleteMessageTest(message,
                              MsgType.ORDER_CANCEL_REQUEST);
        message = fixVersion.getMessageFactory().createMessage(MsgType.ORDER_CANCEL_REQUEST);
        doCompleteMessageTest(message,
                              MsgType.ORDER_SINGLE);
        message = fixVersion.getMessageFactory().createMessage(MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        doCompleteMessageTest(message,
                              MsgType.ORDER_SINGLE);
    }
    /**
     * Runs a round of tests on the given message.
     *
     * @param inMessage a <code>Message</code> value to test
     * @param inDifferentMsgType a <code>String</code> Value containing a msgtype value that is not the same as the one of the given message
     * @throws Exception
     */
    private void doCompleteMessageTest(Message inMessage,
                                       String inDifferentMsgType)
            throws Exception
    {
        // verify test data
        assertFalse(inMessage.getHeader().getString(MsgType.FIELD).equals(inDifferentMsgType));
        // add a field to the message
        assertFalse(inMessage.isSetField(10000));
        inMessage.setString(10000,
                            "field-value");
        assertTrue(inMessage.isSetField(10000));
        // null constructor
        doOneMessageTest(inMessage,
                         null,
                         false);
        assertTrue(inMessage.isSetField(10000));
        // wrong field to remove
        doOneMessageTest(inMessage,
                         "10001(*)",
                         false);
        assertTrue(inMessage.isSetField(10000));
        // wrong message type
        doOneMessageTest(inMessage,
                         "10000(" + inDifferentMsgType + ")",
                         false);
        assertTrue(inMessage.isSetField(10000));
        // wrong field, wrong message
        doOneMessageTest(inMessage,
                         "10001(" + inDifferentMsgType + ")",
                         false);
        assertTrue(inMessage.isSetField(10000));
        // right field, right message
        doOneMessageTest(inMessage,
                         "10000(" + inMessage.getHeader().getString(MsgType.FIELD) + ")",
                         true);
        assertFalse(inMessage.isSetField(10000));
        // right field, all messages
        inMessage.setString(10000,
                            "field-value");
        assertTrue(inMessage.isSetField(10000));
        doOneMessageTest(inMessage,
                         "10000(*)",
                         true);
        assertFalse(inMessage.isSetField(10000));
    }
    /**
     * 
     *
     *
     * @param inMessage
     * @param inModifierArgument
     * @param inExpectedModified
     * @throws Exception
     */
    private void doOneMessageTest(Message inMessage,
                                  String inModifierArgument,
                                  boolean inExpectedModified)
            throws Exception
    {
        FieldRemoverMessageModifier modifier = new FieldRemoverMessageModifier(inModifierArgument);
        assertEquals(inExpectedModified,
                     modifier.modifyMessage(inMessage,
                                            null,
                                            null));
    }
    /**
     * Verifies the given <code>FieldRemoverMessageModifier</code> contains the given attributes.
     *
     * @param inModifier a <code>FieldRemoverMessageModifier</code> value
     * @param inMsgType a <code>String</code> value
     * @param inField an <code>int</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyModifier(FieldRemoverMessageModifier inModifier,
                                String inMsgType,
                                int inField)
            throws Exception
    {
        assertNotNull(inModifier.toString());
        assertEquals(inMsgType,
                     inModifier.getMsgType());
        assertEquals(inField,
                     inModifier.getField());
    }
    /**
     * the FIX version used to construct FIX messages
     */
    private static FIXVersion fixVersion = FIXVersion.FIX_SYSTEM;
}

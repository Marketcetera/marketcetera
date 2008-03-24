package org.marketcetera.quickfix;

import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.marketdata.MarketDataFeedTestSuite;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

/**
 * Tests {@link MessageTranslatorBase}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class MessageTranslatorBaseTest
        extends TestCase
{
    /**
     * Create a new MessageTranslatorBaseTest instance.
     *
     * @param inArg0
     */
    public MessageTranslatorBaseTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        TestSuite suite = new MarketceteraTestSuite(MessageTranslatorBaseTest.class);
        return suite;    
    }

    public void testConstructor()
        throws Exception
    {
        final Message message = MarketDataFeedTestSuite.generateFIXMessage();
        TestMessageTranslator translator = new TestMessageTranslator();
        assertNotNull(translator);
        // test with no subscription type
        message.removeField(SubscriptionRequestType.FIELD);
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute()
                    throws Throwable
            {
                message.getChar(SubscriptionRequestType.FIELD);
            }
        }.run();
        translator = new TestMessageTranslator();
        assertEquals(SubscriptionRequestType.SNAPSHOT,
                     MessageTranslatorBase.determineSubscriptionRequestType(message));
    }
    
    public void testGetSymbol()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                MessageTranslatorBase.getSymbol(null);
            }
        }.run();
        MSymbol google = new MSymbol("GOOG");
        MSymbol msoft = new MSymbol("MSFT");
        Message message = MarketDataFeedTestSuite.generateFIXMessage(Arrays.asList(new MSymbol[] { google, msoft }));
        TestMessageTranslator translator = new TestMessageTranslator();
        List<Group> groups = translator.getGroups(message);
        assertEquals(2,
                     groups.size());
        assertEquals(google,
                     TestMessageTranslator.getSymbol(groups.get(0)));
        assertEquals(msoft,
                     TestMessageTranslator.getSymbol(groups.get(1)));
//        Message message = FIXMessageUtilTest.createOptionNOS(optionRoot, optionContractSpecifier, "200708", new BigDecimal("23"),
//                                                            PutOrCall.CALL, new BigDecimal("1"), new BigDecimal("10"), Side.BUY, msgFactory);

    }
    
}

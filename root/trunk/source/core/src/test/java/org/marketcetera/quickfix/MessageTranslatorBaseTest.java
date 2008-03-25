package org.marketcetera.quickfix;

import java.util.Arrays;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.marketdata.MarketDataFeedTestSuite;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

/**
 * Tests {@link AbstractMessageTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class MessageTranslatorBaseTest
        extends TestCase
{
    private IMessageTranslator mTranslator;
    
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

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        mTranslator = new IMessageTranslator<Object>() {
            public Object translate(Message inMessage)
                    throws MarketceteraException
            {
                return null;
            }
            public Message translate(Object inData)
                    throws MarketceteraException
            {
                return null;
            }
        };
    }

    public void testConstructor()
        throws Exception
    {
        final Message message = MarketDataFeedTestSuite.generateFIXMessage();
        assertNotNull(mTranslator);
        // test with no subscription type
        message.removeField(SubscriptionRequestType.FIELD);
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute()
                    throws Throwable
            {
                message.getChar(SubscriptionRequestType.FIELD);
            }
        }.run();
        assertEquals(SubscriptionRequestType.SNAPSHOT,
                     AbstractMessageTranslator.determineSubscriptionRequestType(message));
    }
    
    public void testGetSymbol()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                AbstractMessageTranslator.getSymbol(null);
            }
        }.run();
        MSymbol google = new MSymbol("GOOG");
        MSymbol msoft = new MSymbol("MSFT");
        Message message = MarketDataFeedTestSuite.generateFIXMessage(Arrays.asList(new MSymbol[] { google, msoft }));
        List<Group> groups = AbstractMessageTranslator.getGroups(message);
        assertEquals(2,
                     groups.size());
        assertEquals(google,
                     AbstractMessageTranslator.getSymbol(groups.get(0)));
        assertEquals(msoft,
                     AbstractMessageTranslator.getSymbol(groups.get(1)));
//        Message message = FIXMessageUtilTest.createOptionNOS(optionRoot, optionContractSpecifier, "200708", new BigDecimal("23"),
//                                                            PutOrCall.CALL, new BigDecimal("1"), new BigDecimal("10"), Side.BUY, msgFactory);

    }
    
}

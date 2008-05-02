package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.marketdata.MarketDataFeedTestSuite;

import quickfix.Message;
import quickfix.field.MDReqID;

/**
 * Base class for all {@link IMessageTranslator} tests.
 * 
 * <p>Test subclasses should extend this class and provide subclass-specific
 * testing.  The superclass provides some basic help like defining a data
 * dictionary for FIX messages and some common testing routines like
 * round-trip FIX message testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public abstract class MessageTranslatorTestBase<T>
        extends TestCase
{
    private static TestSuite sSuite;
    private FIXDataDictionary mFIXDataDictionary;
    
    /**
     * Create a new OpenTickMessageTranslatorTest instance.
     *
     * @param inArg0
     */
    public MessageTranslatorTestBase(String inArg0)
    {
        super(inArg0);
    }
    
    public static Test suite() 
    {
        sSuite = new TestSuite();
        return sSuite;
    }
    
    public static Test suite(Class<?> inClass) 
    {
        sSuite = new TestSuite(inClass);
        return sSuite;
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        FIXDataDictionaryManager.initialize(AbstractMessageTranslator.sMessageVersion, 
                                            AbstractMessageTranslator.sMessageVersion.getDataDictionaryURL());
        mFIXDataDictionary = FIXDataDictionaryManager.getFIXDataDictionary(AbstractMessageTranslator.sMessageVersion);
    }

    public void testRoundTrip()
        throws Exception
    {
        IMessageTranslator<T> translator = getMessageTranslator();
        // generate a FIX message for a market data request
        Message fixMessage = MarketDataFeedTestSuite.generateFIXMessage();
        // translate the FIX message into some other data representation - at this point, we don't
        //  know what
        T xlatedMessage = translator.translate(fixMessage);
        // this message should not be null, but there's not much more we can say about it - in actuality,
        //  it doesn't matter what the format is - it should be sufficient to transmit the contents of the
        //  FIX message to the market data feed server but we cannot evaluate that per se.  if we connected
        //  to the data feed, we could determine empirically if the translation appears to be accurate but
        //  no more than that
        assertNotNull(xlatedMessage);
        // translate the given object back into FIX representation
        Message newMessage = translator.asMessage(xlatedMessage);
        // we can say a lot more about the white box FIX representation
        // first, is it non-null
        assertNotNull(newMessage);
        // next, is it valid
        getFIXDataDictionary().getDictionary().validate(newMessage,
                                                        true);
        // last, and this is the big banana, is it functionally the same as the message we passed in
        // cheat, set the ids to be the same to facilitate comparison
        newMessage.setField(new MDReqID(fixMessage.getString(MDReqID.FIELD)));
        assertEquals(fixMessage.toString(),
                     newMessage.toString());
    }
    
    public void testNullValues()
        throws Exception
    {
        final IMessageTranslator<T> translator = getMessageTranslator();
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                translator.translate((Message)null);
            }            
        }.run();
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                translator.asMessage((T)null);
            }            
        }.run();
    }
    
    /**
     * Returns an {@link IMessageTranslator&lt;T&gt;} instance appropriate for the subclass.
     *
     * <p>The value returned by this method will be used for round-trip FIX message testing.
     * The subclass should return a message translator appropriate for the subclass.
     *
     * @return an<code>IMessageTranslator&lt;T&gt;</code> value
     */
    protected abstract IMessageTranslator<T> getMessageTranslator();

    /**
     * Get the suite value.
     *
     * @return a <code>MessageTranslatorTestBase</code> value
     */
    protected static TestSuite getSuite()
    {
        return sSuite;
    }

    /**
     * Get the mFIXDataDictionary value.
     *
     * @return a <code>MessageTranslatorTestBase</code> value
     */
    protected FIXDataDictionary getFIXDataDictionary()
    {
        return mFIXDataDictionary;
    }
}

package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.marketdata.DataRequest;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataFeedTestSuite;
import org.marketcetera.marketdata.MarketDataRequest;

/**
 * Base class for all {@link DataRequestTranslator} tests.
 * 
 * <p>Test subclasses should extend this class and provide subclass-specific
 * testing.  The superclass provides some basic help like defining a data
 * dictionary for FIX messages and some common testing routines like
 * round-trip FIX message testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
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
        mFIXDataDictionary = FIXDataDictionaryManager.initialize(
                AbstractMessageTranslator.sMessageVersion,
                AbstractMessageTranslator.sMessageVersion.getDataDictionaryURL());
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(mFIXDataDictionary);
    }

    public void testRoundTrip()
        throws Exception
    {
        DataRequestTranslator<T> translator = getMessageTranslator();
        // generate a FIX message for a market data request
        DataRequest request = MarketDataFeedTestSuite.generateDataRequest();
        // translate the FIX message into some other data representation - at this point, we don't
        //  know what
        T xlatedMessage = translator.fromDataRequest(request);
        // this message should not be null, but there's not much more we can say about it - in actuality,
        //  it doesn't matter what the format is - it should be sufficient to transmit the contents of the
        //  FIX message to the market data feed server but we cannot evaluate that per se.  if we connected
        //  to the data feed, we could determine empirically if the translation appears to be accurate but
        //  no more than that
        assertNotNull(xlatedMessage);
        // translate the given object back into a DataRequest
        DataRequest newRequest = translator.toDataRequest(xlatedMessage);
        // we can say a lot more about the white box FIX representation
        // first, is it non-null
        assertNotNull(newRequest);
        assertTrue(request.equivalent(newRequest));
    }
    /**
     * Tests the message translator's ability to handle BBO requests.
     *
     * @throws Exception
     */
    public void testBBO()
        throws Exception
    {
        // construct a BBO market data request
        DataRequest bbo = MarketDataRequest.newTopOfBookRequest("YHOO");
        // now translate the message
        DataRequestTranslator<T> translator = getMessageTranslator();
        T xlatedMessage = translator.fromDataRequest(bbo);
        // this alone isn't necessarily indicative of successful handling, but at least it's a start 
        assertNotNull(xlatedMessage);        
    }
    /**
     * Tests the message translator's ability to handle Full depth of book requests.
     *
     * @throws Exception
     */
    public void testFullBook()
        throws Exception
    {
        // construct a BBO market data request
        DataRequest full = MarketDataRequest.newFullBookRequest("YHOO");
        // now translate the message
        DataRequestTranslator<T> translator = getMessageTranslator();
        T xlatedMessage = translator.fromDataRequest(full);
        // this alone isn't necessarily indicative of successful handling, but at least it's a start 
        assertNotNull(xlatedMessage);        
    }
    public void testNullValues()
        throws Exception
    {
        final DataRequestTranslator<T> translator = getMessageTranslator();
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                translator.fromDataRequest((DataRequest)null);
            }            
        }.run();
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                translator.toDataRequest((T)null);
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
    protected abstract DataRequestTranslator<T> getMessageTranslator();

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

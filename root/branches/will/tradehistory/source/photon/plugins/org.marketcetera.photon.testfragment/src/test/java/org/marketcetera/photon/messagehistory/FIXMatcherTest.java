package org.marketcetera.photon.messagehistory;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.marketdata.AbstractMarketDataFeedTest;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.OrderManagerTest;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXFieldConverterNotAvailable;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.CheckSum;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.SecurityReqID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Urgency;

/* $License$ */

/**
 * Tests {@link FIXMatcher} and its subclasses.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.7.0
 */
public abstract class FIXMatcherTest<T>
    extends TestCase
{
    /**
     * a field that exists in the test message
     */
    protected final int mGoodField = SecurityReqID.FIELD;
    /**
     * a field that does not exist in the test message
     */
    protected final int mBadField = Urgency.FIELD;
    /**
     * the test message
     */
    protected Message mMessage;
    /**
     * the string name of the good field in the test message
     */
    protected String mGoodFieldValue;
    /**
     * the message factory being used in these tests
     */
    protected static FIXMessageFactory sMessageFactory;
    /**
     * Create a new FIXMatcherTest instance.
     *
     * @param inName
     */
    public FIXMatcherTest(String inName)
    {
        super(inName);
    }
    /**
     * Constructs the <code>Test</code> suite necessary to run junit tests.
     *
     * @param inClass a <code>Class&lt;? extends FIXMatcherTest&lt;?&gt;&gt;</code> value
     * @return a <code>Test</code> value
     */
    public static Test suite(Class<? extends FIXMatcherTest<?>> inClass)
    {
        // this is necessary to prepare the logger to work correctly
        BasicConfigurator.configure();
        try {
            // this magic incantation gets the conversion field stuff working
            FIXVersionTestSuite.initializeFIXDataDictionaryManager(FIXVersionTestSuite.ALL_VERSIONS);
            CurrentFIXDataDictionary.setCurrentFIXDataDictionary(FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX_SYSTEM));
            sMessageFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
        } catch (FIXFieldConverterNotAvailable ex) {
            SLF4JLoggerProxy.error(AbstractMarketDataFeedTest.class,
                                   ex);
            fail();
        }
        return new TestSuite(inClass);
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        fail("This test needs to be fixed after the FIX-Away changes");
        mMessage = null; //TODO AbstractMarketDataFeed.securityListRequest();
        mGoodFieldValue = mMessage.getString(mGoodField);
    }
    /**
     * Creates an instance of the test class.
     *
     * @param inFixField an <code>inFixField</code> value containing the field against which to test
     * @param inValue a <code>String</code> value containing the value to compare to the message field
     * @param inInclude a <code>boolean</code> value indicating whether to include or exclude the matches
     * @return a <code>FIXMatcher&lt;T&gt;</code> value
     */
    protected abstract FIXMatcher<T> getInstance(int inFixField,
                                                 String inValue,
                                                 boolean inInclude);
    /**
     * Creates an instance of the test class.
     *
     * @param inFixField an <code>inFixField</code> value containing the field against which to test
     * @param inValue a <code>String</code> value containing the value to compare to the message field
     * @return a <code>FIXMatcher&lt;T&gt;</code> value
     */
    protected abstract FIXMatcher<T> getInstance(int inFixField,
                                                 String inValue);
    /**
     * Gets a list of conditions to test and their expected results.
     * 
     * <p>Subclasses should override this method to populate the parent class's test.  The default
     * implementation returns an empty list.
     *
     * @return a <code>List&lt;MatchTuple&gt;</code> value
     */
    protected List<MatchTuple> getMatchConditions()
    {
        return new ArrayList<MatchTuple>();
    }
    /**
     * Tests match/no match conditions set by the subclass.
     *
     * @throws Exception
     */
    public void testMatches()
        throws Exception
    {
        // collect the test conditions specified by the subclass
        List<MatchTuple> matchConditions = getMatchConditions();
        // execute each test condition
        for(MatchTuple condition : matchConditions) {
            // create a new instance of the subclass to test
            FIXMatcher<T> matcher = getInstance(condition.mFixField,
                                                condition.mValue);
            // this is the FIX message specified as part of the test condition
            ReportHolder holder = new ReportHolder(OrderManagerTest.createReport(condition.mMessage));
            // make sure that the stated condition passes (it should match or not match according to the condition)
            assertEquals(condition.mMatches,
                         matcher.matches(holder));
            // unless the condition states that it can never match (likely because it causes an error), continue testing
            if(!condition.mNeverMatches) {
                // now, create an exclusive condition - the logical inverse of the current condition (it should include everything
                //  *except* the set indicated by the condition)
                matcher = getInstance(condition.mFixField,
                                      condition.mValue,
                                      false);
                assertEquals(!condition.mMatches,
                             matcher.matches(holder));
            }
        }
    }
    /**
     * Tests constructors, getters, and setters of {@link FIXMatcher}.
     *
     * @throws Exception if an error occurs
     */
    public void testConstructorGettersAndSetters()
        throws Exception
    {
        // test constructor with specified include/exclude var
        FIXMatcher<T> matcher = getInstance(1,
                                            "value",
                                            true);
        verifyMatcher(1,
                      "value",
                      true,
                      matcher);
        matcher = getInstance(2,
                              "value2",
                              false);
        verifyMatcher(2,
                      "value2",
                      false,
                      matcher);
        matcher = getInstance(3,
                              "value3");
        verifyMatcher(3,
                      "value3",
                      true,
                      matcher);
        // null message
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                getInstance(mGoodField,
                            null);
            }
        }.run();
        // invalid field
        new ExpectedTestFailure(IllegalArgumentException.class) {
            protected void execute()
                    throws Throwable
            {
                getInstance(-1,
                            mMessage.toString());
            }
        }.run();
        // invalid field, again
        new ExpectedTestFailure(IllegalArgumentException.class) {
            protected void execute()
                    throws Throwable
            {
                getInstance(0,
                            mMessage.toString());
            }
        }.run();
    }
    /**
     * Tests {@link FIXMatcher#getFieldValueString(Message, int)}.
     *
     * @throws Exception if an error occurs
     */
    public void testFieldValueRetrieval()
        throws Exception
    {
        // verify that goodField is good and badField is bad
        assertEquals(mGoodFieldValue,
                     mMessage.getString(mGoodField));
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute()
                    throws Throwable
            {
                mMessage.getString(mBadField);
            }            
        }.run();
        // null message + invalid field
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                FIXMatcher.getFieldValueString(null,
                                               -1);
            }            
        }.run();
        // null message + good field
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                FIXMatcher.getFieldValueString(null,
                                               mGoodField);
            }            
        }.run();
        // non-null message + invalid field
        new ExpectedTestFailure(IllegalArgumentException.class) {
            protected void execute()
                    throws Throwable
            {
                FIXMatcher.getFieldValueString(mMessage,
                                               -1);
            }            
        }.run();
        // message does not contain field
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute()
                    throws Throwable
            {
                FIXMatcher.getFieldValueString(mMessage,
                                               mBadField);
            }            
        }.run();
        // non-null message + good field
        assertEquals(mGoodFieldValue,
                     FIXMatcher.getFieldValueString(mMessage,
                                                    mGoodField));
        // header field
        String headerField = mMessage.getHeader().getString(MsgType.FIELD);
        assertEquals(MsgType.SECURITY_LIST_REQUEST,
                     headerField);
        assertEquals(headerField,
                     FIXMatcher.getFieldValueString(mMessage,
                                                    MsgType.FIELD));
        // cause checksum to be added to message
        assertNotNull(mMessage.toString());
        // trailer field
        String checksumField = mMessage.getTrailer().getString(CheckSum.FIELD);
        assertEquals(checksumField,
                     FIXMatcher.getFieldValueString(mMessage,
                                                    CheckSum.FIELD));
    }
    /**
     * Tests {@link FIXMatcher#convertFIXValueToHumanString(String, int)}.
     *
     * @throws Exception if an error occurs
     */
    public void testConvertFIXValue()
        throws Exception
    {
        // null value
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
            throws Throwable
            {
                FIXMatcher.convertFIXValueToHumanString(null,
                                                        mGoodField);
            }
        }.run();
        // field < 0
        new ExpectedTestFailure(IllegalArgumentException.class) {
            protected void execute()
            throws Throwable
            {
                FIXMatcher.convertFIXValueToHumanString("value",
                                                        -1);
            }
        }.run();
        // field == 0
        new ExpectedTestFailure(IllegalArgumentException.class) {
            protected void execute()
            throws Throwable
            {
                FIXMatcher.convertFIXValueToHumanString("value",
                                                        0);
            }
        }.run();
        // value which has no converter
        assertEquals("value",
                     FIXMatcher.convertFIXValueToHumanString("value",
                                                             Symbol.FIELD));
        // converter exists, but the value doesn't correspond to an entry in photon_fix_messages
        // first, verify that the side we want to test doesn't exist
        String side = "this side does not exist";
        assertEquals(side,
                     FIXFieldLocalizer.getLocalizedFIXValueName("Side",
                                                                side));
        // now, pass this side that does not exist into the converter
        assertEquals(side,
                     FIXMatcher.convertFIXValueToHumanString(side,
                                                             Side.FIELD));
        // and, just to prove that our existing batch of converters does something interesting (not an exhaustive test, just a partial test of each converter):
        // (note that these values are not going to be typically localized, i.e. french users will still use "B" for buy instead of "A" for acheter, or some such)
        assertEquals("B",
                     FIXMatcher.convertFIXValueToHumanString(Side.BUY + "",
                                                             Side.FIELD));
        // check order status
        assertEquals("PARTIAL",
                     FIXMatcher.convertFIXValueToHumanString(OrdStatus.PARTIALLY_FILLED + "",
                                                             OrdStatus.FIELD));
        // check order type
        assertEquals("LMT",
                     FIXMatcher.convertFIXValueToHumanString(OrdType.LIMIT + "",
                                                             OrdType.FIELD));
        // check msg type
        assertEquals("Heartbeat",
                     FIXMatcher.convertFIXValueToHumanString(MsgType.HEARTBEAT + "",
                                                             MsgType.FIELD));
    }
    /**
     * Verifies that the given matcher has the given values.
     *
     * @param inField an <code>int</code> value
     * @param inValue a <code>String</code> value
     * @param inInclude a <code>boolean</code> value
     * @param inMatcher a <code>MockFIXMatcher</code> value
     * @throws Exception if an error occurs
     */
    static <T> void verifyMatcher(int inField,
                                  String inValue,
                                  boolean inInclude,
                                  FIXMatcher<T> inMatcher)
        throws Exception
    {
        assertEquals(inField,
                     inMatcher.getMatcherFIXField());
        assertEquals(inValue,
                     inMatcher.getMatcherValue());
        assertEquals(inInclude,
                     inMatcher.getShouldInclude());
    }
    /**
     * Holds the set of information necessary for a single test condition.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.7.0
     */
    protected static class MatchTuple
    {
        /**
         * Returns a <code>MatchTuple</code> instance with the given condition which
         * expects a match.
         *
         * @param inValue a <code>String</code> value containing the test text to match
         * @param inMessage a <code>Message</code> value containing the <code>FIX</code> message against which to match
         * @param inFixField an <code>int</code> value indicating which field of the <code>FIX</code> message to match
         * @return a <code>MatchTuple</code> value
         */
        protected static MatchTuple match(String inValue,
                                          Message inMessage,
                                          int inFixField)
        {
            return new MatchTuple(true,
                                  inValue,
                                  inMessage,
                                  inFixField,
                                  false);
        }
        /**
         * Returns a <code>MatchTuple</code> instance with the given condition which
         * expects no match.
         *
         * @param inValue a <code>String</code> value containing the test text to match
         * @param inMessage a <code>Message</code> value containing the <code>FIX</code> message against which to match
         * @param inFixField an <code>int</code> value indicating which field of the <code>FIX</code> message to match
         * @return a <code>MatchTuple</code> value
         */
        protected static MatchTuple noMatch(String inValue,
                                            Message inMessage,
                                            int inFixField)
        {
            return new MatchTuple(false,
                                  inValue,
                                  inMessage,
                                  inFixField,
                                  false);
        }
        /**
         * Returns a <code>MatchTuple</code> instance with the given condition which
         * expects never to match.
         *
         * @param inValue a <code>String</code> value containing the test text to match
         * @param inMessage a <code>Message</code> value containing the <code>FIX</code> message against which to match
         * @param inFixField an <code>int</code> value indicating which field of the <code>FIX</code> message to match
         * @return a <code>MatchTuple</code> value
         */
        protected static MatchTuple neverMatches(String inValue,
                                                 Message inMessage,
                                                 int inFixField)
        {
            return new MatchTuple(false,
                                  inValue,
                                  inMessage,
                                  inFixField,
                                  true);
        }
        /**
         * indicates whether the test condition expects to match or not
         */
        private final boolean mMatches;
        /**
         * the value to match
         */
        private final String mValue;
        /**
         * the <code>FIX</code> message against which to match
         */
        private final Message mMessage;
        /**
         * the <code>FIX</code> field in the message to match
         */
        private final int mFixField;
        /**
         * indicates if the test condition will never match (likely because of an error)
         */
        private final boolean mNeverMatches; 
        /**
         * Create a new MatchTuple instance.
         *
         * @param inMatches a <code>boolean</code> value
         * @param inValue a <code>String</code> value
         * @param inMessage a <code>Message</code> value
         * @param inFixField an <code>int</code> value
         * @param inAlwaysFails a <code>boolean</code> value
         */
        private MatchTuple(boolean inMatches,
                           String inValue,
                           Message inMessage,
                           int inFixField,
                           boolean inAlwaysFails)
        {
            mMatches = inMatches;
            mValue = inValue;
            mMessage = inMessage;
            mFixField = inFixField;
            mNeverMatches = inAlwaysFails;
        }
    }
}

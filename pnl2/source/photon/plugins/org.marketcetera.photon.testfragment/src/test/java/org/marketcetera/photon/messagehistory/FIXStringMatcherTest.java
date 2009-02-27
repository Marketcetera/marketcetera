package org.marketcetera.photon.messagehistory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.field.Urgency;

/* $License$ */

/**
 * Tests {@link FIXStringMatcher}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.7.0
 */
public class FIXStringMatcherTest
        extends FIXMatcherTest<String>
{
    /**
     * Constructs the <code>Test</code> suite necessary to run junit tests.
     *
     * @return a <code>Test</code> value
     */
    public static Test suite()
    {
        return FIXMatcherTest.suite(FIXStringMatcherTest.class);
    }
    /**
     * Create a new FIXStringMatcherTest instance.
     *
     * @param inName
     */
    public FIXStringMatcherTest(String inName)
    {
        super(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.messagehistory.FIXMatcherTest#getMatchConditions()
     */
    @Override
    protected List<MatchTuple> getMatchConditions()
    {
        final Message buyOrder = sMessageFactory.newMarketOrder(Long.toString(System.currentTimeMillis()),
                                                                Side.BUY,
                                                                new BigDecimal("100.00"),
                                                                new MSymbol("GOOG"),
                                                                TimeInForce.GOOD_TILL_CANCEL,
                                                                "account");
        final List<MatchTuple> conditions = new ArrayList<MatchTuple>();
        conditions.add(MatchTuple.neverMatches("B",
                                               null,
                                               Side.FIELD));
        // regular field matches
        conditions.add(MatchTuple.match("GOOG",
                                        buyOrder,
                                        Symbol.FIELD));
        // regular field does not match
        conditions.add(MatchTuple.noMatch("MSFT",
                                          buyOrder,
                                          Symbol.FIELD));
        // assert that the urgency field is *not* in the message
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute()
                    throws Throwable
            {
                FIXMatcher.getFieldValueString(mMessage,
                                               mBadField);
            }
        }.run();
        // regular field not in message
        conditions.add(MatchTuple.neverMatches("0",
                                               mMessage,
                                               Urgency.FIELD));
        // conversion field matches
        conditions.add(MatchTuple.match("B",
                                        buyOrder,
                                        Side.FIELD));
        // conversion field does not match
        conditions.add(MatchTuple.noMatch("S",
                                          buyOrder,
                                          Side.FIELD));
        return conditions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.messagehistory.FIXMatcherTest#getInstance(int, java.lang.String, boolean)
     */
    @Override
    protected FIXMatcher<String> getInstance(int inFixField,
                                             String inValue,
                                             boolean inInclude)
    {
        return new MockFIXStringMatcher(inFixField,
                                        inValue,
                                        inInclude);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.messagehistory.FIXMatcherTest#getInstance(int, java.lang.String)
     */
    @Override
    protected FIXMatcher<String> getInstance(int inFixField,
                                             String inValue)
    {
        return new MockFIXStringMatcher(inFixField,
                                        inValue);
    }
    /**
     * Test implementation of <code>FIXStringMatcher</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.7.0
     */
    private static class MockFIXStringMatcher
        extends FIXStringMatcher
    {
        /**
         * Create a new MockFIXStringMatcher instance.
         *
         * @param inFixField
         * @param inValue
         */
        public MockFIXStringMatcher(int inFixField,
                                    String inValue)
        {
            super(inFixField,
                  inValue);
        }
        /**
         * Create a new MockFIXStringMatcher instance.
         *
         * @param inFixField
         * @param inValue
         * @param inShouldInclude
         */
        public MockFIXStringMatcher(int inFixField,
                                    String inValue,
                                    boolean inShouldInclude)
        {
            super(inFixField,
                  inValue,
                  inShouldInclude);
        }        
    }
}

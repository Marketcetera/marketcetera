package org.marketcetera.photon.messagehistory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.trade.MSymbol;

import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

/* $License$ */

/**
 * Tests {@link FIXRegexMatcher}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.7.0
 */
public class FIXRegexMatcherTest
        extends FIXMatcherTest<String>
{
    /**
     * Constructs the <code>Test</code> suite necessary to run junit tests.
     *
     * @return a <code>Test</code> value
     */
    public static Test suite()
    {
        return FIXMatcherTest.suite(FIXRegexMatcherTest.class);
    }
    /**
     * Create a new FIXRegexMatcherTest instance.
     *
     * @param inName
     */
    public FIXRegexMatcherTest(String inName)
    {
        super(inName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.messagehistory.FIXMatcherTest#getInstance(int, java.lang.String, boolean)
     */
    @Override
    protected FIXMatcher<String> getInstance(int inFixField,
                                             String inValue,
                                             boolean inInclude)
    {
        return new MockFIXRegexMatcher(inFixField,
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
        return new MockFIXRegexMatcher(inFixField,
                                       inValue);
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
                                                                "ACCT-1");
        List<MatchTuple> conditions = new ArrayList<MatchTuple>();
        conditions.add(MatchTuple.neverMatches("B",
                                               null,
                                               Side.FIELD));
        // regular field matches
        conditions.add(MatchTuple.match("GOOG",
                                        buyOrder,
                                        Symbol.FIELD));
        // regular field does not match
        conditions.add(MatchTuple.noMatch("GOOGx",
                                          buyOrder,
                                          Symbol.FIELD));
        // regular field matches with regex
        conditions.add(MatchTuple.match("G([O|X].*)G",
                                        buyOrder,
                                        Symbol.FIELD));
        // regular field does not match with regex
        conditions.add(MatchTuple.noMatch("G([X|Y|Z].*)G",
                                          buyOrder,
                                          Symbol.FIELD));
        // conversion field matches
        conditions.add(MatchTuple.match("B",
                                        buyOrder,
                                        Side.FIELD));
        // conversion field does not match
        conditions.add(MatchTuple.noMatch("S",
                                          buyOrder,
                                          Side.FIELD));
        // conversion field does not match
        conditions.add(MatchTuple.noMatch("X",
                                          buyOrder,
                                          Side.FIELD));
        // conversion field matches regex
        conditions.add(MatchTuple.match("[B|S]",
                                        buyOrder,
                                        Side.FIELD));
        // conversion field matches regex
        conditions.add(MatchTuple.match("[B|S|X]",
                                        buyOrder,
                                        Side.FIELD));
        // conversion field does not match regex
        conditions.add(MatchTuple.noMatch("[S|X]",
                                          buyOrder,
                                          Side.FIELD));
        return conditions;
    }
    /**
     * Test implementation of <code>FIXRegexMatcher</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.7.0
     */
    private static class MockFIXRegexMatcher
        extends FIXRegexMatcher
    {
        /**
         * Create a new MockFIXStringMatcher instance.
         *
         * @param inFixField
         * @param inValue
         */
        public MockFIXRegexMatcher(int inFixField,
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
        public MockFIXRegexMatcher(int inFixField,
                                   String inValue,
                                   boolean inShouldInclude)
        {
            super(inFixField,
                  inValue,
                  inShouldInclude);
        }        
    }
}

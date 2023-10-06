package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.marketcetera.admin.impl.SimpleUser;

/* $License$ */

/**
 * Provides common utilities for suggestion tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SuggestionTestBase
        extends TypesTestBase
{
    /**
     * Verifies that the given suggestions are equal, including the order ids.
     *
     * @param inSuggest1 an <code>OrderSingleSuggestion</code> value
     * @param inSuggest2 an <code>OrderSingleSuggestion</code> value
     */
    protected void assertOrderSuggestionEquals(OrderSingleSuggestion inSuggest1,
                                               OrderSingleSuggestion inSuggest2)
    {
        assertOrderSuggestionEquals(inSuggest1,
                                    inSuggest2,
                                    false);
    }
    /**
     * Verifies that the given suggestions are equal, either including or excluding the order id, as indicated.
     *
     * @param inSuggest1 an <code>OrderSingleSuggestion</code> value
     * @param inSuggest2 an <code>OrderSingleSuggestion</code> value
     * @param inIgnoreOrderId a <code>boolean</code> value
     */
    protected void assertOrderSuggestionEquals(OrderSingleSuggestion inSuggest1,
                                               OrderSingleSuggestion inSuggest2,
                                               boolean inIgnoreOrderId)
    {
        if(checkForNull(inSuggest1,inSuggest2)) {
            return;
        }
        assertSuggestionEquals(inSuggest1, 
                               inSuggest2);
        assertOrderSingleEquals(inSuggest1.getOrder(),
                                inSuggest2.getOrder(),
                                inIgnoreOrderId);
    }
    /**
     * Verifies that the given suggestions are equal.
     *
     * @param inSuggest1 a <code>Suggestion</code> value
     * @param inSuggest2 a <code>Suggestion</code> value
     */
    protected void assertSuggestionEquals(Suggestion inSuggest1,
                                          Suggestion inSuggest2)
    {
        assertEquals(inSuggest1.getIdentifier(),
                     inSuggest2.getIdentifier());
        assertEquals(inSuggest1.getScore(),
                     inSuggest2.getScore());
        assertEquals(inSuggest1.getUser(),
                     inSuggest2.getUser());
    }
    /**
     * Tests the setter methods of the given suggestion.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    protected void checkSuggestionSetters(Suggestion inSuggestion)
    {
        inSuggestion.setIdentifier(null);
        assertEquals(null,
                     inSuggestion.getIdentifier());
        String ident = "what?";
        inSuggestion.setIdentifier(ident);
        assertEquals(ident,
                     inSuggestion.getIdentifier());
        inSuggestion.setIdentifier(null);
        assertEquals(null,
                     inSuggestion.getIdentifier());
        inSuggestion.setScore(null);
        assertEquals(null,
                     inSuggestion.getScore());
        BigDecimal score = new BigDecimal("3435.34");
        inSuggestion.setScore(score);
        assertEquals(score,
                     inSuggestion.getScore());
        inSuggestion.setScore(null);
        assertEquals(null,
                     inSuggestion.getScore());
        SimpleUser user = new SimpleUser("user-name",
                                         "user description",
                                         "hashed-password",
                                         true);
        inSuggestion.setUser(user);
        assertEquals(user,
                     inSuggestion.getUser());
        inSuggestion.setUser(null);
        assertEquals(null,
                     inSuggestion.getUser());
    }
    /**
     * Verifies the given suggestion has the given values.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     * @param inIdentifier an <code>Object</code> value
     * @param inScore an <code>Object</code> value
     */
    protected void assertSuggestionValues(Suggestion inSuggestion,
                                          Object inIdentifier,
                                          Object inScore)
    {
        assertEquals(inIdentifier, inSuggestion.getIdentifier());
        assertEquals(inScore, inSuggestion.getScore());
    }
    /**
     * creates new {@link Suggestion} objects
     */
    protected SuggestionFactory suggestionFactory = new SimpleSuggestionFactory();
}

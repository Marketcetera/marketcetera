package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SuggestionTestBase
        extends TypesTestBase
{
    protected void assertOrderSuggestionEquals(OrderSingleSuggestion inSuggest1,
                                               OrderSingleSuggestion inSuggest2)
    {
        assertOrderSuggestionEquals(inSuggest1, inSuggest2, false);
    }
    protected void assertOrderSuggestionEquals(OrderSingleSuggestion inSuggest1,
                                               OrderSingleSuggestion inSuggest2,
                                               boolean inIgnoreOrderID)
    {
        if (checkForNull(inSuggest1, inSuggest2)) return;
        assertSuggestionEquals(inSuggest1,  inSuggest2);
        assertOrderSingleEquals(inSuggest1.getOrder(),
                inSuggest2.getOrder(), inIgnoreOrderID);
    }
    protected void assertSuggestionEquals(Suggestion inSuggest1,
                                          Suggestion inSuggest2)
    {
        assertEquals(inSuggest1.getIdentifier(), inSuggest2.getIdentifier());
        assertEquals(inSuggest1.getScore(), inSuggest2.getScore());
    }

    protected void checkSuggestionSetters(Suggestion inSuggestion)
    {
        inSuggestion.setIdentifier(null);
        assertEquals(null, inSuggestion.getIdentifier());
        String ident = "what?";
        inSuggestion.setIdentifier(ident);
        assertEquals(ident, inSuggestion.getIdentifier());
        inSuggestion.setIdentifier(null);
        assertEquals(null, inSuggestion.getIdentifier());

        inSuggestion.setScore(null);
        assertEquals(null, inSuggestion.getScore());
        BigDecimal score = new BigDecimal("3435.34");
        inSuggestion.setScore(score);
        assertEquals(score, inSuggestion.getScore());
        inSuggestion.setScore(null);
        assertEquals(null, inSuggestion.getScore());
    }

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

package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates the implementor has a {@link Suggestion} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSuggestion
{
    /**
     * Get the suggestion value.
     *
     * @return a <code>Suggestion</code> value
     */
    Suggestion getSuggestion();
}

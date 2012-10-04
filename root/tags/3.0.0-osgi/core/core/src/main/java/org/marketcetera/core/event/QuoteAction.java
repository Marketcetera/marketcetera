package org.marketcetera.core.event;

/**
 * Indicates the action to be taken.
 *
 * @version $Id: QuoteAction.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.6.0
 */
public enum QuoteAction
{
    /**
     * the quote should be added
     */
    ADD,
    /**
     * the quote should replace an existing quote
     */
    CHANGE,
    /**
     * the quote should be deleted
     */
    DELETE
}
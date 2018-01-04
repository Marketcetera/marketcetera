package org.marketcetera.trade;

/* $License$ */

/**
 * Identifies a suggestion handling command.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum SuggestionAction
{
    /**
     * add the suggestion to the suggestion list (server-to-client command)
     */
    ADD,
    /**
     * clear all suggestions (server-to-client command)
     */
    CLEAR,
    /**
     * indicates a suggestion was deleted (client-to-server and server-to-client command)
     */
    DELETE,
    /**
     * resend all suggestions (client-to-server command)
     */
    REFRESH,
    /**
     * indicates a suggestion was sent (client-to-server command)
     */
    SEND;
}

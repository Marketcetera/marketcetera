package org.marketcetera.client.userlimit;

import org.marketcetera.util.log.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("userlimits");  //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    public I18NMessage0P NULL_SYMBOL = new I18NMessage0P(LOGGER,
                                                         "null_symbol");  //$NON-NLS-1$
    public I18NMessage0P NULL_MAX_POSITION = new I18NMessage0P(LOGGER,
                                                               "null_max_position");  //$NON-NLS-1$
    public I18NMessage0P NULL_MAX_VALUE = new I18NMessage0P(LOGGER,
                                                            "null_max_value");  //$NON-NLS-1$
    public I18NMessage0P NULL_MAX_DEVIATION_FROM_LAST = new I18NMessage0P(LOGGER,
                                                                          "null_max_deviation_from_last");  //$NON-NLS-1$
    public I18NMessage0P NULL_MAX_DEVIATION_FROM_MID = new I18NMessage0P(LOGGER,
                                                                         "null_max_deviation_from_mid");  //$NON-NLS-1$
    public I18NMessage1P INVALID_MAX_POSITION = new I18NMessage1P(LOGGER,
                                                                  "invalid_max_position");  //$NON-NLS-1$
    public I18NMessage1P INVALID_MAX_VALUE = new I18NMessage1P(LOGGER,
                                                               "invalid_max_value");  //$NON-NLS-1$
    public I18NMessage1P INVALID_MAX_DEVIATION_FROM_LAST = new I18NMessage1P(LOGGER,
                                                                             "invalid_max_deviation_from_last");  //$NON-NLS-1$
    public I18NMessage1P INVALID_MAX_DEVIATION_FROM_MID = new I18NMessage1P(LOGGER,
                                                                            "invalid_max_deviation_from_mid");  //$NON-NLS-1$
    public I18NMessage3P INVALID_SYMBOL_CHARACTERS = new I18NMessage3P(LOGGER,
                                                                       "invalid_symbol_characters");  //$NON-NLS-1$
    public I18NMessage0P LIST_DELIMITER = new I18NMessage0P(LOGGER,
                                                            "list_delimiter");  //$NON-NLS-1$
    public I18NMessage2P LESS_THAN_A_PENNY = new I18NMessage2P(LOGGER,
                                                               "less_than_a_penny");  //$NON-NLS-1$
    public I18NMessage2P NO_SYMBOL_DATA = new I18NMessage2P(LOGGER,
                                                            "no_symbol_data");  //$NON-NLS-1$
    public I18NMessage3P POSITION_LIMIT_EXCEEDED = new I18NMessage3P(LOGGER,
                                                                     "position_limit_exceeded");  //$NON-NLS-1$
    public I18NMessage3P VALUE_LIMIT_EXCEEDED = new I18NMessage3P(LOGGER,
                                                                  "value_limit_exceeded");  //$NON-NLS-1$
    public I18NMessage2P NO_TRADE_DATA = new I18NMessage2P(LOGGER,
                                                           "no_trade_data");  //$NON-NLS-1$
    public I18NMessage2P MAX_DEVIATION_FROM_LAST_EXCEEDED = new I18NMessage2P(LOGGER,
                                                                              "max_deviation_from_last_exceeded");  //$NON-NLS-1$
    public I18NMessage0P NO_QUOTE_DATA = new I18NMessage0P(LOGGER,
                                                           "no_quote_data");  //$NON-NLS-1$
    public I18NMessage2P MAX_DEVIATION_FROM_MID_EXCEEDED = new I18NMessage2P(LOGGER,
                                                                             "max_deviation_from_mid_exceeded");  //$NON-NLS-1$
}

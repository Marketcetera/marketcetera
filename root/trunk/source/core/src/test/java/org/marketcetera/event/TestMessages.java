package org.marketcetera.event;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessage6P;
import org.marketcetera.util.log.I18NMessageNP;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Test messages for event tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public interface TestMessages
{
    /**
     * the message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("core_event_test");
    /**
     * the logger
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /*
     * the messages
     */
    static final I18NMessage0P MESSAGE_0P = new I18NMessage0P(LOGGER,
                                                              "message_0p");
    static final I18NMessage1P MESSAGE_1P = new I18NMessage1P(LOGGER,
                                                              "message_1p");
    static final I18NMessage2P MESSAGE_2P = new I18NMessage2P(LOGGER,
                                                              "message_2p");
    static final I18NMessage3P MESSAGE_3P = new I18NMessage3P(LOGGER,
                                                              "message_3p");
    static final I18NMessage4P MESSAGE_4P = new I18NMessage4P(LOGGER,
                                                              "message_4p");
    static final I18NMessage5P MESSAGE_5P = new I18NMessage5P(LOGGER,
                                                              "message_5p");
    static final I18NMessage6P MESSAGE_6P = new I18NMessage6P(LOGGER,
                                                              "message_6p");
    static final I18NMessageNP MESSAGE_NP = new I18NMessageNP(LOGGER,
                                                              "message_np");
}

package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
final class Messages {
	
	static I18NMessage0P MARKET_DATA_FEED_INVALID_PROVIDER_TYPE;
	static I18NMessage0P MARKET_DATA_FEED_NOT_SINGLETON;
	static I18NMessage0P MARKET_DATA_FEED_NOT_EMITTER;
	static I18NMessage0P MARKET_DATA_FEED_INVALID_OBJECT_NAME;
	static I18NMessage0P MARKET_DATA_FEED_INVALID_INTERFACE;
	static I18NMessage1P MARKET_DATA_FEED_INVALID_STATUS_NOTIFICATION;
	static I18NMessage1P MARKET_DATA_FEED_FAILED_TO_DETERMINE_CAPABILITY;

	static I18NMessage1P MARKET_DATA_MANAGER_IGNORING_PROVIDER;
	static I18NMessage1P MARKET_DATA_MANAGER_FEED_START_FAILED;
	static I18NMessage1P MARKET_DATA_MANAGER_FEED_RECONNECT_FAILED;

	static I18NMessage0P MARKET_DATA_RECEIVER_FACTORY_DESCRIPTION;

	static I18NMessage0P MARKET_DATA_RECEIVER_NO_SUBSCRIBER;
	static I18NMessage0P MARKET_DATA_RECEIVER_NO_REQUEST;
	static I18NMessage1P MARKET_DATA_RECEIVER_NO_SOURCE;

	static I18NMessage2P DATA_FLOW_MANAGER_UNEXPECTED_DATA;
	static I18NMessage2P DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID;
	static I18NMessage2P DATA_FLOW_MANAGER_EVENT_INSTRUMENT_MISMATCH;
	static I18NMessage3P DATA_FLOW_MANAGER_CAPABILITY_UNSUPPORTED;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }

}

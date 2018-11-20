package org.marketcetera.marketdata.manual;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */

/**
 * Internationalization messages for the manual market data adapter.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 16191 2012-06-27 00:13:01Z colin $
 * @since $RELEASE$
 */
@ClassVersion("$Id: Messages.java 16191 2012-06-27 00:13:01Z colin $")
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketdata_manual", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                        "provider_description"); //$NON-NLS-1$
    static final I18NMessage0P DATA_REQUEST_PAYLOAD_REQUIRED = new I18NMessage0P(LOGGER,"data_request_payload_required"); //$NON-NLS-1$
    static final I18NMessage2P INVALID_DATA_REQUEST_PAYLOAD = new I18NMessage2P(LOGGER,"invalid_data_request_payload"); //$NON-NLS-1$
    static final I18NMessage1P UNSUPPORTED_DATA_REQUEST_PAYLOAD = new I18NMessage1P(LOGGER,"unsupported_data_request_payload"); //$NON-NLS-1$
}

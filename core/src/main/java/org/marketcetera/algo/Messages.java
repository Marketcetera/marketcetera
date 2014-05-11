package org.marketcetera.algo;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages for this package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 16604 2013-06-26 14:49:42Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: Messages.java 16604 2013-06-26 14:49:42Z colin $")
public interface Messages
{
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("core_algo");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage2P ALGO_TAG_VALUE_PATTERN_MISMATCH = new I18NMessage2P(LOGGER,"algo_tag_value_pattern_mismatch"); //$NON-NLS-1$
    static final I18NMessage1P ALGO_TAG_VALUE_REQUIRED = new I18NMessage1P(LOGGER,"algo_tag_value_required"); //$NON-NLS-1$
    static final I18NMessage2P ALGO_SPEC_MISMATCH = new I18NMessage2P(LOGGER,"algo_spec_mismatch"); //$NON-NLS-1$
}

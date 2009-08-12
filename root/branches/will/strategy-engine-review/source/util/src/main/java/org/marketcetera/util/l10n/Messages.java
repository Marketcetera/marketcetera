package org.marketcetera.util.l10n;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_l10n"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage1P NONSTATIC_FIELD_IGNORED=
        new I18NMessage1P(LOGGER,"nonstatic_field_ignored"); //$NON-NLS-1$
    static final I18NMessage1P NULL_FIELD_IGNORED=
        new I18NMessage1P(LOGGER,"null_field_ignored"); //$NON-NLS-1$
    static final I18NMessage1P INTROSPECTION_FAILED=
        new I18NMessage1P(LOGGER,"introspection_failed"); //$NON-NLS-1$
    static final I18NMessage1P MISSING_PROVIDER=
        new I18NMessage1P(LOGGER,"missing_provider"); //$NON-NLS-1$
    static final I18NMessage1P NONEXISTENT_RESOURCE=
        new I18NMessage1P(LOGGER,"nonexistent_resource"); //$NON-NLS-1$
    static final I18NMessage1P LOADING_FAILED=
        new I18NMessage1P(LOGGER,"loading_failed"); //$NON-NLS-1$
    static final I18NMessage1P BAD_TEXT=
        new I18NMessage1P(LOGGER,"bad_text"); //$NON-NLS-1$
    static final I18NMessage3P PARAM_COUNT_MISMATCH=
        new I18NMessage3P(LOGGER,"param_count_mismatch"); //$NON-NLS-1$
    static final I18NMessage1P EXTRA_SRC_MESSAGE=
        new I18NMessage1P(LOGGER,"extra_src_message"); //$NON-NLS-1$
    static final I18NMessage1P EXTRA_DST_MESSAGE=
        new I18NMessage1P(LOGGER,"extra_dst_message"); //$NON-NLS-1$
}

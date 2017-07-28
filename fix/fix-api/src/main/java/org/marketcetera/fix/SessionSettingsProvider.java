package org.marketcetera.fix;

import quickfix.SessionSettings;

/* $License$ */

/**
 * Provides QuickFix/J session settings.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionSettingsProvider
{
    /**
     * Create QFJ session settings.
     *
     * @return a <code>SessionSettings</code> value
     */
    SessionSettings create();
}

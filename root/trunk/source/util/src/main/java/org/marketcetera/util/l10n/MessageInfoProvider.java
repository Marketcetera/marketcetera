package org.marketcetera.util.l10n;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A provider of meta-information for a message collection.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface MessageInfoProvider
{
    /**
     * Returns the receiver's meta-information.
     *
     * @return The meta-information.
     */

    MessageInfo[] getMessageInfo();
}

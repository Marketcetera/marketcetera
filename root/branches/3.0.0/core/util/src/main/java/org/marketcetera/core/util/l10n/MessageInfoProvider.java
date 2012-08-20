package org.marketcetera.core.util.l10n;

import java.util.List;
import org.marketcetera.api.attributes.ClassVersion;

/**
 * A provider of meta-information for a message collection.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id: MessageInfoProvider.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@ClassVersion("$Id: MessageInfoProvider.java 16063 2012-01-31 18:21:55Z colin $")
public interface MessageInfoProvider
{
    /**
     * Returns the receiver's meta-information.
     *
     * @return The meta-information.
     */

    List<MessageInfo> getMessageInfo();
}

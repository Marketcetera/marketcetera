package org.marketcetera.util.l10n;

import java.util.List;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A provider of meta-information for a message collection.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id: MessageInfoProvider.java 16154 2012-07-14 16:34:05Z colin $
 */

/* $License$ */

@ClassVersion("$Id: MessageInfoProvider.java 16154 2012-07-14 16:34:05Z colin $")
public interface MessageInfoProvider
{
    /**
     * Returns the receiver's meta-information.
     *
     * @return The meta-information.
     */

    List<MessageInfo> getMessageInfo();
}

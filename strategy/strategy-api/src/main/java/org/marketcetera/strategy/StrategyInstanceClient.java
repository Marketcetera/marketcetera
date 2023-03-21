package org.marketcetera.strategy;

import org.marketcetera.core.notifications.INotification.Severity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface StrategyInstanceClient
{
    void emitMessage(Severity inSeverity,
                     String inMessage);
}

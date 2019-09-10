package org.marketcetera.trade.event;

import org.marketcetera.admin.HasUser;

/* $License$ */

/**
 * Indicates an incoming FIX Message event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface InjectedFixMessageEvent
        extends FixMessageEvent,HasUser
{
}

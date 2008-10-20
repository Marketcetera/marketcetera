package org.marketcetera.systemmodel;

import java.util.Date;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public interface PositionQuery
{
    public Position getPositionAsOf(Date inDate);
}

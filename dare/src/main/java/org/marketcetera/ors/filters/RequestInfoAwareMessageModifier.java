package org.marketcetera.ors.filters;

import org.marketcetera.ors.info.RequestInfo;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RequestInfoAwareMessageModifier
        extends MessageModifier
{
    boolean modifyMessage(RequestInfo inRequestInfo);
}

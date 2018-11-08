package com.marketcetera.ors.filters;

import com.marketcetera.ors.info.RequestInfo;

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

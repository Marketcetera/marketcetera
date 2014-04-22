package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates <code>SAClient</code> instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface SAClientFactory
{
    /**
     * Creates an <code>SAClient</code> instance.
     *
     * @param inParameters an <code>SAClientParameters</code> value
     * @return an <code>SAClient</code> value
     */
    public SAClient create(SAClientParameters inParameters);
}

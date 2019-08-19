package org.marketcetera.fix;

import java.util.Map;

import org.marketcetera.core.MutableDomainObjectFactory;

/* $License$ */

/**
 * Creates {@link FixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableFixSessionFactory
        extends MutableDomainObjectFactory<FixSession,MutableFixSession>,FixSessionFactory
{
    /**
     * Create a <code>FixSession</code> object.
     *
     * @param inAttributes a <code>Map&lt;String,String&gt;</code> value
     * @return a <code>FixSession</code> value
     */
    @Override
    MutableFixSession create(Map<String,String> inAttributes);
}

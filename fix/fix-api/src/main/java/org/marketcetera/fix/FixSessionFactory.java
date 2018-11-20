package org.marketcetera.fix;

import java.util.Map;

import org.marketcetera.core.DomainObjectFactory;

/* $License$ */

/**
 * Creates {@link FixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionFactory
        extends DomainObjectFactory<FixSession>
{
    /**
     * Create a <code>FixSession</code> object.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     */
    FixSession create(FixSession inFixSession);
    /**
     * Create a <code>FixSession</code> object.
     *
     * @param inAttributes a <code>Map&lt;String,String&gt;</code> value
     * @return a <code>FixSession</code> value
     */
    FixSession create(Map<String,String> inAttributes);
}

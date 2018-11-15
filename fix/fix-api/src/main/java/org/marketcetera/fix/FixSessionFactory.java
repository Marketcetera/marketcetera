package org.marketcetera.fix;

import java.util.Map;


/* $License$ */

/**
 * Creates {@link FixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionFactory
{
    /**
     * Create a <code>FixSession</code> object.
     *
     * @return a <code>FixSession</code> value
     */
    FixSession create();
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

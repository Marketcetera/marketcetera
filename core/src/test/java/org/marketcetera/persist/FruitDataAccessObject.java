package org.marketcetera.persist;

import java.util.List;

/* $License$ */

/**
 * Provides datastore access to <code>Fruit</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FruitDataAccessObject
        extends TestNDDataAccessObject<Fruit>
{
    /**
     * Get all fruit of the given type.
     *
     * @param inType a <code>Fruit.Type</code> value
     * @return a <code>List&lt;Fruit&gt;</code> value
     */
    public List<Fruit> getByType(Fruit.Type inType);
}

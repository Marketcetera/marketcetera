package org.marketcetera.persist;

import java.util.List;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FruitService
        extends NDEntityService<Fruit>
{
    public Fruit add(String inName,
                     String inDescription,
                     Fruit.Type inType);
}

package org.marketcetera.persist;

import org.springframework.data.repository.NoRepositoryBean;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NoRepositoryBean
public interface FruitService
        extends TestEntityService<Fruit>
{
    public Fruit add(String inName,
                     String inDescription,
                     Fruit.Type inType);
}

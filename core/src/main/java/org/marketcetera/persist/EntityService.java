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
public interface EntityService<Clazz extends EntityBase>
{
    public void create(Clazz inData);
    public List<Clazz> findAll();
}

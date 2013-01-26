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
    public Clazz create(Clazz inData);
    public Clazz read(long inId);
    public List<Clazz> readAll();
    public Clazz update(Clazz inData);
    public void delete(Clazz inData);
}

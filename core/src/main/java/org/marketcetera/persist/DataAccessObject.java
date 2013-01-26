package org.marketcetera.persist;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Order;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Provides datastore access to system objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface DataAccessObject<Clazz extends EntityBase>
{
    /**
     * Gets the <code>Clazz</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Clazz</code> value
     * @throws EntityNotFoundException if no object exists with the given id
     */
    public Clazz getById(long inId);
    /**
     * Gets all <code>Clazz</code> values.
     *
     * @return a <code>List&lt;Clazz&gt;</code> value
     */
    public List<Clazz> getAll();
    /**
     * Gets all <code>Clazz</code> values in the indicated order.
     *
     * @param inOrderBy a <code>List&lt;Order&gt;</code> value
     * @return a <code>List&lt;Clazz&gt;</code> value
     */
    public List<Clazz> getAll(List<Order> inOrderBy);
    /**
     * Gets the indicated number of <code>Clazz</code> values starting from the given point.
     *
     * @param inPageSize an <code>int</code> value
     * @param inFirstResult an <code>int</code> value
     * @param inOrderBy a <code>List&lt;Order&gt;</code> value
     * @return a <code>List&lt;Clazz&gt;</code> value
     */
    public List<Clazz> getAll(int inPageSize,
                              int inFirstResult,
                              List<Order> inOrderBy);
    /**
     * Saves the given <code>Clazz</code> to the database.
     *
     * @param inData a <code>Clazz</code> value
     */
    public Clazz persist(Clazz inData);
    /**
     * Deletes the given <code>Clazz</code> from the database.
     *
     * @param inData a <code>Clazz</code> value
     */
    public void remove(Clazz inData);
    /**
     * Gets the total number of <code>Clazz</code> objects in the database.
     *
     * @return an <code>int</code> value
     */
    public int getCount();
}

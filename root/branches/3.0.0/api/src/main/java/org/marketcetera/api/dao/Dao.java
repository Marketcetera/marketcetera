package org.marketcetera.api.dao;

import java.util.List;

import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 * Provides datastore access to system objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Dao<Clazz extends SystemObject>
{
    /**
     * Gets the <code>Clazz</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Clazz</code> value
     */
    public Clazz getById(long inId);
    /**
     * Gets all <code>Clazz</code> values.
     *
     * @return a <code>List&lt;Clazz&gt;</code> value
     */
    public List<Clazz> getAll();
    /**
     * Saves the given <code>Clazz</code> to the database.
     *
     * @param inData a <code>Clazz</code> value
     */
    public void save(Clazz inData);
    /**
     * Adds the given <code>Clazz</code> to the database.
     *
     * @param inData a <code>Clazz</code> value
     */
    public void add(Clazz inData);
    /**
     * Deletes the given <code>Clazz</code> from the database.
     *
     * @param inData a <code>Clazz</code> value
     */
    public void delete(Clazz inData);
    /**
     * Gets the total number of <code>Clazz</code> objects in the database.
     *
     * @return an <code>int</code> value
     */
    public int getCount();
}

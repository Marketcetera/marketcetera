package org.marketcetera.core.systemmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.api.systemmodel.NamedObject;
import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 * Provides a test data store of a given object type.
 *
 * @version $Id$
 * @since $Release$
 */
public class TestDataStore<DataClazz extends NamedObject & SystemObject>
{
    /**
     * Create a new TestDataStore instance.
     */
    public TestDataStore()
    {
        dataByName = new HashMap<String,DataClazz>();
        dataById = new HashMap<Long,DataClazz>();
    }
    /**
     * Adds the given data to the test data store.
     *
     * @param inData a <code>DataClazz</code> value
     */
    public void add(DataClazz inData)
    {
        dataByName.put(inData.getName(),
                       inData);
        dataById.put(inData.getId(),
                     inData);
    }
    /**
     * Removes the given data from the test data store.
     *
     * @param inData a <code>DataClazz</code> value
     */
    public void remove(DataClazz inData)
    {
        dataByName.remove(inData.getName());
        dataById.remove(inData.getId());
    }
    /**
     * Gets the data with the given name from the test data store.
     *
     * @param inName a <code>String</code> value
     * @return a <code>DataClazz</code> value or <code>null</code>
     */
    public DataClazz getByName(String inName)
    {
        return dataByName.get(inName);
    }
    /**
     * Gets the data with the given id from the test data store.
     *
     * @param inId a <code>long</code> value
     * @return a <code>DataClazz</code> value
     */
    public DataClazz getById(long inId)
    {
        return dataById.get(inId);
    }
    /**
     * Gets all data from the test data store.
     *
     * @return a <code>Collection&lt;DataClazz&gt;</code> value
     */
    public Collection<DataClazz> getAll()
    {
        return dataByName.values();
    }
    /**
     * test data values by name
     */
    private final Map<String,DataClazz> dataByName;
    /**
     * test data values by id
     */
    private final Map<Long,DataClazz> dataById;

}

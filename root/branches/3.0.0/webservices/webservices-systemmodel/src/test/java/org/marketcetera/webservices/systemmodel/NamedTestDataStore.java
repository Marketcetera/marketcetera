package org.marketcetera.webservices.systemmodel;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.api.systemmodel.NamedObject;
import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NamedTestDataStore.java 16254 2012-09-04 23:19:20Z colin $
 * @since $Release$
 */
public class NamedTestDataStore<DataClazz extends SystemObject & NamedObject>
        extends TestDataStore<DataClazz>
{
    /**
     * Create a new NamedTestDataStore instance.
     */
    public NamedTestDataStore()
    {
        super();
        dataByName = new HashMap<String,DataClazz>();
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
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.TestDataStore#add(org.marketcetera.api.systemmodel.NamedObject)
     */
    @Override
    public void add(DataClazz inData)
    {
        super.add(inData);
        dataByName.put(inData.getName(),
                       inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.TestDataStore#remove(org.marketcetera.api.systemmodel.NamedObject)
     */
    @Override
    public void remove(DataClazz inData)
    {
        super.remove(inData);
        dataByName.remove(inData);
    }
    /**
     * test data values by name
     */
    private final Map<String,DataClazz> dataByName;
}

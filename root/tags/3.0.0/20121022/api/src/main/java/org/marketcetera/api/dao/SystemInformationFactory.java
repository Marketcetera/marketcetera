package org.marketcetera.api.dao;

/* $License$ */

/**
 * Creates <code>SystemInformation</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SystemInformationFactory
{
    /**
     * Creates a <code>SystemInformation</code> object.
     *
     * @return a <code>SystemInformation</code> value
     */
    public SystemInformation create();
    /**
     * Creates a <code>SystemInformation</code> object with attributes from the given object.
     *
     * @param inData a <code>SystemInformation</code> value
     * @return a <code>SystemInformation</code> value
     */
    public SystemInformation create(SystemInformation inData);
}

package org.marketcetera.systemmodel.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class OrderDestinationIdImpl
        implements OrderDestinationID
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.OrderDestinationID#getValue()
     */
    @Override
    public String getValue()
    {
        return value;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return value;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OrderDestinationIdImpl)) {
            return false;
        }
        OrderDestinationIdImpl other = (OrderDestinationIdImpl) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }
    /**
     * Create a new OrderDestinationIdImpl instance.
     *
     * @param inValue a <code>String</code> value
     */
    OrderDestinationIdImpl(String inValue)
    {
        value = StringUtils.trimToNull(inValue);
        Validate.notNull(value,
                         "Order destination ID must not be null");
    }
    /**
     * 
     */
    private final String value;
}

package org.marketcetera.admin;

/* $License$ */

/**
 * Provides a simple <code>Permission</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PermissionDescriptor.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public class PermissionDescriptor
        extends AbstractNamedDescriptor
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PermissionDescriptor [getName()=").append(getName()).append(", getDescription()=")
                .append(getDescription()).append("]");
        return builder.toString();
    }
}

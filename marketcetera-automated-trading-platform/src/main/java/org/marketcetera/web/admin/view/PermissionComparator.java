package org.marketcetera.web.admin.view;

import java.util.Comparator;

import org.apache.commons.collections4.Equator;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.admin.Permission;

/**
 * Compares and equates <code>Permission</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PermissionComparator
        implements Comparator<Permission>,Equator<Permission>
{
    /* (non-Javadoc)
     * @see org.apache.commons.collections4.Equator#equate(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equate(Permission inO1,
                          Permission inO2)
    {
        return new EqualsBuilder().append(inO1.getName(),inO2.getName()).isEquals();
    }
    /* (non-Javadoc)
     * @see org.apache.commons.collections4.Equator#hash(java.lang.Object)
     */
    @Override
    public int hash(Permission inO)
    {
        return new HashCodeBuilder().append(inO.getName()).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Permission inO1,
                       Permission inO2)
    {
        return new CompareToBuilder().append(inO1.getName(),inO2.getName()).toComparison();
    }
    /**
     * object instance to use for comparisons
     */
    public static final PermissionComparator instance = new PermissionComparator();
}
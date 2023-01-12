package org.marketcetera.web.admin.view;

import java.util.Comparator;

import org.apache.commons.collections4.Equator;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.admin.User;

/**
 * Compares and equates <code>User</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserComparator
        implements Comparator<User>,Equator<User>
{
    /* (non-Javadoc)
     * @see org.apache.commons.collections4.Equator#equate(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equate(User inO1,
                          User inO2)
    {
        return new EqualsBuilder().append(inO1.getName(),inO2.getName()).isEquals();
    }
    /* (non-Javadoc)
     * @see org.apache.commons.collections4.Equator#hash(java.lang.Object)
     */
    @Override
    public int hash(User inO)
    {
        return new HashCodeBuilder().append(inO.getName()).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(User inO1,
                       User inO2)
    {
        return new CompareToBuilder().append(inO1.getName(),inO2.getName()).toComparison();
    }
    /**
     * object instance to use for comparisons
     */
    public static final UserComparator instance = new UserComparator();
}
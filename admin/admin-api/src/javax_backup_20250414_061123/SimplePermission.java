package org.marketcetera.admin.impl;

import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.admin.Permission;
import org.marketcetera.persist.NDEntityBase;

/* $License$ */

/**
 * Provides a simple Permission implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="permission")
@XmlAccessorType(XmlAccessType.NONE)
public class SimplePermission
        extends NDEntityBase
        implements Permission,Comparator<SimplePermission>
{
    /**
     * Create a new SimplePermission instance.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     */
    public SimplePermission(String inName,
                            String inDescription)
    {
        setName(inName);
        setDescription(inDescription);
    }
    /**
     * Create a new SimplePermission instance.
     */
    public SimplePermission() {}
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimplePermission [name=").append(getName()).append(", description=").append(getDescription()).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(SimplePermission inO1,
                       SimplePermission inO2)
    {
        return new CompareToBuilder().append(inO1.getName(),inO2.getName()).toComparison();
        
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(getName()).toHashCode();
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
        if (!(obj instanceof SimplePermission)) {
            return false;
        }
        SimplePermission other = (SimplePermission) obj;
        return new EqualsBuilder().append(other.getName(),getName()).isEquals();
    }
    private static final long serialVersionUID = 3085570489230392139L;
}

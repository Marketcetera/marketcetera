package org.marketcetera.algo;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.core.Validator;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the template of a broker algorithm.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class BrokerAlgoSpec
        implements Serializable, Comparable<BrokerAlgoSpec>
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = StringUtils.trimToNull(inName);
    }
    /**
     * Get the algoTagSpecs value.
     *
     * @return a <code>Set&lt;BrokerAlgoTagSpec&gt;</code> value
     */
    public Set<BrokerAlgoTagSpec> getAlgoTagSpecs()
    {
        return algoTagSpecs;
    }
    /**
     * Sets the algoTagSpecs value.
     *
     * @param inAlgoTagSpecs a <code>Set&lt;BrokerAlgoTagSpec&gt;</code> value
     */
    public void setAlgoTagSpecs(Set<BrokerAlgoTagSpec> inAlgoTagSpecs)
    {
        algoTagSpecs = inAlgoTagSpecs;
    }
    /**
     * Get the validator value.
     *
     * @return a <code>Validator&lt;BrokerAlgo&gt;</code> value
     */
    public Validator<BrokerAlgo> getValidator()
    {
        return validator;
    }
    /**
     * Sets the validator value.
     *
     * @param inValidator a <code>Validator&lt;BrokerAlgo&gt;</code> value
     */
    public void setValidator(Validator<BrokerAlgo> inValidator)
    {
        validator = inValidator;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BrokerAlgoSpec [name=").append(name).append(", algoTagSpecs=").append(algoTagSpecs).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(name).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BrokerAlgoSpec other = (BrokerAlgoSpec) obj;
        return new EqualsBuilder().append(name,other.name).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(BrokerAlgoSpec inOther)
    {
        return new CompareToBuilder().append(name,inOther.name).toComparison();
    }
    /**
     * user-readable name of the broker algo
     */
    @XmlAttribute
    private String name;
    /**
     * broker algo tag specifications of the broker algo
     */
    @XmlElementWrapper(name="algoTagSpecs")
    @XmlElement(name="tagSpec",type=BrokerAlgoTagSpec.class)
    private Set<BrokerAlgoTagSpec> algoTagSpecs;
    /**
     * validator which validates the bound broker tag values at the algo level rather than at the tag level, may be <code>null</code>
     */
    private transient Validator<BrokerAlgo> validator;
    private static final long serialVersionUID = -8372920301146812888L;
}

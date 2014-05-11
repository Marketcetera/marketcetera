package org.marketcetera.algo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.Validator;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a broker algorithm bound with user-supplied values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class BrokerAlgo
        implements Serializable
{
    /**
     * Create a new BrokerAlgo instance.
     */
    public BrokerAlgo() {}
    /**
     * Create a new BrokerAlgo instance.
     *
     * @param inSpec a <code>BrokerAlgoSpec</code> value
     */
    public BrokerAlgo(BrokerAlgoSpec inSpec)
    {
        setAlgoSpec(inSpec);
    }
    /**
     * Create a new BrokerAlgo instance.
     *
     * @param inSpec a <code>BrokerAlgoSpec</code> value
     * @param inTags a <code>Set&lt;BrokerAlgoTag&gt;</code> value
     */
    public BrokerAlgo(BrokerAlgoSpec inSpec,
                      Set<BrokerAlgoTag> inTags)
    {
        setAlgoSpec(inSpec);
        setAlgoTags(inTags);
    }
    /**
     * Get the algoSpec value.
     *
     * @return a <code>BrokerAlgoSpec</code> value
     */
    public BrokerAlgoSpec getAlgoSpec()
    {
        return algoSpec;
    }
    /**
     * Sets the algoSpec value.
     *
     * @param inAlgoSpec a <code>BrokerAlgoSpec</code> value
     */
    public final void setAlgoSpec(BrokerAlgoSpec inAlgoSpec)
    {
        algoSpec = inAlgoSpec;
    }
    /**
     * Get the algoTags value.
     *
     * @return a <code>Set&lt;BrokerAlgoTag&gt;</code> value
     */
    public Set<BrokerAlgoTag> getAlgoTags()
    {
        return algoTags;
    }
    /**
     * Sets the algoTags value.
     *
     * @param inAlgoTags a <code>Set&lt;BrokerAlgoTag&gt;</code> value
     */
    public final void setAlgoTags(Set<BrokerAlgoTag> inAlgoTags)
    {
        algoTags = inAlgoTags;
    }
    /**
     * Applies the implied tags and values of the broker algo to the given order.
     *
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     */
    public void applyTo(NewOrReplaceOrder inOrder)
    {
        if(algoTags == null) {
            return;
        }
        Map<String,String> customFields = inOrder.getCustomFields();
        if(customFields == null) {
            customFields = new HashMap<String,String>();
        }
        for(BrokerAlgoTag algoTag : algoTags) {
            customFields.put(String.valueOf(algoTag.getTagSpec().getTag()),
                             algoTag.getValue());
        }
        inOrder.setCustomFields(customFields);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BrokerAlgo [algoSpec=").append(algoSpec).append(", algoTags=").append(algoTags).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(algoSpec).append(algoTags).toHashCode();
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
        BrokerAlgo other = (BrokerAlgo) obj;
        return new EqualsBuilder().append(algoSpec,other.algoSpec).append(algoTags,other.algoTags).isEquals();
    }
    /**
     * Maps validators from the given cannonical algo spec to this algo spec.
     * 
     * <p>If this object's <code>BrokerAlgoSpec</code> is <code>null</code>, this method has no effect.
     *
     * @param inCannonicalAlgoSpec a <code>BrokerAlgoSpec</code> value
     * @throws CoreException if the given <code>BrokerAlgoSpec</code> is not the same as this object's <code>BrokerAlgoSpec</code>
     */
    public void mapValidatorsFrom(BrokerAlgoSpec inCannonicalAlgoSpec)
    {
        if(algoSpec == null) {
            return;
        }
        if(!algoSpec.equals(inCannonicalAlgoSpec)) {
            throw new CoreException(new I18NBoundMessage2P(Messages.ALGO_SPEC_MISMATCH,
                                                           algoSpec.getName(),
                                                           inCannonicalAlgoSpec.getName()));
        }
        algoSpec.setValidator(inCannonicalAlgoSpec.getValidator());
        if(algoTags != null && inCannonicalAlgoSpec.getAlgoTagSpecs() != null) {
            Map<Integer,Validator<BrokerAlgoTag>> validators = new HashMap<Integer,Validator<BrokerAlgoTag>>();
            for(BrokerAlgoTagSpec algoTagSpec : inCannonicalAlgoSpec.getAlgoTagSpecs()) {
                if(algoTagSpec.getValidator() != null) {
                    validators.put(algoTagSpec.getTag(),
                                   algoTagSpec.getValidator());
                }
            }
            for(BrokerAlgoTag algoTag : algoTags) {
                algoTag.getTagSpec().setValidator(validators.get(algoTag.getTagSpec().getTag()));
            }
        }
    }
    /**
     * Validates the bound broker algo.
     * 
     * <p>This method will not completely validate the algo unless {@link #mapValidatorsFrom(BrokerAlgoSpec)}
     * has been invoked with the cannonical broker algo spec first.
     * @throws RuntimeException if validation fails
     */
    public void validate()
    {
        // perform spec-by-spec validation
        if(algoTags != null) {
            for(BrokerAlgoTag tag : algoTags) {
                tag.validate();
            }
        }
        // perform top-level validation
        if(algoSpec != null) {
            Validator<BrokerAlgo> algoValidator = algoSpec.getValidator();
            if(algoValidator != null) {
                algoValidator.validate(this);
            }
        }
    }
    /**
     * contains the broker algo template
     */
    @XmlElement
    private BrokerAlgoSpec algoSpec;
    /**
     * contains the broker algo tag values
     */
    @XmlElementWrapper(name="algoTags")
    @XmlElement(name="tag",type=BrokerAlgoTag.class)
    private Set<BrokerAlgoTag> algoTags;
    private static final long serialVersionUID = -9165368996244848275L;
}

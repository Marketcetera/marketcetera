package org.marketcetera.fix.impl;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.fix.FixSessionAttributeDescriptor;

/* $License$ */

/**
 * Provides a simple <code>FixSessionAttributeDescriptor</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="fixSessionAttributeDescriptor")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleFixSessionAttributeDescriptor
        implements Serializable,FixSessionAttributeDescriptor,Comparable<SimpleFixSessionAttributeDescriptor>
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
        name = inName;
    }
    /**
     * Get the defaultValue value.
     *
     * @return a <code>String</code> value
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }
    /**
     * Sets the defaultValue value.
     *
     * @param inDefaultValue a <code>String</code> value
     */
    public void setDefaultValue(String inDefaultValue)
    {
        defaultValue = inDefaultValue;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Get the pattern value.
     *
     * @return a <code>String</code> value
     */
    public String getPattern()
    {
        return pattern;
    }
    /**
     * Sets the pattern value.
     *
     * @param inPattern a <code>String</code> value
     */
    public void setPattern(String inPattern)
    {
        pattern = inPattern;
    }
    /**
     * Get the required value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRequired()
    {
        return required;
    }
    /**
     * Sets the required value.
     *
     * @param inRequired a <code>boolean</code> value
     */
    public void setRequired(boolean inRequired)
    {
        required = inRequired;
    }
    /**
     * Get the advice value.
     *
     * @return a <code>String</code> value
     */
    public String getAdvice()
    {
        return advice;
    }
    /**
     * Sets the help value.
     *
     * @param inAdvice a <code>String</code> value
     */
    public void setAdvice(String inAdvice)
    {
        advice = inAdvice;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixSessionAttributeDescriptor [name=").append(name).append(", defaultValue=")
                .append(defaultValue).append(", description=").append(description).append(", pattern=").append(pattern)
                .append(", required=").append(required).append(", advice=").append(advice).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SimpleFixSessionAttributeDescriptor inO)
    {
        return new CompareToBuilder().append(name,inO.name).toComparison();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        if (!(obj instanceof SimpleFixSessionAttributeDescriptor)) {
            return false;
        }
        SimpleFixSessionAttributeDescriptor other = (SimpleFixSessionAttributeDescriptor) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
    /**
     * name value
     */
    @XmlAttribute
    private String name;
    /**
     * default value, if any
     */
    @XmlAttribute
    private String defaultValue;
    /**
     * description value
     */
    @XmlAttribute
    private String description;
    /**
     * validation pattern value
     */
    @XmlAttribute
    private String pattern;
    /**
     * required value
     */
    @XmlAttribute
    private boolean required;
    /**
     * advice value
     */
    @XmlAttribute
    private String advice;
    private static final long serialVersionUID = 8354324947365412641L;
}

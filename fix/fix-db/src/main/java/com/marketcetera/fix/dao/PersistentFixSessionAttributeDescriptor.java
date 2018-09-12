package com.marketcetera.fix.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.persist.EntityBase;

/* $License$ */

/**
 * Provides a persistent <code>FixSessionAttributeDescriptor</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Table(name="fix_session_attr_dscrptrs")
@Entity(name="FixSessionAttributeDescriptor")
public class PersistentFixSessionAttributeDescriptor
        extends EntityBase
        implements FixSessionAttributeDescriptor
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptor#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptor#getDefaultValue()
     */
    @Override
    public String getDefaultValue()
    {
        return defaultValue;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptor#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptor#getPattern()
     */
    @Override
    public String getPattern()
    {
        return pattern;
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
     * Sets the defaultValue value.
     *
     * @param inDefaultValue a <code>String</code> value
     */
    public void setDefaultValue(String inDefaultValue)
    {
        defaultValue = inDefaultValue;
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
     * Sets the pattern value.
     *
     * @param inPattern a <code>String</code> value
     */
    public void setPattern(String inPattern)
    {
        pattern = inPattern;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptor#isRequired()
     */
    @Override
    public boolean isRequired()
    {
        return required;
    }
    /**
     * Sets the required value.
     *
     * @param inRequired a <code>boolean</code> value
     */
    public void setIsRequired(boolean inRequired)
    {
        required = inRequired;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionAttributeDescriptor#getAdvice()
     */
    @Override
    public String getAdvice()
    {
        return advice;
    }
    /**
     * Sets the advice value.
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
        builder.append("PersistentFixSessionAttributeDescriptor [name=").append(name).append(", defaultValue=")
                .append(defaultValue).append(", description=").append(description).append(", pattern=").append(pattern)
                .append(", required=").append(required).append(", advice=").append(advice).append("]");
        return builder.toString();
    }
    /**
     * name value
     */
    @Column(name="name",nullable=false,unique=true)
    private String name;
    /**
     * default value
     */
    @Column(name="default_value",nullable=true)
    private String defaultValue;
    /**
     * description value
     */
    @Column(name="description",nullable=true,length=1024)
    private String description;
    /**
     * pattern value
     */
    @Column(name="pattern",nullable=true)
    private String pattern;
    /**
     * required value
     */
    @Column(name="required",nullable=false)
    private boolean required;
    /**
     * advice value
     */
    @Column(name="advice",nullable=true)
    private String advice;
    private static final long serialVersionUID = 8354324947365412641L;
}

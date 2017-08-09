package org.marketcetera.admin.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.persist.EntityBase;

/* $License$ */

/**
 * Provideds a simple <code>UserAttribute</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="userAttribute")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleUserAttribute
        extends EntityBase
        implements UserAttribute
{
    /**
     * Create a new SimpleUserAttribute instance.
     *
     * @param inUser a <code>User</code> value
     * @param inType a <code>UserAttributeType</code> value
     * @param inAttribute a <code>String</code> value
     */
    public SimpleUserAttribute(User inUser,
                               UserAttributeType inType,
                               String inAttribute)
    {
        user = inUser;
        attributeType = inType;
        attribute = inAttribute;
    }
    /**
     * Create a new SimpleUserAttribute instance.
     *
     * @param inUserAttribute a <code>UserAttribute</code> value
     */
    public SimpleUserAttribute(UserAttribute inUserAttribute)
    {
        this(inUserAttribute.getUser(),
             inUserAttribute.getAttributeType(),
             inUserAttribute.getAttribute());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserAttribute#getUser()
     */
    @Override
    public User getUser()
    {
        return user;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserAttribute#getAttributeType()
     */
    @Override
    public UserAttributeType getAttributeType()
    {
        return attributeType;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.UserAttribute#getAttribute()
     */
    @Override
    public String getAttribute()
    {
        return attribute;
    }
    /**
     * Sets the attributeType value.
     *
     * @param inAttributeType a <code>UserAttributeType</code> value
     */
    public void setAttributeType(UserAttributeType inAttributeType)
    {
        attributeType = inAttributeType;
    }
    /**
     * Sets the attribute value.
     *
     * @param inAttribute a <code>String</code> value
     */
    public void setAttribute(String inAttribute)
    {
        attribute = inAttribute;
    }
    /**
     * Sets the user value.
     *
     * @param inUser a <code>User</code> value
     */
    public void setUser(User inUser)
    {
        user = inUser;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleUserAttribute [user=").append(user).append(", attributeType=").append(attributeType)
                .append(", attribute=").append(attribute).append("]");
        return builder.toString();
    }
    /**
     * user attribute value
     */
    @XmlAttribute
    private UserAttributeType attributeType;
    /**
     * attribute value
     */
    @XmlAttribute
    private String attribute;
    /**
     * user value
     */
    @XmlElement
    private User user;
    private static final long serialVersionUID = -5646693749019993079L;
}

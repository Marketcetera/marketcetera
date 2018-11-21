package org.marketcetera.admin.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.marketcetera.admin.MutableUserAttribute;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.persist.EntityBase;

/* $License$ */

/**
 * Provides a persistent <code>UserAttribute</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentUserAttribute.java 85209 2016-03-22 19:42:24Z colin $
 * @since 1.2.0
 */
@Table(name="user_attributes")
@Entity(name="UserAttribute")
public class PersistentUserAttribute
        extends EntityBase
        implements MutableUserAttribute
{
    /**
     * Create a new PersistentUserAttribute instance.
     */
    public PersistentUserAttribute()
    {
    }
    /**
     * Create a new PersistentUserAttribute instance.
     *
     * @param inUserAttribute a <code>UserAttribute</code> value
     */
    public PersistentUserAttribute(UserAttribute inUserAttribute)
    {
        this(inUserAttribute.getUser(),
             inUserAttribute.getAttributeType(),
             inUserAttribute.getAttribute());
    }
    /**
     * Create a new PersistentUserAttribute instance.
     *
     * @param inUser a <code>User</code> value
     * @param inType a <code>UserAttributeType</code> value
     * @param inAttribute a <code>String</code> value
     */
    public PersistentUserAttribute(User inUser,
                                   UserAttributeType inType,
                                   String inAttribute)
    {
        user = inUser;
        userAttributeType = inType;
        attribute = inAttribute;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.UserAttribute#getAttributeType()
     */
    @Override
    public UserAttributeType getAttributeType()
    {
        return userAttributeType;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.UserAttribute#getAttribute()
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
        userAttributeType = inAttributeType;
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
    /**
     * Sets the attribute value.
     *
     * @param inAttribute a <code>String</code> value
     */
    public void setAttribute(String inAttribute)
    {
        attribute = inAttribute;
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentUserAttribute [user=").append(user).append(", userAttributeType=")
                .append(userAttributeType).append(", attribute=").append(attribute).append("]");
        return builder.toString();
    }
    /**
     * owning user value
     */
    @ManyToOne(targetEntity=PersistentUser.class)
    @JoinColumn(name="user_id")
    private User user;
    /**
     * attribute type value
     */
    @Column(name="user_attribute_type",nullable=false)
    private UserAttributeType userAttributeType;
    /**
     * attribute value
     */
    @Lob
    @Column(name="attribute",nullable=false,length=262144)
    private String attribute;
    private static final long serialVersionUID = 4409150813670715607L;
}

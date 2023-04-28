package org.marketcetera.admin.user;

import static org.marketcetera.admin.Messages.EMPTY_PASSWORD;
import static org.marketcetera.admin.Messages.SIMPLE_USER_NAME;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.User;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.persist.ValidationException;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.log.I18NMessage0P;


/* $License$ */
/**
 * A user that can logon to the message bus used by the system.
 * Each user has a name, description and password.
 * <p>
 * Every user needs to have a non-empty name and password. The password
 * is saved as hashed value created from both the name and password value.
 * The user name needs to be set before the password is set. If the user name
 * is changed, user's password is unset.
 *
 * @author anshul@marketcetera.com
 */
@Entity(name="user")
@Table(name = "metc_users",uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@AttributeOverride(name = "name", column = @Column(nullable = false))
@ClassVersion("$Id$")
public class PersistentUser
        extends NDEntityBase
        implements MutableUser
{
    /**
     * Create a new PersistentUser instance.
     */
    public PersistentUser() {}
    /**
     * Create a new PersistentUser instance.
     *
     * @param inUser a <code>User</code> value
     */
    public PersistentUser(User inUser)
    {
        active = inUser.isActive();
        setName(inUser.getName());
        setDescription(inUser.getDescription());
        superuser = false;
        userData = null;
        hashedPassword = inUser.getHashedPassword();
    }
    /**
     * The superuser flag of this user.
     * @return The flag.
     */
    public boolean isSuperuser() {
        return superuser;
    }
    /**
     * Set the superuser flag for this user.
     * @param superuser the superuser flag for this user.
     */
    public void setSuperuser(boolean superuser) {
        this.superuser = superuser;
    }
    /**
     * The active flag of this user.
     * @return The flag.
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Set the active flag for this user.
     * @param active the active flag for this user.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Gets the user data as a <code>String</code>.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getUserData()
    {
        return userData;
    }
    /**
     * Sets the user data.
     * 
     * @param inUserData a <code>String</code> value
     */
    public void setUserData(String inUserData)
    {
        userData = inUserData;
    }
    /**
     * The UserID of this user.
     * @return The UserID.
     */
    public UserID getUserID() {
        return new UserID(getId());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableUser#setUserId(org.marketcetera.trade.UserID)
     */
    @Override
    public void setUserId(UserID inUserId)
    {
        setId(inUserId.getValue());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableUser#setIsActive(boolean)
     */
    @Override
    public void setIsActive(boolean inIsActive)
    {
        active = inIsActive;
    }
    /**
     * Returns true if the user password is set.
     * @return true if the user password is set.
     */
    public boolean isPasswordSet() {
        return getHashedPassword() != null;
    }
    /**
     * Sets the user name. The user password is emptied whenever the
     * user name is modified. Make sure to the set the user
     * password before attempting to save the user.
     *
     * @param name the user name
     */
    @Override
    public void setName(String name) {
        if(name == null || (!name.equals(getName()))) {
            super.setName(name);
            setHashedPassword(null);
        }
    }
    /**
     * Validates if the attributes of this instance are valid
     * to attempt a save operation.
     * <p>
     * This method validates if the user name is non-empty, contains
     * only letters, numbers and space characters, and is less than
     * 256 characters in length.
     * <p>
     * This method validates if the user password is non-empty.
     *
     * @throws ValidationException if there were validation failures
     */
    @PreUpdate
    @PrePersist
    public void validate() throws ValidationException {
        super.validate();
        if(getHashedPassword() == null || getHashedPassword().length() == 0) {
            throw new ValidationException(EMPTY_PASSWORD);
        }
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PersistentUser other = (PersistentUser) obj;
        return new EqualsBuilder().append(getName(),other.getName()).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("User ").append(getName()).append('[').append(getId()).append("] active=").append(active).append(" superuser=").append(superuser);
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(User inO)
    {
        return new CompareToBuilder().append(inO.getName(),getName()).toComparison();
    }
    /**
     * The custom localized name for users.
     *
     * @return custom localized name for users.
     */
    @SuppressWarnings("unused")
    private static I18NMessage0P getUserFriendlyName() {
        return SIMPLE_USER_NAME;
    }
    /**
     * indicates if this user is a super user
     */
    @Column(name="is_superuser",nullable=false)
    private boolean superuser = false;
    /**
     * indicates if this user is currently active
     */
    @Column(name="is_active",nullable=false)
    private boolean active = true;
    /**
     * the user data associated with this used - may be <code>null</code>
     */
    @Column(name="user_data",nullable=true,length=8096)
    private String userData;
    /**
     * hashed password value
     */
    @Column(name="password",nullable=false)
    private String hashedPassword = null;
    private static final long serialVersionUID = -244334398553751199L;
}

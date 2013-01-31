package org.marketcetera.security;

import static org.marketcetera.persist.Messages.UNSPECIFIED_NAME_ATTRIBUTE;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.Messages;
import org.marketcetera.persist.NDEntityBase;
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
@Entity
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
@AttributeOverride(name = "name", column = @Column(nullable = false))
@Table(name="users",uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
@ClassVersion("$Id$")
public class User
        extends NDEntityBase
{
    /**
     * The superuser flag of this user.
     * @return The flag.
     */
    public boolean isSuperuser()
    {
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
    public UserID getUserID()
    {
        return new UserID(getId());
    }
    /**
     * Returns true if the user password is set.
     * @return true if the user password is set.
     */
    public boolean isPasswordSet()
    {
        return getPassword() != null;
    }
    /**
     * Sets the user name. The user password is emptied whenever the
     * user name is modified. Make sure to the set the user
     * password before attempting to save the user.
     *
     * @param name the user name
     */
    @Override
    public void setName(String name)
    {
        if(name == null || (!name.equals(getName()))) {
            super.setName(name);
            setPassword((char[])null);
        }
    }
    /**
     * Sets the user's password. The user name should be set to a
     * non-empty value before this method is invoked 
     *
     * This method can be used to set the password
     * when the current user password is empty. The user password is empty
     * for a newly created user or a user who's name has been reset via
     * {@link #setName(String)}
     *
     * @param password The user password value, cannot be null.
     *
     * @throws PersistenceException If the user password is already set, or if
     * an empty password is supplied or if the user name is not set.
     */
    public void setPassword(char[] password)
    {
        if(getName() == null) {
            throw new PersistenceException(UNSPECIFIED_NAME_ATTRIBUTE.getText());
        }
        if(isPasswordSet()) {
            throw new PersistenceException(Messages.CANNOT_SET_PASSWORD.getText(getName()));
        }
        validateAndSetPassword(password);
    }
    /**
     * Changes the user's password after
     * {@link #validatePassword(char[]) validating} the supplied password.
     * @param originalPassword the original password. This password should
     * match the currently configured password.
     * <p>
     * The new password is not saved to the database. To save
     * the new password to the database, invoke {@link #save()}
     *
     * @param newPassword the new password, cannot be empty.
     *
     * @throws org.marketcetera.persist.PersistenceException if there were
     * errors validating the original or the new password password.
     */
    public void changePassword(char [] originalPassword,
                               char[] newPassword)
    {
        validatePassword(originalPassword);
        validateAndSetPassword(newPassword);
    }

    /**
     * Verifies if the supplied password matches the configured
     * password for the user. If no nonempty password is presently
     * configured, validation succeeds regardless of the supplied
     * password; this provides a back-door to address forgotten
     * password problems, wherein an admin can modify the database
     * directly and empty out the present password.
     *
     * @param password the password to test.
     *
     * @throws PersistenceException If a nonempty password is presently
     * configured and either an empty password value was specified, or
     * the specified password doesn't match the currently configured
     * user password.
     */
    public void validatePassword(char[] password)
    {
        if(getPassword() == null || getPassword().length() == 0) {
            return;
        }
        validatePasswordValue(password);
        if(!getPassword().equals(hash(getName().toCharArray(),password))) {
            throw new PersistenceException(Messages.INVALID_PASSWORD.getText());
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
     * @throws PersistenceException if there were validation failures
     */
    public void validate()
    {
        super.validate();
        if(getPassword() == null || getPassword().length() == 0) {
            throw new PersistenceException(Messages.EMPTY_PASSWORD.getText());
        }
    }
    /**
     * Validates if the supplied password matches the password rules
     * and saves it
     *
     * @param password the user password
     *
     * @throws PersistenceException if the supplied password is empty.
     */
    private void validateAndSetPassword(char[] password)
    {
        validatePasswordValue(password);
        setPassword(hash(getName().toCharArray(),
                         password));
    }

    private String getPassword()
    {
        return password;
    }
    private void setPassword(String inHashedPassword)
    {
        password = inHashedPassword;
    }
    /**
     * Validates if the supplied password value is valid
     *
     * @param password the user password
     *
     * @throws PersistenceException if the user password is empty
     */
    private static void validatePasswordValue(char[] password)
    {
        if(password == null || password.length == 0) {
            throw new PersistenceException(Messages.EMPTY_PASSWORD.getText());
        }
    }
    /**
     * The custom localized name for users.
     *
     * @return custom localized name for users.
     */
    @SuppressWarnings("unused")
    private static I18NMessage0P getUserFriendlyName()
    {
        return Messages.SIMPLE_USER_NAME;
    }
    /**
     * Hashes the supplied value
     *
     * @param value the supplied char array that needs to be hashed.
     *
     * @return the hashed value
     *
     * @throws IllegalArgumentException If there's a bug in the code.
     */
    private static String hash(char[] ...value)  {
        try {
            MessageDigest dig = digest.get();
            for(char[] c:value) {
                dig.update(new String(c).getBytes("UTF-16")); //$NON-NLS-1$
            }
            return new BigInteger(dig.digest()).toString(Character.MAX_RADIX);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    /**
     * The digest used to hash the password.
     */
    private static ThreadLocal<MessageDigest> digest = new ThreadLocal<MessageDigest>() {
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }
        }
    };
    @Column(nullable = false)
    private String password = null;
    @Column(nullable = false)
    private boolean active = true;
    @Column(nullable = false)
    private boolean superuser = false;
    /**
     * the user data associated with this used - may be <code>null</code>
     */
    @Column(nullable = true,columnDefinition="text")
    private String userData;
    private static final long serialVersionUID = -244334398553751199L;
}

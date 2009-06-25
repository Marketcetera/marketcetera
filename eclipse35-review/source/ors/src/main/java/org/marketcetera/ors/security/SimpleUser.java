package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.UserID;
import org.marketcetera.persist.*;
import org.marketcetera.persist.PersistenceException;
import static org.marketcetera.persist.Messages.UNSPECIFIED_NAME_ATTRIBUTE;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NMessage0P;
import static org.marketcetera.ors.security.Messages.*;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.io.UnsupportedEncodingException;

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
@ClassVersion("$Id$")
@Entity
@Table(
        name = "ors_users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})}
)
@AttributeOverride(name = "name", column = @Column(nullable = false))
public class SimpleUser extends NDEntityBase {
    private static final long serialVersionUID = -244334398553751199L;

    /**
     * The superuser flag of this user.
     * @return The flag.
     */
    @Column(nullable = false)
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

    private boolean superuser = false;

    /**
     * The active flag of this user.
     * @return The flag.
     */
    @Column(nullable = false)
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

    private boolean active = true;

    /**
     * The UserID of this user.
     * @return The UserID.
     */
    @Transient
    public UserID getUserID() {
        return new UserID(getId());
    }

    /**
     * Returns true if the user password is set.
     * @return true if the user password is set.
     */
    @Transient
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
     * @throws ValidationException If the user password is already set, or if
     * an empty password is supplied or if the user name is not set.
     */
    public void setPassword(char[] password) throws ValidationException {
        if(getName() == null) {
            throw new ValidationException(UNSPECIFIED_NAME_ATTRIBUTE);
        }
        if(isPasswordSet()) {
            throw new ValidationException(
                    new I18NBoundMessage1P(CANNOT_SET_PASSWORD,getName()));
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
     * @throws org.marketcetera.persist.ValidationException if there were
     * errors validating the original or the new password password.
     */
    public void changePassword(char [] originalPassword,
                               char[] newPassword)
            throws ValidationException {
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
     * @throws ValidationException If a nonempty password is presently
     * configured and either an empty password value was specified, or
     * the specified password doesn't match the currently configured
     * user password.
     */
    public void validatePassword(char[] password) throws ValidationException {
        if(getHashedPassword() == null || getHashedPassword().length() == 0) {
            return;
        }
        validatePasswordValue(password);
        if(!getHashedPassword().equals(hash(getName().toCharArray(),password))) {
            throw new ValidationException(INVALID_PASSWORD);
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
    public void validate() throws PersistenceException {
        super.validate();
        if(getHashedPassword() == null || getHashedPassword().length() == 0) {
            throw new ValidationException(EMPTY_PASSWORD);
        }
    }

    /**
     * Saves the user to the database.
     *
     * @throws ValidationException if {@link #validate() validation}
     * failed when saving the user.
     * @throws org.marketcetera.persist.EntityExistsException if a user
     * with the supplied name already exists in the database.
     * @throws org.marketcetera.persist.PersistenceException If there were
     * errors saving the user to the database.
     */
    public void save() throws org.marketcetera.persist.PersistenceException {
        saveRemote(null);
    }

    /**
     * Deletes the user from the database. After a user is deleted,
     * any attempt to login as that user fails. Do note that
     * deleting a user will not force logout the user from the system.
     *
     * @throws PersistenceException if there were errors deleting the user
     */
    public void delete() throws PersistenceException {
        deleteRemote(null);
    }

    /**
     * Validates if the supplied password matches the password rules
     * and saves it
     *
     * @param password the user password
     *
     * @throws ValidationException if the supplied password is empty.
     */
    private void validateAndSetPassword(char[] password) throws ValidationException {
        validatePasswordValue(password);
        setHashedPassword(hash(getName().toCharArray(), password));
    }

    @Column(nullable = false)
    private String getHashedPassword() {
        return hashedPassword;
    }

    private void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    private String hashedPassword = null;

    /**
     * Validates if the supplied password value is valid
     *
     * @param password the user password
     *
     * @throws ValidationException if the user password is empty
     */
    private static void validatePasswordValue(char[] password)
            throws ValidationException {
        if(password == null || password.length == 0) {
            throw new ValidationException(EMPTY_PASSWORD);
        }
    }

    /**
     * The custom localized name for users.
     *
     * @return custom localized name for users.
     */
    private static I18NMessage0P getUserFriendlyName() {
        return SIMPLE_USER_NAME;
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
    private static ThreadLocal<MessageDigest> digest =
            new ThreadLocal<MessageDigest>(){
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e);
            }
        }
    };
    static final String ATTRIBUTE_ACTIVE = "active"; //$NON-NLS-1$
    static final String ENTITY_NAME = "SimpleUser"; //$NON-NLS-1$
}

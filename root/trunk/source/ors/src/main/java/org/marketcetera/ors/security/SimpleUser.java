package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.*;
import org.marketcetera.persist.PersistenceException;
import static org.marketcetera.persist.Messages.UNSPECIFIED_NAME_ATTRIBUTE;
import org.marketcetera.util.log.I18NBoundMessage1P;
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
@ClassVersion("$Id$") //$NON-NLS-1$
@Entity
@Table(
        name = "ors_users", //$NON-NLS-1$
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})} //$NON-NLS-1$
)
@AttributeOverride(name = "name", column = @Column(nullable = false)) //$NON-NLS-1$
public class SimpleUser extends NDEntityBase {
    private static final long serialVersionUID = -244334398553751199L;

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
        if(getHashedPassword() != null) {
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
     * Verifies if the supplied password matches the configured password
     * for the user.
     *
     * @param password the password to test.
     *
     * @throws ValidationException If an empty password value was specified, or
     * if a password hasn't yet been configured for the user, or the specified
     * password doesn't match the currently configured user password.
     */
    public void validatePassword(char[] password) throws ValidationException {
        validatePasswordValue(password);
        if(null == getHashedPassword()) {
            throw new ValidationException(PASSWORD_NOT_SET);
        }
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
     * @throws ValidationException
     */
    public void validate() throws ValidationException {
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
    static final String ENTITY_NAME = "SimpleUser"; //$NON-NLS-1$
}

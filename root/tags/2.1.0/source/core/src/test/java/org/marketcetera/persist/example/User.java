package org.marketcetera.persist.example;
import org.marketcetera.persist.*;
import org.marketcetera.persist.PersistenceException;
import static org.marketcetera.persist.JPQLConstants.*;
import org.marketcetera.util.log.I18NMessage0P;

import javax.persistence.*;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Instances of this class represent individual users
 * of the system.
 */
@Entity
@Table(name = "test_user", //$NON-NLS-1$
        uniqueConstraints={@UniqueConstraint(columnNames={"name"})}) //$NON-NLS-1$
public class User extends NDEntityBase implements SummaryUser {
    /**
     * The maximum number of failed password attempts after
     * which, the user is locked out.
     */
    private static final int MAX_FAILED_PASSWORD_ATTEMPTS = 3;
    private static final long serialVersionUID = 6264976679188252861L;

    /**
     * Sets the user password. The password is hashed
     * before its saved. The password is changed only if the supplied
     * old password matches the current password. If old password is
     * ignored if the current password is not set, ie. is null.
     *
     * @param newPassword the new password for the user, cannot be null.
     * @param oldPassword the old password for the user, can be null.
     *
     * @return true if the password was changed, false if it wasn't
     *
     * @throws org.marketcetera.persist.PersistenceException if there was
     * an error saving the password changes to the user account
     */
    public boolean changePassword(char[] newPassword, char [] oldPassword)
            throws PersistenceException {
        if(newPassword == null) {
            throw new NullPointerException();
        }
        if(getName() == null) {
            throw new NullPointerException();
        }
        if(validatePassword(oldPassword)) {
            setHashedPassword(hash(newPassword, getName().toCharArray()));
            save();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Validate if the supplied password matches user's password.
     * If the password match fails, the {@link #getNumFailedPasswordAttempts()}
     * counter is incremented. If the number of such failures, exceed the
     * configured maximum, the user's account is {@link #isLocked() locked}.
     *
     * @param password The password that needs to be validated, cannot be null.
     *
     * @throws IllegalStateException if the user is disabled or locked.
     * @throws org.marketcetera.persist.PersistenceException If there was
     * an error authenticating the user or updating the user based on
     * results of this authentication attempt
     */
    public void authenticate(char[] password) throws PersistenceException {
        if(!isEnabled()) {
            failedPasswordAttempt();
            throw new IllegalStateException("user is not enabled"); //$NON-NLS-1$
        }
        if(isLocked()) {
            failedPasswordAttempt();
            throw new IllegalStateException("user is locked"); //$NON-NLS-1$
        }
        if(!validatePassword(password)) {
            failedPasswordAttempt();
            throw new IllegalArgumentException("Invalid password"); //$NON-NLS-1$
        }
        if(getNumFailedPasswordAttempts() > 0) {
            setNumFailedPasswordAttempts(0);
            save();
        }
    }

    /**
     * Increments the number of failed password attempts and locks
     * the user if the attempts exceed the maximum allowed failed attempts
     *
     * @throws PersistenceException if there was an error saving the
     * changes to the User
     */
    private void failedPasswordAttempt() throws PersistenceException {
        setNumFailedPasswordAttempts(getNumFailedPasswordAttempts() + 1);
        if(getNumFailedPasswordAttempts() >= MAX_FAILED_PASSWORD_ATTEMPTS) {
            setLocked(true);
        }
        save();
    }

    /**
     * Verify if the password matches user's current password.
     * If the user's current password is not set, this method
     * always returns true.
     *
     * @param password the password to test.
     *
     * @return true if the password matches, false if it doesn't
     */
    private boolean validatePassword(char[] password) {
        if(getHashedPassword() == null) {
            return true;
        } else if(password != null) {
            return getHashedPassword().equals(hash(password,
                    getName().toCharArray()));
        } else {
            return false;
        }
    }

    /**
     * Unlocks the user account if its locked. This method
     * is a no-op if the User is not locked
     *
     * @throws org.marketcetera.persist.PersistenceException if there
     * was an error unlocking the User
     */
    public void unLock() throws PersistenceException {
        if(isLocked()) {
            setLocked(false);
            setNumFailedPasswordAttempts(0);
            save();
        }
    }

    /**
     * If the user is enabled
     *
     * @return true if the user is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set if the user is enabled
     *
     * @param enabled true if the user is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * if the user is locked out. This happens if the
     * number of failed password attempts for a user
     * exceed the configured maximum
     *
     * @return true if the user is locked.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Set if the user account is locked
     *
     * @param locked true if the account is locked.
     */
    private void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Returns the number of times a user has failed to
     * supply the correct password
     *
     * @return the number of times user has failed to supply
     * the correct password.
     */
    public int getNumFailedPasswordAttempts() {
        return numFailedPasswordAttempts;
    }

    /**
     * Set the number of times a user has failed to supply
     * the correct password.
     *
     * @param numFailedPasswordAttempts the number of times
     * a user has failed to supply the correct password.
     */
    void setNumFailedPasswordAttempts(int numFailedPasswordAttempts) {
        this.numFailedPasswordAttempts = numFailedPasswordAttempts;
    }

    /**
     * The user's email address.
     *
     * @return user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the user's email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The user's employee ID.
     *
     * @return the user's employee ID.
     */
    public String getEmployeeID() {
        return employeeID;
    }

    /**
     * Sets the user's employee ID.
     *
     * @param employeeID the user's employee ID.
     */
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    /**
     * The groups that this user is a member of
     *
     * @return The set of groups that this user is a member of.
     */
    @ManyToMany(mappedBy = "users", targetEntity = Group.class) //$NON-NLS-1$
    public Set<SummaryGroup> getGroups() {
        return groups;
    }

    /**
     * Sets the group value. This method is not meant to be
     * used by users. Its only meant to be used by the
     * ORM library
     *
     * @param groups the set of groups that this user belongs to
     */
    private void setGroups(Set<SummaryGroup> groups) {
        this.groups = groups;
    }

    /**
     * The settings for the user.
     *
     * @return the settings for the user.
     */
    @Transient
    public Map<String, String> getSettings() {
        Map<String, Setting> setting = getSetting();
        if(setting == null) {
            return null;
        } else {
            HashMap<String,String> map =
                    new HashMap<String, String>(setting.size());
            for(String s: setting.keySet()) {
                map.put(s,setting.get(s).getValue());
            }
            return map;
        }
    }

    /**
     * Set the settings for the user.
     *
     * @param settings the user settings.
     */
    public void setSettings(Map<String, String> settings) {
        if(settings == null) {
            setSetting(null);
        } else {
            Map<String, Setting> map = getSetting();
            if(map == null) {
                map = new HashMap<String, Setting>(settings.size());
            } else {
                //remove all the settings that are no longer there
                map.keySet().retainAll(settings.keySet());
            }
            Setting s;
            for(String key: settings.keySet()) {
                s = map.get(key);
                if(s == null) {
                    s = new Setting(key,settings.get(key),this);
                } else {
                    //Update the setting object to preserve its ID
                    //allowing us to update the object in place when
                    //its value is updated.
                    s.setValue(settings.get(key));
                }
                map.put(key, s);
            }
            setSetting(map);
        }
    }

    /**
     * Saves this user to the system. If this user doesn't exist,
     * its created, otherwise the existing user is updated.
     *
     * @throws org.marketcetera.persist.PersistenceException if there was
     * an error saving the User
     */
    public void save() throws org.marketcetera.persist.PersistenceException {
        saveRemote(null);
    }

    /**
     * Deletes this user from the system.
     *
     * @throws org.marketcetera.persist.PersistenceException if there was
     * an error deleting the User
     */
    public void delete() throws PersistenceException {
        deleteRemote(null);
    }

    public String toString() {
        return super.toString() + "User{" + //$NON-NLS-1$
                "hashedPassword='" + hashedPassword + '\'' + //$NON-NLS-1$
                ", enabled=" + enabled + //$NON-NLS-1$
                ", locked=" + locked + //$NON-NLS-1$
                ", numFailedPasswordAttempts=" + numFailedPasswordAttempts + //$NON-NLS-1$
                ", email='" + email + '\'' + //$NON-NLS-1$
                ", employeeID='" + employeeID + '\'' + //$NON-NLS-1$
                '}';
    }

    /**
     * The user's hashed password.
     * @return the user's hashed password
     */
    private String getHashedPassword() {
        return hashedPassword;
    }

    private void setHashedPassword(String password) {
        this.hashedPassword = password;
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL) //$NON-NLS-1$
    @MapKey(name = "name") //$NON-NLS-1$
    private Map<String, Setting> getSetting() {
        return setting;
    }

    private void setSetting(Map<String, Setting> setting) {
        this.setting = setting;
    }
    
    @Override
    protected void preSaveLocal(EntityManager em,
                                PersistContext context)
            throws PersistenceException {
        //remove all settings if already persistent
        if(isPersistent()) {
            final String settingAlias = "s"; //$NON-NLS-1$
            final String parameterOwner = ATTRIBUTE_OWNER;
            StringBuilder sb = new StringBuilder(DELETE);
            sb.append(S).append(FROM).append(S).append(Setting.ENTITY_NAME).
                    append(S).append(settingAlias).append(S).append(WHERE).append(S).
                    append(settingAlias).append(DOT).append(ATTRIBUTE_OWNER).append(DOT).
                    append(ATTRIBUTE_ID).append(EQUALS).append(PARAMETER_PREFIX).
                    append(parameterOwner);
            Query q = em.createQuery(sb.toString());
            q.setParameter(parameterOwner,getId());
            q.executeUpdate();
        }
    }


    @Override
    protected SaveResult deleteLocal(EntityManager em,
                                     PersistContext context)
            throws PersistenceException {
        final String alias = "s"; //$NON-NLS-1$
        final String parameter = Setting.ATTRIBUTE_OWNER;
        em.createQuery(DELETE + S + FROM + S + Setting.ENTITY_NAME + S + 
                alias + S + WHERE + S + alias + DOT +
                Setting.ATTRIBUTE_OWNER + DOT +
                EntityBase.ATTRIBUTE_ID + S + EQUALS + S +PARAMETER_PREFIX +
                parameter).
                setParameter(parameter,getId()).
                executeUpdate();
        return super.deleteLocal(em, context);
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

    /**
     * The custom localized name for users.
     *
     * @return custom localized name for users.
     */
    private static I18NMessage0P getUserFriendlyName() {
        return Messages.NAME_USER;
    }
    /**
     * The attribute employee ID used in JPQL queries
     */
    static final String ATTRIBUTE_EMPLOYEE_ID = "employeeID"; //$NON-NLS-1$
    /**
     * The attribute email used in JPQL queries
     */
    static final String ATTRIBUTE_EMAIL = "email"; //$NON-NLS-1$
    /**
     * The attribute enabled used in JPQL queries
     */
    static final String ATTRIBUTE_ENABLED = "enabled"; //$NON-NLS-1$
    /**
     * The attribute owner used in JPQL queries
     */
    static final String ATTRIBUTE_OWNER = "owner"; //$NON-NLS-1$
    /**
     * The attribute groups used in JPQL queries
     */
    static final String ATTRIBUTE_GROUPS = "groups"; //$NON-NLS-1$
    /**
     * The attribute setting used in JPQL queries
     */
    static final String ATTRIBUTE_SETTING = "setting"; //$NON-NLS-1$
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = "User"; //$NON-NLS-1$

    private String hashedPassword = null;
    private boolean enabled = true;
    private boolean locked = false;
    private int numFailedPasswordAttempts = 0;
    private String email = null;
    private String employeeID = null;
    private Set<SummaryGroup> groups;
    private Map<String,Setting> setting;
}

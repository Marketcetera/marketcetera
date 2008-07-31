package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.persist.SummaryNDEntityBase;

/* $License$ */
/**
 * Presents a summary view of a {@link User}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface SummaryUser extends SummaryNDEntityBase {
    /**
     * If the user is enabled
     * 
     * @return true if the user is enabled
     */
    boolean isEnabled();

    /**
     * if the user is locked out. This happens if the
     * number of failed password attempts for a user
     * exceed the configured maximum
     *
     * @return true if the user is locked.
     */
    boolean isLocked();

    /**
     * Returns the number of times a user has failed to
     * supply the correct password
     *
     * @return the number of times user has failed to supply
     * the correct password.
     */
    int getNumFailedPasswordAttempts();

    /**
     * The user's email address.
     *
     * @return user's email address.
     */
    String getEmail();

    /**
     * The user's employee ID.
     *
     * @return the user's employee ID.
     */
    String getEmployeeID();
}

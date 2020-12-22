package org.marketcetera.ors.mbeans;

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
/**
 * MBean interface for ORS operations.
 *
 * @author toli
 * @version $Id$
 */
public interface ORSAdminMBean
{
    /**
     * Sends a {@link quickfix.fix44.UserRequest} message containing the password reset message.
     *
     * @param broker a <code>String</code> value
     * @param oldPassword a <code>String</code> value
     * @param newPassword a <code>String</code> value
     */
    public void sendPasswordReset(String broker, String oldPassword, String newPassword);
    /**
     * Syncs up the in-memory sessions to reflect the current user
     * definitions in the database.
     */
    public void syncSessions();
}

package com.marketcetera.ors.mbeans;

import org.marketcetera.core.ClassVersion;

/**
 * MBean interface for ORS operations.
 * @author toli
 * @version $Id: ORSAdminMBean.java 16468 2014-05-12 00:36:56Z colin $
 */

@ClassVersion("$Id: ORSAdminMBean.java 16468 2014-05-12 00:36:56Z colin $")
public interface ORSAdminMBean {

    /** Sends a {@link quickfix.fix44.UserRequest} message containing the password reset message */
    public void sendPasswordReset(String broker, String oldPassword, String newPassword);

    /**
     * Syncs up the in-memory sessions to reflect the current user
     * definitions in the database.
     */

    public void syncSessions();
}

package org.marketcetera.oms.mbeans;

import org.marketcetera.core.ClassVersion;

/**
 * MBean interface for OMS operations
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public interface OMSAdminMBean {

    /** Sends a {@link quickfix.fix44.UserRequest} message containing the password reset message */
    public void sendPasswordReset(String senderCompID, String targetCompID, String oldPassword, String newPassword);
}
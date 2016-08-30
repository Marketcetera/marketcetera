package org.marketcetera.rpc.client;

import java.util.Locale;

/* $License$ */

/**
 * Provides parameters necessary to connect to an RPC server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RpcClientParameters
{
    /**
    *
    *
    * @param inHostname
    */
    void setHostname(String inHostname);
    String getHostname();
    void setPort(int inPort);
    int getPort();
    void setUsername(String inUsername);
    String getUsername();
    void setPassword(String inPassword);
    String getPassword();
    void setLocale(Locale inLocale);
    Locale getLocale();
    void setHeartbeatInterval(long inHeartbeatInterval);
    long getHeartbeatInterval();
    void setShutdownWait(long inShutdownWait);
    long getShutdownWait();
}

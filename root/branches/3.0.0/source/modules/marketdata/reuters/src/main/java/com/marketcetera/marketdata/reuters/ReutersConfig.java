package com.marketcetera.marketdata.reuters;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersConfig.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
public interface ReutersConfig
{
    public String getServerType();
    public String getTraceSelector();
    public String getEventMechanism();
    public String getTransportProtocol();
    public String getApplication();
    public String useJni();
    public String useDictFromFile();
    public String mountTrace();
    public int getIpcTraceFlags();
    public int getConnectionTimeout();
    public int getPingInterval();
    public int getConnectionRetryInterval();
    public int getConnectMaxRetryDelay();
    public String getMountVersion();
    public String getMasterFidFile();
    public String getEnumTypeFile();
}

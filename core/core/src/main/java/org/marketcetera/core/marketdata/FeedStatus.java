package org.marketcetera.core.marketdata;

public enum FeedStatus 
{
    OFFLINE, ERROR, AVAILABLE, UNKNOWN;
    
    public boolean isRunning()
    {        
        return equals(AVAILABLE);
    }
}

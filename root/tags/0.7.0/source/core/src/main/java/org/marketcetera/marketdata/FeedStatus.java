package org.marketcetera.marketdata;

public enum FeedStatus 
{
    OFFLINE, ERROR, AVAILABLE, UNKNOWN;
    
    public boolean isRunning()
    {        
        return equals(AVAILABLE);
    }
}

package org.marketcetera.trade.impl;

import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * Stub class temporarily created for Spring Boot 3 migration.
 * Original implementation required QueryDSL, which isn't compatible with Jakarta EE.
 * 
 * See OutgoingMessageLookupStrategy.java.disabled for the original implementation.
 */
public class OutgoingMessageLookupStrategy
{
    public OutgoingMessageLookupStrategy()
    {
        SLF4JLoggerProxy.warn(this, "This is a stub implementation for Spring Boot 3 migration");
    }
}
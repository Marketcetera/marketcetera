package org.marketcetera.core;

/** Marker interface for all {@link ApplicationBase} subclasses
 * to force them to implement functions for MBean introspection
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface ApplicationMBeanBase
{
    public void shutdown() throws Exception;
}

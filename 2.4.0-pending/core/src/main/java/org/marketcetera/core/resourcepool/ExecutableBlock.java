package org.marketcetera.core.resourcepool;

public interface ExecutableBlock
{
    public Object execute(Resource inResource)
        throws Throwable;
}

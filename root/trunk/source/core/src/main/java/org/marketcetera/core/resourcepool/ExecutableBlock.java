package org.marketcetera.core.resourcepool;

public interface ExecutableBlock
{
    public void execute(Resource inResource)
        throws Throwable;
}

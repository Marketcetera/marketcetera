package org.marketcetera.core;

@ClassVersion("$Id$") //$NON-NLS-1$
public interface Clock {

	public abstract long getTime();

	public abstract long getApproximateTime();

}

package org.marketcetera.core;

@ClassVersion("$Id$")
public interface Clock {

	public abstract long getTime();

	public abstract long getApproximateTime();

}
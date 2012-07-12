package org.marketcetera.core;

import java.util.concurrent.atomic.AtomicLong;

@ClassVersion("$Id$") //$NON-NLS-1$
public class OffsetClock implements Clock {

	Clock mReferenceClock;
	AtomicLong mOffset = new AtomicLong();
	
	public OffsetClock(Clock referenceClock, long initialOffset){
		mReferenceClock = referenceClock;
		mOffset.set(initialOffset);
	}
	
	public long getTime() {
		return mReferenceClock.getTime() + mOffset.get();
	}

	public long getApproximateTime() {
		return mReferenceClock.getApproximateTime() + mOffset.get();
	}

	public long getOffset() {
		return mOffset.get();
	}

	public void setOffset(long offset) {
		mOffset.set(offset);
	}

	public Clock getReferenceClock() {
		return mReferenceClock;
	}

}

package org.marketcetera.core;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class DelayedItem<T> implements Delayed {
    private long mItemSendTime;
    private T mItem;
    private Clock mClock;

    /**
     * Creates a new instance of DelayedItem
     * @param delayInMillis the delay time in milliseconds from now
     * @param anItem the item to be delayed
     */
    public DelayedItem(long delayInMillis, T anItem, Clock clock) {
        if (anItem == null) {
            throw new IllegalArgumentException(Messages.ERROR_NULL_DELAYED_ITEM.getText());
        }
        if (clock == null)
        {
            mClock = new Clock() {
                public long getTime() { return System.currentTimeMillis(); }
                public long getApproximateTime() { return System.currentTimeMillis(); }
            };
        } else {
            mClock = clock;
        }
        mItemSendTime = mClock.getTime() + delayInMillis;
        SLF4JLoggerProxy.debug(this, "Will send at {}", new Date(mItemSendTime));  //$NON-NLS-1$  //non-i18n
        mItem = anItem;
    }

    public DelayedItem(long delayInMillis, T anItem)
    {
        this(delayInMillis, anItem, null);
    }

    /**
     * This method returns a positive number if the delay time of this object is greater
     * than the delay time for the compared object, and a negative number if the opposite is
     * true.  It returns zero if the delay times of the two objects are exactly the same
     * @param object The object to be compared
     * @return The comparison value as described above
     */
    public int compareTo(java.util.concurrent.Delayed object) {
        if (  mItemSendTime < (((DelayedItem) object).mItemSendTime)  )
            return -1;
        else if (  mItemSendTime > (((DelayedItem) object).mItemSendTime) )
            return 1;
        return 0;
    }

    /**
     * Compare delay times for equality
     * @param object the object to compare
     * @return True if the delay time is the same as object's delay time
     */
    public boolean equals(Object object) {
        return ((DelayedItem) object).mItemSendTime == mItemSendTime;
    }

    /**
     * Compare delay times for equality
     * @param object the object to compare
     * @return True if the delay time is the same as object's delay time
     */
    public boolean equals(DelayedItem object) {
        return ((DelayedItem) object).mItemSendTime == mItemSendTime;
    }

    /**
     * Returns the amount of time left until the delay is over, in the units specified
     * by unit.
     * @param unit The units to calibrate delay time in
     * @return The amount of time until the delay is over.
     */
    public long getDelay(TimeUnit unit) {
        long n = mItemSendTime - mClock.getTime();
        return unit.convert(n, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets the time as represented by milliseconds from the epoch that the delay should
     * end.
     * @return the time the delay should end
     */
    public long getItemSendTime() {
        return mItemSendTime;
    }

    /**
     * This gets the item that is being delayed.  This is applications specific and is 
     * the type specified by the generic's type parameter.
     * @return the item to be delayed
     */
    public T getItem(){ return mItem;}

    /**
     * Converts this object to a human readable string.
     * @return A human readable string describing this object
     */
    public String toString() {
        return Messages.DELAYED_ITEM_DESC.getText(getDelay(TimeUnit.MILLISECONDS), mItem.toString());
    }
}

package org.marketcetera.core.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Test implementation of {@link ISubscriber}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class MockSubscriber
    implements ISubscriber
{
    private boolean mInteresting = false;
    private volatile Object mData = null;
    private boolean mInterestingThrows = false;
    private boolean mPublishThrows = false;
    private int mCounter = 0;
    private static int sCounter = 0;
    private int mPublishCount = 0;
    private List<Object> mPublications;
    private volatile Thread mPublishThread = null;
    private volatile Semaphore mReleaseSemaphore = null;
    private volatile Semaphore mAcquireSemaphore = null;
    
    public MockSubscriber()
    {
        this(true,
             false,
             false);
    }
    
    public MockSubscriber(boolean inInteresting,
                          boolean inInterestingThrows,
                          boolean inPublishThrows)
    {
        mInteresting = inInteresting;
        mInterestingThrows = inInterestingThrows;
        mPublishThrows = inPublishThrows;
        mPublications = new ArrayList<Object>();
    }

    public boolean isInteresting(Object inData)
    {
        if(mInterestingThrows) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        return mInteresting;
    }

    public void publishTo(Object inData)
    {
        if(mAcquireSemaphore != null) {
            try {
                mAcquireSemaphore.acquire();
            } catch (InterruptedException ignore) {
            }
        }
        if(mPublishThrows) {
            throw new NullPointerException("This exception is expected"); //$NON-NLS-1$
        }
        mData = inData;
        mCounter = ++sCounter;
        mPublishCount += 1;
        mPublications.add(inData);
        mPublishThread = Thread.currentThread();
        if(mReleaseSemaphore != null) {
            mReleaseSemaphore.release();
        }
    }
    
    public int getPublishCount()
    {
        return mPublishCount;
    }

    public int getCounter()
    {
        return mCounter;
    }

    public Object getData()
    {
        return mData;
    }

    public boolean getInteresting()
    {
        return mInteresting;
    }

    public boolean getInterestingThrows()
    {
        return mInterestingThrows;
    }

    public boolean getPublishThrows()
    {
        return mPublishThrows;
    }

    public void setData(Object inData)
    {
        mData = inData;
    }

    public void setInteresting(boolean inInteresting)
    {
        mInteresting = inInteresting;
    }

    public void setInterestingThrows(boolean inInterestingThrows)
    {
        mInterestingThrows = inInterestingThrows;
    }

    public void setPublishThrows(boolean inPublishThrows)
    {
        mPublishThrows = inPublishThrows;
    }
    
    public List<Object> getPublications()
    {
        return mPublications;
    }

    public Thread getPublishThread()
    {
        return mPublishThread;
    }

    public void setReleaseSemaphore(Semaphore inReleaseSemaphore)
    {
        mReleaseSemaphore = inReleaseSemaphore;
    }

    public void setAcquireSemaphore(Semaphore inAcquireSemaphore)
    {
        mAcquireSemaphore = inAcquireSemaphore;
    }

    /**
     * Sets the publishCount value.
     *
     * @param inPublishCount publish count value.
     */
    public void setPublishCount(int inPublishCount)
    {
        mPublishCount = inPublishCount;
    }
    /**
     * Resets the data collectors.
     * 
     * <p>After calling this method, the subscriber will be returned
     * to its initial creation state.
     */
    public void reset()
    {
        setPublishCount(0);
        setData(null);
        mPublications.clear();
        mPublishThread = null;
        mReleaseSemaphore = null;
        mAcquireSemaphore = null;
    }
}

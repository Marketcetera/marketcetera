package org.marketcetera.core.publisher;

/**
 * Test implementation of {@link Subscriber}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class TestSubscriber
    implements Subscriber
{
    private boolean mInteresting = false;
    private Object mData = null;
    private boolean mInterestingThrows = false;
    private boolean mPublishThrows = false;
    private int mCounter = 0;
    private static int sCounter = 0;
    private int mPublishCount = 0;
    
    public TestSubscriber()
    {
        this(true,
             false,
             false);
    }
    
    public TestSubscriber(boolean inInteresting,
                          boolean inInterestingThrows,
                          boolean inPublishThrows)
    {
        mInteresting = inInteresting;
        mInterestingThrows = inInterestingThrows;
        mPublishThrows = inPublishThrows;
    }

    public boolean isInteresting(Object inData)
    {
        if(mInterestingThrows) {
            throw new NullPointerException("This exception is expected");
        }
        return mInteresting;
    }

    public void publishTo(Object inData)
    {
        if(mPublishThrows) {
            throw new NullPointerException("This exception is expected");
        }
        mData = inData;
        mCounter = ++sCounter;
        mPublishCount += 1;
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

    public void setCounter(int inCounter)
    {
        mCounter = inCounter;
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
}
package org.marketcetera.core.publisher;

import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of {@link ISubscriber}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestSubscriber
    implements ISubscriber
{
    private boolean mInteresting = false;
    private Object mData = null;
    private boolean mInterestingThrows = false;
    private boolean mPublishThrows = false;
    private int mCounter = 0;
    private static int sCounter = 0;
    private int mPublishCount = 0;
    private List<Object> mPublications;
    
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
        mPublications = new ArrayList<Object>();
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
        mPublications.add(inData);
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

    /**
     * Sets the publishCount value.
     *
     * @param a <code>TestSubscriber</code> value
     */
    public void setPublishCount(int inPublishCount)
    {
        mPublishCount = inPublishCount;
    }
}
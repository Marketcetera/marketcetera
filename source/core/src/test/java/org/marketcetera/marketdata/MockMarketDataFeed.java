package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.TestMessages.EXPECTED_EXCEPTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.MockEventTranslator;

/* $License$ */

/**
 * Test implementation of <code>AbstractMarketDataFeed</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class MockMarketDataFeed
    extends AbstractMarketDataFeed<MockMarketDataFeedToken,
                                   MockMarketDataFeedCredentials,
                                   MockDataRequestTranslator,
                                   MockEventTranslator,
                                   String,
                                   MockMarketDataFeed>
{
    private final int mDelay;
    private Set<Capability> capabilities = EnumSet.noneOf(Capability.class);
    private int mCounter = 0;
    
    private enum State { 
        logged_out, logged_in;
        
        private boolean isLoggedIn()
        {
            return this.equals(State.logged_in);
        }
    };
      
    private State mState;
    
    private boolean mLoginFails = false;
    private boolean mInitFails = false;
    private boolean mExecutionFails = false;
    private boolean mCancelFails = false;
    private boolean mExecuteReturnsNothing = false;
    private boolean mExecuteReturnsNull = false;
    private boolean mIsLoggedInThrows = false;
    private boolean mLoginThrows = false;
    private boolean mInitThrows = false;
    private boolean mBeforeExecuteThrows = false;
    private boolean mBeforeExecuteReturnsFalse = false;
    private boolean mGenerateTokenThrows = false;
    private boolean mGetEventTranslatorThrows = false;
    private boolean mGetMessageTranslatorThrows = false;
    private boolean mAfterExecuteThrows = false;
    private boolean mShouldTimeout = false;
    private long mTimeout = 60;
    
    private static final Random sRandom = new Random(System.nanoTime());
    
    private Set<String> mCanceledHandles = new LinkedHashSet<String>();
    private Set<String> mCreatedHandles = new LinkedHashSet<String>();
    private final List<EventBase> eventsToReturn = new ArrayList<EventBase>();
    
    public MockMarketDataFeed()
        throws NoMoreIDsException
    {
        this(FeedType.SIMULATED);
    }
    /**
     * Create a new <code>TestMarketDataFeed</code> instance.
     *
     * @param inFeedType
     * @throws NoMoreIDsException 
     */
    public MockMarketDataFeed(FeedType inFeedType)
        throws NoMoreIDsException
    {
        this(inFeedType,
             MockMarketDataFeed.class.toString(),
             0);
    }

    public MockMarketDataFeed(FeedType inFeedType,
                              String inProviderName,
                              int inDelay) 
        throws NoMoreIDsException
    {
        super(inFeedType,
              inProviderName);
        mDelay = inDelay;
        setState(State.logged_out);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return capabilities;
    }
    /**
     * Sets the capabilities to use for this feed. 
     *
     * @param inCapabilities a <code>Set&lt;Capability&gt;</code> value
     */
    public void setCapabilities(Set<Capability> inCapabilities)
    {
        capabilities = inCapabilities;
    }
    /**
     * Sets the events to return for a market data request.
     *
     * @param inEvents a <code>List&lt;EventBase&gt;</code> value
     */
    public void setEventsToReturn(List<EventBase> inEvents)
    {
        eventsToReturn.clear();
        eventsToReturn.addAll(inEvents);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(quickfix.Message)
     */
    @Override
    protected MockMarketDataFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec)
            throws FeedException
    {
        if(getGenerateTokenThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return MockMarketDataFeedToken.getToken(inTokenSpec,
                                                this);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    protected boolean doLogin(MockMarketDataFeedCredentials inCredentials)
    {
        if(getLoginThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        if(getLoginFails()) {
            return false;
        }

        setState(State.logged_in);
        return true;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    protected void doLogout()
    {
        setState(State.logged_out);
    }
    private final ConcurrentLinkedQueue<String> mQueue = new ConcurrentLinkedQueue<String>();
    
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#afterDoExecute()
     */
    @Override
    protected void afterDoExecute(MockMarketDataFeedToken inToken,
                                  Exception inException)
    {
        if(getAfterExecuteThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        String handle = mQueue.poll();
        if(inToken != null) {
            inToken.setHandle(handle);
        }
        if(handle != null) {
            if(eventsToReturn.isEmpty()) {
                dataReceived(handle,
                             inToken.getTokenSpec().getDataRequest());
            } else {
                for(EventBase event : eventsToReturn) {
                    dataReceived(handle,
                                 event);
                }
                eventsToReturn.clear();
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    protected MockDataRequestTranslator getMessageTranslator()
    {
        if(getGetMessageTranslatorThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return new MockDataRequestTranslator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn()
     */
    protected boolean isLoggedIn()
    {
        if(getShouldTimeout()) {
            try {
                Thread.sleep((getTimeout() + 5) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(getIsLoggedInThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return getState().isLoggedIn();
    }

    /**
     * Get the state value.
     *
     * @return a <code>TestMarketDataFeed</code> value
     */
    public State getState()
    {
        return mState;
    }

    /**
     * Sets the state value.
     *
     * @param a <code>TestMarketDataFeed</code> value
     */
    private void setState(State inState)
    {
        mState = inState;
    }

    /**
     * Get the allowLogin value.
     *
     * @return a <code>TestMarketDataFeed</code> value
     */
    public boolean getLoginFails()
    {
        return mLoginFails;
    }

    /**
     * Sets the allowLogin value.
     *
     * @param a <code>TestMarketDataFeed</code> value
     */
    public void setLoginFails(boolean inAllowLogin)
    {
        mLoginFails = inAllowLogin;
    }

    /**
     * Get the initFails value.
     *
     * @return a <code>TestMarketDataFeed</code> value
     */
    public boolean isInitFails()
    {
        return mInitFails;
    }

    /**
     * Sets the initFails value.
     *
     * @param a <code>TestMarketDataFeed</code> value
     */
    public void setInitFails(boolean inInitFails)
    {
        mInitFails = inInitFails;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doInitialize(org.marketcetera.marketdata.AbstractMarketDataFeedToken)
     */
    protected boolean doInitialize(MockMarketDataFeedToken inToken)
        throws InterruptedException
    {
        if(getInitThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        if(isInitFails()) {
            super.doInitialize();
            return false;
        }
        return super.doInitialize();
    }

    /**
     * Get the executionFails value.
     *
     * @return a <code>TestMarketDataFeed</code> value
     */
    public boolean getExecutionFails()
    {
        return mExecutionFails;
    }

    /**
     * Sets the executionFails value.
     *
     * @param a <code>TestMarketDataFeed</code> value
     */
    public void setExecutionFails(boolean inExecutionFails)
    {
        mExecutionFails = inExecutionFails;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    protected void doCancel(String inHandle)
    {        
        mCanceledHandles.add(inHandle);
        if(isCancelFails()) {
            throw new NullPointerException("This exception is expected");
        }
    }
    
    public List<String> getCanceledHandles()
    {
        return new ArrayList<String>(mCanceledHandles);
    }
    
    public List<String> getCreatedHandles()
    {
        return new ArrayList<String>(mCreatedHandles);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    protected MockEventTranslator getEventTranslator()
    {
        if(getGetEventTranslatorThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return MockEventTranslator.getTestEventTranslator();
    }
    /**
     * Get the cancelFails value.
     *
     * @return a <code>TestMarketDataFeed</code> value
     */
    public boolean isCancelFails()
    {
        return mCancelFails;
    }
    /**
     * Sets the cancelFails value.
     *
     * @param a <code>TestMarketDataFeed</code> value
     */
    public void setCancelFails(boolean inCancelFails)
    {
        mCancelFails = inCancelFails;
    }
    @Override
    protected boolean beforeDoExecute(MockMarketDataFeedToken inToken)
    {
        if(getBeforeExecuteThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        if(getBeforeExecuteReturnsFalse()) {
            return false;
        }
        if(inToken != null &&
           inToken.getShouldFail()) {
            throw new NullPointerException("This exception is expected");
        }
        return super.beforeDoExecute(inToken);
    }
    public boolean getExecuteReturnsNothing()
    {
        return mExecuteReturnsNothing;
    }
    public void setExecuteReturnsNothing(boolean executeReturnsNothing)
    {
        mExecuteReturnsNothing = executeReturnsNothing;
    }
    public boolean getIsLoggedInThrows()
    {
        return mIsLoggedInThrows;
    }
    public void setIsLoggedInThrows(boolean inIsLoggedInThrows)
    {
        mIsLoggedInThrows = inIsLoggedInThrows;
    }
    public boolean getLoginThrows()
    {
        return mLoginThrows;
    }
    public void setLoginThrows(boolean inLoginThrows)
    {
        mLoginThrows = inLoginThrows;
    }
    public boolean getInitThrows()
    {
        return mInitThrows;
    }
    public void setInitThrows(boolean inInitThrows)
    {
        mInitThrows = inInitThrows;
    }
    public boolean getBeforeExecuteThrows()
    {
        return mBeforeExecuteThrows;
    }
    public void setBeforeExecuteThrows(boolean inBeforeExecuteThrows)
    {
        mBeforeExecuteThrows = inBeforeExecuteThrows;
    }
    public boolean getGenerateTokenThrows()
    {
        return mGenerateTokenThrows;
    }
    public void setGenerateTokenThrows(boolean inGenerateTokenThrows)
    {
        mGenerateTokenThrows = inGenerateTokenThrows;
    }
    public boolean getGetEventTranslatorThrows()
    {
        return mGetEventTranslatorThrows;
    }
    public void setGetEventTranslatorThrows(boolean inGetEventTranslatorThrows)
    {
        mGetEventTranslatorThrows = inGetEventTranslatorThrows;
    }
    public boolean getBeforeExecuteReturnsFalse()
    {
        return mBeforeExecuteReturnsFalse;
    }
    public void setBeforeExecuteReturnsFalse(boolean inBeforeExecuteReturnsFalse)
    {
        mBeforeExecuteReturnsFalse = inBeforeExecuteReturnsFalse;
    }
    public boolean getGetMessageTranslatorThrows()
    {
        return mGetMessageTranslatorThrows;
    }
    public void setGetMessageTranslatorThrows(boolean inGetMessageTranslatorThrows)
    {
        mGetMessageTranslatorThrows = inGetMessageTranslatorThrows;
    }
    public boolean getAfterExecuteThrows()
    {
        return mAfterExecuteThrows;
    }
    public void setAfterExecuteThrows(boolean inAfterExecuteThrows)
    {
        mAfterExecuteThrows = inAfterExecuteThrows;
    }
    public boolean getExecuteReturnsNull()
    {
        return mExecuteReturnsNull;
    }
    public void setExecuteReturnsNull(boolean inExecuteReturnsNull)
    {
        mExecuteReturnsNull = inExecuteReturnsNull;
    }
    /**
     * Causes the given data to be submitted in reference to the given handle.
     * 
     * <p>This method can be used to simulate a repeatedly-updated subscription.
     * 
     * @param inHandle a <code>String</code> value
     * @param inData an <code>Object</code> value
     */
    public void submitData(String inHandle,
                           Object inData)
    {
        dataReceived(inHandle,
                     inData);
    }
    /**
     * Get the timeout value.
     *
     * @return a <code>TestMarketDataFeed</code> value
     */
    public boolean getShouldTimeout()
    {
        return mShouldTimeout;
    }
    /**
     * Sets the timeout value.
     *
     * @param a <code>TestMarketDataFeed</code> value
     */
    public void setShouldTimeout(boolean inTimeout)
    {
        mShouldTimeout = inTimeout;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getTimeout()
     */
    @Override
    protected long getTimeout()
    {
        if(getShouldTimeout()) {
            mTimeout = 10;
            return mTimeout;
        }
        return super.getTimeout();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.lang.Object)
     */
    @Override
    protected List<String> doMarketDataRequest(String inData)
            throws FeedException
    {
        if(getExecutionFails()) {
            throw new FeedException(EXPECTED_EXCEPTION);
        }
        if(mDelay > 0) {
            try {
                Thread.sleep(sRandom.nextInt(mDelay));
            } catch (InterruptedException e) {
                throw new FeedException(e);
            }
        }
        String handle = String.format("%d",
                                      ++mCounter);
        if(!getExecuteReturnsNothing() &&
           !getExecuteReturnsNull()) {
            mCreatedHandles.add(handle);
            mQueue.add(handle);
        }
        if(getExecuteReturnsNull()) {
            return null;
        }
        return getExecuteReturnsNothing() ? new ArrayList<String>() : Arrays.asList(handle);
    }
}

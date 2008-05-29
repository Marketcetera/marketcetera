package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.event.TestEventTranslator;
import org.marketcetera.quickfix.TestMessageTranslator;

import quickfix.Message;

/**
 * Test implementation of <code>AbstractMarketDataFeed</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFeed
    extends AbstractMarketDataFeed<TestMarketDataFeedToken,
                                   TestMarketDataFeedCredentials,
                                   TestMessageTranslator,
                                   TestEventTranslator,
                                   String,
                                   TestMarketDataFeed>
{
    private final int mDelay;
    
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
    
    private static final Random sRandom = new Random(System.nanoTime());
    
    private List<String> mCanceledHandles = new ArrayList<String>();
    private List<String> mCreatedHandles = new ArrayList<String>();
    
    public TestMarketDataFeed() 
        throws NoMoreIDsException
    {
        this(FeedType.SIMULATED,
             null);
    }
    /**
     * Create a new <code>TestMarketDataFeed</code> instance.
     *
     * @param inFeedType
     * @param inCredentials
     * @throws NoMoreIDsException 
     */
    public TestMarketDataFeed(FeedType inFeedType) 
        throws NoMoreIDsException
    {
        this(inFeedType,
             TestMarketDataFeed.class.toString(),
             null,
             0);
    }
    /**
     * Create a new <code>TestMarketDataFeed</code> instance.
     *
     * @param inFeedType
     * @param inCredentials
     * @throws NoMoreIDsException 
     */
    public TestMarketDataFeed(FeedType inFeedType,
                              TestMarketDataFeedCredentials inCredentials) 
        throws NoMoreIDsException
    {
        this(inFeedType,
             TestMarketDataFeed.class.toString(),
             inCredentials,
             0);
    }

    public TestMarketDataFeed(FeedType inFeedType,
                              String inProviderName,
                              TestMarketDataFeedCredentials inCredentials,
                              int inDelay) 
        throws NoMoreIDsException
    {
        super(inFeedType,
              inProviderName, 
              inCredentials);
        mDelay = inDelay;
        setState(State.logged_out);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(quickfix.Message)
     */
    @Override
    protected TestMarketDataFeedToken generateToken(MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> inTokenSpec)
            throws FeedException
    {
        if(getGenerateTokenThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return TestMarketDataFeedToken.getToken(inTokenSpec,
                                                this);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    protected boolean doLogin(TestMarketDataFeedCredentials inCredentials)
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

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doMarketDataRequest(java.lang.Object)
     */
    protected List<String> doMarketDataRequest(String inData)
            throws FeedException
    {
        if(getExecutionFails()) {
            throw new FeedException("This exception is expected");
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
    private final ConcurrentLinkedQueue<String> mQueue = new ConcurrentLinkedQueue<String>();
    
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#afterDoExecute()
     */
    @Override
    protected void afterDoExecute(TestMarketDataFeedToken inToken, 
                                  Throwable inException)
    {
        if(getAfterExecuteThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        String handle = mQueue.poll();
        if(handle != null) {
            dataReceived(handle,
                         inToken.getTokenSpec().getMessage());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    protected TestMessageTranslator getMessageTranslator()
    {
        if(getGetMessageTranslatorThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return new TestMessageTranslator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn()
     */
    protected boolean isLoggedIn(TestMarketDataFeedCredentials inCredentials)
    {
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
    protected boolean doInitialize(TestMarketDataFeedToken inToken)
        throws InterruptedException
    {
        if(getInitThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        if(isInitFails()) {
            super.doInitialize(inToken);
            return false;
        }
        return super.doInitialize(inToken);
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
    protected TestEventTranslator getEventTranslator()
    {
        if(getGetEventTranslatorThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return new TestEventTranslator();
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doDerivativeSecurityListRequest(java.lang.Object)
     */
    @Override
    protected List<String> doDerivativeSecurityListRequest(String inData)
            throws FeedException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doSecurityListRequest(java.lang.Object)
     */
    @Override
    protected List<String> doSecurityListRequest(String inData)
            throws FeedException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
    @Override
    protected boolean beforeDoExecute(TestMarketDataFeedToken inToken)
        throws InterruptedException
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
     * Causes the given message to be submitted in reference to the given handle.
     * 
     * <p>This method can be used to simulate a repeatedly-updated subscription.
     * 
     * @param inHandle a <code>String</code> value
     * @param inMessage a <code>Message</code> value
     */
    public void submitData(String inHandle,
                           Message inMessage)
    {
        dataReceived(inHandle,
                     inMessage);
    }
}

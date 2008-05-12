package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.event.TestEventTranslator;
import org.marketcetera.quickfix.TestMessageTranslator;

/**
 *
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
                                   String,TestMarketDataFeed>
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
        return TestMarketDataFeedToken.getToken(inTokenSpec,
                                                this);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    protected boolean doLogin(TestMarketDataFeedCredentials inCredentials)
    {
        if(isLoginFails()) {
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
        if(isExecutionFails()) {
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
        mCreatedHandles.add(handle);
        mQueue.add(handle);
        return Arrays.asList(handle);
    }
    private final ConcurrentLinkedQueue<String> mQueue = new ConcurrentLinkedQueue<String>();
    
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#afterDoExecute()
     */
    @Override
    protected void afterDoExecute(TestMarketDataFeedToken inToken)
    {
        String handle = mQueue.poll();
        dataReceived(handle,
                     this);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    protected TestMessageTranslator getMessageTranslator()
    {
        return new TestMessageTranslator();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn()
     */
    protected boolean isLoggedIn(TestMarketDataFeedCredentials inCredentials)
    {
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
    public boolean isLoginFails()
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
    {
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
    public boolean isExecutionFails()
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
        return mCanceledHandles;
    }
    
    public List<String> getCreatedHandles()
    {
        return mCreatedHandles;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    protected TestEventTranslator getEventTranslator()
    {
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
}

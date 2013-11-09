package org.marketcetera.strategyagent;

import org.marketcetera.core.ApplicationContainer;

/**
 * A test agent that prevents the strategy agent from exiting the
 * process.
 */
public class TestAgentRunner
        extends StrategyAgent
{
    /**
     * Create a new TestAgent instance.
     */
    public TestAgentRunner()
    {
        mExitCode = StrategyAgentTestBase.NO_EXIT;
    }
    /**
     * Returns the exit code of the agent.
     *
     * @return the exit code of the agent.
     */
    public int getExitCode() {
        return mExitCode;
    }
    //        @Override
    //        protected void exit(int inExitCode) {
    //            mExitCode = inExitCode;
    //        }

    //        @Override
    //        public void startWaitingForever() {
    //            if (mWaitForever) {
    //                super.startWaitingForever();
    //            }
    //        }
    /**
     * 
     *
     *
     * @param inArguments
     * @throws InterruptedException 
     */
    public void launch(final String[] inArguments)
            throws InterruptedException
            {
        appThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                ApplicationContainer.main(inArguments);
            }
        });
        appThread.start();
        while(ApplicationContainer.getInstance() == null || !ApplicationContainer.getInstance().isWaitingForever()) {
            Thread.sleep(250);
        }
            }
    /**
     * 
     *
     *
     */
    public void stop()
    {
        super.stop();
        if(appThread != null) {
            appThread.interrupt();
        }
        try {
            appThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the appThread value.
     *
     * @return a <code>Thread</code> value
     */
    public Thread getAppThread()
    {
        return appThread;
    }
    private Thread appThread;
    private int mExitCode;
}

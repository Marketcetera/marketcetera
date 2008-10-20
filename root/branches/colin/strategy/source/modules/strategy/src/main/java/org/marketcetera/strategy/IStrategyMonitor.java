package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
public interface IStrategyMonitor
{
    public enum Status
    {
        /**
         * the strategy has not yet started
         */
        NOT_STARTED,
        /**
         * the strategy is running
         */
        RUNNING,
        /**
         * the strategy is done
         */
        COMPLETE,
        /**
         * the strategy failed to run - this is not an error within the strategy, but something
         * fundamental in the structure of the strategy or the strategy execution framework
         */
        FAILED,
        /**
         * the strategy encountered an error during its run.  strategy errors are defined by the
         * strategy author.  this does not reflect a fundamental error in the structure of the
         * strategy or in the strategy execution framework.
         */
        ERROR;
        public boolean isRunning()
        {
            return this.equals(RUNNING);
        }
    }
    public void statusChange(Status inStatus);
    public void onFailed(Throwable t);
}

package org.marketcetera.strategy;

import java.util.List;

import org.marketcetera.event.ExecutionReport;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public interface TestStrategy
{
    public void doGetCurrentTime();
    public List<ExecutionReport> getExecutionReport();
}

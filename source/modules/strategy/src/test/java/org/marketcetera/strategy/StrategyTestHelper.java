package org.marketcetera.strategy;

import java.io.File;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyTestHelper
{
    static final File SAMPLE_STRATEGY_DIR = new File("src" + File.separator + "test" + File.separator + "sample_data",
                                                     "inputs");   
    static final File JAVA_STRATEGY = new File(SAMPLE_STRATEGY_DIR,
                                               "JavaStrategy.java");
}

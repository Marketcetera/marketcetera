package org.marketcetera.cluster;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/* $License$ */

/**
 * Provides test configuration for simple cluster tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("org.marketcetera")
public class SimpleClusterTestConfiguration
{
}

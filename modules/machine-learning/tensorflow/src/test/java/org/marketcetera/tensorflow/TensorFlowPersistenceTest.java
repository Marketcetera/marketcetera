package org.marketcetera.tensorflow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 * Tests TensorFlow persistence capability.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test.xml"})
public class TensorFlowPersistenceTest
{
    @Test
    public void testOne()
            throws Exception
    {
        
    }
    /**
     * test application context
     */
    @Autowired
    private ApplicationContext applicationContext;
}

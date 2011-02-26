package org.marketcetera.systemmodel.persistence;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.server.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring/appServlet/servlet-context.xml"})
public class UserTest
{
    @BeforeClass
    public static void setup()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void test1()
            throws Exception
    {
        assertNotNull(userManager);
    }
    @Autowired
    private UserManager userManager;
}

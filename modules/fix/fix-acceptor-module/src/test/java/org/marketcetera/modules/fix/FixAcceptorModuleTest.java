package org.marketcetera.modules.fix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ProviderInfo;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 * Tests {@link FixAcceptorModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/sample_data/conf/test.xml"})
public class FixAcceptorModuleTest
{
    /**
     * Set up data before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        moduleManager = ApplicationContainer.getInstance().getContext().getBean(ModuleManager.class);
    }
    /**
     * Test the module settings.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testModuleSettings()
            throws Exception
    {
        ProviderInfo providerInfo = moduleManager.getProviderInfo(FixAcceptorModuleFactory.PROVIDER_URN);
        assertNotNull(providerInfo);
        assertFalse(providerInfo.isAutoInstantiate());
        assertFalse(providerInfo.isMultipleInstances());
        List<ModuleURN> instances = moduleManager.getModuleInstances(FixAcceptorModuleFactory.PROVIDER_URN);
        assertFalse(instances.isEmpty());
        assertEquals(1,
                     instances.size());
        ModuleURN instanceUrn = moduleManager.getModuleInstances(FixAcceptorModuleFactory.PROVIDER_URN).iterator().next();
        ModuleInfo moduleInfo = moduleManager.getModuleInfo(instanceUrn);
        assertFalse(moduleInfo.getState().isStarted());
    }
    /**
     * Test starting an acceptor module.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testModuleStart()
            throws Exception
    {
        ModuleURN instanceUrn = moduleManager.getModuleInstances(FixAcceptorModuleFactory.PROVIDER_URN).iterator().next();
        moduleManager.start(instanceUrn);
        moduleManager.stop(instanceUrn);
    }
    /**
     * test module manager
     */
    private ModuleManager moduleManager;
}

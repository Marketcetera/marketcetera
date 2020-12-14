package org.marketcetera.admin.provisioning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.cluster.ClusterDataFactory;
import org.marketcetera.cluster.SimpleClusterDataFactory;
import org.marketcetera.cluster.SimpleClusterService;
import org.marketcetera.cluster.mock.MockProvisioningComponent;
import org.marketcetera.cluster.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 * Public void tests {@link ProvisioningAgent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootTest
@SpringBootConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ProvisioningAgentTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        MockProvisioningComponent testComponent = MockProvisioningComponent.getInstance();
        if(testComponent != null) {
            testComponent.clear();
            assertEquals(0,
                         testComponent.getInvoked());
        }
    }
    /**
     * Test provisioning a valid XML file.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidXml()
            throws Exception
    {
        deployFile("/valid-provisioning.xml");
        verifyProvisioning();
    }
    /**
     * Test provisioning an invalid XML file.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testInvalidXml()
            throws Exception
    {
        deployFile("/log4j2-test.xml");
        verifyNoProvisioning();
    }
    @Ignore@Test
    public void testValidJar()
            throws Exception
    {
    }
    @Ignore@Test
    public void testInvalidJar()
            throws Exception
    {
    }
    /**
     * Get the cluster service value.
     *
     * @return a <code>ClusterService</code> value
     */
    @Bean
    public ClusterService getClusterService()
    {
        return new SimpleClusterService();
    }
    /**
     * Get the cluster data factory value.
     *
     * @return a <code>ClusterDataFactory</code> valu
     */
    @Bean
    public ClusterDataFactory getClusterDataFactory()
    {
        return new SimpleClusterDataFactory();
    }
    /**
     * Get the provisioning agent value.
     *
     * @return a <code>ProvisioningAgent</code> value
     */
    @Bean
    public ProvisioningAgent getProvisioningAgent()
    {
        ProvisioningAgent provisioningAgent = new ProvisioningAgent();
        provisioningAgent.setPollingInterval(provisioningAgentPollingInterval);
        return provisioningAgent;
    }
    /**
     * Verify that provisioning has not occurred.
     */
    private void verifyNoProvisioning()
    {
        MockProvisioningComponent testComponent = MockProvisioningComponent.getInstance();
        if(testComponent != null) {
            assertEquals(0,
                         testComponent.getInvoked());
        }
    }
    /**
     * Verify that provisioning has occurred.
     */
    private void verifyProvisioning()
    {
        MockProvisioningComponent testComponent = MockProvisioningComponent.getInstance();
        assertNotNull(testComponent);
        assertEquals(1,
                     testComponent.getInvoked());
    }
    /**
     * Deploy the file with the given resource name to the provisioning directory.
     *
     * <p>It is expected that the given file is a classpath resource. The name should probably
     * start with '/'.
     * 
     * @param inResourceName a <code>String</code> value
     * @throws Exception if the file could not be deployed
     */
    private void deployFile(String inResourceName)
            throws Exception
    {
        URL validProvisioningAgentUrl = ProvisioningAgentTest.class.getResource(inResourceName);
        File validProvisioningAgentFile = new File(validProvisioningAgentUrl.toURI());
        File provisioningAgentTarget = new File(provisioningAgent.getProvisioningDirectory(),
                                                inResourceName);
        FileUtils.copyFile(validProvisioningAgentFile,
                           provisioningAgentTarget);
        Thread.sleep(provisioningAgentPollingInterval*2);
    }
    /**
     * interval at which to poll for provisioning files, in ms
     */
    private long provisioningAgentPollingInterval = 1000;
    /**
     * bring in the {@link ProvisioningAgent} created in {{@link #getProvisioningAgent()}
     */
    @Autowired
    private ProvisioningAgent provisioningAgent;
}

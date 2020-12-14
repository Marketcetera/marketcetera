package org.marketcetera.admin.provisioning;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

import com.google.common.io.Files;

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
    @Before
    public void setup()
            throws Exception
    {
    }
    @Test
    public void testValidXml()
            throws Exception
    {
        assertNull(MockProvisioningComponent.getInstance());
        URL validProvisioningAgentUrl = ProvisioningAgentTest.class.getResource("/test.xml");
        File validProvisioningAgentFile = new File(validProvisioningAgentUrl.toURI());
        File provisioningAgentDirectory = new File(provisioningAgent.getProvisioningDirectory(),
                                                   "test.xml");
        FileUtils.copyFile(validProvisioningAgentFile,
                           provisioningAgentDirectory);
        Thread.sleep(provisioningAgentPollingInterval*2);
        assertNotNull(MockProvisioningComponent.getInstance());
    }
    @Ignore@Test
    public void testInvalidXml()
            throws Exception
    {
        assertNull(MockProvisioningComponent.getInstance());
    }
    @Ignore@Test
    public void testValidJar()
            throws Exception
    {
        assertNull(MockProvisioningComponent.getInstance());
    }
    @Ignore@Test
    public void testInvalidJar()
            throws Exception
    {
        assertNull(MockProvisioningComponent.getInstance());
    }
    @Bean
    public ClusterService getClusterService()
    {
        return new SimpleClusterService();
    }
    @Bean
    public ClusterDataFactory getClusterDataFactory()
    {
        return new SimpleClusterDataFactory();
    }
    @Bean
    public ProvisioningAgent getProvisioningAgent()
    {
        File provisioningDirectory = Files.createTempDir();
        String provisioningDirectoryPath = provisioningDirectory.getAbsolutePath();
        ProvisioningAgent provisioningAgent = new ProvisioningAgent();
        provisioningAgent.setPollingInterval(provisioningAgentPollingInterval);
        provisioningAgent.setProvisioningDirectory(provisioningDirectoryPath);
        return provisioningAgent;
    }
    private long provisioningAgentPollingInterval = 1000;
    @Autowired
    private ProvisioningAgent provisioningAgent;
}

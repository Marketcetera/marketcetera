package org.marketcetera.cluster.service;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterWorkUnitDescriptor;
import org.marketcetera.cluster.ClusterWorkUnitType;
import org.marketcetera.cluster.MutableClusterData;
import org.marketcetera.cluster.SimpleClusterWorkUnitDescriptor;
import org.marketcetera.cluster.SimpleClusterWorkUnitSpec;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;

/**
 * Test class to verify JAXB functionality with Jakarta EE in the cluster module.
 * 
 * @author Colin DuPlantis
 */
public class JaxbClusterTest {
    
    @BeforeClass
    public static void setupJaxb() {
        // Set system properties to ensure proper Jakarta EE JAXB implementation
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", 
                          "org.eclipse.persistence.jaxb.JAXBContextFactory");
    }
    
    /**
     * Tests the creation of a JAXBContext instance and verifies that marshalling
     * and unmarshalling work correctly with the Jakarta EE implementation.
     */
    @Test
    public void testJaxbContext() throws Exception {
        // Create a JAXBContext with the SimpleClusterMetaData class
        JAXBContext context = JAXBContext.newInstance(SimpleClusterMetaData.class);
        assertNotNull("JAXBContext could not be created", context);
        
        // Create marshaller and unmarshaller
        Marshaller marshaller = context.createMarshaller();
        assertNotNull("Marshaller could not be created", marshaller);
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        assertNotNull("Unmarshaller could not be created", unmarshaller);
        
        // Set up validation event handler
        unmarshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            public boolean handleEvent(ValidationEvent inEvent) {
                throw new RuntimeException(inEvent.getMessage(),
                                          inEvent.getLinkedException());
            }
        });
        
        // Create a SimpleClusterMetaData instance for testing
        MockClusterData clusterData = new MockClusterData();
        Set<ClusterWorkUnitDescriptor> workUnits = new HashSet<>();
        workUnits.add(new SimpleClusterWorkUnitDescriptor(
                new SimpleClusterWorkUnitSpec(ClusterWorkUnitType.SINGLETON, "testId", "testUid"), 
                "testMemberUuid"));
        
        SimpleClusterMetaData metaData = new SimpleClusterMetaData(clusterData, workUnits);
        
        // Marshal to XML
        StringWriter output = new StringWriter();
        marshaller.marshal(metaData, output);
        String xmlData = output.getBuffer().toString();
        assertNotNull("Marshalled XML should not be null", xmlData);
        
        // Unmarshal from XML
        Object unmarshalledObj = unmarshaller.unmarshal(
                new InputStreamReader(new ByteArrayInputStream(xmlData.getBytes())));
        assertNotNull("Unmarshalled object should not be null", unmarshalledObj);
    }
    
    /**
     * Mock implementation of ClusterData for testing.
     */
    private static class MockClusterData implements ClusterData {
        private static final long serialVersionUID = 1L;

        @Override
        public int getTotalInstances() {
            return 1;
        }

        @Override
        public String getHostId() {
            return "testHostId";
        }

        @Override
        public int getHostNumber() {
            return 1;
        }

        @Override
        public int getInstanceNumber() {
            return 1;
        }

        @Override
        public String getUuid() {
            return "testUuid";
        }
        
        @Override
        public MutableClusterData getMutableView() {
            // Return null for test purposes since we don't use this method in the test
            return null;
        }
    }
}
package org.marketcetera.admin.provisioning;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test to diagnose JAXB context creation issues directly
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JaxbModuleLoaderTest {

    @Test
    public void testDirectJaxbContextCreation() throws Exception {
        // Set system properties
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", 
                "org.eclipse.persistence.jaxb.JAXBContextFactory");
        
        // Try to create a JAXBContext directly
        try {
            JAXBContext context = JAXBContext.newInstance(TestEntity.class);
            System.out.println("Successfully created JAXBContext: " + context);
        } catch (JAXBException e) {
            System.out.println("Failed to create JAXBContext: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Simple test entity for JAXB
     */
    @XmlRootElement
    public static class TestEntity {
        private String name;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}
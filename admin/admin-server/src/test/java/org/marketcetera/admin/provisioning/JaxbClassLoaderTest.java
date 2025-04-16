package org.marketcetera.admin.provisioning;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class to diagnose JAXBContext class loading issues
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class JaxbClassLoaderTest {

    @Before
    public void setup() {
        // Set key system properties
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", 
                "org.eclipse.persistence.jaxb.JAXBContextFactory");
    }

    @Test
    public void testJaxbContextLoading() throws Exception {
        // Print classpath
        System.out.println("CLASSPATH: " + System.getProperty("java.class.path"));
        
        // Try loading Jakarta JAXBContext
        try {
            Class<?> jakartaClass = Class.forName("jakarta.xml.bind.JAXBContext");
            System.out.println("Successfully loaded Jakarta JAXB: " + jakartaClass.getName());
            System.out.println("  from: " + jakartaClass.getProtectionDomain().getCodeSource().getLocation());
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load Jakarta JAXB: " + e.getMessage());
        }
        
        // Try loading javax JAXBContext
        try {
            Class<?> javaxClass = Class.forName("javax.xml.bind.JAXBContext");
            System.out.println("Successfully loaded Java EE JAXB: " + javaxClass.getName());
            System.out.println("  from: " + javaxClass.getProtectionDomain().getCodeSource().getLocation());
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load Java EE JAXB: " + e.getMessage());
        }
        
        // Try loading implementation classes
        try {
            Class<?> moxy = Class.forName("org.eclipse.persistence.jaxb.JAXBContextFactory");
            System.out.println("Successfully loaded MOXy JAXB: " + moxy.getName());
            System.out.println("  from: " + moxy.getProtectionDomain().getCodeSource().getLocation());
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load MOXy JAXB: " + e.getMessage());
        }
        
        // Check if cluster-core has been loaded and what class loader is used
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            System.out.println("Context ClassLoader: " + cl);
            System.out.println("Parent ClassLoader: " + cl.getParent());
            
            // Try to manually load the problematic class
            Class<?> abstractServiceClass = cl.loadClass("org.marketcetera.cluster.service.AbstractClusterService");
            System.out.println("Successfully loaded AbstractClusterService: " + abstractServiceClass.getName());
            System.out.println("  from: " + abstractServiceClass.getProtectionDomain().getCodeSource().getLocation());
            
            // Try to load SimpleClusterMetaData
            Class<?> metadataClass = cl.loadClass("org.marketcetera.cluster.service.SimpleClusterMetaData");
            System.out.println("Successfully loaded SimpleClusterMetaData: " + metadataClass.getName());
            System.out.println("  from: " + metadataClass.getProtectionDomain().getCodeSource().getLocation());
            
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load AbstractClusterService: " + e.getMessage());
        }
    }
}
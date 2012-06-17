package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import javax.management.MXBean;

/* $License$ */
/**
 * MXBean interface to the factory for testing JMX integration.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: JMXTestFactoryMXBean.java 82330 2012-04-10 16:29:13Z colin $")
@MXBean(true)
@DisplayName("A factory for testing JMX integration")
public interface JMXTestFactoryMXBean {
    @DisplayName("The total number of instances created by the factory")
    int getNumInstancesCreated();

    @DisplayName("resets the total number of instances created by the factory")
    void resetNumInstancesCreated();

    @DisplayName("The annotation that should be assigned to every new instance created")
    String getNewInstanceAnnotation();

    @DisplayName("The annotation that should be assigned to every new instance created")
    void setNewInstanceAnnotation(
            @DisplayName("Annotation value that should be assigned to every new instance created")
            String inNewInstanceAnnotation);
}

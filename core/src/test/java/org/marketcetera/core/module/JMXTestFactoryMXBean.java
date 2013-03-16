package org.marketcetera.core.module;

import javax.management.MXBean;

/* $License$ */
/**
 * MXBean interface to the factory for testing JMX integration.
 *
 */
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

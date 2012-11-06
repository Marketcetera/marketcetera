package org.marketcetera.itests;

import javax.inject.Inject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.api.security.SecurityService;
import org.marketcetera.api.security.Subject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.OptionUtils.combine;

/**
 * @version $Id$
 * @date 8/19/12 4:01 AM
 */
@Ignore
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class SecurityTest {

    @Inject
    private SecurityService securityService;

    @Test
    public void testServicePublished() throws Exception {
        Subject subject = securityService.getSubject();
        assertNotNull("Subject cannot be null", subject);
    }

    @Configuration
    public Option[] configOptions() {

        return combine(new Option[]{karafDistributionConfiguration().frameworkUrl(maven().groupId("org.apache.karaf").
                artifactId("apache-karaf").type("tar.gz").versionAsInProject()).karafVersion("3.0.0-SNAPSHOT").name("Apache Karaf"),
                provision(
                        mavenBundle().groupId("org.marketcetera.security").artifactId("shiro-security").version("3.0.0-SNAPSHOT"),
                        mavenBundle().groupId("org.marketcetera").artifactId("api").version("3.0.0-SNAPSHOT")
                )
        });

    }
}

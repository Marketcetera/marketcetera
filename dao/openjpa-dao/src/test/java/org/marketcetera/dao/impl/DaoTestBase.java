package org.marketcetera.dao.impl;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.marketcetera.core.LoggerConfiguration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.maven;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public abstract class DaoTestBase
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * 
     *
     *
     * @return
     */
    @Configuration
    public Option[] config()
    {
        return new Option[] { karafDistributionConfiguration().frameworkUrl(maven().groupId("org.apache.karaf").
                                                                           artifactId("apache-karaf").type("zip").versionAsInProject()).karafVersion("3.0.0-SNAPSHOT").name("Apache Karaf")};
    }
}

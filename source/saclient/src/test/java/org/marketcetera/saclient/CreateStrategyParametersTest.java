package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;

/* $License$ */
/**
 * Tests {@link CreateStrategyParameters}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class CreateStrategyParametersTest {
    /**
     * Verifies all constructor parameter checks.
     *
     * @throws Exception if there were unexpected test failures.
     */
    @Test
    public void constructorChecks() throws Exception {
        //strategy name
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                new CreateStrategyParameters("blah", null, "JAVA", new File("dontmatter"), null, false);
            }
        };
        //language
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                new CreateStrategyParameters("blah", "naah", null, new File("dontmatter"), null, false);
            }
        };
        //strategy script
        new ExpectedFailure<NullPointerException>(null){
            @Override
            protected void run() throws Exception {
                new CreateStrategyParameters("blah", "naah", "JAVA", null, null, false);
            }
        };
        //non existent strategy script
        final File source = new File("doesnotexist");
        new ExpectedFailure<FileNotFoundException>(source.getAbsolutePath()){
            @Override
            protected void run() throws Exception {
                new CreateStrategyParameters("blah", "naah", "JAVA", source, null, false);
            }
        };
        //non readable strategy script
        final File unreadable = File.createTempFile("strat","txt");
        unreadable.deleteOnExit();
        unreadable.setReadable(false);
        unreadable.setReadable(false, false);
        //test unreadability failure only if we can make the file unreadable.
        if(!unreadable.canRead()) {
            new ExpectedFailure<FileNotFoundException>(unreadable.getAbsolutePath()){
                @Override
                protected void run() throws Exception {
                    new CreateStrategyParameters("blah", "naah", "JAVA",
                            unreadable, null, false);
                }
            };
        } else {
            SLF4JLoggerProxy.info(this, "Cannot make a test file unreadable.");
        }
    }

    /**
     * Tests the constructor and getters.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void constructAndGet() throws Exception {
        String instanceName = "myname";
        String strategyName = "name";
        String language = "JAVA";
        String parameters = "game=lame";
        File tmp = File.createTempFile("strat", ".tmp");
        tmp.deleteOnExit();
        CreateStrategyParameters csp = new CreateStrategyParameters(
                instanceName, strategyName, language,
                tmp, parameters, false);
        assertCSP(csp, instanceName, strategyName, language, parameters, false);
        //test with only the required parameters specified.
        csp = new CreateStrategyParameters(null, strategyName, language,
                tmp, null, true);
        assertCSP(csp, null, strategyName, language, null, true);
        //test with mininmal parameter values
        csp = new CreateStrategyParameters("", "", "", tmp, "", true);
        assertCSP(csp, "", "", "", "", true);
    }

    /**
     * Tests file handling.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void fileHandling() throws Exception {
        //Create a temp file with text.
        File tmp = File.createTempFile("strat", ".tmp");
        tmp.deleteOnExit();
        String strategyContents = "Test strategy script";
        CopyCharsUtils.copy(strategyContents.toCharArray(),
                tmp.getAbsolutePath());
        CreateStrategyParameters csp = new CreateStrategyParameters(null,
                "mname", "JAVA", tmp, null, false);
        //verify that the input stream yields the correct contents.
        InputStream is = csp.getStrategySource();
        assertEquals(strategyContents, IOUtils.toString(is));
        is.close();
        //verify failure when a non existent file is supplied
        final CreateStrategyParameters csp2 = new CreateStrategyParameters(
                null, "mname", "JAVA", tmp, null, true);
        assertTrue(tmp.delete());
        assertFalse(tmp.exists());
        new ExpectedFailure<FileNotFoundException>(null){
            @Override
            protected void run() throws Exception {
                csp2.getStrategySource();
            }
        };
    }

    /**
     * Verifies the contents of a create strategy parameters instance.
     *
     * @param inParms the instance whose contents need to be verified.
     * @param inInstanceName the instance name
     * @param inStrategyName the strategy name
     * @param inLanguage the language
     * @param inParameters the parameters
     * @param inRouteToServer if the orders should be routed to the server.
     */
    private static void assertCSP(CreateStrategyParameters inParms,
                           String inInstanceName,
                           String inStrategyName,
                           String inLanguage,
                           String inParameters,
                           boolean inRouteToServer) {
        assertEquals(inInstanceName, inParms.getInstanceName());
        assertEquals(inStrategyName, inParms.getStrategyName());
        assertEquals(inLanguage, inParms.getLanguage());
        assertEquals(inParameters, inParms.getParameters());
        assertEquals(inRouteToServer, inParms.isRouteOrdersToServer());
    }
}

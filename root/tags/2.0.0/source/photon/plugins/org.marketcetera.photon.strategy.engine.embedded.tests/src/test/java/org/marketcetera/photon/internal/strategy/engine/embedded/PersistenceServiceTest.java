package org.marketcetera.photon.internal.strategy.engine.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullElementFailure;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.impl.DeployedStrategyImpl;
import org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.MockConnection;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link PersistenceService}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class PersistenceServiceTest extends PhotonTestBase {

    private IPersistenceService mFixture;
    private File mTempFile;
    private StrategyEngine mEngine;

    @Before
    public void before() throws Exception {
        setLevel(PersistenceService.class.getName(), Level.ALL);
        mTempFile = File.createTempFile("PersistenceServiceTest", null);
        mFixture = new PersistenceService(mTempFile);
        mEngine = createEngine("Embedded Engine");
    }

    @After
    public void after() {
        mTempFile.delete();
    }

    @Test
    public void testNullFile() throws Exception {
        new ExpectedNullArgumentFailure("file") {
            @Override
            protected void run() throws Exception {
                new PersistenceService(null);
            }
        };
    }

    @Test
    public void testNullConnection() throws Exception {
        new ExpectedNullArgumentFailure("connection") {
            @Override
            protected void run() throws Exception {
                mFixture.restore(null);
            }
        };
    }

    @Test
    public void testInvalidList() throws Exception {
        new ExpectedNullArgumentFailure("strategies") {
            @Override
            protected void run() throws Exception {
                mFixture.save(null);
            }
        };
        new ExpectedNullElementFailure("strategies") {
            @Override
            protected void run() throws Exception {
                mFixture.save(Collections.<Strategy> singletonList(null));
            }
        };
    }

    @Test
    public void testSaveAndRestore() throws Exception {
        DeployedStrategy strat1 = createDeployedStrategy("strat1");
        strat1.setClassName("class1");
        strat1.setLanguage("language1");
        strat1.setScriptPath("file1");
        strat1.getParameters().put("key1", "value1");
        DeployedStrategy strat2 = createDeployedStrategy("strat2");
        strat2.setClassName("class2");
        strat2.setLanguage("language2");
        strat2.setScriptPath("file2");
        strat2.getParameters().put("key2", "value2");
        mFixture.save(Arrays.asList(strat1, strat2));
        // create a new service to ensure no caching is happening
        mFixture = new PersistenceService(mTempFile);
        mFixture.restore(mEngine.getConnection());
        List<DeployedStrategy> deployed = mEngine.getDeployedStrategies();
        assertThat(deployed.size(), is(2));
        assertStrategy(deployed.get(0), "strat1", "class1", "language1",
                "file1", false, ImmutableMap.of("key1", "value1"));
        assertStrategy(deployed.get(1), "strat2", "class2", "language2",
                "file2", false, ImmutableMap.of("key2", "value2"));

    }

    @Test
    public void testUnexpectedObjectIgnored() throws Exception {
        setLevel(PersistenceService.class.getName(), Level.WARN); // skip debug
        // for this
        // test
        XMLResourceImpl resource = new XMLResourceImpl(URI
                .createFileURI(mTempFile.toString()));
        resource.getContents().add(mEngine);
        resource.save(null);
        mFixture.restore(mEngine.getConnection());
        List<DeployedStrategy> deployed = mEngine.getDeployedStrategies();
        assertThat(deployed.size(), is(0));
        assertSingleEvent(
                Level.WARN,
                PersistenceService.class.getName(),
                "An unexpected object 'class org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineImpl' was encountered in the embedded engine persistence file. It will be ignored.",
                PersistenceService.class.getName());
    }

    @Test
    public void testMissingFileIgnored() throws Exception {
        mTempFile.delete();
        mFixture.restore(mEngine.getConnection());
        List<DeployedStrategy> deployed = mEngine.getDeployedStrategies();
        assertThat(deployed.size(), is(0));
        assertSingleEvent(
                Level.DEBUG,
                PersistenceService.class.getName(),
                "Did not restore persisted state for the embedded engine because the file does not exist.",
                PersistenceService.class.getName());
    }

    @Test
    public void testCorruptFileErrors() throws Exception {
        mTempFile.createNewFile();
        mFixture.restore(mEngine.getConnection());
        List<DeployedStrategy> deployed = mEngine.getDeployedStrategies();
        assertThat(deployed.size(), is(0));
        assertSingleEvent(Level.ERROR, PersistenceService.class.getName(),
                "Failed to restore persisted state for the embedded engine.",
                PersistenceService.class.getName());
    }

    @Test
    public void testDeployFailureErrors() throws Exception {
        setLevel(PersistenceService.class.getName(), Level.WARN);
        DeployedStrategy strat1 = createDeployedStrategy("asdf");
        strat1.setScriptPath("path");
        DeployedStrategy strat2 = createDeployedStrategy("xyz");
        strat2.setScriptPath("path");
        mFixture.save(Arrays.asList(strat1, strat2));
        MockConnection mockConnection = new MockConnection() {
            public DeployedStrategy deploy(Strategy strategy) throws Exception {
                if (strategy.getInstanceName().equals("asdf")) {
                    throw new Exception();
                }
                return super.deploy(strategy);
            };
        };
        mEngine.setConnection(mockConnection);
        mFixture.restore(mockConnection);
        List<DeployedStrategy> deployed = mEngine.getDeployedStrategies();
        assertThat(deployed.size(), is(1));
        assertThat(deployed.get(0).getInstanceName(), is("xyz"));
        assertSingleEvent(
                Level.ERROR,
                PersistenceService.class.getName(),
                "Failed to deploy strategy with instance name 'asdf' when attempting to restore persisted state for the embedded engine.",
                PersistenceService.class.getName());
    }

    @Test
    public void testStrategWithoutScriptIgnored() throws Exception {
        setLevel(PersistenceService.class.getName(), Level.WARN);
        /*
         * Override toString for easy message validation.
         */
        DeployedStrategy strat1 = new DeployedStrategyImpl() {
            @Override
            public String toString() {
                return getInstanceName();
            }
        };
        strat1.setInstanceName("asdf");
        DeployedStrategy strat2 = createDeployedStrategy("xyz");
        strat2.setScriptPath("path");
        mFixture.save(Arrays.asList(strat1, strat2));
        mFixture.restore(mEngine.getConnection());
        List<DeployedStrategy> deployed = mEngine.getDeployedStrategies();
        assertThat(deployed.size(), is(1));
        assertThat(deployed.get(0).getInstanceName(), is("xyz"));
        assertSingleEvent(
                Level.WARN,
                PersistenceService.class.getName(),
                "Did not save strategy because script path is missing and it will not be able to be restored: 'asdf'",
                PersistenceService.class.getName());
    }

    @Test
    public void testUnwritableFile() throws Exception {
        if (!mTempFile.setWritable(false))
            return;
        new ExpectedFailure<IOException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.save(Collections.<Strategy> emptyList());
            }
        };
    }

    @Test
    public void testUnreadableFile() throws Exception {
        if (!mTempFile.setReadable(false))
            return;
        new ExpectedFailure<IOException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.restore(new MockConnection());
            }
        };
    }
}

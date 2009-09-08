package org.marketcetera.photon.internal.strategy.engine.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.MockConnection;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Tests {@link PersistenceService}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
    public void testSaveAndRestore() throws Exception {
        final DeployedStrategy strat1 = createDeployedStrategy("asdf");
        final DeployedStrategy strat2 = createDeployedStrategy("xyz");
        mFixture.save(Arrays.asList(strat1, strat2));
        // create a new service to ensure no caching is happening
        mFixture = new PersistenceService(mTempFile);
        mFixture.restore(mEngine.getConnection());
        List<DeployedStrategy> deployed = mEngine.getDeployedStrategies();
        assertThat(deployed.size(), is(2));
        assertThat(deployed.get(0).getInstanceName(), is("asdf"));
        assertThat(deployed.get(1).getInstanceName(), is("xyz"));
    }

    @Test
    public void testUnexpectedObjectIgnored() throws Exception {
        setLevel(PersistenceService.class.getName(), Level.WARN); // skip debug for this test
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
        assertSingleEvent(
                Level.ERROR,
                PersistenceService.class.getName(),
                "Failed to restore persisted state for the embedded engine.",
                PersistenceService.class.getName());
    }

    @Test
    public void testDeployFailureErrors() throws Exception {
        setLevel(PersistenceService.class.getName(), Level.WARN); // skip debug for this test
        final DeployedStrategy strat1 = createDeployedStrategy("asdf");
        final DeployedStrategy strat2 = createDeployedStrategy("xyz");
        mFixture.save(Arrays.asList(strat1, strat2));
        final MockConnection mockConnection = new MockConnection() {
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
}

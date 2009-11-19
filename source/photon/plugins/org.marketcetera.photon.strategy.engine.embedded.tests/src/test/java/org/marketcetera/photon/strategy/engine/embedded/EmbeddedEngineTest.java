package org.marketcetera.photon.strategy.engine.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.internal.strategy.engine.embedded.IPersistenceService;
import org.marketcetera.photon.internal.strategy.engine.embedded.PersistenceService;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.test.OSGITestUtil;

/* $License$ */

/**
 * Tests {@link EmbeddedEngine}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class EmbeddedEngineTest {

    private File mScript;
    private File mSaveFile;

    @Before
    public void before() throws Exception {
        mScript = File.createTempFile("EmbeddedEngineTestStrategy", ".java");
        mSaveFile = Platform.getStateLocation(
                Platform.getBundle(EmbeddedEngine.PLUGIN_ID)).append(
                "strategies.xml").toFile();
        mSaveFile.delete();
    }

    @After
    public void after() {
        mScript.delete();
        mSaveFile.delete();
    }
    
    @Test
    public void testPluginId() {
        OSGITestUtil.assertBundle(EmbeddedEngine.PLUGIN_ID);
    }

    @Test
    public void testCreateEngine() throws Exception {
        new ExpectedNullArgumentFailure("guiExecutor") {
            @Override
            protected void run() throws Exception {
                EmbeddedEngine.createEngine(null, false);
            }
        };

        IPersistenceService service = new PersistenceService(mSaveFile);
        final DeployedStrategy strat1 = createStrategy(mScript, "strat1");
        final DeployedStrategy strat2 = createStrategy(mScript, "strat2");
        service.save(Arrays.asList(strat1, strat2));

        // first create without restore
        StrategyEngine engine = EmbeddedEngine.createEngine(
                new ImmediateExecutorService(), false);
        assertThat(engine.getDeployedStrategies().size(), is(0));

        // now restore
        engine = EmbeddedEngine.createEngine(new ImmediateExecutorService(),
                true);
        assertThat(engine.getDeployedStrategies().size(), is(2));
        assertThat(engine.getDeployedStrategies().get(0).getInstanceName(),
                is("strat1"));
        assertThat(engine.getDeployedStrategies().get(1).getInstanceName(),
                is("strat2"));

        ModuleManager moduleManager = ModuleSupport.getModuleManager();
        ModuleURN urn = new ModuleURN("metc:strategy:system:strat1");
        moduleManager.getModuleInfo(urn);
        moduleManager.deleteModule(urn);
        urn = new ModuleURN("metc:strategy:system:strat2");
        moduleManager.getModuleInfo(urn);
        moduleManager.deleteModule(urn);
    }

    private DeployedStrategy createStrategy(File script, String instanceName) {
        final DeployedStrategy strat = createDeployedStrategy(instanceName);
        strat.setScriptPath(script.getPath());
        strat.setClassName("EmbeddedEngineTestStrategy");
        strat.setLanguage("RUBY");
        return strat;
    }

}

package org.marketcetera.photon.strategy.engine;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createStrategy;

import java.io.File;
import java.text.MessageFormat;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.file.CopyCharsUtils;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Test {@link AbstractStrategyEngineConnection}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public abstract class AbstractStrategyEngineConnectionTestBase extends
        PhotonTestBase {

    abstract protected AbstractStrategyEngineConnection createFixture();

    private File mTempScript;

    @Before
    public void before() throws Exception {
        mTempScript = File.createTempFile("my_strategy", "rb");
        CopyCharsUtils
                .copy(
                        "include_class \"org.marketcetera.strategy.ruby.Strategy\"\nclass MyStrategy < Strategy\nend"
                                .toCharArray(), mTempScript.getAbsolutePath());
    }

    @After
    public void after() throws Exception {
        mTempScript.delete();
    }

    protected File getTempScript() {
        return mTempScript;
    }

    protected Strategy createStrategyToDeploy() {
        Strategy strategy = createStrategy("strat1");
        strategy.setScriptPath(getTempScript().getPath());
        strategy.setClassName("MyStrategy");
        strategy.setLanguage("RUBY");
        return strategy;
    }

    @Test
    public void testDeployNull() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        new ExpectedNullArgumentFailure("strategy") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(null);
            }
        };
    }

    @Test
    public void testDeployNoScript() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setScriptPath(null);
        new ExpectedFailure<I18NException>(
                "The strategy script path was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployBlankScript() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setScriptPath(" ");
        new ExpectedFailure<I18NException>(
                "The strategy script path was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployNoClassName() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setClassName(null);
        new ExpectedFailure<I18NException>(
                "The strategy class name was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployBlankClassName() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setClassName("");
        new ExpectedFailure<I18NException>(
                "The strategy class name was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployNoLanguage() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setLanguage(null);
        new ExpectedFailure<I18NException>(
                "The strategy language was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployBlankLanguage() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setLanguage("   ");
        new ExpectedFailure<I18NException>(
                "The strategy language was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployNoInstanceName() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setInstanceName(null);
        new ExpectedFailure<I18NException>(
                "The strategy instance name was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployBlankInstanceName() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setInstanceName("");
        new ExpectedFailure<I18NException>(
                "The strategy instance name was not specified.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeploy() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        assertThat(fixture.getEngine().getDeployedStrategies(),
                hasItem(deployed));
        assertDeployedStrategy(deployed, fixture.getEngine(),
                StrategyState.STOPPED, "strat1", "MyStrategy", "RUBY",
                mTempScript.getPath(), false, null);
    }

    @Test
    public void testDeployWithParameters() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategy("strat2");
        strategy.setScriptPath(getTempScript().getPath());
        strategy.setClassName("MyStrategy2");
        strategy.setLanguage("JAVA");
        strategy.setRouteOrdersToServer(true);
        strategy.getParameters().put("abc", "xyz");
        strategy.getParameters().put("123", "abc");
        DeployedStrategy deployed = fixture.deploy(strategy);
        assertDeployedStrategy(deployed, fixture.getEngine(),
                StrategyState.STOPPED, "strat2", "MyStrategy2", "JAVA",
                mTempScript.getPath(), true, ImmutableMap.of("abc", "xyz",
                        "123", "abc"));
    }

    @Test
    public void testDeployWithScriptURL() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        setLevel(fixture.getClass().getName(), Level.DEBUG);
        String url = mTempScript.toURI().toString();
        String actualPath = mTempScript.getAbsolutePath();
        Strategy strategy = createStrategy("strat3");
        strategy.setScriptPath(url);
        strategy.setClassName("Clazz123");
        strategy.setLanguage("JAVA");
        DeployedStrategy deployed = fixture.deploy(strategy);
        assertDeployedStrategy(deployed, fixture.getEngine(),
                StrategyState.STOPPED, "strat3", "Clazz123", "JAVA", url,
                false, null);
        // there should also be a debug message
        assertLastEvent(
                Level.DEBUG,
                fixture.getClass().getName(),
                MessageFormat
                        .format(
                                "Resolved strategy scriptPath ''{0}'' as a URL to file ''{1}''.",
                                url, actualPath),
                AbstractStrategyEngineConnection.class.getName());
    }

    @Test
    public void testUndeploy() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        fixture.undeploy(deployed);
        assertThat(fixture.getEngine().getDeployedStrategies(),
                not(hasItem(deployed)));
    }

    @Test
    public void testUndeployNull() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                fixture.undeploy(null);
            }
        };
    }

    @Test
    public void testStart() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        fixture.start(deployed);
        assertThat(deployed.getState(), is(StrategyState.RUNNING));
    }

    @Test
    public void testStartNull() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                fixture.start(null);
            }
        };
    }

    @Test
    public void testStop() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        fixture.start(deployed);
        fixture.stop(deployed);
        assertThat(deployed.getState(), is(StrategyState.STOPPED));
    }

    @Test
    public void testStopNull() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                fixture.stop(null);
            }
        };
    }

    @Test
    public void testRestart() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        fixture.start(deployed);
        fixture.stop(deployed);
        fixture.start(deployed);
        assertThat(deployed.getState(), is(StrategyState.RUNNING));
    }

    @Test
    public void testUndeployRunningStrategy() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        fixture.start(deployed);
        fixture.undeploy(deployed);
        assertThat(fixture.getEngine().getDeployedStrategies(),
                not(hasItem(deployed)));
    }

    @Test
    public void testUpdate() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        Strategy newConfiguration = createStrategy(null);
        newConfiguration.setRouteOrdersToServer(true);
        newConfiguration.getParameters().put("xyz", "123");
        fixture.update(deployed, newConfiguration);
        assertDeployedStrategy(deployed, fixture.getEngine(),
                StrategyState.STOPPED, "strat1", "MyStrategy", "RUBY",
                mTempScript.getPath(), true, ImmutableMap.of("xyz", "123"));
    }

    @Test
    public void testUpdateNullStrategy() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy newConfiguration = createStrategy(null);
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                fixture.update(null, newConfiguration);
            }
        };
    }

    @Test
    public void testUpdateNullConfiguration() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        final DeployedStrategy deployed = fixture.deploy(strategy);
        new ExpectedNullArgumentFailure("newConfiguration") {
            @Override
            protected void run() throws Exception {
                fixture.update(deployed, null);
            }
        };
    }

    @Test
    public void testRefreshDeployedStrategy() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        Strategy newConfiguration = createStrategy(null);
        newConfiguration.setRouteOrdersToServer(true);
        newConfiguration.getParameters().put("xyz", "123");
        externalUpdateAndStartStrategy(deployed.getUrn(), newConfiguration);
        fixture.refresh(deployed);
        assertDeployedStrategy(deployed, fixture.getEngine(),
                StrategyState.RUNNING, "strat1", "MyStrategy", "RUBY",
                mTempScript.getPath(), true, ImmutableMap.of("xyz", "123"));
    }

    abstract protected void externalUpdateAndStartStrategy(ModuleURN urn,
            Strategy newConfiguration) throws Exception;

    @Test
    public void testRefreshDeployedStrategyThatNoLongerExists()
            throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed = fixture.deploy(strategy);
        externalUndeployStrategy(deployed.getUrn());
        fixture.refresh(deployed);
        assertThat(fixture.getEngine().getDeployedStrategies(),
                not(hasItem(deployed)));
    }

    abstract protected void externalUndeployStrategy(ModuleURN urn)
            throws Exception;

    @Test
    public void testRefreshNullStrategy() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                fixture.refresh(null);
            }
        };
    }

    @Test
    public void testRefresh() throws Exception {
        AbstractStrategyEngineConnection fixture = createFixture();

        /*
         * Deploy a strategy and change it externally.
         */
        Strategy strategy = createStrategyToDeploy();
        DeployedStrategy deployed1 = fixture.deploy(strategy);
        Strategy newConfiguration = createStrategy(null);
        newConfiguration.setRouteOrdersToServer(true);
        newConfiguration.getParameters().put("xyz", "123");
        externalUpdateAndStartStrategy(deployed1.getUrn(), newConfiguration);

        /*
         * Deploy a strategy and undeploy it externally.
         */
        strategy = createStrategyToDeploy();
        strategy.setInstanceName("strat2");
        DeployedStrategy deployed2 = fixture.deploy(strategy);
        externalUndeployStrategy(deployed2.getUrn());

        /*
         * Deploy a strategy externally.
         */
        strategy = createStrategyToDeploy();
        strategy.setInstanceName("strat3");
        externalDeployStrategy(strategy);

        /*
         * Refresh and validate.
         */
        fixture.refresh();
        assertThat(fixture.getEngine().getDeployedStrategies().size(), is(2));
        for (DeployedStrategy deployed : fixture.getEngine()
                .getDeployedStrategies()) {
            if (deployed == deployed1) {
                assertDeployedStrategy(deployed, fixture.getEngine(),
                        StrategyState.RUNNING, "strat1", "MyStrategy", "RUBY",
                        getTempScript().getPath(), true, ImmutableMap.of("xyz",
                                "123"));
            } else {
                assertDeployedStrategy(deployed, fixture.getEngine(),
                        StrategyState.STOPPED, "strat3", "MyStrategy", "RUBY",
                        null, false, null);
            }
        }
    }

    abstract protected void externalDeployStrategy(Strategy strategy)
            throws Exception;

}

package org.marketcetera.strategyagent;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientModuleFactory;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.MockServer;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.*;
import org.marketcetera.strategy.Language;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.file.Deleter;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

import com.google.common.collect.Maps;

/* $License$ */
/**
 * Verifies Remoting capabilities of the strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
public class StrategyAgentRemotingTest
        extends StrategyAgentTestBase
{
    /**
     * Initializes the mock server, its client connection and the strategy
     * agent so that remote receiver is able to authenticate its clients
     * successfully.
     *
     * @throws Exception if there were unexpected failures.
     */
    @BeforeClass
    public static void createServerAndClient()
            throws Exception
    {
        StrategyAgentRemotingConfigTest.setupConfiguration();
        sServer = new MockServer();
        ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                                                DEFAULT_CREDENTIAL.toCharArray(),
                                                MockServer.URL,
                                                Node.DEFAULT_HOST,
                                                Node.DEFAULT_PORT));
        useWs = true;
        createSaWith();
    }
    /**
     * Closes the client connection and shuts down the mock server.
     *
     * @throws Exception if there were errors.
     */
    @AfterClass
    public static void stopServerAndClient()
            throws Exception
    {
        if(ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
        if (sServer != null) {
            sServer.close();
        }
        shutdownSa();
    }
    /**
     * Closes the SA client connection if it's active.
     */
    @After
    public void closeClient()
    {
        if(sSAClient != null) {
            sSAClient.close();
            sSAClient = null;
        }
        for(ModuleURN strategyInstance : moduleManager.getModuleInstances(STRATEGY_PROVIDER_URN)) {
            try {
                moduleManager.stop(strategyInstance);
            } catch (Exception ignored) {}
            try {
                moduleManager.deleteModule(strategyInstance);
            } catch (Exception ignored) {}
        }
    }
    /**
     * Tests failures due to user authentication failure.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void loginFailures()
            throws Exception
    {
        //test null password
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactoryImpl.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL, null,
                                RECEIVER_URL, WS_HOST, WS_PORT)).start();
            }
        };
        //empty password
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactoryImpl.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL, "".toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT)).start();
            }
        };
        //incorrect password
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactoryImpl.getInstance().create(
                        new SAClientParameters(DEFAULT_CREDENTIAL, "what?".toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT)).start();
            }
        };
        //incorrect username
        new ExpectedFailure<ConnectionException>(
                org.marketcetera.saclient.Messages.ERROR_WS_CONNECT){
            @Override
            protected void run() throws Exception {
                SAClientFactoryImpl.getInstance().create(
                        new SAClientParameters("who", DEFAULT_CREDENTIAL.toCharArray(),
                                RECEIVER_URL, WS_HOST, WS_PORT)).start();
            }
        };
        //finally a successful login
        createClient();
    }
    /**
     * Tests {@link StrategyAgent#authenticate(org.marketcetera.util.ws.stateless.StatelessClientContext, String, char[])}
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void clientAuth()
            throws Exception
    {
        final Authenticator authenticator = new DefaultAuthenticator();
        //null context
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                authenticator.shouldAllow(null,"value", "value".toCharArray());
            }
        };
        final StatelessClientContext ctx = new StatelessClientContext();
        assertNull(ctx.getAppId());
        //context without appID
        assertTrue(authenticator.shouldAllow(ctx,
                                             DEFAULT_CREDENTIAL, 
                                             DEFAULT_CREDENTIAL.toCharArray()));
        //context with invalid appID
        ctx.setAppId(Util.getAppId("invalid",
                                   ApplicationVersion.getVersion().getVersionInfo()));
        assertTrue(authenticator.shouldAllow(ctx,
                                             DEFAULT_CREDENTIAL, 
                                             DEFAULT_CREDENTIAL.toCharArray()));
        //context with correct name & version number
        ctx.setAppId(Util.getAppId(SAClientVersion.APP_ID_NAME, ApplicationVersion.getVersion().getVersionInfo()));
        assertTrue(authenticator.shouldAllow(ctx, DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray()));
        //valid contexts
        //invalid user/password
        assertFalse(authenticator.shouldAllow(ctx,"go","go".toCharArray()));
        //invalid password
        assertFalse(authenticator.shouldAllow(ctx,DEFAULT_CREDENTIAL,"go".toCharArray()));
        //null password
        assertFalse(authenticator.shouldAllow(ctx,DEFAULT_CREDENTIAL,null));
        //invalid user
        assertFalse(authenticator.shouldAllow(ctx,"go",DEFAULT_CREDENTIAL.toCharArray()));
        //null user
        assertFalse(authenticator.shouldAllow(ctx,null,DEFAULT_CREDENTIAL.toCharArray()));
    }

    @Test
    public void getInstances() throws Exception {
        final SAClient saClient = createClient();
        List<ModuleURN> urns = saClient.getInstances(null);
        assertFalse(urns.toString(), urns.isEmpty());
        //verify it contains the receiver and the client instances
        assertTrue(urns.toString(), urns.contains(ClientModuleFactory.INSTANCE_URN));
        assertTrue(urns.toString(), urns.contains(RECEIVER_URN));
        //try sending a URN of a provider that doesn't exist
        urns = saClient.getInstances(new ModuleURN("metc:not:exist"));
        assertTrue(urns.toString(), urns.isEmpty());
        //try sending a URN of a provider that has some instances
        urns = saClient.getInstances(RECEIVER_URN.parent());
        assertFalse(urns.toString(), urns.isEmpty());
        assertEquals(urns.toString(), 1, urns.size());
        assertTrue(urns.toString(), urns.contains(RECEIVER_URN));
    }
    
    @Test
    public void getProviders() throws Exception {
        SAClient saClient = createClient();
        List<ModuleURN> urns = saClient.getProviders();
        assertFalse(urns.toString(), urns.isEmpty());
        assertTrue(urns.toString(), urns.contains(ClientModuleFactory.INSTANCE_URN.parent()));
        assertTrue(urns.toString(), urns.contains(RECEIVER_URN.parent()));
    }

    @Test
    public void getModuleInfo() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getModuleInfo(null);
            }
        });
        //non-existent module's URN
        final String urn = "metc:instance:not:exist";
        ConnectionException failure = new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getModuleInfo(new ModuleURN(urn));
            }
        }.getException();
        assertEquals(failure.getCause().getLocalizedMessage(),
                org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn));
        //valid URN
        ModuleTestBase.assertModuleInfo(saClient.getModuleInfo(RECEIVER_URN),
                RECEIVER_URN, ModuleState.STARTED,  null, null,
                false, true, true, false, false);
    }

    @Test
    public void getPropertiesFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getProperties(null);
            }
        });
        //non-existent module
        final String urn = "metc:instance:not:exist";
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.getProperties(new ModuleURN(urn));
            }
        }.getException();
        assertThat(failure.getCause(), Matchers.instanceOf(InstanceNotFoundException.class));
        assertEquals(failure.getCause().getMessage(), new ModuleURN(urn).toObjectName().toString());
    }

    @Test
    public void setPropertiesFailure()
            throws Exception
    {
        final SAClient saClient = createClient();
        // null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run()
                    throws Exception
            {
                saClient.setProperties(null,
                                       null);
            }
        });
        // non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(Messages.SET_PROPERTY_MODULE_NOT_STRATEGY,
                                                   RECEIVER_URN),
                            new WSOpFailure() {
            @Override
            protected void run()
                    throws Exception
            {
                saClient.setProperties(RECEIVER_URN,
                                       null);
            }
        });
        // non existent strategy
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN,
                                            "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                //Supply a non-null property value
                Map<String, Object> propMap = new HashMap<String, Object>();
                saClient.setProperties(urn, propMap);
            }
        }.getException();
        assertThat(failure.getCause(), Matchers.instanceOf(InstanceNotFoundException.class));
        assertEquals(failure.getCause().getMessage(), urn.toObjectName().toString());
        //non editable properties
        final String key = "OutputDestination";
        verifyNestedFailure(new I18NBoundMessage3P(
                Messages.UNEDITABLE_STRATEGY_PROPERTY, key, urn,
                SAServiceImpl.EDITABLE_STRATEGY_PROPERTIES.toString()),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        Map<String, Object> propMap = new HashMap<String, Object>();
                        propMap.put(key, "value");
                        saClient.setProperties(urn, propMap);
                    }
                });
    }

    /**
     * Tests failures when creating strategy.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void createStrategyFailure() throws Exception {
        final SAClient saClient = createClient();
        //null parameter failure
        verifyNestedFailure(Messages.NO_STRATEGY_CREATE_PARMS_SPECIFIED, new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.createStrategy(null);
            }
        });
        //Create a temp file
        final File scriptFile = File.createTempFile("script",".tmp");
        final String name = "Strat";
        //Test failure from specifying an incorrect language.
        ConnectionException failure = new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.createStrategy(new CreateStrategyParameters(name,
                        "HelloWorld", "UBY", scriptFile, null, false));
            }
        }.getException();
        //verify that we failed to convert the language.
        ExpectedFailure.assertI18NException(failure.getCause(),
                org.marketcetera.strategy.Messages.INVALID_LANGUAGE_ERROR, "UBY");
        //Test for not-existing files
        CreateStrategyParameters parameters = new CreateStrategyParameters(name,
                "HelloWorld", "RUBY", scriptFile, null, false);
        //now delete the script file
        Deleter.apply(scriptFile);
        //Create strategy succeeds
        final ModuleURN urn = saClient.createStrategy(parameters);
        assertEquals(name, urn.instanceName());
        //But we get an error when we try to start it because the strategy file is empty
        failure = new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.start(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.strategy.Messages.FAILED_TO_START.getText()));
    }

    /**
     * Tests failures when fetching strategy create parameters.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void getStrategyCreateParmsFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getStrategyCreateParms(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                            Messages.NO_CREATE_PARAMETERS_FOR_STRATEGY,
                RECEIVER_URN), new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.getStrategyCreateParms(RECEIVER_URN);
            }
        });
        //non existent strategy
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        verifyNestedFailure(new I18NBoundMessage1P(
                            Messages.NO_CREATE_PARAMETERS_FOR_STRATEGY, urn),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.getStrategyCreateParms(urn);
                    }
                });
    }
    
    @Test
    public void startFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.start(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                Messages.START_MODULE_NOT_STRATEGY, RECEIVER_URN),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.start(RECEIVER_URN);
                    }
                });
        //non strategy module
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.start(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn)));
    }

    @Test
    public void stopFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.stop(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                Messages.STOP_MODULE_NOT_STRATEGY, RECEIVER_URN),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.stop(RECEIVER_URN);
                    }
                });
        //non strategy module
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.stop(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn)));
    }

    @Test
    public void deleteFailure() throws Exception {
        final SAClient saClient = createClient();
        //null URN
        verifyNullURNFailure(new WSOpFailure() {
            @Override
            protected void run() throws Exception {
                saClient.delete(null);
            }
        });
        //non strategy URN
        verifyNestedFailure(new I18NBoundMessage1P(
                Messages.DELETE_MODULE_NOT_STRATEGY, RECEIVER_URN),
                new WSOpFailure() {
                    @Override
                    protected void run() throws Exception {
                        saClient.delete(RECEIVER_URN);
                    }
                });
        //non strategy module
        final ModuleURN urn = new ModuleURN(STRATEGY_PROVIDER_URN, "notexist");
        ConnectionException failure = new WSOpFailure(){
            @Override
            protected void run() throws Exception {
                saClient.delete(urn);
            }
        }.getException();
        assertThat(failure.getCause().getLocalizedMessage(),
                Matchers.containsString(org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(urn)));
    }

    /**
     * Tests strategy lifecycle and various methods that can be successfully
     * invoked after the strategy has been created.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void strategyLifecycle()
            throws Exception
    {
        final SAClient saClient = createClient();
        // create, start, stop, delete, get/set props, get createParms
        // verify no instances exist yet
        List<ModuleURN> instances = saClient.getInstances(STRATEGY_PROVIDER_URN);
        assertTrue(instances.toString(),
                   instances.isEmpty());
        // create a strategy
        assertTrue(TEST_STRATEGY.getAbsolutePath(),
                   TEST_STRATEGY.isFile());
        String name = "myStrat";
        ModuleURN urn = saClient.createStrategy(new CreateStrategyParameters(name,
                                                                             "HelloWorld",
                                                                             "RUBY",
                                                                             TEST_STRATEGY,
                                                                             null,
                                                                             false));
        assertEquals(name,
                     urn.instanceName());
        assertEquals(STRATEGY_PROVIDER_URN,
                     urn.parent());
        // verify the instance is reported
        instances = saClient.getInstances(STRATEGY_PROVIDER_URN);
        assertEquals(instances.toString(),
                     1,
                     instances.size());
        assertEquals(urn,
                     instances.get(0));
        // verify strategy properties and state
        ModuleTestBase.assertModuleInfo(saClient.getModuleInfo(urn),
                                        urn,
                                        ModuleState.CREATED,
                                        null,
                                        null,
                                        false,
                                        false,
                                        true,
                                        true,
                                        true);
        Map<String,String> props = toStringProps(saClient.getProperties(urn));
        assertNotNull(props);
        assertFalse(props.isEmpty());
        assertEquals(props.toString(),
                     5,
                     props.size());
        assertThat(props,
                   allOf(hasEntry("Parameters",
                                  null),
                         hasEntry("Name",
                                  "HelloWorld"),
                         hasEntry("Language",
                                  Language.RUBY.toString()),
                         hasEntry(STRAT_PROP_ROUTING_ORDERS,
                                  String.valueOf(false)),
                         hasEntry("OutputDestination",
                                  RECEIVER_URN.parent().getValue())));
        // start the strategy
        saClient.start(urn);
        // verify properties and state
        assertEquals(ModuleState.STARTED,
                     saClient.getModuleInfo(urn).getState());
        props = toStringProps(saClient.getProperties(urn));
        assertNotNull(props);
        assertEquals(props.toString(),
                     6,
                     props.size());
        assertThat(props,
                   allOf(hasEntry("Parameters",
                                  null),
                         hasEntry("Name",
                                  "HelloWorld"),
                         hasEntry("Language",
                                  Language.RUBY.toString()),
                         hasEntry(STRAT_PROP_ROUTING_ORDERS,
                                  String.valueOf(false)),
                         hasEntry("Status",
                                  "RUNNING"),
                         hasEntry("OutputDestination",
                                  RECEIVER_URN.parent().getValue())));
        // stop the strategy
        saClient.stop(urn);
        // verify properties and state
        assertEquals(ModuleState.STOPPED,
                     saClient.getModuleInfo(urn).getState());
        // change properties
        props = Maps.newHashMap();
        String paramValue = "key1=value1";
        props.put("Parameters",
                  paramValue);
        Map<String,Object> actualProps = toObjectProps(props);
        actualProps.put(STRAT_PROP_ROUTING_ORDERS,
                        true);
        props = toStringProps(saClient.setProperties(urn,
                                                     actualProps));
        assertNotNull(props);
        assertEquals(props.toString(),
                     2,
                     props.size());
        assertThat(props,
                   allOf(hasEntry("Parameters",
                                  String.valueOf(paramValue)),
                         hasEntry(STRAT_PROP_ROUTING_ORDERS,
                                  String.valueOf(true))));
        // verify that the property indeed changed by fetching them again
        props = toStringProps(saClient.getProperties(urn));
        assertNotNull(props);
        assertEquals(props.toString(),
                     6,
                     props.size());
        assertThat(props,
                   allOf(hasEntry("Parameters",
                                  String.valueOf(paramValue)),
                         hasEntry("Name",
                                  "HelloWorld"),
                         hasEntry("Language",
                                  Language.RUBY.toString()),
                         hasEntry(STRAT_PROP_ROUTING_ORDERS,
                                  String.valueOf(true)),
                         hasEntry("Status",
                                  "STOPPED"),
                         hasEntry("OutputDestination",
                                  RECEIVER_URN.parent().getValue())));
        props.clear();
        actualProps.clear();
        actualProps.put(STRAT_PROP_ROUTING_ORDERS,
                        BigDecimal.ONE);
        actualProps = saClient.setProperties(urn,
                                             actualProps);
        assertNotNull(actualProps);
        assertEquals(1,
                     actualProps.size());
        assertThat(actualProps,
                   Matchers.hasKey(STRAT_PROP_ROUTING_ORDERS));
        Object err = actualProps.get(STRAT_PROP_ROUTING_ORDERS);
        assertThat(err,
                   Matchers.instanceOf(RemoteProperties.class));
        RemoteProperties prop = (RemoteProperties)err;
        // verify the we had the expected failure. 
        assertThat(prop.getServerString(),
                Matchers.containsString(InvalidAttributeValueException.class.getName()));
        // delete strategy
        saClient.delete(urn);
        // verify it's not reported any more
        instances = saClient.getInstances(STRATEGY_PROVIDER_URN);
        assertTrue(instances.toString(),
                   instances.isEmpty());
    }
    /**
     * Transforms the given map to a map with object values.
     *
     * @param inProperties a <code>Map&lt;String,String&gt;</code> value
     * @return a <code>Map&lt;String,Object&gt;</code> value
     */
    private Map<String,Object> toObjectProps(Map<String,String> inProperties)
    {
        if(inProperties == null) {
            return null;
        }
        Map<String,Object> output = Maps.newHashMap();
        for(Map.Entry<String,String> entry : inProperties.entrySet()) {
            output.put(entry.getKey(),entry.getValue());
        }
        return output;
    }
    /**
     * Transforms the given map to a map with string values.
     *
     * @param inProperties a <code>Map&lt;String,Object&gt;</code> value
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    private Map<String,String> toStringProps(Map<String,Object> inProperties)
    {
        if(inProperties == null) {
            return null;
        }
        Map<String,String> output = Maps.newHashMap();
        for(Map.Entry<String,Object> entry : inProperties.entrySet()) {
            output.put(entry.getKey(),entry.getValue() == null ? null : String.valueOf(entry.getValue()));
        }
        return output;
    }
    private void verifyNullURNFailure(final WSOpFailure inTest) throws Exception {
        verifyNestedFailure(Messages.CANNOT_PROCESS_NULL_URN, inTest); }

    private void verifyNestedFailure(I18NBoundMessage inMessage,
                                     WSOpFailure inTest)
            throws Exception {
        assertEquals(inMessage, ((I18NException) inTest.getException().
                getCause()).getI18NBoundMessage());

    }

    private static SAClient createClient()
            throws ConnectionException
    {
        sSAClient = SAClientFactoryImpl.getInstance().create(new SAClientParameters(DEFAULT_CREDENTIAL,
                                                                                    DEFAULT_CREDENTIAL.toCharArray(),
                                                                                    RECEIVER_URL,
                                                                                    WS_HOST,
                                                                                    WS_PORT));
        sSAClient.start();
        return sSAClient;
    }

    /**
     * A class to test WS operation failure
     */
    private static abstract class WSOpFailure
            extends ExpectedFailure<ConnectionException> {
        protected WSOpFailure() throws Exception {
            super(org.marketcetera.saclient.Messages.ERROR_WS_OPERATION);
        }
    }

    private static MockServer sServer;
    private static final String DEFAULT_CREDENTIAL = "DrNo";
    private static final String WS_HOST = "localhost";
    private static final int WS_PORT = 9001;
    private static final int JMS_PORT = 61617;
    private static final String RECEIVER_URL = "tcp://" + WS_HOST + ":" + JMS_PORT;
    private static final File TEST_STRATEGY = new File("src/test/sample_data/test_strategy.rb");
    private static volatile SAClient sSAClient;
    private static final ModuleURN RECEIVER_URN = new ModuleURN("metc:remote:receiver:single");
    private static final ModuleURN STRATEGY_PROVIDER_URN = new ModuleURN("metc:strategy:system");
    private static final String STRAT_PROP_ROUTING_ORDERS = "RoutingOrdersToORS";
}

package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.module.*;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import org.apache.commons.io.IOUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/* $License$ */
/**
 * Tests {@link SAClient} web services.
 * <p/>
 * For each web service, verifies that the parameters to each web service
 * are correctly received and that the return values are correctly received.
 * And if the web service failed that the exception is correctly received.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SAClientWSTest extends SAClientTestBase {

    @Test
    public void getProviders() throws Exception {
        //Test a non-empty and an empty list.
        List<List<ModuleURN>> lists = Arrays.asList(Arrays.asList(
                new ModuleURN("test:prov:A"), new ModuleURN("test:prov:B")),
                new ArrayList<ModuleURN>());
        for (List<ModuleURN> list : lists) {
            final List<ModuleURN> urnList = list;
            testAPI(new WSTester<List<ModuleURN>>() {
                @Override
                protected List<ModuleURN> invokeApi(boolean isNullParams) throws Exception {
                    return getClient().getProviders();
                }

                @Override
                protected List<ModuleURN> setReturnValue(boolean isNullParams) {
                    List<ModuleURN> moduleURNs = isNullParams ? null : urnList;
                    getMockSAService().setURNList(moduleURNs);
                    //nulls are returned as empty lists.
                    return moduleURNs == null ? new ArrayList<ModuleURN>() : moduleURNs;
                }
            });
            resetServiceParameters();
        }
    }

    @Test
    public void getInstances() throws Exception {
        //Test a non-empty and an empty list.
        List<List<ModuleURN>> lists = Arrays.asList(Arrays.asList(
                new ModuleURN("test:prov:me:A"), new ModuleURN("test:prov:me:B")),
                new ArrayList<ModuleURN>());

        final ModuleURN input = new ModuleURN("test:prov:me");
        for (List<ModuleURN> list : lists) {
            final List<ModuleURN> urnList = list;
            testAPI(new WSTester<List<ModuleURN>>() {
                @Override
                protected List<ModuleURN> invokeApi(boolean isNullParams) throws Exception {
                    return getClient().getInstances(isNullParams ? null : input);
                }

                @Override
                protected List<ModuleURN> setReturnValue(boolean isNullParams) {
                    List<ModuleURN> moduleURNs = isNullParams ? null : urnList;
                    getMockSAService().setURNList(moduleURNs);
                    //nulls are returned as empty lists.
                    return moduleURNs == null ? new ArrayList<ModuleURN>() : moduleURNs;
                }

                @Override
                protected void verifyInputParams(boolean isNullParams) {
                    assertEquals(isNullParams ? null : input, getMockSAService().getURN());
                }
            });
            resetServiceParameters();
        }
    }

    @Test
    public void getModuleInfo() throws Exception {
        final ModuleURN input = new ModuleURN("test:prov:me:A");
        final ModuleInfo output = new ModuleInfo(input, ModuleState.STARTED,
                null, null, new Date(), new Date(), null, false, false, false,
                false, false, null, null, 0, false, 0);
        testAPI(new WSTester<ModuleInfo>() {
            @Override
            protected ModuleInfo invokeApi(boolean isNullParams) throws Exception {
                return getClient().getModuleInfo(isNullParams ? null : input);
            }

            @Override
            protected ModuleInfo setReturnValue(boolean isNullParams) {
                ModuleInfo value = isNullParams ? null : output;
                getMockSAService().setModuleInfo(value);
                return value;
            }

            @Override
            protected void verifyInputParams(boolean isNullParams) {
                assertEquals(isNullParams ? null : input, getMockSAService().getURN());
            }
        });
    }

    @Test
    public void createStrategy() throws Exception {
        File f = File.createTempFile("strat", ".tst");
        f.deleteOnExit();
        CopyCharsUtils.copy("Test Strategy Contents".toCharArray(), f.getAbsolutePath());
        final CreateStrategyParameters input = new CreateStrategyParameters(
                "instance", "strategy", "java", f, "key=value", false);
        final ModuleURN output = new ModuleURN("test:prov:me:A");
        testAPI(new WSTester<ModuleURN>() {
            @Override
            protected ModuleURN invokeApi(boolean isNullParams) throws Exception {
                return getClient().createStrategy(isNullParams ? null : input);
            }

            @Override
            protected ModuleURN setReturnValue(boolean isNullParams) {
                ModuleURN value = isNullParams ? null : output;
                getMockSAService().setURN(value);
                return value;
            }

            @Override
            protected void verifyInputParams(boolean isNullParams) throws Exception {
                verifyEquals(isNullParams ? null : input,
                        getMockSAService().getCreateStrategyParameters());
            }
        });
        //Test non-existent file behavior
        resetServiceParameters();
        //Create the parameter and then delete the file.
        CreateStrategyParameters parms = new CreateStrategyParameters(
                "instance", "strategy", "java", f, "key=value", false);
        assertTrue(f.delete());
        assertFalse(f.exists());
        getClient().createStrategy(parms);
        InputStream file = getMockSAService().getCreateStrategyParameters().getStrategySource();
        assertNotNull(file);
        //If the file doesn't exist at the time it's being sent, it comes out
        //as empty at the other end.
        assertEquals(0, IOUtils.toByteArray(file).length);
    }

    @Test
    public void getStrategyCreateParameters() throws Exception {
        final ModuleURN input = new ModuleURN("test:prov:me:A");
        File f = File.createTempFile("strat", ".tst");
        f.deleteOnExit();
        CopyCharsUtils.copy("Test Strategy Contents".toCharArray(), f.getAbsolutePath());
        final CreateStrategyParameters output = new CreateStrategyParameters(
                "instance", "strategy", "java", f, "key=value", false);
        testAPI(new WSTester<CreateStrategyParameters>() {
            @Override
            protected CreateStrategyParameters invokeApi(boolean isNullParams) throws Exception {
                return getClient().getStrategyCreateParms(isNullParams ? null : input);
            }

            @Override
            protected CreateStrategyParameters setReturnValue(boolean isNullParams) {
                CreateStrategyParameters value = isNullParams ? null : output;
                getMockSAService().setCreateStrategyParameters(value);
                return value;
            }
        });
        //Test non-existent file behavior
        resetServiceParameters();
        //Create the parameter and then delete the file.
        getMockSAService().setCreateStrategyParameters(
                new CreateStrategyParameters("instance", "strategy", "java",
                        f, "key=value", false));
        assertTrue(f.delete());
        assertFalse(f.exists());
        InputStream file = getClient().getStrategyCreateParms(null).getStrategySource();
        assertNotNull(file);
        //If the file doesn't exist at the time it's being sent, it comes out
        //as empty at the other end.
        assertEquals(0, IOUtils.toByteArray(file).length);
    }

    @Test
    public void start() throws Exception {
        final ModuleURN input = new ModuleURN("test:prov:me:A");
        testAPI(new WSTester<Void>() {
            @Override
            protected Void invokeApi(boolean isNullParams) throws Exception {
                getClient().start(isNullParams ? null : input);
                return null;
            }

            @Override
            protected void verifyInputParams(boolean isNullParams) {
                assertEquals(isNullParams ? null : input, getMockSAService().getURN());
            }
        });
    }

    @Test
    public void stop() throws Exception {
        final ModuleURN input = new ModuleURN("test:prov:me:A");
        testAPI(new WSTester<Void>() {
            @Override
            protected Void invokeApi(boolean isNullParams) throws Exception {
                getClient().stop(isNullParams ? null : input);
                return null;
            }

            @Override
            protected void verifyInputParams(boolean isNullParams) {
                assertEquals(isNullParams ? null : input, getMockSAService().getURN());
            }
        });
    }

    @Test
    public void delete() throws Exception {
        final ModuleURN input = new ModuleURN("test:prov:me:A");
        testAPI(new WSTester<Void>() {
            @Override
            protected Void invokeApi(boolean isNullParams) throws Exception {
                getClient().delete(isNullParams ? null : input);
                return null;
            }

            @Override
            protected void verifyInputParams(boolean isNullParams) {
                assertEquals(isNullParams ? null : input, getMockSAService().getURN());
            }
        });

    }

    @Test
    public void getProperties() throws Exception {
        final ModuleURN input = new ModuleURN("test:prov:me:A");
        final Map<String, Object> m = new HashMap<String, Object>();
        m.put("first", BigDecimal.TEN);
        m.put("second", "next");
        m.put("third", 909);
        //Test a non-empty and an empty map.
        List<Map<String, Object>> maps = Arrays.asList(m,
                new HashMap<String, Object>());
        for (Map<String, Object> map : maps) {
            final Map<String, Object> output = map;
            testAPI(new WSTester<Map<String, Object>>() {
                @Override
                protected Map<String, Object> invokeApi(boolean isNullParams) throws Exception {
                    return getClient().getProperties(isNullParams ? null : input);
                }

                @Override
                protected Map<String, Object> setReturnValue(boolean isNullParams) {
                    getMockSAService().setPropertiesOut(isNullParams ? null :
                            new MapWrapper<String, Object>(output));
                    return isNullParams ? null : output;
                }

                @Override
                protected void verifyInputParams(boolean isNullParams) throws Exception {
                    verifyEquals(isNullParams ? null : input, getMockSAService().getURN());
                }
            });
            resetServiceParameters();
        }
    }

    @Test
    public void setProperties() throws Exception {
        final ModuleURN input1 = new ModuleURN("test:prov:me:A");
        final Map<String, Object> i2 = new HashMap<String, Object>();
        i2.put("first", BigDecimal.ONE);
        i2.put("second", "mnext");
        i2.put("third", 999);
        //Test a non-empty and an empty map.
        List<Map<String, Object>> inputs = Arrays.asList(i2, new HashMap<String, Object>());
        final Map<String, Object> out = new HashMap<String, Object>();
        out.put("first", BigDecimal.TEN);
        out.put("second", "next");
        out.put("third", 909);
        //Test a non-empty and an empty map.
        List<Map<String, Object>> outputs = Arrays.asList(out, new HashMap<String, Object>());
        for (int i = 0; i < inputs.size(); i++) {
            final Map<String, Object> input2 = inputs.get(i);
            final Map<String, Object> output = outputs.get(i);
            testAPI(new WSTester<Map<String, Object>>() {
                @Override
                protected Map<String, Object> invokeApi(boolean isNullParams) throws Exception {
                    return getClient().setProperties(isNullParams ? null : input1, isNullParams ? null : input2);
                }

                @Override
                protected Map<String, Object> setReturnValue(boolean isNullParams) {
                    getMockSAService().setPropertiesOut(isNullParams ? null : new MapWrapper<String, Object>(output));
                    return isNullParams ? null : output;
                }

                @Override
                protected void verifyInputParams(boolean isNullParams) throws Exception {
                    assertEquals(isNullParams ? null : input1, getMockSAService().getURN());
                    MapWrapper<String, Object> mapWrapper = getMockSAService().getPropertiesIn();
                    verifyEquals(isNullParams ? null : input2, mapWrapper == null ? null : mapWrapper.getMap());
                }
            });
            resetServiceParameters();
        }
    }

    @After
    public void reset() {
        resetServiceParameters();
    }

    /**
     * Tests the API using the supplied tester instance.
     * First the API is tested with non-null parameters, then with null
     * parameters and finally the failure of the API is tested.
     *
     * @param inTester the tester to test the API invocation.
     * @param <R>      the return type of the API.
     * @throws Exception if there were unexpected errors
     */
    private static <R> void testAPI(final WSTester<R> inTester) throws Exception {
        //Test a regular invocation.
        R value = inTester.setReturnValue(false);
        verifyEquals(value, inTester.invokeApi(false));
        inTester.verifyInputParams(false);

        //Test invocation with nulls
        resetServiceParameters();
        value = inTester.setReturnValue(true);
        verifyEquals(value, inTester.invokeApi(true));
        inTester.verifyInputParams(true);

        //Test a failure
        resetServiceParameters();
        I18NException failure = new I18NException(
                new I18NMessage0P(Messages.LOGGER, "test"));
        getMockSAService().setFailure(failure);
        inTester.setReturnValue(false);
        ConnectionException e = new ExpectedFailure<ConnectionException>() {
            @Override
            protected void run() throws Exception {
                inTester.invokeApi(false);
            }
        }.getException();
        assertNotNull(e.getCause());
        assertEquals(failure, e.getCause());
        //Verify that input parameters were received even when failure occured.
        inTester.verifyInputParams(false);

        //Test service interruption
        verifyInvocationCannotBeInterrupted(inTester);
    }

    private static <R> void verifyInvocationCannotBeInterrupted(final WSTester<R> inTester)
            throws Exception {
        resetServiceParameters();
        getMockSAService().setSleep(true);
        inTester.setReturnValue(false);
        final Semaphore sema = new Semaphore(0);
        final AtomicReference<Exception> interruptFailure = new AtomicReference<Exception>();
        Thread t = new Thread(){
            @Override
            public void run() {
                sema.release();
                try {
                    inTester.invokeApi(false);
                } catch (Exception ex) {
                    interruptFailure.set(ex);
                }
            }
        };
        t.start();
        //Wait for the thread to be started
        sema.acquire();
        //Interrupt it as soon as it is found started
        t.interrupt();
        //wait for it to end
        t.join();
        //verify that we are not able to interrupt it
        assertNull("API invocation got interrupted!", interruptFailure.get());
    }

    /**
     * Verifies the equality of the two objects. Performs special handling
     * of certain types.
     *
     * @param inExpected expected object.
     * @param inActual   actual object.
     * @throws IOException if there were errors.
     */
    private static void verifyEquals(Object inExpected, Object inActual) throws IOException {
        if (inExpected == null) {
            assertNull(inActual);
        } else {
            assertNotNull("Expected:" + inExpected, inActual);
            //special handling for certain types
            if (inExpected instanceof ModuleInfo) {
                ModuleInfo e = (ModuleInfo) inExpected;
                ModuleInfo a = (ModuleInfo) inActual;
                ModuleTestBase.assertModuleInfo(a,
                        e.getURN(), e.getState(), e.getInitiatedDataFlows(),
                        e.getParticipatingDataFlows(), e.isAutocreated(),
                        e.isAutostart(), e.isReceiver(), e.isEmitter(),
                        e.isFlowRequester());
                assertEquals(e.getLastStartFailure(), a.getLastStartFailure());
                assertEquals(e.getLastStopFailure(), a.getLastStopFailure());
                assertEquals(e.getLockQueueLength(), a.getLockQueueLength());
                assertEquals(e.getReadLockCount(), a.getReadLockCount());
                assertEquals(e.isWriteLocked(), a.isWriteLocked());
            } else if (inExpected instanceof Map) {
                //Convert both maps to the same type
                Map e = new HashMap<Object,Object>((Map<?,?>) inExpected);
                Map a = new HashMap<Object,Object>((Map<?,?>) inActual);
                assertEquals(e, a);
            } else if (inExpected instanceof CreateStrategyParameters) {
                CreateStrategyParameters e = (CreateStrategyParameters) inExpected;
                CreateStrategyParameters a = (CreateStrategyParameters) inActual;
                assertEquals(e.getInstanceName(), a.getInstanceName());
                assertEquals(e.getStrategyName(), a.getStrategyName());
                assertEquals(e.getLanguage(), a.getLanguage());
                InputStream ein = e.getStrategySource();
                InputStream ain = a.getStrategySource();
                assertArrayEquals(IOUtils.toByteArray(ein),
                        IOUtils.toByteArray(ain));
                ein.close();
                ain.close();
            } else {
                assertEquals(inExpected, inActual);
            }
        }
    }

    /**
     * A class to aid testing the behavior of all SA client web services.
     *
     * @param <R> the return type of the web service being invoked.
     */
    private abstract static class WSTester<R> {
        /**
         * Sets the return value for the API on {@link MockSAServiceImpl}.
         *
         * @param isNullParams if testing mode is testing null
         *                     parameters/return values.
         * @return the expected return value of the API call.
         */
        protected R setReturnValue(boolean isNullParams) {
            return null;
        }

        /**
         * Invoke the WS API being tested.
         *
         * @param isNullParams if the testing mode is testing null
         *                     parameters/return values.
         * @return the actual value returned by the API invocation.
         * @throws Exception if there were errors.
         */
        protected abstract R invokeApi(boolean isNullParams) throws Exception;

        /**
         * Verify the input parameter values received by {@link MockSAServiceImpl}
         * after the API invocation went through.
         *
         * @param isNullParams if the testing mode is testing null
         *                     parameters/return values.
         * @throws Exception if there were errors.
         */
        protected void verifyInputParams(boolean isNullParams) throws Exception {
        }
    }
}

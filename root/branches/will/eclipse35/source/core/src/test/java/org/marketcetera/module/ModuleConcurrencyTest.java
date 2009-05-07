package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/* $License$ */
/**
 * Tests operation of various locks used within the ModuleManager.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public class ModuleConcurrencyTest extends ModuleTestBase {
    /**
     * Tests locks acquisition when creating module instances
     * from singleton factories.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void singletonFactoryLocking() throws Exception {
        //set up the factory
        ConcurrentTestFactory.setSingleton(true);
        ReentrantLock lock = new ReentrantLock();
        ConcurrentTestFactory.setNextCreateLock(lock);
        //initialize the module manager
        initManager();
        //acquire the lock to cause the module instance creation to block
        lock.lock();
        //Verify provider state
        ProviderInfo info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertProviderInfo(info, ConcurrentTestFactory.PROVIDER_URN,
                new String[]{ModuleURN.class.getName()},
                new Class[]{ModuleURN.class}, new I18NMessage0P(
                Messages.LOGGER, "provider").getText(), false, false);
        assertFalse(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
        //Spawn a thread to create the instance.
        final ModuleURN urn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN, "service");
        Future<ModuleURN> result1 = sService.submit(new Callable<ModuleURN>() {
            public ModuleURN call() throws Exception {
                return getManager().createModule(ConcurrentTestFactory.PROVIDER_URN, urn);
            }
        });
        //Wait until it gets stuck acquiring the lock
        while(lock.getQueueLength() == 0) {
            Thread.sleep(500);
        }
        //Verify provider state
        info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertTrue(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
        //Clear the factory lock
        ConcurrentTestFactory.setNextCreateLock(null);
        //Spawn another thread to create the instance
        final Future<ModuleURN> result2 = sService.submit(new Callable<ModuleURN>() {
            public ModuleURN call() throws Exception {
                return getManager().createModule(ConcurrentTestFactory.PROVIDER_URN, urn);
            }
        });
        //Wait for this thread to get stuck acquiring the factory lock
        while(info.getLockQueueLength() < 1) {
            Thread.sleep(500);
            info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        }
        //Verify that modules from other providers can be instantiated
        //while this one is locked
        canPerformModuleOperationsDifferentProvider();
        //release the lock so that the first task gets through.
        lock.unlock();
        //wait for the first task to complete
        assertEquals(urn,result1.get());
        //verify that the second task fails
        ExecutionException failure = new ExpectedFailure<ExecutionException>(null) {
            protected void run() throws Exception {
                result2.get();
            }
        }.getException();
        assertTrue(failure.getCause() instanceof ModuleCreationException);
        assertEquals(Messages.CANNOT_CREATE_SINGLETON,
                ((ModuleCreationException)failure.getCause()).
                        getI18NBoundMessage().getMessage());
        //Verify provider state
        info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertFalse(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
    }

    /**
     * Verifies that factory locks are acquired when a factory is
     * not singleton and that attempts to create modules with duplicate
     * URNs fails.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void multipleFactoryLocking() throws Exception {
        //set up the factory
        ConcurrentTestFactory.setSingleton(false);
        ReentrantLock lock = new ReentrantLock();
        ConcurrentTestFactory.setNextCreateLock(lock);
        //initialize the module manager
        initManager();
        //acquire the lock to cause the module instance creation to block
        lock.lock();
        //Verify provider state
        ProviderInfo info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertProviderInfo(info, ConcurrentTestFactory.PROVIDER_URN,
                new String[]{ModuleURN.class.getName()},
                new Class[]{ModuleURN.class}, new I18NMessage0P(
                Messages.LOGGER, "provider").getText(), false, true);
        assertFalse(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
        //Spawn a thread to create the instance.
        final ModuleURN urn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN, "service");
        Future<ModuleURN> result1 = sService.submit(new Callable<ModuleURN>() {
            public ModuleURN call() throws Exception {
                return getManager().createModule(ConcurrentTestFactory.PROVIDER_URN, urn);
            }
        });
        //Wait until it gets stuck acquiring the lock
        while(lock.getQueueLength() == 0) {
            Thread.sleep(500);
        }
        //Verify provider state
        info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertTrue(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
        //Clear the factory lock
        ConcurrentTestFactory.setNextCreateLock(null);
        //Spawn another thread to create the instance
        final Future<ModuleURN> result2 = sService.submit(new Callable<ModuleURN>() {
            @Override
            public ModuleURN call() throws Exception {
                return getManager().createModule(ConcurrentTestFactory.PROVIDER_URN, urn);
            }
        });
        //This thread will get blocked on the factory lock. Wait until it does
        while(info.getLockQueueLength() < 1) {
            Thread.sleep(500);
            info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        }
        //Verify that modules from other providers can be instantiated
        //while this one is locked
        canPerformModuleOperationsDifferentProvider();
        //Now unlock the first thread and verify that is succeeds
        lock.unlock();
        assertEquals(urn, result1.get());
        //Verify that the second thread fails with duplicate URN error.
        ExecutionException failure = new ExpectedFailure<ExecutionException>(null) {
            @Override
            protected void run() throws Exception {
                result2.get();
            }
        }.getException();
        assertTrue(failure.getCause() instanceof ModuleCreationException);
        assertEquals(failure.toString(),  Messages.DUPLICATE_MODULE_URN,
                ((ModuleCreationException)failure.getCause()).
                        getI18NBoundMessage().getMessage());
        //Verify provider state
        info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertFalse(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
    }

    /**
     * Verifies that factory lock is acquired when module instance attribute
     * setters are being invoked. This is only verifying the current behavior.
     *
     * @throws Exception if there were errors.
     */
    @Test(timeout = 10000)
    public void moduleSetAttributeLocking() throws Exception {
        //set up the factory
        ConcurrentTestFactory.setSingleton(false);
        //initialize the module manager
        initManager();
        //Add a configuration provider to cause module attribute setters
        //to get invoked
        getManager().setConfigurationProvider(new ModuleConfigurationProvider() {
            @Override
            public String getDefaultFor(ModuleURN inURN, String inAttribute)
                    throws ModuleException {
                return "myvalue";
            }
            @Override
            public void refresh() throws ModuleException {
                //do nothing.
            }
        });
        //acquire a lock to cause the module attribute setting to block
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        //Verify provider state
        ProviderInfo info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertProviderInfo(info, ConcurrentTestFactory.PROVIDER_URN,
                new String[]{ModuleURN.class.getName()},
                new Class[]{ModuleURN.class}, new I18NMessage0P(
                Messages.LOGGER, "provider").getText(), false, true);
        assertFalse(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
        final ModuleURN urn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN, "service");
        //Setup the module to block when it's attribute value is set.
        ConcurrentTestModule.helper(urn).setSetValueLock(lock);
        //Spawn a thread to create the instance.
        Future<ModuleURN> result1 = sService.submit(new Callable<ModuleURN>() {
            public ModuleURN call() throws Exception {
                return getManager().createModule(ConcurrentTestFactory.PROVIDER_URN, urn);
            }
        });
        //Wait until it gets stuck acquiring the lock
        while(lock.getQueueLength() == 0) {
            Thread.sleep(500);
        }
        //Verify provider state
        info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertTrue(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
        //Clear the module instance lock
        ConcurrentTestModule.helper(urn).setSetValueLock(null);
        //Spawn another thread to create the instance
        final Future<ModuleURN> result2 = sService.submit(new Callable<ModuleURN>() {
            @Override
            public ModuleURN call() throws Exception {
                return getManager().createModule(ConcurrentTestFactory.PROVIDER_URN, urn);
            }
        });
        //This thread will get blocked on the factory lock. Wait until it does
        while(info.getLockQueueLength() < 1) {
            Thread.sleep(500);
            info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        }
        //Verify that modules from other providers can be instantiated
        //while this one is locked
        canPerformModuleOperationsDifferentProvider();
        //Now unlock the first thread and verify that is succeeds
        lock.unlock();
        assertEquals(urn, result1.get());
        //Verify that the second thread fails with duplicate URN error.
        ExecutionException failure = new ExpectedFailure<ExecutionException>(null) {
            @Override
            protected void run() throws Exception {
                result2.get();
            }
        }.getException();
        assertTrue(failure.getCause() instanceof ModuleCreationException);
        assertEquals(failure.toString(),  Messages.DUPLICATE_MODULE_URN,
                ((ModuleCreationException)failure.getCause()).
                        getI18NBoundMessage().getMessage());
        //Verify provider state
        info = getManager().getProviderInfo(ConcurrentTestFactory.PROVIDER_URN);
        assertFalse(info.isLocked());
        assertEquals(0, info.getLockQueueLength());
    }

    /**
     * Tests locking & module operations when module start takes a long time
     * to complete and succeeds.
     *
     * @throws Exception if there were errors.
     */
    @Test(timeout = 10000)
    public void moduleBlockedPreStartPass() throws Exception {
        //Setup the module to block on start
        final ModuleURN urn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN, "service");
        ReentrantLock lock = new ReentrantLock();
        ConcurrentTestModule.helper(urn).setPreStartLock(lock);
        lock.lock();
        //Run the start tests.
        Future<Object> result1 = runStartTests(lock, urn);
        //wait for start to complete
        result1.get();
        //verify module state
        ModuleInfo info = getManager().getModuleInfo(urn);
        //the only data flows running are the ones initiated by the module
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, flows.length);
        assertModuleInfo(info, urn, ModuleState.STARTED, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //stop and delete the module
        getManager().stop(urn);
        getManager().deleteModule(urn);
    }
    /**
     * Tests locking & module operations when module start takes a long time
     * to complete and fails.
     *
     * @throws Exception if there were errors.
     */
    @Test(timeout = 10000)
    public void moduleBlockedPreStartFail() throws Exception {
        //Setup the module to block on start
        final ModuleURN urn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN, "service");
        ReentrantLock lock = new ReentrantLock();
        ConcurrentTestModule.helper(urn).setPreStartLock(lock).setPreStartFail(true);
        lock.lock();
        //Run the start tests.
        final Future<Object> result1 = runStartTests(lock, urn);
        //wait for start to complete
        ExecutionException failure = new ExpectedFailure<ExecutionException>(null) {
            protected void run() throws Exception {
                result1.get();
            }
        }.getException();
        ExpectedFailure.assertI18NException(failure.getCause(), TestMessages.FAILURE);

        //verify module state
        ModuleInfo info = getManager().getModuleInfo(urn);
        assertModuleInfo(info, urn, ModuleState.START_FAILED, null, null,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //delete the module
        getManager().deleteModule(urn);
    }
    /**
     * Tests locking & module operations when module's setFlowSupport
     * API takes a lot of time. Do note that setFlowSupport is meant
     * to be doing anything complicated. This unit test is there to
     * verify that the system is robust enough to deal with a rogue
     * implementation of setFlowSupport.
     *
     * @throws Exception if there were errors.
     */
    @Test(timeout = 10000)
    public void moduleBlockedSetFlowSupport() throws Exception {
        //Setup the module to block on start
        final ModuleURN urn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN, "service");
        ReentrantLock lock = new ReentrantLock();
        ConcurrentTestModule.helper(urn).setSetFlowSupportLock(lock);
        lock.lock();
        //Run the start tests.
        final Future<Object> result1 = runStartTests(lock, urn);
        //wait for start to complete
        result1.get();
        //verify module state
        ModuleInfo info = getManager().getModuleInfo(urn);
        //the only data flows running are the ones initiated by the module
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, flows.length);
        assertModuleInfo(info, urn, ModuleState.STARTED, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //stop and delete the module
        getManager().stop(urn);
        getManager().deleteModule(urn);
    }

    /**
     * Verifies locking & module operations when stopping module instances.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void moduleBlockedPreStopPass() throws Exception {
        //Setup the module to block on stop
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        ReentrantLock lock = new ReentrantLock();
        ConcurrentTestModule.helper(urn).setPreStopLock(lock);
        lock.lock();
        Future<Object> result1 = runStopTests(urn, lock);
        ModuleInfo info;

        result1.get();
        //verify module state
        info = getManager().getModuleInfo(urn);
        assertModuleInfo(info, urn, ModuleState.STOPPED, null, null,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //delete the module
        getManager().deleteModule(urn);

    }
    /**
     * Verifies locking & module operations when stopping module instance fails.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void moduleBlockedStopFail() throws Exception {
        //Setup the module to block and then fail on stop
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        ReentrantLock lock = new ReentrantLock();
        ConcurrentTestModule.helper(urn).setPreStopLock(lock).setPreStopFail(true);
        lock.lock();
        final Future<Object> future = runStopTests(urn, lock);
        //wait for stop to complete
        ExecutionException failure = new ExpectedFailure<ExecutionException>(null) {
            protected void run() throws Exception {
                future.get();
            }
        }.getException();
        ExpectedFailure.assertI18NException(failure.getCause(), TestMessages.FAILURE);

        //verify module state
        ModuleInfo info = getManager().getModuleInfo(urn);
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, flows.length);
        assertModuleInfo(info, urn, ModuleState.STOP_FAILED, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //stop the module
        ConcurrentTestModule.helper(urn).setPreStopLock(null).setPreStopFail(false);
        getManager().stop(urn);
        //delete the module
        getManager().deleteModule(urn);
    }

    /**
     * Verifies locking vis-a-vis module start when a module's requestData()
     * API is slow.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void participatingModuleBlockedRequestDataAndStart() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");

        //test module start as a concurrent operation. It should block
        //initially when request data is in progress and should eventually
        //succeed.
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public ModuleStateException call() throws Exception {
                createStartFailure(urn, ModuleState.STARTED);
                return null;
            }
        };
        runBlockedRequestDataTests(urn, concurrentTestOperation);
    }
    /**
     * Verifies locking vis-a-vis module stop when a module's requestData()
     * API is slow.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void participatingModuleBlockedRequestDataAndStop() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        //test module stop as a concurrent operation. It should block
        //initially when request data is in progress and should eventually
        //succeed.
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                new ExpectedFailure<DataFlowException>(
                        Messages.CANNOT_STOP_MODULE_DATAFLOWS, urn.toString(),
                        ExpectedFailure.IGNORE) {
                    protected void run() throws Exception {
                        getManager().stop(urn);
                    }
                };
                return null;
            }
        };
        runBlockedRequestDataTests(urn, concurrentTestOperation);
    }
    @Test(timeout = 10000)
    public void requestingModuleBlockedRequestDataAndStart() throws Exception {
        final ModuleURN reqUrn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "requester");
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                createStartFailure(reqUrn, ModuleState.STARTING);
                return null;
            }
        };
        runRequestingModuleBlockedTests(reqUrn, concurrentTestOperation);
    }
    @Test(timeout = 10000)
    public void requestingModuleBlockedRequestDataAndStop() throws Exception {
        final ModuleURN reqUrn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN, "requester");
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                createStopFailure(reqUrn, ModuleState.STARTING);
                return null;
            }
        };
        runRequestingModuleBlockedTests(reqUrn, concurrentTestOperation);
    }
    @Test(timeout = 10000)
    public void requestingModuleBlockedRequestDataAndDelete() throws Exception {
        final ModuleURN reqUrn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "requester");
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                createDeleteFailure(reqUrn, ModuleState.STARTING);
                return null;
            }
        };
        runRequestingModuleBlockedTests(reqUrn, concurrentTestOperation);
    }

    /**
     * Verifies locking vis-a-vis module delete when a module's requestData()
     * API is slow.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void participatingModuleBlockedRequestDataAndDelete() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        //test module stop as a concurrent operation. It should block
        //initially when request data is in progress and should eventually
        //succeed.
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                createDeleteFailure(urn, ModuleState.STARTED);
                return null;
            }
        };
        runBlockedRequestDataTests(urn, concurrentTestOperation);
    }
    /**
     * Verifies locking vis-a-vis module start when a module's cancel()
     * API is slow.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void participatingModuleBlockedCancelAndStart() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");

        //test module start as a concurrent operation. It should block
        //initially when cancel request is in progress and should eventually
        //succeed.
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public ModuleStateException call() throws Exception {
                createStartFailure(urn, ModuleState.STARTED);
                return null;
            }
        };
        runBlockedCancelRequestTests(urn, concurrentTestOperation);
    }
    /**
     * Verifies locking vis-a-vis module stop when a module's cancel()
     * API is slow.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void participatingModuleBlockedCancelAndStop() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        //test module stop as a concurrent operation. It should block
        //initially when cancel is in progress and should eventually
        //succeed.
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                new ExpectedFailure<DataFlowException>(
                        Messages.CANNOT_STOP_MODULE_DATAFLOWS, urn.toString(),
                        ExpectedFailure.IGNORE) {
                    protected void run() throws Exception {
                        getManager().stop(urn);
                    }
                };
                return null;
            }
        };
        runBlockedCancelRequestTests(urn, concurrentTestOperation);
    }
    /**
     * Verifies locking vis-a-vis module delete when a module's cancel()
     * API is slow.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void participatingModuleBlockedCancelAndDelete() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        //test module stop as a concurrent operation. It should block
        //initially when cancel request is in progress and should eventually
        //succeed.
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                createDeleteFailure(urn, ModuleState.STARTED);
                return null;
            }
        };
        runBlockedCancelRequestTests(urn, concurrentTestOperation);
    }

    /**
     * Verifies write locking done when deleting auto-created instances.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void moduleAutoDeleteLockAndStart() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                new ExpectedFailure<ModuleNotFoundException>(
                        Messages.MODULE_NOT_FOUND, urn.toString()) {
                    protected void run() throws Exception {
                        getManager().start(urn);
                    }
                };
                return null;
            }
        };
        runModuleAutoDeleteTest(urn, concurrentTestOperation);
    }
    /**
     * Verifies write locking done when deleting auto-created instances.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void moduleAutoDeleteLockAndStop() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                createStopFailure(urn, ModuleState.STOPPED);
                return null;
            }
        };
        runModuleAutoDeleteTest(urn, concurrentTestOperation);
    }
    /**
     * Verifies write locking done when deleting auto-created instances.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void moduleAutoDeleteLockAndDelete() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                new ExpectedFailure<ModuleNotFoundException>(
                        Messages.MODULE_NOT_FOUND, urn.toString()) {
                    protected void run() throws Exception {
                        getManager().deleteModule(urn);
                    }
                };
                return null;
            }
        };
        runModuleAutoDeleteTest(urn, concurrentTestOperation);
    }
    /**
     * Verifies write locking done when deleting auto-created instances.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 10000)
    public void moduleAutoDeleteLockAndDataFlow() throws Exception {
        final ModuleURN urn = new ModuleURN(
                ConcurrentTestFactory.PROVIDER_URN, "service");
        Callable<Object> concurrentTestOperation = new Callable<Object>() {
            public Object call() throws Exception {
                createFlowFailure(urn, ModuleState.STOPPED);
                return null;
            }
        };
        runModuleAutoDeleteTest(urn, concurrentTestOperation);
    }

    /**
     * Runs a series of tests where call to requestData() blocks. While that
     * call is blocked, the supplied operation is executed in parallel. This
     * supplied operation is expected to block until requestData() unblocks, as
     * it will attempt to acquire the module write lock.
     *
     * @param inUrn the module URN of the module to create.
     * @param inConcurrentTestOperation the test operation that needs to be
     * run concurrently to verify that it acquires a write lock.
     *
     * @throws Exception if there is an error
     */
    private void runBlockedRequestDataTests(
            final ModuleURN inUrn,
            Callable<Object> inConcurrentTestOperation)
            throws Exception {
        ReentrantLock lock = new ReentrantLock();
        //set up the factory
        ConcurrentTestFactory.setSingleton(false);
        //initialize the module manager
        initManager();
        //Create an instance
        assertEquals(inUrn, getManager().createModule(
                ConcurrentTestFactory.PROVIDER_URN, inUrn));
        //Start the module
        getManager().start(inUrn);
        //Verify module state
        ModuleInfo info = getManager().getModuleInfo(inUrn);
        //the only data flows running are the ones initiated by the module
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, flows.length);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //Setup the module to block on request data
        ConcurrentTestModule.helper(inUrn).setRequestDataLock(lock);
        lock.lock();
        //spawn a thread to setup a data flow that will block
        Future<DataFlowID> future1 = sService.submit(new Callable<DataFlowID>() {
            public DataFlowID call() throws Exception {
                return getManager().createDataFlow(new DataRequest[]{
                        new DataRequest(inUrn)
                });
            }
        });
        //Wait until it gets blocked on the lock within requestData
        while(lock.getQueueLength() == 0) {
            Thread.sleep(500);
        }
        //verify module state
        info = getManager().getModuleInfo(inUrn);
        //refresh flows as the flow currently in progress gets reported
        DataFlowID[] pflows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, flows, pflows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(1, info.getReadLockCount());
        //Allow subsequent request data to not lock
        ConcurrentTestModule.helper(inUrn).setRequestDataLock(null);
        //verify we can create and cancel data flows
        DataFlowID flowID = getManager().createDataFlow(new DataRequest[]{
                new DataRequest(inUrn)
        });
        getManager().cancel(flowID);
        //verify we can perform operations on other modules
        canPerformModuleOperationsDifferentProvider();
        canPerformModuleOperationsSameProvider();
        //Run the concurrent test operation
        Future<Object> future2 = sService.submit(inConcurrentTestOperation);
        //wait until this operation blocks as it tries to acquire write lock
        do {
            Thread.sleep(500);
            info = getManager().getModuleInfo(inUrn);
        } while (info.getLockQueueLength() < 1);
        //now unlock the lock to let the data flow creation complete
        lock.unlock();
        //Wait for the data flow to get created
        flowID = future1.get();
        //Wait for the concurrent test task to complete successfully
        future2.get();
        //Now cancel the second flow
        getManager().cancel(flowID);
        //And verify the module state
        info = getManager().getModuleInfo(inUrn);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //stop and delete the module
        getManager().stop(inUrn);
        getManager().deleteModule(inUrn);
    }

    private void runRequestingModuleBlockedTests(
            final ModuleURN inReqUrn,
            Callable<Object> inConcurrentTestOperation) throws Exception {
        ModuleURN pcptURN = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN,
                "participant");
        //Setup the factory
        ConcurrentTestFactory.setSingleton(false);
        //init manager
        initManager();
        //create the modules
        assertEquals(pcptURN, getManager().createModule(pcptURN.parent(), pcptURN));
        assertEquals(inReqUrn, getManager().createModule(inReqUrn.parent(),
                inReqUrn));
        //start the participant
        getManager().start(pcptURN);
        //the only data flows running are the ones initiated by the module
        DataFlowID[] pFlows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, pFlows.length);
        //configure the participant to block when it's requested data
        ReentrantLock pcptLock = new ReentrantLock();
        ConcurrentTestModule.helper(pcptURN).setRequestDataLock(pcptLock);
        pcptLock.lock();
        //configure the flow requester to create a data flow with the participant
        ConcurrentTestModule.helper(inReqUrn).setFlowRequests(new DataRequest[]{
                new DataRequest(pcptURN)
        });
        //start the requester. it will get blocked in prestart when creating
        //the data flow for the participant
        Future<Object> future1 = sService.submit(new Callable<Object>() {
            public Object call() throws Exception {
                getManager().start(inReqUrn);
                return null;
            }
        });
        //wait until this task gets blocked on the thread
        while(pcptLock.getQueueLength() < 1) {
            Thread.sleep(500);
        }
        //Verify module state
        ModuleInfo info = getManager().getModuleInfo(inReqUrn);
        //the only data flows running are the ones initiated by modules
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(3, flows.length);
        //The requester initiated flows is the total set of flows minus
        //the participant flows
        DataFlowID[] rFlows = subtract(flows, pFlows);
        //The only flow the requester is participating in is the one it initiated.
        DataFlowID[] rpFlows = new DataFlowID[]{
                ConcurrentTestModule.getModule(inReqUrn).getFlowID()};
        assertModuleInfo(info, inReqUrn, ModuleState.STARTING, rFlows, rpFlows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(1, info.getReadLockCount());
        assertEquals(0, info.getLockQueueLength());
        //Verify that an attempt to create data flow over this module fails
        createFlowFailure(inReqUrn, ModuleState.STARTING);
        //Verify that the module can itself request data flows
        assertNotNull(ConcurrentTestModule.getModule(inReqUrn));
        DataFlowID flowID = ConcurrentTestModule.getModule(
                inReqUrn).createFlow(new DataRequest[]{
                new DataRequest(inReqUrn)
        });
        //Run the concurrent operation and verify that it initially blocks
        //and eventually succeeds
        Future<Object> future2 = sService.submit(inConcurrentTestOperation);
        //Wait until this thread gets blocked on the module lock
        while(info.getLockQueueLength() < 1) {
            Thread.sleep(500);
            info = getManager().getModuleInfo(inReqUrn);
        }
        //Ensure that requesters preStart locks so that we can let the other
        //task acquire the lock without the possibility of start completing
        //and completely starting the module.
        ReentrantLock reqLock = new ReentrantLock();
        ConcurrentTestModule.helper(inReqUrn).setPreStartLock(reqLock);
        reqLock.lock();
        //Now unblock the requestData() call
        pcptLock.unlock();
        //Wait until it gets blocked in the reqLock.
        while(reqLock.getQueueLength() < 1) {
            Thread.sleep(500);
        }
        //wait for the second task to complete
        future2.get();
        //unblock the requesters preStart()
        reqLock.unlock();
        //wait for the first task to complete
        future1.get();
        //cancel the other flow that we had created
        ConcurrentTestModule.getModule(inReqUrn).cancelFlow(flowID);
        //verify module state
        info = getManager().getModuleInfo(inReqUrn);
        assertEquals(ModuleState.STARTED, info.getState());
        assertEquals(2, info.getInitiatedDataFlows().length);
        assertEquals(1, info.getParticipatingDataFlows().length);
        HashSet<DataFlowID> flowSet = new HashSet<DataFlowID>(
                Arrays.asList(info.getInitiatedDataFlows()));
        flowSet.remove(info.getParticipatingDataFlows()[0]);
        flowSet.addAll(Arrays.asList(pFlows));
        //verify the participating module state
        assertModuleInfo(getManager(), pcptURN, ModuleState.STARTED, pFlows,
                flowSet.toArray(new DataFlowID[flowSet.size()]), false, false,
                true, true, true);
        //wait for second task to complete, it should not fail.
        future2.get();
        //stop and delete the modules
        getManager().stop(inReqUrn);
        getManager().stop(pcptURN);
        getManager().deleteModule(inReqUrn);
        getManager().deleteModule(pcptURN);
    }
    /**
     * Runs a series of tests where call to cancel() blocks. While that
     * call is blocked, the supplied operation is executed in parallel. This
     * supplied operation is expected to block until requestData() unblocks, as
     * it will attempt to acquire the write lock on the module.
     *
     * @param inUrn the module URN of the module to create.
     * @param inConcurrentTestOperation the test operation that needs to be
     * run concurrently.
     * @throws Exception if there is an error
     */
    private void runBlockedCancelRequestTests(
            final ModuleURN inUrn,
            Callable<Object> inConcurrentTestOperation)
            throws Exception {
        ReentrantLock lock = new ReentrantLock();
        //set up the factory
        ConcurrentTestFactory.setSingleton(false);
        //initialize the module manager
        initManager();
        //Create an instance
        assertEquals(inUrn, getManager().createModule(
                ConcurrentTestFactory.PROVIDER_URN, inUrn));
        //Start the module
        getManager().start(inUrn);
        //Verify module state
        ModuleInfo info = getManager().getModuleInfo(inUrn);
        //the only data flows running are the ones initiated by the module
        DataFlowID[] initFlows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, initFlows.length);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, initFlows, initFlows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //Create a data flow.
        DataFlowID flowID = getManager().createDataFlow(new DataRequest[]{
                new DataRequest(inUrn)
        });
        //Refresh the set of flows for this module
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(2, flows.length);
        //verify module state
        info = getManager().getModuleInfo(inUrn);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, initFlows, flows,
                false, false, true, true, true);
        //Setup the module to block on cancel
        ConcurrentTestModule.helper(inUrn).setCancelLock(lock);
        lock.lock();
        //spawn a thread to cancel the dataflow, which blocks
        final DataFlowID flowID1 = flowID;
        Future<Object> future1 = sService.submit(new Callable<Object>() {
            public Object call() throws Exception {
                getManager().cancel(flowID1);
                return null;
            }
        });
        //Wait until it gets blocked on the lock within cancel
        while (lock.getQueueLength() == 0) {
            Thread.sleep(500);
        }
        //verify module state
        info = getManager().getModuleInfo(inUrn);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, initFlows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //Allow subsequent request data to not lock
        ConcurrentTestModule.helper(inUrn).setCancelLock(null);
        //verify that another attempt to cancel the flow doesn't block and fails
        new ExpectedFailure<DataFlowException>(
                Messages.DATA_FLOW_ALREADY_CANCELING, flowID1.toString()){
            protected void run() throws Exception {
                getManager().cancel(flowID1);
            }
        };
        //verify we can create and cancel data flows on the
        //same module without blocking
        flowID = getManager().createDataFlow(new DataRequest[]{
                new DataRequest(inUrn)
        });
        getManager().cancel(flowID);
        //verify we can perform operations on other modules
        canPerformModuleOperationsDifferentProvider();
        canPerformModuleOperationsSameProvider();
        //Initiate the concurrent testing operation
        Future<Object> future2 = sService.submit(inConcurrentTestOperation);
        //the concurrent operation doesn't block as cancellation doesn't
        //acquire any locks and so it should complete without failures.
        future2.get();
        //now unlock the lock to let the data flow creation complete
        lock.unlock();
        //Wait for the data flow to get canceled
        future1.get();
        //Refresh the set of flows for this module
        flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, flows.length);
        //And verify the module state
        info = getManager().getModuleInfo(inUrn);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, initFlows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //stop and delete the module
        getManager().stop(inUrn);
        getManager().deleteModule(inUrn);
    }

    private void runModuleAutoDeleteTest(
            ModuleURN inUrn,
            Callable<Object> inConcurrentTestOperation) throws Exception {
        ReentrantLock lock = new ReentrantLock();
        //set up the factory
        ConcurrentTestFactory.setSingleton(false);
        ConcurrentTestFactory.setAutoCreate(true);
        //initialize the module manager
        initManager();
        //verify factory
        assertProviderInfo(getManager(), ConcurrentTestFactory.PROVIDER_URN,
                new String[]{ModuleURN.class.getName()},
                new Class[]{ModuleURN.class}, new I18NMessage0P(
                Messages.LOGGER, "provider").getText(),true, true);
        //Create a data flow
        final DataFlowID flowID = getManager().createDataFlow(new DataRequest[]{
                new DataRequest(inUrn)
        });
        //verify module
        ModuleInfo info = getManager().getModuleInfo(inUrn);
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(2, flows.length);
        //init flows is the set of flows minus the flow we created above
        Set<DataFlowID> initFlowsList = new HashSet<DataFlowID>(Arrays.asList(flows));
        initFlowsList.remove(flowID);
        DataFlowID[] initFlows = initFlowsList.toArray(new DataFlowID[initFlowsList.size()]);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, initFlows, flows, true,
                true, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0,info.getReadLockCount());
        //Setup the module to acquire a lock when it's deleted
        ConcurrentTestModule.helper(inUrn).setPreStopLock(lock);
        //Now cancel the flow, it should block as the attempt to stop the
        //module will block
        lock.lock();
        Future<Object> future1 = sService.submit(new Callable<Object>() {
            public Object call() throws Exception {
                getManager().cancel(flowID);
                return null;
            }
        });
        //Wait until it blocks
        while(!info.isWriteLocked()) {
            Thread.sleep(500);
            info = getManager().getModuleInfo(inUrn);
        }
        //verify module state
        assertModuleInfo(info, inUrn, ModuleState.STOPPING, initFlows, initFlows,
                true, true, true, true, true);
        //Setup the module to not acquire a lock when it's deleted anymore
        ConcurrentTestModule.helper(inUrn).setPreStopLock(null);
        //verify that we can perform other operations while this module is stuck
        canPerformModuleOperationsDifferentProvider();
        canPerformModuleOperationsSameProvider();
        // Now verify the supplied concurrent operation on this module block
        // and succeed as expected
        //Spawn a thread to start the module, it should get stuck and then
        //eventually fail
        Future<Object> future2 = sService.submit(inConcurrentTestOperation);
        //wait until it gets stuck in the lock
        while(info.getLockQueueLength() < 1) {
            Thread.sleep(500);
            info = getManager().getModuleInfo(inUrn);
        }
        //let the delete complete by unlocking the lock
        lock.unlock();
        future1.get();
        //verify that the module is deleted
        assertTrue(getManager().getModuleInstances(
                ConcurrentTestFactory.PROVIDER_URN).isEmpty());
        //verify that the concurrent task completes successfully
        future2.get();
    }

    private Future<Object> runStartTests(ReentrantLock inLock, final ModuleURN inUrn) throws Exception {
        //set up the factory
        ConcurrentTestFactory.setSingleton(false);
        //initialize the module manager
        initManager();
        //Create an instance
        assertEquals(inUrn, getManager().createModule(
                ConcurrentTestFactory.PROVIDER_URN, inUrn));
        //Verify module state
        ModuleInfo info = getManager().getModuleInfo(inUrn);
        assertModuleInfo(info, inUrn, ModuleState.CREATED, null, null,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //Spawn a thread to start the module
        Future<Object>result1 = sService.submit(new Callable<Object>(){
            public Object call() throws Exception {
                getManager().start(inUrn);
                return null;
            }
        });
        //Wait until it gets blocked on the lock within prestart
        while(inLock.getQueueLength() == 0) {
            Thread.sleep(500);
        }
        //verify module state
        info = getManager().getModuleInfo(inUrn);
        //the only flows running right now
        List<DataFlowID> flowList = getManager().getDataFlows(true);
        DataFlowID[] flows = flowList.isEmpty()? null: flowList.toArray(new DataFlowID[flowList.size()]);
        assertModuleInfo(info, inUrn, ModuleState.STARTING, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //verify that another attempt to start the module fails
        createStartFailure(inUrn, ModuleState.STARTING);
        //verify that stop module fails
        createStopFailure(inUrn, ModuleState.STARTING);
        //verify that delete module fails
        createDeleteFailure(inUrn, ModuleState.STARTING);
        //verify create data flow fails
        createFlowFailure(inUrn, ModuleState.STARTING);
        //verify that module operations from a different provider can be performed
        canPerformModuleOperationsDifferentProvider();
        //verify that module operations for a different module from the same
        //provider can be carried out.
        canPerformModuleOperationsSameProvider();
        //unlock to let start complete
        inLock.unlock();
        return result1;
    }


    private Future<Object> runStopTests(final ModuleURN inUrn,
                                        ReentrantLock inLock)
            throws Exception {
        //set up the factory
        ConcurrentTestFactory.setSingleton(false);
        //initialize the module manager
        initManager();
        //Create an instance
        assertEquals(inUrn, getManager().createModule(
                ConcurrentTestFactory.PROVIDER_URN, inUrn));
        //Start the module
        getManager().start(inUrn);
        //Verify module state
        ModuleInfo info = getManager().getModuleInfo(inUrn);
        //the only data flows running are the ones initiated by the module
        DataFlowID[] flows = getManager().getDataFlows(true).toArray(new DataFlowID[0]);
        assertEquals(1, flows.length);
        assertModuleInfo(info, inUrn, ModuleState.STARTED, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //Spawn a thread to stop the module
        Future<Object> future = sService.submit(new Callable<Object>(){
            public Object call() throws Exception {
                getManager().stop(inUrn);
                return null;
            }
        });
        //Wait until it gets blocked on the lock within prestop
        while(inLock.getQueueLength() == 0) {
            Thread.sleep(500);
        }
        //verify module state
        info = getManager().getModuleInfo(inUrn);
        assertModuleInfo(info, inUrn, ModuleState.STOPPING, flows, flows,
                false, false, true, true, true);
        assertFalse(info.isWriteLocked());
        assertEquals(0, info.getReadLockCount());
        //verify that another attempt to start the module fails
        createStartFailure(inUrn, ModuleState.STOPPING);
        //verify that stop module fails
        createStopFailure(inUrn, ModuleState.STOPPING);
        //verify that delete module fails
        createDeleteFailure(inUrn, ModuleState.STOPPING);
        //verify create data flow fails
        createFlowFailure(inUrn, ModuleState.STOPPING);
        //verify that module operations from a different provider can be performed
        canPerformModuleOperationsDifferentProvider();
        //verify that module operations for a different module from the same
        //provider can be carried out.
        canPerformModuleOperationsSameProvider();
        //unlock to let stop complete
        inLock.unlock();
        return future;
    }

    /**
     * Verifies that modules from a different provider can be created
     * concurrently with a different provider creating its modules.
     *
     * @throws Exception if there were errors.
     */
    private void canPerformModuleOperationsDifferentProvider() throws Exception {
        ModuleURN urn = new ModuleURN(MultipleModuleFactory.PROVIDER_URN,
                "test");
        assertEquals(urn, getManager().createModule(
                MultipleModuleFactory.PROVIDER_URN, urn));
        //this module is autostarted
        getManager().stop(urn);
        getManager().deleteModule(urn);
    }
    /**
     * Verifies that modules from a different provider can be created
     * concurrently with a different provider creating its modules.
     *
     * @throws Exception if there were errors.
     */
    private void canPerformModuleOperationsSameProvider() throws Exception {
        //Clear the module so that it does not acquire any locks.
        ModuleURN urn = new ModuleURN(ConcurrentTestFactory.PROVIDER_URN,
                "test");
        assertEquals(urn, getManager().createModule(
                ConcurrentTestFactory.PROVIDER_URN, urn));
        //Start the module if it wasn't autostarted
        if (!getManager().getModuleInfo(urn).getState().isStarted()) {
            getManager().start(urn);
        }
        DataFlowID flowID = getManager().createDataFlow(new DataRequest[]{
                new DataRequest(urn, null)
        });
        getManager().cancel(flowID);
        getManager().stop(urn);
        getManager().deleteModule(urn);
    }
    private DataFlowID[] subtract(DataFlowID[] inFrom, DataFlowID[]inValue) {
        HashSet<DataFlowID> set = new HashSet<DataFlowID>(Arrays.asList(inFrom));
        set.removeAll(Arrays.asList(inValue));
        return set.toArray(new DataFlowID[set.size()]);
    }
    private ExpectedFailure createFlowFailure(final ModuleURN inURN,
                                              ModuleState inExpectedState)
            throws Exception {
        return new ExpectedFailure<ModuleStateException>(
                Messages.DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT,
                inURN.toString(), inExpectedState,
                ModuleState.PARTICIPATE_FLOW_STATES.toString()){
            protected void run() throws Exception {
                getManager().createDataFlow(new DataRequest[]{
                        new DataRequest(inURN)
                });
            }
        };
    }
    private ExpectedFailure createDeleteFailure(final ModuleURN inUrn,
                                                Object inExpectedState)
            throws Exception {
        return new ExpectedFailure<ModuleStateException>(
                Messages.DELETE_FAILED_MODULE_STATE_INCORRECT, inUrn.toString(),
                inExpectedState,
                ModuleState.DELETABLE_STATES.toString()){
            protected void run() throws Exception {
                getManager().deleteModule(inUrn);
            }
        };
    }
    private ExpectedFailure createStopFailure(final ModuleURN inUrn,
                                              Object inExpectedState)
            throws Exception {
        return new ExpectedFailure<ModuleStateException>(
                Messages.MODULE_NOT_STOPPED_STATE_INCORRECT, inUrn.toString(),
                inExpectedState, ModuleState.STOPPABLE_STATES.toString()){
            protected void run() throws Exception {
                getManager().stop(inUrn);
            }
        };
    }
    private ExpectedFailure createStartFailure(final ModuleURN inUrn,
                                               Object inExpectedState)
            throws Exception {
        return new ExpectedFailure<ModuleStateException>(
                Messages.MODULE_NOT_STARTED_STATE_INCORRECT, inUrn.toString(),
                inExpectedState, ModuleState.STARTABLE_STATES.toString()){
            protected void run() throws Exception {
                getManager().start(inUrn);
            }
        };

    }

    ModuleManager getManager() {
        return mManager;
    }
    void initManager() throws Exception {
        mManager = new ModuleManager();
        mManager.init();
    }
    @Before
    public void setUp() throws Exception {
        ConcurrentTestFactory.clear();
        ConcurrentTestModule.clear();
    }
    @After
    public void cleanUp() throws Exception {
        if(mManager != null) {
            mManager.stop();
            mManager = null;
        }
    }
    private final static ExecutorService sService = Executors.newCachedThreadPool();
    private ModuleManager mManager;
}

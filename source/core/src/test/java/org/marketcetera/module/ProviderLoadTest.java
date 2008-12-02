package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.regex.Pattern;
import java.util.ServiceConfigurationError;
import java.io.IOException;

/* $License$ */
/**
 * Tests module factory loading, refreshes and errors.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ProviderLoadTest extends ModuleTestBase {
    @After
    public void cleanup() throws Exception {
        mManager.stop();
    }
    /**
     * Tests various failures in {@link ModuleManager#init()} &
     * {@link ModuleManager#refresh()}
     *  and their expected behavior.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void loading() throws Exception {
        //Initialize the testing classloader that helps us
        //create various failures.
        ModuleTestClassLoader loader = new ModuleTestClassLoader(
                LifecycleTest.class.getClassLoader());
        loader.setFailResources(Pattern.compile("META-INF/services/.*"));
        mManager = new ModuleManager(loader);
        new ExpectedFailure<ModuleNotFoundException>(
                Messages.MODULE_NOT_FOUND,
                SinkModuleFactory.INSTANCE_URN.toString()){
            protected void run() throws Exception {
                mManager.init();
            }
        };
        assertEquals(0, mManager.getProviders().size());
        //Let the resources be found but do not let single module factory to be loaded
        loader.setFailResources(null);
        loader.setFailClasses(Pattern.compile(".*SingleModuleFactory.*"));
        mManager = new ModuleManager(loader);
        assertTrue(new ExpectedFailure<ModuleException>(
                Messages.MODULE_CONFIGURATION_ERROR){
            protected void run() throws Exception {
                mManager.init();
            }
        }.getException().getCause() instanceof ServiceConfigurationError);
        loader.setFailClasses(null);
        loader.setFilterResources(Pattern.compile(".*test.*"));
        mManager = new ModuleManager(loader);
        mManager.init();
        assertEquals(1, mManager.getProviders().size());
        assertEquals(SinkModuleFactory.PROVIDER_URN,mManager.getProviders().get(0));
        assertEquals(1, mManager.getModuleInstances(null).size());
        assertEquals(SinkModuleFactory.INSTANCE_URN,
                mManager.getModuleInstances(null).get(0));
        //try refresh and verify nothing changes
        mManager.refresh();
        assertEquals(1, mManager.getProviders().size());
        assertEquals(SinkModuleFactory.PROVIDER_URN,mManager.getProviders().get(0));
        assertEquals(1, mManager.getModuleInstances(null).size());
        assertEquals(SinkModuleFactory.INSTANCE_URN,
                mManager.getModuleInstances(null).get(0));
        //Now setup the manager with a refresh listener
        final Refresher refresher = new Refresher();
        mManager.setRefreshListener(refresher);
        //verify that another attempt to setup refresh listener fails
        new ExpectedFailure<ModuleException>(
                Messages.REFRESH_LISTENER_ALREADY_SETUP,
                refresher.getClass().getName()){
            protected void run() throws Exception {
                mManager.setRefreshListener(refresher);
            }
        };
        //setup the refresh listener to let module manager refresh itself
        refresher.setup(true);
        assertFalse(refresher.isInvoked());
        //now remove the filter but setup a classloading failure
        loader.setFilterResources(null);
        loader.setFailClasses(Pattern.compile(".*ModuleFactory.*"));
        //Refresh should fail
        assertTrue(new ExpectedFailure<ModuleException>(Messages.MODULE_CONFIGURATION_ERROR){
            protected void run() throws Exception {
                mManager.refresh();
            }
        }.getException().getCause() instanceof ServiceConfigurationError);
        //Nothing should've changed.
        assertEquals(1, mManager.getProviders().size());
        assertEquals(SinkModuleFactory.PROVIDER_URN,mManager.getProviders().get(0));
        assertEquals(1, mManager.getModuleInstances(null).size());
        assertEquals(SinkModuleFactory.INSTANCE_URN,
                mManager.getModuleInstances(null).get(0));
        //verify refresher was invoked
        assertTrue(refresher.isInvoked());
        //Setup the refresher to not let module manager refresh itself
        refresher.setup(false);
        assertFalse(refresher.isInvoked());
        //verify that refresh doesn't fail as module manager doesn't refresh
        mManager.refresh();
        //verify refresher was invoked
        assertTrue(refresher.isInvoked());
        //Verify nothing changed.
        assertEquals(1, mManager.getProviders().size());
        assertEquals(SinkModuleFactory.PROVIDER_URN,mManager.getProviders().get(0));
        assertEquals(1, mManager.getModuleInstances(null).size());
        assertEquals(SinkModuleFactory.INSTANCE_URN,
                mManager.getModuleInstances(null).get(0));
        //now change the refresher to throw an exception
        refresher.setThrowException(true);
        //refresh should fail
        new ExpectedFailure<ModuleException>(Messages.ERROR_REFRESH){
            protected void run() throws Exception {
                mManager.refresh();
            }
        };
        assertTrue(refresher.isInvoked());
        //change the refresher to allow module manager to refresh itself
        refresher.setup(true);
        refresher.setThrowException(false);
        //Now unset the class pattern and retry refresh
        loader.setFailClasses(null);
        mManager.refresh();
        //Should see all the providers now
        ModuleTestBase.checkAllProviders(mManager.getProviders());

        assertEquals(4, mManager.getModuleInstances(null).size());
        assertContains(mManager.getModuleInstances(null),new ModuleURN[]{
                SinkModuleFactory.INSTANCE_URN,
                EmitterModuleFactory.INSTANCE_URN,
                SingleModuleFactory.INSTANCE_URN,
                CopierModuleFactory.INSTANCE_URN});
    }
    private ModuleManager mManager;
    private static class Refresher implements RefreshListener {
        public void setup(boolean inReturnValue) {
            mReturnValue = inReturnValue;
            mInvoked = false;
        }

        public void setThrowException(boolean inThrowException) {
            mThrowException = inThrowException;
            mInvoked = false;
        }

        public boolean isInvoked() {
            return mInvoked;
        }

        public boolean refresh() throws IOException {
            mInvoked = true;
            if(mThrowException) {
                throw new IOException();
            }
            return mReturnValue;
        }
        private boolean mInvoked;
        private boolean mReturnValue;
        private boolean mThrowException;
    }
}

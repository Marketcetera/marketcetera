package org.marketcetera.photon.commons.osgi;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.test.OSGITestUtil.getTestBundleContext;
import static org.marketcetera.photon.test.OSGITestUtil.registerMockService;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.osgi.HighestRankedTracker.IHighestRankedTrackerListener;
import org.marketcetera.photon.test.ExpectedFailure;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link HighestRankedTracker}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class HighestRankedTrackerTest {

    private IHighestRankedTrackerListener mMockCallback;
    private BundleContext mMockContext;
    private HighestRankedTracker mFixture;

    @Before
    public void before() {
        mMockCallback = mock(IHighestRankedTrackerListener.class);
        mMockContext = mock(BundleContext.class);
    }

    @Test
    public void testWithMocks() {
        mFixture = new HighestRankedTracker(mMockContext, "Bogus",
                mMockCallback);

        Object service1 = "service1";
        ServiceReference mockReference1 = mock(ServiceReference.class);
        Object service2 = "service2";
        ServiceReference mockReference2 = mock(ServiceReference.class);
        Object service3 = "service3";
        ServiceReference mockReference3 = mock(ServiceReference.class);
        when(mMockContext.getServiceReference("Bogus")).thenReturn(
                mockReference1, mockReference2, mockReference1, null,
                mockReference3, mockReference3);
        when(mMockContext.getService(mockReference1)).thenReturn(service1);
        when(mMockContext.getService(mockReference2)).thenReturn(service2);
        when(mMockContext.getService(mockReference3)).thenReturn(service3);

        // context returns service1
        mFixture.addingService(null);
        verify(mMockCallback).highestRankedServiceChanged(service1);
        reset(mMockCallback);
        
        // context returns service2
        mFixture.removedService(null, null);
        verify(mMockCallback).highestRankedServiceChanged(service2);
        reset(mMockCallback);
        
        // context returns service1
        mFixture.modifiedService(null, null);
        verify(mMockCallback).highestRankedServiceChanged(service1);
        reset(mMockCallback);
        
        
        // context returns null
        mFixture.addingService(null);
        verify(mMockCallback).highestRankedServiceChanged(null);
        reset(mMockCallback);
        
        // context returns service3
        mFixture.addingService(null);
        verify(mMockCallback).highestRankedServiceChanged(service3);
        reset(mMockCallback);
        
        // context returns service3 again (no change)
        mFixture.addingService(null);
        verify(mMockCallback, never()).highestRankedServiceChanged(service3);
    }
    
    @Test
    public void testUngetService() throws Exception {
        mFixture = new HighestRankedTracker(mMockContext, "Bogus",
                mMockCallback);
        Object service1 = "service1";
        ServiceReference mockReference1 = mock(ServiceReference.class);
        when(mMockContext.getServiceReference("Bogus")).thenReturn(
                mockReference1);
        when(mMockContext.getService(mockReference1)).thenReturn(service1);
        mFixture.addingService(null);
        verify(mMockContext).ungetService(mockReference1);
    }
    
    @Test
    public void testUngetServiceWhenExceptionThrown() throws Exception {
        mFixture = new HighestRankedTracker(mMockContext, "Bogus",
                mMockCallback);
        Object service1 = "service1";
        ServiceReference mockReference1 = mock(ServiceReference.class);
        when(mMockContext.getServiceReference("Bogus")).thenReturn(
                mockReference1);
        when(mMockContext.getService(mockReference1)).thenReturn(service1);
        doThrow(new RuntimeException()).when(mMockCallback).highestRankedServiceChanged(anyObject());
        new ExpectedFailure<RuntimeException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.addingService(null);
            }
        };
        verify(mMockContext).ungetService(mockReference1);
    }

    @Test
    public void testIntegration() {
        mFixture = new HighestRankedTracker(getTestBundleContext(),
                String.class.getName(), mMockCallback);

        Object service1 = "service1";
        Object service2 = "service2";
        Object service3 = "service3";

        ServiceRegistration registration1 = registerMockService(String.class,
                service1, 10);

        mFixture.open();

        verify(mMockCallback).highestRankedServiceChanged(service1);
        reset(mMockCallback);

        ServiceRegistration registration2 = registerMockService(String.class,
                service2, 15);

        verify(mMockCallback).highestRankedServiceChanged(service2);
        reset(mMockCallback);

        ServiceRegistration registration3 = registerMockService(String.class,
                service3, 10);

        verify(mMockCallback, never()).highestRankedServiceChanged(service3);
        reset(mMockCallback);        

        registration2.unregister();

        verify(mMockCallback).highestRankedServiceChanged(service1);
        reset(mMockCallback);
        
        registration3.setProperties(new Hashtable<String, Integer>(ImmutableMap
                .of(Constants.SERVICE_RANKING, 20)));

        /*
         * Would expect service3 to be returned but BundleContext is not working
         * that way (see testBundleContextCaveat)
         */
        // verify(mMockCallback).highestRankedServiceChanged(service3);

        registration1.unregister();

        // verify(mMockCallback).highestRankedServiceChanged(service3);

        registration3.unregister();

        verify(mMockCallback).highestRankedServiceChanged(null);
    }

    /**
     * Tests http://bugs.eclipse.org/288365. If this test fails, the bug might
     * be fixed and testIntegration can be updated along with the
     * {@link HighestRankedTracker} javadocs.
     */
    @Test
    public void testBundleContextCaveat() {
        Object service1 = "service1";
        Object service2 = "service2";

        ServiceRegistration registration1 = registerMockService(String.class,
                service1, 15);

        ServiceRegistration registration2 = registerMockService(String.class,
                service2, 10);

        assertThat(getTestBundleContext().getService(
                getTestBundleContext().getServiceReference(
                        String.class.getName())), is(service1));

        registration2.setProperties(new Hashtable<String, Integer>(ImmutableMap
                .of(Constants.SERVICE_RANKING, 20)));

        /*
         * I think this should really return service2!!
         */
        assertThat(getTestBundleContext().getService(
                getTestBundleContext().getServiceReference(
                        String.class.getName())), is(service1));

        registration1.unregister();
        registration2.unregister();
    }

    @Test
    public void testValidation() throws Exception {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run() throws Exception {
                new HighestRankedTracker(null, "Bogus", mMockCallback);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run() throws Exception {
                new HighestRankedTracker(mMockContext, null, mMockCallback);
            }
        };
        new ExpectedNullArgumentFailure("callback") {
            @Override
            protected void run() throws Exception {
                new HighestRankedTracker(mMockContext, "Bogus", null);
            }
        };
    }

}

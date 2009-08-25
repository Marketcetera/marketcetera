package org.marketcetera.photon.commons.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullElementFailure;
import org.marketcetera.photon.commons.ui.ColorManager.IColorDescriptorProvider;
import org.marketcetera.photon.commons.ui.SWTUtilsTest.ExpectedThreadCheckFailure;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.ExpectedIllegalArgumentException;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Base test class for color management classes.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ColorManagerTestBase extends PhotonTestBase {

    abstract protected void init();

    abstract protected void dispose();

    abstract protected Color get();

    @After
    public void after() {
        // don't want to leave a hanging display
        final Display display = Display.getCurrent();
        if (display != null) {
            display.dispose();
        }
    }

    @Test
    public void testInitColors() throws Exception {
        Display display = new Display();
        assertNull();
        init();
        assertNotNull();
        dispose();
        assertNull();
        init();
        assertNotNull();
        display.dispose();
        assertNull();
        display = new Display();
        init();
        assertNotNull();
        display.dispose();
        assertNull();
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                init();
            }
        };
        assertNull();
    }

    @Test
    public void multipleThreads() throws Exception {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final CountDownLatch initialized1 = new CountDownLatch(1);
        final CountDownLatch dispose = new CountDownLatch(1);

        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
                Display d = new Display();
                try {
                    assertNull();
                    init();
                    assertNotNull();
                    // let other thread verify this initialization had no effect
                    initialized1.countDown();
                    // wait for other thread run tests
                    dispose.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    // make sure the latch is open so test doesn't hang
                    initialized1.countDown();
                    // auto dispose
                    d.dispose();
                }
            }
        });

        Display d = new Display();
        // wait for other thread to initialize
        initialized1.await();
        // verify other threads colors are available
        assertNotNull();
        // init should fail
        new ExpectedFailure<IllegalStateException>(
                "Colors already initialized for a different display.") {
            @Override
            protected void run() throws Exception {
                init();
            }
        };
        // verify state not corrupted
        assertNotNull();
        // wait for other thread to finish
        dispose.countDown();
        future.get();
        // colors should no longer be available
        assertNull();
        // init on this thread
        init();
        // colors available again
        assertNotNull();
        // dispose
        d.dispose();
        assertNull();
    }

    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("descriptors") {
            @Override
            protected void run() throws Exception {
                ColorManager.createFor(null);
            }
        };
        new ExpectedNullElementFailure("descriptors") {
            @Override
            protected void run() throws Exception {
                ColorManager.createFor(Arrays.asList((ColorDescriptor) null));
            }
        };
        new ExpectedNullArgumentFailure("providers") {
            @Override
            protected void run() throws Exception {
                ColorManager.createForProviders(null);
            }
        };
        new ExpectedNullElementFailure("providers") {
            @Override
            protected void run() throws Exception {
                ColorManager.createForProviders(Arrays
                        .asList((IColorDescriptorProvider) null));
            }
        };
        final IColorDescriptorProvider badProvider = mock(IColorDescriptorProvider.class);
        new ExpectedIllegalArgumentException(MessageFormat.format(
                "Color descriptor provider [{0}] provided a null descriptor.",
                badProvider)) {
            @Override
            protected void run() throws Exception {
                ColorManager.createForProviders(Collections
                        .singleton(badProvider));
            }
        };
        final ColorDescriptor badDescriptor = mock(ColorDescriptor.class);
        new ExpectedFailure<Exception>(MessageFormat.format(
                "Color descriptor [{0}] is not managed by this manager.",
                badDescriptor)) {
            @Override
            protected void run() throws Exception {
                ColorManager.createFor(Collections.<ColorDescriptor>emptyList()).getColor(badDescriptor);
            }
        };
    }

    private void assertNotNull() {
        assertThat(get(), not(nullValue(Color.class)));
    }

    private void assertNull() {
        assertThat(get(), nullValue());
    }

}

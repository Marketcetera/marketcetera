package org.marketcetera.photon.test;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withStyle;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Utility methods for Photon UI testing.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class SWTTestUtil {

    /**
     * Process UI input but do not return for the specified time interval.
     * 
     * Must be called from UI thread.
     * 
     * @param delay
     *            the delay quantity
     * @param delayUnit
     *            the delay unit
     */
    public static void delay(long delay, TimeUnit delayUnit) {
        Display display = Display.getCurrent();
        if (display == null)
            throw new IllegalArgumentException("Must be called from UI thread");

        long endTimeMillis = System.currentTimeMillis()
                + delayUnit.toMillis(delay);

        while (System.currentTimeMillis() < endTimeMillis) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.update();
    }

    /**
     * Process UI input until either condition.call returns true or the timeout
     * expires. If a timeout occurs, an unchecked exception will be thrown.
     * 
     * Must be called from UI thread.
     * 
     * @param timeout
     *            the timeout quantity
     * @param timeoutUnit
     *            the timeout unit
     * @param condition
     *            condition controlling when method should return
     * @throws Exception
     *             if condition throws an exception
     */
    public static void conditionalDelay(long timeout, TimeUnit timeoutUnit,
            Callable<Boolean> condition) throws Exception {
        Display display = Display.getCurrent();
        if (display == null)
            throw new IllegalArgumentException("Must be called from UI thread");

        long endTimeMillis = System.currentTimeMillis()
                + timeoutUnit.toMillis(timeout);

        while (!condition.call()) {
            if (System.currentTimeMillis() > endTimeMillis)
                throw new RuntimeException("Timeout");
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.update();
    }

    /**
     * A version of conditionalDelay that re-throws checked exceptions as
     * unchecked ones. This can be used if the caller knows their condition will
     * not throw an exception, or if they just want to let exceptions bubble up.
     * 
     * Must be called from UI thread.
     * 
     * @param timeout
     *            the timeout quantity
     * @param timeoutUnit
     *            the timeout unit
     * @param condition
     *            condition controlling when method should return
     */
    public static void conditionalDelayUnchecked(long timeout,
            TimeUnit timeoutUnit, Callable<Boolean> condition) {
        try {
            conditionalDelay(timeout, timeoutUnit, condition);
        } catch (Exception e) {
            if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new RuntimeException(e);
        }
    }

    /**
     * Wait until all Eclipse jobs complete or the timeout expires. If a timeout
     * occurs, an unchecked exception will be thrown.
     * 
     * Must be called from UI thread.
     * 
     * @param timeout
     *            the timeout quantity
     * @param timeoutUnit
     *            the timeout unit
     */
    public static void waitForJobs(long timeout, TimeUnit timeoutUnit) {
        conditionalDelayUnchecked(timeout, timeoutUnit,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return Job.getJobManager().currentJob() == null;
                    }
                });
    }

    /**
     * Tests that a text widget is read only, but still enabled (for copy).
     * 
     * @param text
     *            the widget
     * @param value
     *            the value that is expected in the text box
     */
    public static void testReadOnlyText(SWTBotText text, String value) {
        assertThat(text.getText(), is(value));
        text.typeText("abc");
        assertThat(text.getText(), is(value));
        assertThat(text.isEnabled(), is(true));
    }

    /**
     * Tests that a button does not exist.
     * 
     * @param text
     *            the button text
     */
    @SuppressWarnings("unchecked")
    public static void assertButtonDoesNotExist(String text) {
        assertThat(new SWTBot().getFinder().findControls(
                allOf(widgetOfType(Button.class), withMnemonic(text),
                        withStyle(SWT.PUSH, "SWT.PUSH"))).size(), is(0));
    }

    /**
     * Returns an array of {@link MenuState} reflecting the given widget's
     * context menu state.
     * 
     * @param widget
     *            the widget handle
     * @return the menu items
     * @throws Exception
     *             if something goes wrong
     */
    public static Map<String, MenuState> getMenuItems(
            final AbstractSWTBot<? extends Control> widget) throws Exception {
        return AbstractUIRunner.syncCall(new Callable<Map<String, MenuState>>() {
            @Override
            public Map<String, MenuState> call() {
                Map<String, MenuState> result = Maps.newHashMap();
                Menu bar = widget.widget.getMenu();
                if (bar != null) {
                    bar.notifyListeners(SWT.Show, new Event());
                    MenuItem[] items = bar.getItems();
                    for (MenuItem menuItem : items) {
                        if ((menuItem.getStyle() & SWT.SEPARATOR) == 0) {
                            result.put(menuItem.getText(), new MenuState(menuItem));
                        }
                    }
                    bar.notifyListeners(SWT.Hide, new Event());
                }
                return ImmutableMap.copyOf(result);
            }
        });
    }
}

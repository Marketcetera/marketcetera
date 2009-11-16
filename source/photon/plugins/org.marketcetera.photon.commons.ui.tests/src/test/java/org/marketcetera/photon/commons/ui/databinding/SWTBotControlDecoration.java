package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.marketcetera.photon.test.AbstractUIRunner;

/* $License$ */

/**
 * Wrapper for a {@link ControlDecoration} for testing.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SWTBotControlDecoration {

    private final Display mDisplay;
    private final ControlDecoration mDecoration;
    private final Field mVisibleField;

    /**
     * Constructor.
     * 
     * @param control
     *            the SWT Bot wrapper for the control being decorated
     */
    public SWTBotControlDecoration(
            final AbstractSWTBot<? extends Control> control) {
        mDisplay = control.display;
        mDecoration = (ControlDecoration) UIThreadRunnable.syncExec(mDisplay,
                new Result<Object>() {
                    @Override
                    public Object run() {
                        return control.widget.getData("CONTROL_DECORATION");
                    }
                });
        if (mDecoration == null) {
            throw new AssertionError("no decoration found");
        }
        try {
            mVisibleField = ControlDecoration.class.getDeclaredField("visible");
            mVisibleField.setAccessible(true);
        } catch (Exception e) {
            throw new AssertionError(
                    "failed to find/access 'visible' field on ControlDecoration");
        }
    }

    /**
     * Return the ControlDecoration's description.
     * 
     * @return the ControlDecoration's description
     * @throws Throwable
     *             propagated from the UI thread
     */
    public String getDescriptionText() throws Exception {
        return AbstractUIRunner.syncCall(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return mDecoration.getDescriptionText();
            }
        });
    }

    /**
     * Return the ControlDecoration's image.
     * 
     * @return the ControlDecoration's image
     * @throws Throwable
     *             propagated from the UI thread
     */
    public Image getImage() throws Exception {
        return AbstractUIRunner.syncCall(new Callable<Image>() {
            @Override
            public Image call() throws Exception {
                return mDecoration.getImage();
            }
        });
    }

    /**
     * Return whether the ControlDecoration is visible
     * 
     * @return if the ControlDecoration is visible
     * @throws Throwable
     *             propagated from the UI thread
     */
    public boolean isVisible() throws Exception {
        return AbstractUIRunner.syncCall(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return mVisibleField.getBoolean(mDecoration);
            }
        });
    }

    /**
     * Assert the "required" decoration is showing with the provided message.
     * 
     * @param message
     *            the expected message
     */
    public void assertRequired(String message) throws Exception {
        assertVisible(FieldDecorationRegistry.DEC_REQUIRED, message);
    }

    /**
     * Assert no image is shown but the decoration is still visible.
     * 
     * @param message
     *            the expected message
     */
    public void assertNoImage(String message) throws Exception {
        assertThat(isVisible(), is(true));
        assertThat(getImage(), nullValue());
        assertThat(getDescriptionText(), is(message));
    }

    /**
     * Assert the "error" decoration is showing with the provided message.
     * 
     * @param message
     *            the expected message
     */
    public void assertError(String message) throws Exception {
        assertVisible(FieldDecorationRegistry.DEC_ERROR, message);
    }

    /**
     * Assert the decoration is hidden.
     * 
     * @param message
     *            the expected message
     */
    public void assertHidden() throws Exception {
        assertThat(isVisible(), is(false));
    }

    private void assertVisible(String key, String message) throws Exception {
        assertThat(isVisible(), is(true));
        assertThat(getImage(), is(getDecorationImage(key)));
        assertThat(getDescriptionText(), is(message));
    }

    private Image getDecorationImage(final String key) throws Exception {
        return AbstractUIRunner.syncCall(new Callable<Image>() {
            @Override
            public Image call() throws Exception {
                return FieldDecorationRegistry.getDefault().getFieldDecoration(
                        key).getImage();
            }
        });
    }
}
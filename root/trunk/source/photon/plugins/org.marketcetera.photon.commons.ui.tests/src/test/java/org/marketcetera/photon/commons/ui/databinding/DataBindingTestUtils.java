package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Utilities for testing data binding code, particularly code using this support
 * classes in this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class DataBindingTestUtils extends PhotonTestBase {

    public static void testRequiredText(SWTBot bot, EObject object,
            EStructuralFeature feature, String description, String tooltip,
            String testText) throws Exception {
        testRequiredControl(bot, bot.textWithLabel(description + ":"), object,
                feature, description, tooltip, testText);
    }

    public static void testOptionalText(SWTBot bot, EObject object,
            EStructuralFeature feature, String description, String tooltip,
            String testText) throws Exception {
        testOptionalControl(bot, bot.textWithLabel(description + ":"), object,
                feature, description, tooltip, "abc");
    }

    public static void testRequiredControl(SWTBot bot,
            AbstractSWTBot<? extends Control> control, EObject object,
            EStructuralFeature feature, String description, String tooltip,
            String testText) throws Exception {
        assertThat(bot.label(description + ":").getToolTipText(), is(tooltip));
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(
                control);
        String message = RequiredFieldSupportTest
                .getRequiredValueMessage(description);
        decoration.assertRequired(message);
        assertThat(control.getText(), is(""));
        setText(control, testText);
        assertThat(String.valueOf(eGet(object, feature)), is(testText));
        decoration.assertHidden();
        setText(control, "");
        assertThat(eGet(object, feature), nullValue());
        decoration.assertRequired(message);
    }

    public static void testOptionalControl(SWTBot bot,
            AbstractSWTBot<? extends Control> control, EObject object,
            EStructuralFeature feature, String description, String tooltip,
            String testText) throws Exception {
        assertThat(bot.label(description + ":").getToolTipText(), is(tooltip));
        assertThat(control.getText(), is(""));
        setText(control, testText);
        assertThat(String.valueOf(eGet(object, feature)), is(testText));
        setText(control, "");
        assertThat(eGet(object, feature), nullValue());
    }

    public static void setText(final AbstractSWTBot<? extends Control> control,
            final String text) throws Exception {
        /*
         * AbstractSWTBot does not have setText(), but most subclasses have it.
         * This makes testing easier, but will fail if called for a control that
         * doesn't support it.
         */
        Method method = control.getClass().getMethod("setText",
                new Class[] { String.class });
        method.invoke(control, new Object[] { text });
    }

    public static Object eGet(final EObject object,
            final EStructuralFeature feature) throws Exception {
        return AbstractUIRunner.syncCall(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return object.eGet(feature);
            }
        });
    }

    private DataBindingTestUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}

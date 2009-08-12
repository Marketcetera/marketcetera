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

/* $License$ */

/**
 * Utilities for testing data binding code, particularly code using this support
 * classes in this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class DataBindingTestUtils {

    public static void testRequiredText(SWTBot bot, EObject object,
            EStructuralFeature feature, String description, String tooltip)
            throws Exception {
        testRequiredControl(bot, bot.textWithLabel(description + ":"), object,
                feature, description, tooltip);
    }

    public static void testOptionalText(SWTBot bot, EObject object,
            EStructuralFeature feature, String description, String tooltip)
            throws Exception {
        testOptionalControl(bot, bot.textWithLabel(description + ":"), object,
                feature, description, tooltip);
    }

    public static void testRequiredControl(SWTBot bot,
            AbstractSWTBot<? extends Control> control, EObject object,
            EStructuralFeature feature, String description, String tooltip)
            throws Exception {
        assertThat(bot.label(description + ":").getToolTipText(), is(tooltip));
        SWTBotControlDecoration decoration = new SWTBotControlDecoration(
                control);
        String message = RequiredFieldSupportTest
                .getRequiredValueMessage(description);
        decoration.assertRequired(message);
        assertThat(control.getText(), is(""));
        setText(control, "abc");
        assertThat(eGet(object, feature), is((Object) "abc"));
        decoration.assertHidden();
        setText(control, "");
        assertThat(eGet(object, feature), nullValue());
        decoration.assertError(message);
    }

    public static void testOptionalControl(SWTBot bot,
            AbstractSWTBot<? extends Control> control, EObject object,
            EStructuralFeature feature, String description, String tooltip)
            throws Exception {
        assertThat(bot.label(description + ":").getToolTipText(), is(tooltip));
        assertThat(control.getText(), is(""));
        setText(control, "abc");
        assertThat(eGet(object, feature), is((Object) "abc"));
        setText(control, "");
        assertThat(eGet(object, feature), nullValue());
    }

    public static void setText(final AbstractSWTBot<? extends Control> control,
            final String text) throws Exception {
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

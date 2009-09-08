package org.marketcetera.photon.commons.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.EnumSet;
import java.util.Map;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.ColorManager.IColorDescriptorProvider;

/* $License$ */

/**
 * Base test class for enum based color management classes.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class EnumColorManagerTestBase<E extends Enum<E>> extends
        ColorManagerTestBase {

    private final Class<E> mClass;
    private final Map<E, ColorDescriptor> mExpectedDescriptors;

    public EnumColorManagerTestBase(Class<E> clazz,
            Map<E, ColorDescriptor> expectedDescriptors) {
        mClass = clazz;
        mExpectedDescriptors = expectedDescriptors;
    }

    protected abstract Color getColor(E item);

    @Override
    protected Color get() {
        return getColor(EnumSet.allOf(mClass).iterator().next());
    }

    @Test
    public void testDescriptors() {
        for (E item : EnumSet.allOf(mClass)) {
            assertThat(((IColorDescriptorProvider) item).getDescriptor(),
                    is(mExpectedDescriptors.get(item)));
        }
    }

    @Test
    public void testInitColors() {
        Display d = new Display();
        assertAllNull();
        init();
        assertAllNotNull();
        dispose();
        assertAllNull();
        init();
        assertAllNotNull();
        d.dispose();
        assertAllNull();
    }

    private void assertAllNotNull() {
        for (E item : EnumSet.allOf(mClass)) {
            assertThat(getColor(item), not(nullValue(Color.class)));
        }
    }

    private void assertAllNull() {
        for (E item : EnumSet.allOf(mClass)) {
            assertThat(getColor(item), nullValue());
        }
    }

}

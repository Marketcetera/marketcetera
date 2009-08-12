package org.marketcetera.photon.test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;

/* $License$ */

/**
 * SWT/JFace related asserts.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class JFaceAsserts {

    public static void assertImage(Image actual, ImageDescriptor expected) {
        Image missingImage = JFaceResources.getResources().createImage(
                ImageDescriptor.getMissingImageDescriptor());
        Image expectedImage = JFaceResources.getResources().createImage(
                expected);
        assertThat(expectedImage, not(is(missingImage)));
        assertThat(actual, is(expectedImage));
        JFaceResources.getResources().destroyImage(expected);
        JFaceResources.getResources().destroyImage(
                ImageDescriptor.getMissingImageDescriptor());
    }

    private JFaceAsserts() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }

}

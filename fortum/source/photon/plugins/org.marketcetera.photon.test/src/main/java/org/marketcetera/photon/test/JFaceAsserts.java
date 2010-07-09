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
 * @since 2.0.0
 */
public class JFaceAsserts {

    /**
     * Asserts that the provided "actual" image is equal to the image described
     * by the "expected" image descriptor.
     * 
     * @param actual
     *            the actual image
     * @param expected
     *            the expected image descriptor
     */
    public static void assertImage(Image actual, ImageDescriptor expected) {
        Image expectedImage = createImageAndAssertNotMissing(expected);
        try {
            assertThat(actual, is(expectedImage));
        } finally {
            JFaceResources.getResources().destroyImage(expected);
        }
    }

    /**
     * Creates an image and asserts that it is not the JFace missing image.
     * 
     * NOTE: the caller of this method is responsible for disposing the image
     * using {@code JFaceResources.getResources().destroyImage(image)} when no
     * longer needed.
     * 
     * @param descriptor
     *            the descriptor to create
     * @return the Image, must be disposed by the caller
     */
    public static Image createImageAndAssertNotMissing(
            ImageDescriptor descriptor) {
        Image missingImage = JFaceResources.getResources().createImage(
                ImageDescriptor.getMissingImageDescriptor());
        try {
            Image image = JFaceResources.getResources().createImage(descriptor);
            assertThat(image, not(is(missingImage)));
            return image;
        } finally {
            JFaceResources.getResources().destroyImage(
                    ImageDescriptor.getMissingImageDescriptor());
        }
    }

    private JFaceAsserts() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }

}

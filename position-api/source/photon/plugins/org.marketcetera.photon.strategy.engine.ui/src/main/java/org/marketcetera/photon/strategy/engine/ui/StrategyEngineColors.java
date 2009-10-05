package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.marketcetera.photon.commons.ui.ColorManager;
import org.marketcetera.photon.commons.ui.ColorManager.IColorDescriptorProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages the colors used by the strategy engine UI.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyEngineColors {

    private static final ColorManager sColorManager = ColorManager
            .createForEnum(StrategyEngineColor.class);

    /**
     * Initializes the {@link StrategyEngineColor} colors for the current
     * thread.
     * 
     * @see ColorManager#initColors()
     */
    public static void init() {
        sColorManager.initColors();
    }

    /**
     * Disposes the {@link StrategyEngineColor} colors for the current thread.
     * 
     * @see ColorManager#disposeColors()
     */
    public static void dispose() {
        sColorManager.disposeColors();
    }

    /**
     * Colors used by the strategy engine UI.
     */
    @ClassVersion("$Id$")
    public enum StrategyEngineColor implements IColorDescriptorProvider {

        /**
         * Foreground color for labels representing disconnected strategy
         * engines.
         */
        ENGINE_DISCONNECTED(ColorDescriptor.createFrom(new RGB(145, 145, 145))),

        /**
         * Foreground color for labels representing stopped strategies.
         */
        STRATEGY_STOPPED(ColorDescriptor.createFrom(new RGB(145, 145, 145)));

        private final ColorDescriptor mDescriptor;

        private StrategyEngineColor(ColorDescriptor descriptor) {
            mDescriptor = descriptor;
        }

        @Override
        public ColorDescriptor getDescriptor() {
            return mDescriptor;
        }

        /**
         * Returns the Color represented by this instance, or null if
         * {@link StrategyEngineColors} has not been initialized.
         * 
         * @return the color for this instance
         */
        public Color getColor() {
            return sColorManager.getColor(mDescriptor);
        }
    }
}

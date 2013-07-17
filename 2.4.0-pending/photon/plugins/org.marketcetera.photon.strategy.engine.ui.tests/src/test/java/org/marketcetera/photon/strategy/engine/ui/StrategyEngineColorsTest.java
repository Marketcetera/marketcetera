package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.marketcetera.photon.commons.ui.EnumColorManagerTestBase;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineColors.StrategyEngineColor;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link StrategyEngineColors}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class StrategyEngineColorsTest extends
        EnumColorManagerTestBase<StrategyEngineColor> {

    public StrategyEngineColorsTest() {
        super(StrategyEngineColor.class, ImmutableMap.of(
                StrategyEngineColor.ENGINE_DISCONNECTED, ColorDescriptor
                        .createFrom(new RGB(145, 145, 145)),
                StrategyEngineColor.STRATEGY_STOPPED, ColorDescriptor
                        .createFrom(new RGB(145, 145, 145))));
    }

    @Override
    protected void init() {
        StrategyEngineColors.init();
    }

    @Override
    protected void dispose() {
        StrategyEngineColors.dispose();
    }

    @Override
    protected Color getColor(StrategyEngineColor item) {
        return item.getColor();
    }

}

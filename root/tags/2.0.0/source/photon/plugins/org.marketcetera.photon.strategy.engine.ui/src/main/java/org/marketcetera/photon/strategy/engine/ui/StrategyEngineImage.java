package org.marketcetera.photon.strategy.engine.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.marketcetera.photon.commons.ui.EclipseImages;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Images used in the strategy engine UI.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum StrategyEngineImage {
    
    /**
     * Image for a strategy engine
     */
    ENGINE_OBJ(EclipseImages.OBJ, "engine.gif"), //$NON-NLS-1$
    
    /**
     * Image for a connected strategy engine
     */
    ENGINE_CONNECTED_OBJ(EclipseImages.OBJ, "engine_connected.gif"), //$NON-NLS-1$
    
    /**
     * Image for a disconnected strategy engine
     */
    ENGINE_DISCONNECTED_OBJ(EclipseImages.OBJ, "engine_disconnected.gif"), //$NON-NLS-1$
    
    /**
     * Image for a strategy
     */
    STRATEGY_OBJ(EclipseImages.OBJ, "strategy.gif"), //$NON-NLS-1$
    
    /**
     * Image for a connected strategy engine
     */
    STRATEGY_RUNNING_OBJ(EclipseImages.OBJ, "strategy_running.gif"), //$NON-NLS-1$
    
    /**
     * Image for a disconnected strategy engine
     */
    STRATEGY_STOPPED_OBJ(EclipseImages.OBJ, "strategy_stopped.gif"), //$NON-NLS-1$
    
    /**
     * Deploy strategy wizard banner
     */
    DEPLOY_WIZARD_WIZBAN(EclipseImages.WIZBAN, "deploy_wiz.png"); //$NON-NLS-1$

    /**
     * Returns the image descriptor that can be used to obtain the image.
     * 
     * @return the image descriptor
     */
    public ImageDescriptor getImageDescriptor() {
        return mImageDescriptor;
    }

    private final ImageDescriptor mImageDescriptor;

    private StrategyEngineImage(EclipseImages type, String name) {
        mImageDescriptor = type.getImageDescriptor(StrategyEngineUI.PLUGIN_ID, name);
    }
}



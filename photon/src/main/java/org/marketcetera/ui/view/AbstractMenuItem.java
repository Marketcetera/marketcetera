package org.marketcetera.ui.view;

import org.marketcetera.ui.service.UiMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.image.Image;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractMenuItem
        implements MenuContent
{
    protected Image getIcon(String inName)
    {
        if(icon == null) {
            icon = new Image(inName);
        }
        return icon;
    }
    private Image icon;
    /**
     * web message service value
     */
    @Autowired
    protected UiMessageService webMessageService;
}

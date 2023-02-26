package org.marketcetera.ui.view;

import org.marketcetera.ui.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    protected Node getIcon(String inName)
    {
        if(iconView == null) {
            icon = new Image(inName);
            iconView = new ImageView();
            iconView.setImage(icon);
        }
        return iconView;
    }
    private Image icon;
    private ImageView iconView;
    /**
     * web message service value
     */
    @Autowired
    protected WebMessageService webMessageService;
}

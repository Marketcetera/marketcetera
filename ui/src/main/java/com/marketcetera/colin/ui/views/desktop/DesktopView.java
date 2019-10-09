package com.marketcetera.colin.ui.views.desktop;

import org.marketcetera.fix.ActiveFixSession;

import com.marketcetera.colin.ui.MainView;
import com.marketcetera.colin.ui.utils.WebUiConst;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
//@Tag("desktop-view")
@Route(value = WebUiConst.PAGE_DESKTOP, layout = MainView.class)
@RouteAlias(value = WebUiConst.PAGE_ROOT, layout = MainView.class)
@PageTitle(WebUiConst.TITLE_DESKTOP)
public class DesktopView
        extends VerticalLayout
{
    public DesktopView()
    {
        grid = new Grid<>();
        add(grid);
        
    }
    
    private Grid<ActiveFixSession> grid;
    private static final long serialVersionUID = 7323613882125927022L;
}

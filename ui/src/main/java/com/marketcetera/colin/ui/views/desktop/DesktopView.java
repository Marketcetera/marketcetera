package com.marketcetera.colin.ui.views.desktop;

import org.marketcetera.fix.ActiveFixSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.colin.app.security.CurrentMetcUser;
import com.marketcetera.colin.ui.MainView;
import com.marketcetera.colin.ui.utils.WebUiConst;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
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
        userLabel = new Label();
        userLabel.setText(String.valueOf(currentUser.getUser()));
        add(userLabel);
    }
    @Autowired
    private CurrentMetcUser currentUser;
    private Label userLabel;
    private Grid<ActiveFixSession> grid;
    private static final long serialVersionUID = 7323613882125927022L;
}

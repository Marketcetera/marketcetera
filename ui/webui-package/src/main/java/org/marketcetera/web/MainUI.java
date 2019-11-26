package org.marketcetera.web;

import org.marketcetera.core.CloseableLock;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.view.ApplicationMenu;
import org.marketcetera.web.view.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

/* $License$ */

/**
 * Main UI controller.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Push
@SpringUI
@Theme("mytheme")
@PreserveOnRefresh
@Title("Marketcetera Automated Trading Platform")
public class MainUI
        extends UI
{
    /* (non-Javadoc)
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest inRequest)
    {
        setSizeFull();
        Layout rootLayout = new CssLayout();
        rootLayout.setId("rootLayout");
        rootLayout.setSizeFull();
        final Layout menuLayout = new CssLayout();
        menuLayout.setId("menuLayout");
        setContent(rootLayout);
        Layout contentLayout = new CssLayout();
        contentLayout.setId("contentLayout");
        contentLayout.setSizeFull();
        final Layout headerLayout = new CssLayout();
        headerLayout.setId("headerLayout");
        headerLayout.setStyleName(ValoTheme.MENU_TITLE);
        Label titleLabel = new Label();
        titleLabel.setCaption("<h1>Marketcetera Automated Trading Platform</h1>");
        titleLabel.setCaptionAsHtml(true);
        headerLayout.addComponent(titleLabel);
        rootLayout.addComponents(headerLayout,
                                 menuLayout,
                                 contentLayout);
        navigator.init(this,
                       contentLayout);
        menuLayout.setVisible(false);
        // We use a view change handler to ensure the user is always redirected
        // to the login view if the user is not logged in.
        getNavigator().addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent inEvent)
            {
                // Check if a user has logged in
                boolean isLoggedIn = SessionUser.getCurrentUser() != null;
                boolean isLoginView = inEvent.getNewView() instanceof LoginView;
                if(!isLoggedIn && !isLoginView) {
                    // Redirect to login view always if a user has not yet logged in
                    menuLayout.setVisible(false);
                    getNavigator().navigateTo(LoginView.NAME);
                    return false;
                } else if (isLoggedIn && isLoginView) {
                    // If someone tries to access to login view while logged in, then cancel
                    return false;
                }
                return true;
            }
            @Override
            public void afterViewChange(ViewChangeEvent inEvent)
            {
                boolean isLoggedIn = SessionUser.getCurrentUser() != null;
                if(isLoggedIn) {
                    try(CloseableLock menuLock = CloseableLock.create(VaadinSession.getCurrent().getLockInstance())) {
                        menuLock.lock();
                        ApplicationMenu applicationMenu = VaadinSession.getCurrent().getAttribute(ApplicationMenu.class);
                        if(applicationMenu == null) {
                            SLF4JLoggerProxy.debug(this,
                                                   "Session is now logged in, building application menu");
                            applicationMenu = applicationContext.getBean(ApplicationMenu.class);
                            menuLayout.addComponent(applicationMenu.getMenu());
                            VaadinSession.getCurrent().setAttribute(ApplicationMenu.class,
                                                                    applicationMenu);
                        }
                        applicationMenu.refreshMenuPermissions();
                    }
                    menuLayout.setVisible(true);
                }
            }
            private static final long serialVersionUID = 7868495691502830440L;
        });
    }
    /**
     * provides access to the application configuration
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * navigator value
     */
    @Autowired
    private SpringNavigator navigator;
    private static final long serialVersionUID = -56010080786096996L;
}

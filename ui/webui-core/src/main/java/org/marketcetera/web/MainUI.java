package org.marketcetera.web;

import java.util.Map;

import org.marketcetera.core.CloseableLock;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.StyleService;
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
import com.vaadin.ui.VerticalLayout;
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
        implements BrokerStatusLayoutProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.BrokerStatusLayoutProvider#getBrokerStatusLayout()
     */
    @Override
    public Layout getBrokerStatusLayout()
    {
        return brokerSessionLayout;
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest inRequest)
    {
        setId(getClass().getSimpleName() + ".mainUi");
        styleService.addStyle(this);
        setSizeFull();
        rootLayout = new CssLayout();
        rootLayout.setId(getClass().getSimpleName() + ".rootLayout");
        rootLayout.setSizeFull();
        styleService.addStyle(rootLayout);
        menuLayout = new CssLayout();
        menuLayout.setId(getClass().getSimpleName() + ".menuLayout");
        styleService.addStyle(menuLayout);
        setContent(rootLayout);
        desktopLayout = new VerticalLayout();
        desktopLayout.setId(getClass().getSimpleName() + ".desktopLayout");
        desktopLayout.setWidth("100%");
        styleService.addStyle(desktopLayout);
        headerLayout = new CssLayout();
        headerLayout.setId(getClass().getSimpleName() + ".headerLayout");
        headerLayout.addStyleName(ValoTheme.MENU_TITLE);
        styleService.addStyle(headerLayout);
        footerLayout = new CssLayout();
        footerLayout.setId(getClass().getSimpleName() + ".footerLayout");
        footerLayout.setWidth("100%");
        footerLayout.setHeightUndefined();
        footerLayout.addStyleName("layout-with-top-border");
        styleService.addStyle(footerLayout);
        userLabel = new Label();
        userLabel.setId(getClass().getSimpleName() + ".userLabel");
        userLabel.setWidthUndefined();
        userLabel.addStyleName("widget-margin");
        styleService.addStyle(userLabel);
        timeLabel = new Label();
        timeLabel.setId(getClass().getSimpleName() + ".timeLabel");
        timeLabel.setWidthUndefined();
        timeLabel.addStyleName("widget-margin");
        styleService.addStyle(timeLabel);
        brokerSessionLayout = new CssLayout();
        brokerSessionLayout.setId(getClass().getSimpleName() + ".brokerSessionLayout");
        brokerSessionLayout.setWidth("85%");
        brokerSessionLayout.setHeight("100%");
        styleService.addStyle(brokerSessionLayout);
        footerLayout.addComponents(userLabel,
                                   timeLabel,
                                   brokerSessionLayout);
        titleLabel = new Label();
        titleLabel.setId(getClass().getSimpleName() + ".titleLabel");
        titleLabel.setCaption("<h1>Marketcetera Automated Trading Platform</h1>");
        titleLabel.setCaptionAsHtml(true);
        styleService.addStyle(titleLabel);
        headerLayout.addComponent(titleLabel);
        rootLayout.addComponents(headerLayout,
                                 menuLayout,
                                 desktopLayout,
                                 footerLayout);
        navigator.init(this,
                       desktopLayout);
        menuLayout.setVisible(false);
        footerLayout.setVisible(false);
        brokerSessionLayout.setVisible(false);
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
                    footerLayout.setVisible(false);
                    brokerSessionLayout.setVisible(false);
                    userLabel.setValue("");
                    desktopLayout.setHeight("100%");
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
                SessionUser sessionUser = SessionUser.getCurrentUser();
                boolean isLoggedIn = sessionUser != null;
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
                    userLabel.setValue(sessionUser.getUsername());
                    sessionUser.establishTimeUpdate(timeLabel::setValue);
                    desktopLayout.setHeight("85%");
                    menuLayout.setVisible(true);
                    footerLayout.setVisible(true);
                    brokerSessionLayout.setVisible(true);
                    Map<String,WidgetProvider> widgetProviders = applicationContext.getBeansOfType(WidgetProvider.class,
                                                                                                   true,
                                                                                                   true);
                    System.out.println("COCO: found widget providers: " + widgetProviders);
                }
            }
            private static final long serialVersionUID = 7868495691502830440L;
        });
    }
    /**
     * holds broker session glyphs in the footer
     */
    private CssLayout brokerSessionLayout;
    /**
     * holds the logged-in user name
     */
    private Label userLabel;
    /**
     * time-of-day label
     */
    private Label timeLabel;
    /**
     * layout for the page footer
     */
    private CssLayout footerLayout;
    /**
     * layout for the entire page, including header, menu, workspace desktop, and footer
     */
    private Layout rootLayout;
    /**
     * layout for the application menu
     */
    private Layout menuLayout;
    /**
     * layout for the main desktop workspace
     */
    private Layout desktopLayout;
    /**
     * layout for the page header
     */
    private Layout headerLayout;
    /**
     * application title label
     */
    private Label titleLabel;
    /**
     * provides access to style services
     */
    @Autowired
    private StyleService styleService;
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

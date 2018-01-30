package org.marketcetera.webui;

import org.marketcetera.core.CloseableLock;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.webui.view.ApplicationMenu;
import org.marketcetera.webui.view.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Push
@SpringUI
@Theme(ValoTheme.THEME_NAME)
@Title("Marketcetera Automated Trading Platform")
public class MainUI
        extends UI
{
    /**
     * 
     *
     *
     * @param inArgs
     */
   public static void main(String[] inArgs)
   {
       SpringApplication.run(MainUI.class,
                             inArgs);
   }
   /* (non-Javadoc)
    * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
    */
   @Override
   protected void init(VaadinRequest inRequest)
   {
       //           setContent(new Label(myService.sayHi()));
       final VerticalLayout headerLayout = new VerticalLayout(new Label("HEADER"));
       final VerticalLayout footerLayout = new VerticalLayout(new Label("FOOTER"));

       final VerticalLayout contentLayout = new VerticalLayout();
       // XXX: place the center layout into a panel, which allows scrollbars
       final Panel contentPanel = new Panel(contentLayout);
       contentPanel.setSizeFull();
       // XXX: add the panel instead of the layout
       final VerticalLayout mainLayout = new VerticalLayout(headerLayout, contentPanel, footerLayout);
       mainLayout.setSizeFull();
       mainLayout.setExpandRatio(contentPanel, 1);
       setContent(mainLayout);
       Navigator navigator = new Navigator(this,
                                           contentLayout);
       navigator.addProvider(viewProvider);
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
                   headerLayout.setVisible(false);
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
                       if(applicationMenu == null) {
                           SLF4JLoggerProxy.debug(this,
                                                  "Session is now logged in, building application menu");
                           applicationMenu = new ApplicationMenu();
                           headerLayout.addComponent(applicationMenu.getMenu());
                       }
                   }
                   headerLayout.setVisible(true);
               }
           }
           private static final long serialVersionUID = 7868495691502830440L;
       });
   }
   /**
    * top-level application menu, may be <code>null</code> until the user logs in and tries to access a view
    */
   private ApplicationMenu applicationMenu;
   /**
    * provides views defined to Spring
    */
   @Autowired
   private SpringViewProvider viewProvider;
   private static final long serialVersionUID = 5112718563577745283L;
}

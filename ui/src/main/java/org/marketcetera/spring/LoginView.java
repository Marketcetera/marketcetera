package org.marketcetera.spring;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView
        extends VerticalLayout
{
    public LoginView()
    {
        login.setAction("login"); // 
        login.setOpened(true); // 
        login.setTitle("Spring Secured Vaadin");
        login.setDescription("Login Overlay Example");
        getElement().appendChild(login.getElement()); // 
    }
    public static final String ROUTE = "login";
    private LoginOverlay login = new LoginOverlay(); // 
    private static final long serialVersionUID = -1304331418579631350L;
}

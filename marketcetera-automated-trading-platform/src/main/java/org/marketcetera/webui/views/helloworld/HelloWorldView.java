package org.marketcetera.webui.views.helloworld;

import javax.annotation.security.PermitAll;

import org.marketcetera.webui.security.AuthenticatedUser;
import org.marketcetera.webui.views.MainLayout;
import org.marketcetera.webui.views.login.LoginView;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PermitAll
@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HelloWorldView
        extends HorizontalLayout
        implements BeforeEnterObserver
{
    private TextField name;
    private Button sayHello;

    public HelloWorldView(AuthenticatedUser inAuthenticatedUser)
    {
        authenticatedUser = inAuthenticatedUser;
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        add(name, sayHello);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event)
    {
        if(authenticatedUser.get().isEmpty()) {
            event.forwardTo(LoginView.class);
        }
    }
    private final AuthenticatedUser authenticatedUser;
    private static final long serialVersionUID = 2721922659936438309L;
}

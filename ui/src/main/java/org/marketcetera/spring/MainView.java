package org.marketcetera.spring;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(MainView.Route)
public class MainView
        extends VerticalLayout
{
    public MainView(@Autowired MessageBean bean) {
        Button button = new Button("Click me",
                e -> Notification.show(bean.getMessage()));
        add(button);
    }
    public static final String Route = "MAIN";
    private static final long serialVersionUID = -6643881628008151766L;
}

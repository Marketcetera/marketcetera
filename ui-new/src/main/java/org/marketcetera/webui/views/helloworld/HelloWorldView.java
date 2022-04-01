package org.marketcetera.webui.views.helloworld;

import java.util.Arrays;

import javax.annotation.security.PermitAll;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.webui.views.MainLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PermitAll
@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HelloWorldView
        extends HorizontalLayout
{
    private TextField name;
    private Button sayHello;

    public HelloWorldView() {
        addClassName("hello-world-view");
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        add(name, sayHello);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
        sayHello.addClickListener(e -> {
            String windowId = PlatformServices.generateId();
            Dialog newWindow = new Dialog();
            newWindow.setCloseOnEsc(true);
            newWindow.setCloseOnOutsideClick(false);
            newWindow.setDraggable(true);
            newWindow.setEnabled(true);
            newWindow.setHeight("25%");
            newWindow.setModal(false);
            newWindow.setResizable(true);
            newWindow.setWidth("50%");
            newWindow.addResizeListener(inEvent -> {
                System.out.println("COCO: " + windowId + " " + inEvent.getWidth() + " x " + inEvent.getHeight());
                getPosition(new SerializableConsumer<Position>() {
                    private static final long serialVersionUID = -5377037155755143051L;
                    @Override
                    public void accept(Position inArg0)
                    {
                        System.out.println("COCO: " + windowId + " x=" + inArg0.x + " y=" + inArg0.y);
                    }}
                );
            });
            System.out.println("COCO: adding new window: " + windowId);
            HelloWorldView.this.add(newWindow);
            newWindow.open();
        });
    }
    public void getPosition(SerializableConsumer<Position> consumer)
    {
        System.out.println("COCO: entering getPosition");
        getElement().executeJs("return [this.$.overlay.$.overlay.style['top'], this.$.overlay.$.overlay.style['left']]").then(String.class, s -> {
            String[] split = StringUtils.split(s, ',');
            System.out.println("COCO: split is " + Arrays.toString(split));
            if(split.length == 2 && split[0] != null && split[1] != null) {
                Position position = new Position(split[0], split[1]);
                consumer.accept(position);
            } else {
                System.out.println("COCO: didn't work");
            }
        });
        System.out.println("COCO: exiting getPosition");
    }
    private static class Position
    {
        private Position(String inX,
                         String inY)
        {
            x = Integer.parseInt(inX);
            y = Integer.parseInt(inY);
        }
        private final int x;
        private final int y;
    }
    private static final long serialVersionUID = -1057501741617924329L;
}

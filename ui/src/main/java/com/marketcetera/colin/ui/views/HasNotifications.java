package com.marketcetera.colin.ui.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.marketcetera.colin.ui.utils.WebUiConst;

/**
 * Interface for views showing notifications to users
 *
 */
public interface HasNotifications extends HasElement {

	default void showNotification(String message) {
		showNotification(message, false);
	}

	default void showNotification(String message, boolean persistent) {
		if (persistent) {
			Button close = new Button("Close");
			close.getElement().setAttribute("theme", "tertiary small error");
			Notification notification = new Notification(new Text(message), close);
			notification.setPosition(Position.BOTTOM_START);
			notification.setDuration(0);
			close.addClickListener(event -> notification.close());
			notification.open();
		} else {
			Notification.show(message, WebUiConst.NOTIFICATION_DURATION, Position.BOTTOM_STRETCH);
		}
	}
}

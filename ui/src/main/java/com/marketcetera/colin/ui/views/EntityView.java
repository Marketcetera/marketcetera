package com.marketcetera.colin.ui.views;

import com.vaadin.flow.data.binder.ValidationException;

/**
 * A master / detail view for entities of the type <code>T</code>. The view
 * has a list of entities (the 'master' part) and a dialog to show a single
 * entity (the 'detail' part). The dialog has two modes: a view mode and an
 * edit mode.
 * <p>
 * The view can also show notifications, error messages, and confirmation
 * requests.
 *
 * @param <T> the entity type
 */
public interface EntityView<T> extends HasConfirmation, HasNotifications {

	/**
	 * Shows an error notification with a given text.
	 *
	 * @param message a user-friendly error message
	 * @param isPersistent if <code>true</code> the message requires a user
	 *                     action to disappear (if <code>false</code> it
	 *                     disappears automatically after some time)
	 */
	default void showError(String message, boolean isPersistent) {
		showNotification(message, isPersistent);
	}

	/**
	 * Returns the current dirty state of the entity dialog.
	 *
	 * @return <code>true</code> if the entity dialog is open in the 'edit'
	 * mode and has unsaved changes
	 */
	boolean isDirty();

	/**
	 * Remove the reference to the entity and reset dirty status.
	 */
	void clear();

	/**
	 * Writes the changes from the entity dialog into the given entity instance
	 * (see {@link com.vaadin.flow.data.binder.Binder#writeBean(Object)})
	 *
	 * @param entity
	 *            the entity instance to save the changes into
	 * @throws ValidationException
	 *             if the values entered into the entity dialog cannot be
	 *             converted into entity properties
	 */
	void write(T entity) throws ValidationException;

	String getEntityName();

	default void showCreatedNotification() {
		showNotification(getEntityName() + " was created");
	}

	default void showUpdatedNotification() {
		showNotification(getEntityName() + " was updated");
	}

	default void showDeletedNotification() {
		showNotification(getEntityName() + " was deleted");
	}
}

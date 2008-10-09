package org.marketcetera.photon.notification.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.NotificationPlugin;
import org.marketcetera.photon.notification.PlayWave;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Preferences page controlling behavior of desktop notifications.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class DesktopNotificationPreferencesPage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage, Messages {

	/**
	 * Labels and values for priority radio group
	 */
	private static final String[][] PRIORITY_NAMES_AND_VALUES = {
			{
					DESKTOP_NOTIFICATIONS_PARENTHETICAL_PATTERN
							.getText(DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_HIGH
									.getText(), DESKTOP_NOTIFICATIONS_FEWEST
									.getText()), Severity.HIGH.name() },
			{ DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_MEDIUM.getText(),
					Severity.MEDIUM.name() },
			{
					DESKTOP_NOTIFICATIONS_PARENTHETICAL_PATTERN.getText(
							DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_LOW.getText(),
							DESKTOP_NOTIFICATIONS_MOST.getText()),
					Severity.LOW.name() } };

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		GridLayoutFactory.fillDefaults().applyTo(composite);

		GridDataFactory.fillDefaults().applyTo(
				createPriorityLevelEditor(composite));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(
				createSoundGroup(composite));

		initialize();

		return composite;
	}

	/**
	 * Creates the priority level preference radio group.
	 * 
	 * @param parent
	 *            parent composite
	 * @return composite containing the priority level radio group
	 */
	private Composite createPriorityLevelEditor(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		RadioGroupFieldEditor notificationLevelEditor = new RadioGroupFieldEditor(
				NotificationPreferences.PRIORITY,
				DESKTOP_NOTIFICATIONS_PRIORITY.getText(), 1,
				PRIORITY_NAMES_AND_VALUES, composite, true);
		addField(notificationLevelEditor);
		return composite;
	}

	/**
	 * Creates the sound preference area.
	 * 
	 * @param parent
	 *            parent composite
	 * @return sound preference area group
	 */
	private Group createSoundGroup(Composite parent) {
		Font font = parent.getFont();

		// the group
		Group group = new Group(parent, SWT.NONE);
		group.setFont(font);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);
		group.setText(DESKTOP_NOTIFICATIONS_SOUNDS_GROUP.getText());

		// description label
		Label label = new Label(group, SWT.WRAP);
		label.setText(DESKTOP_NOTIFICATIONS_SOUNDS_GROUP_DESCRIPTION.getText());
		label.setFont(font);
		GridDataFactory.swtDefaults().grab(true, false).span(2, 1).align(
				SWT.FILL, SWT.CENTER).applyTo(label);

		// table containing notification types
		Table notificationTypeList = new Table(group, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		notificationTypeList.setFont(parent.getFont());
		GridDataFactory.swtDefaults().applyTo(notificationTypeList);
		createTableItem(notificationTypeList,
				DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_HIGH.getText(),
				Severity.HIGH);
		createTableItem(notificationTypeList,
				DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_MEDIUM.getText(),
				Severity.MEDIUM);
		createTableItem(notificationTypeList,
				DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_LOW.getText(),
				Severity.LOW);
		
		// sound details section
		final Composite soundDetails = new Composite(group, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(soundDetails);
		final StackLayout layout = new StackLayout();
		soundDetails.setLayout(layout);
		final Composite empty = new Composite(soundDetails, SWT.NONE);
		final Composite high = createSoundDetail(soundDetails, Severity.HIGH);
		final Composite medium = createSoundDetail(soundDetails, Severity.MEDIUM);
		final Composite low = createSoundDetail(soundDetails, Severity.LOW);
		layout.topControl = empty;
		notificationTypeList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object data = e.item.getData();
				if (data.equals(Severity.HIGH))
					layout.topControl = high;
				else if (data.equals(Severity.MEDIUM))
					layout.topControl = medium;
				else if (data.equals(Severity.LOW))
					layout.topControl = low;
				else
					layout.topControl = empty;
				soundDetails.layout();
			}
		});

		return group;
	}

	/**
	 * Helper method to create a simple table item.
	 * 
	 * @param table
	 *            the parent table
	 * @param text
	 *            the item text
	 * @param data
	 *            the item data
	 */
	private void createTableItem(Table table, String text, Object data) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(text);
		item.setData(data);
	}

	/**
	 * Helper method to create a "sound detail" group where sound preferences
	 * are configured for a given severity.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param severity
	 *            the notification severity
	 * @return the created composite
	 */
	private Composite createSoundDetail(Composite parent, Severity severity) {
		Font font = parent.getFont();

		// Grouping composite
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		// Button controlling whether to play sound
		final Button b = new Button(composite, SWT.NONE | SWT.CHECK);
		b.setText(DESKTOP_NOTIFICATIONS_PLAY_SOUND_LABEL.getText());
		b.setFont(font);
		String clipPref = NotificationPreferences.SOUND_ENABLED_PREFIX
				+ severity.name();
		addField(new SimpleFieldEditor(clipPref) {
			@Override
			protected void doLoad() {
				if (b != null)
					b.setSelection(getPreferenceStore().getBoolean(
							getPreferenceName()));
			}

			@Override
			protected void doLoadDefault() {
				if (b != null)
					b.setSelection(getPreferenceStore().getDefaultBoolean(
							getPreferenceName()));
			}

			@Override
			protected void doStore() {
				getPreferenceStore().setValue(getPreferenceName(),
						b.getSelection());
			}
		});

		// Sound file selection area
		final Composite fileComposite = new Composite(composite, SWT.NONE);
		fileComposite.setFont(font);
		GridDataFactory.fillDefaults().grab(true, false).indent(20, 0).applyTo(
				fileComposite);
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(true).applyTo(
				fileComposite);
		final FileFieldEditor clipEditor = new SoundClipFieldEditor(
				NotificationPreferences.SOUND_CLIP_PREFIX + severity.name(),
				fileComposite);
		addField(clipEditor);

		// Enable/disable sound clip editor based on button selection
		clipEditor.setEnabled(getPreferenceStore().getBoolean(clipPref),
				fileComposite);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clipEditor.setEnabled(b.getSelection(), fileComposite);
			}
		});

		return composite;
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return NotificationPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void createFieldEditors() {
		// Do nothing, field editors are created in createContents
	}

	/**
	 * A FileFieldEditor with Test button that tries to play the selected file
	 * as a sound clip.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 0.8.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	private static final class SoundClipFieldEditor extends FileFieldEditor {

		/**
		 * Cache of text widget (created in superclass) holding file name
		 */
		private Text text;

		/**
		 * Cache of button (created in superclass) which opens file selection
		 * dialog
		 */
		private Button browse;

		/**
		 * Button to test selected sound clip
		 */
		private Button test;

		/**
		 * Constructor.
		 * 
		 *@param name
		 *            the name of the preference this field editor works on
		 * @param parent
		 *            the parent of the field editor's control
		 */
		private SoundClipFieldEditor(String name, Composite parent) {
			super(name, "", true, parent); //$NON-NLS-1$
		}

		@Override
		protected void createControl(Composite parent) {
			setEmptyStringAllowed(false);
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			text = getTextControl(parent);
			GridDataFactory.fillDefaults().span(2, 1).grab(true, false)
					.applyTo(text);
			browse = getChangeControl(parent);
			GridDataFactory.defaultsFor(browse).applyTo(browse);
			test = new Button(parent, SWT.PUSH);
			test.setFont(parent.getFont());
			test.setText(DESKTOP_NOTIFICATIONS_TEST_BUTTON_LABEL.getText());
			GridDataFactory.defaultsFor(test).applyTo(test);

			test.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String file = text.getText();
					if (!file.isEmpty())
						new PlayWave(file).start();
				}
			});
		}

		@Override
		public void setEnabled(boolean enabled, Composite parent) {
			if (text != null)
				text.setEnabled(enabled);
			if (browse != null)
				browse.setEnabled(enabled);
			if (test != null)
				test.setEnabled(enabled);
		}

		@Override
		protected boolean checkState() {
			// Validation is too buggy to be useful so it is skipped
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=76127
			return true;
		}
	}

	/**
	 * Field editor without UI. Subclass must implement {@link #doLoad()},
	 * {@link #doLoadDefault()}, and {@link #doStore()} to work with custom UI.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 0.8.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	private abstract class SimpleFieldEditor extends FieldEditor {

		/**
		 * Constructor.
		 * 
		 * @param name the preference name
		 */
		public SimpleFieldEditor(String name) {
			super();
			setPreferenceName(name);
		}

		@Override
		protected void adjustForNumColumns(int numColumns) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void doFillIntoGrid(Composite parent, int numColumns) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getNumberOfControls() {
			throw new UnsupportedOperationException();
		}

	}

}

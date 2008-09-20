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
 * @since $Release$
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

		Group group = new Group(parent, SWT.NONE);
		group.setFont(font);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);
		group.setText(DESKTOP_NOTIFICATIONS_SOUNDS_GROUP.getText());

		Label label = new Label(group, SWT.WRAP);
		label
				.setText("Customize sound notifications by selecting the priority");
		label.setFont(font);
		GridDataFactory.swtDefaults().grab(true, false).span(2, 1).align(
				SWT.FILL, SWT.CENTER).applyTo(label);

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
		final Composite soundsDetails = new Composite(group, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(soundsDetails);
		final StackLayout layout = new StackLayout();
		soundsDetails.setLayout(layout);
		final Composite empty = new Composite(soundsDetails, SWT.NONE);
		final Composite c1 = createSoundDetail(soundsDetails, Severity.HIGH,
				DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_HIGH.getText());
		final Composite c2 = createSoundDetail(soundsDetails, Severity.MEDIUM,
				DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_MEDIUM.getText());
		final Composite c3 = createSoundDetail(soundsDetails, Severity.LOW,
				DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_LOW.getText());
		layout.topControl = empty;

		notificationTypeList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item.getData().equals(Severity.HIGH))
					layout.topControl = c1;
				else if (e.item.getData().equals(Severity.MEDIUM))
					layout.topControl = c2;
				else if (e.item.getData().equals(Severity.LOW))
					layout.topControl = c3;
				else
					layout.topControl = empty;
				soundsDetails.layout();
			}
		});

		return group;
	}

	private void createTableItem(Table table, String text, Object data) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(text);
		item.setData(data);
	}

	private Composite createSoundDetail(Composite parent, Severity severity,
			String label) {
		Font font = parent.getFont();

		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		final Button b = new Button(composite, SWT.NONE | SWT.CHECK);
		b.setText("Play sound");
		b.setFont(font);
		String clipPref = NotificationPreferences.SOUND_ENABLED_PREFIX
				+ severity.name();
		addField(new SimpleFieldEditor(clipPref) {
			@Override
			protected void doLoad() {
				if (b != null) {
					boolean value = getPreferenceStore().getBoolean(
							getPreferenceName());
					b.setSelection(value);
				}
			}

			@Override
			protected void doLoadDefault() {
				if (b != null) {
					boolean value = getPreferenceStore().getDefaultBoolean(
							getPreferenceName());
					b.setSelection(value);
				}
			}

			@Override
			protected void doStore() {
				getPreferenceStore().setValue(getPreferenceName(),
						b.getSelection());
			}
		});

		final Composite fileComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).indent(20, 0).applyTo(
				fileComposite);
		GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(true).applyTo(
				fileComposite);
		final FileFieldEditor clipEditor = new SoundClipFieldEditor(
				NotificationPreferences.SOUND_CLIP_PREFIX + severity.name(),
				DESKTOP_NOTIFICATIONS_SOUND_CLIP_LABEL.getText(label),
				fileComposite);
		addField(clipEditor);
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
	 * A FileFieldEditor with Test button that plays the sound file.
	 * 
	 * Static scope for unit testing.
	 */
	static final class SoundClipFieldEditor extends FileFieldEditor {
		private Button test;
		private Button browse;
		private Text text;

		private SoundClipFieldEditor(String name, String label, Composite parent) {
			super(name, label, true, parent);
		}

		@Override
		protected void createControl(Composite parent) {
			setEmptyStringAllowed(false);
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			text = getTextControl(parent);
			GridDataFactory.fillDefaults().span(2, 1).grab(true, false)
					.applyTo(text);
			browse = getChangeControl(parent);
			GridDataFactory.fillDefaults().applyTo(browse);
			test = new Button(parent, SWT.PUSH);
			test.setText("Test");
			GridDataFactory.fillDefaults().applyTo(test);

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
			boolean b = checkState();
			if (test != null)
				test.setEnabled(enabled && b);
		}

		@Override
		protected boolean checkState() {
			return true;
			// if (!text.isEnabled()) {
			// clearErrorMessage();
			// return true;
			// }
			// boolean b = super.checkState();
			// test.setEnabled(browse.isEnabled() && b);
			// return b;
		}

		@Override
		public void showErrorMessage(String msg) {
			super.showErrorMessage(DESKTOP_NOTIFICATIONS_SOUND_CLIP_INVALID
					.getText(getLabelText()));
		}
	}

	abstract class SimpleFieldEditor extends FieldEditor {

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

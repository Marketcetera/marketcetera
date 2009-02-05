package org.marketcetera.photon.preferences;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.TimeOfDay;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Field editor that provides a {@link DateTime} widget for a time and
 * serializes it according to {@link TimeOfDay}.
 * 
 * It can be configured to be "optional", i.e. the widget will be guarded by an
 * enablement check box.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class TimeOfDayFieldEditor extends FieldEditor {

	private final boolean mOptional;

	private final TimeZone mTimeZone = TimeZone.getDefault();

	private ControlEnableState mEnablement;

	private DateTime mTime;

	private Button mEnableButton;

	private Composite mComposite;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the widget container
	 * @param optional
	 *            indicates whether the preference is optional
	 */
	public TimeOfDayFieldEditor(String name, String labelText,
			Composite parent, boolean optional) {
		init(name, Messages.TIME_OF_DAY_FIELD_EDITOR_PARENTHETICAL_PATTERN
				.getText(labelText, mTimeZone.getDisplayName()));
		mOptional = optional;
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		// nothing to do
	}

	@Override
	protected void createControl(Composite parent) {
		if (mOptional) {
			GridLayoutFactory.swtDefaults().applyTo(parent);
			mEnableButton = new Button(parent, SWT.CHECK);
			mEnableButton
					.setText(Messages.TIME_OF_DAY_FIELD_EDITOR_ENABLE_BUTTON_LABEL
							.getText());
			GridDataFactory.defaultsFor(mEnableButton).applyTo(mEnableButton);
			mComposite = new Composite(parent, SWT.NONE);
			GridDataFactory.fillDefaults().indent(10, 0).applyTo(mComposite);
			super.createControl(mComposite);
			mEnablement = ControlEnableState.disable(mComposite);
			mEnableButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (mEnableButton.getSelection()) {
						mEnablement.restore();
					} else {
						mEnablement = ControlEnableState.disable(mComposite);
					}
				}
			});
		} else {
			super.createControl(parent);
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		getLabelControl(parent);
		mTime = new DateTime(parent, SWT.TIME | SWT.LONG);
		mTime.setTime(0, 0, 0);
		GridDataFactory.defaultsFor(mTime).applyTo(mTime);
	}

	@Override
	protected void doLoad() {
		load(getPreferenceStore().getString(getPreferenceName()));
	}

	@Override
	protected void doLoadDefault() {
		load(getPreferenceStore().getDefaultString(getPreferenceName()));
	}

	private void load(String string) {
		if (StringUtils.isNotBlank(string)) {
			TimeOfDay time = TimeOfDay.create(string);
			if (time != null) {
				mTime.setTime(time.getHour(mTimeZone), time
						.getMinute(mTimeZone), time.getSecond(mTimeZone));
				if (mOptional) {
					mEnableButton.setSelection(true);
					mEnablement.restore();
				}
			}
		}
	}

	@Override
	protected void doStore() {
		String value;
		if (mOptional && !mEnableButton.getSelection()) {
			value = ""; //$NON-NLS-1$
		} else {
			TimeOfDay time = TimeOfDay.create(mTime.getHours(), mTime
					.getMinutes(), mTime.getSeconds(), mTimeZone);
			value = time.toFormattedString();
		}
		getPreferenceStore().setValue(getPreferenceName(), value);
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

}

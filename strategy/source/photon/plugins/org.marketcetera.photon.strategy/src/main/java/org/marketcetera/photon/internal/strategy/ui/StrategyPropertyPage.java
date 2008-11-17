package org.marketcetera.photon.internal.strategy.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.marketcetera.photon.internal.strategy.Strategy;
import org.marketcetera.photon.internal.strategy.StrategyManager;
import org.marketcetera.photon.internal.strategy.Strategy.State;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * {@link PropertyPage} to edit {@link Strategy} properties.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyPropertyPage extends PropertyPage {

	private Text mNameText;
	
	private Strategy mStrategy;

	private void addNameSection(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		Text file = StrategyUI.createFileText(composite);
		file.setText(mStrategy.getFile().getFullPath().toString());

		final Text className = StrategyUI.createClassNameText(composite, true);
		className.setText(mStrategy.getClassName());
		mNameText = StrategyUI.createDisplayNameText(composite);
		mNameText.setText(mStrategy
				.getDisplayName());
		
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
	}

	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addNameSection(composite);
		addSeparator(composite);
		if (mStrategy.getState().equals(State.RUNNING))
			ControlEnableState.disable(composite);
		return composite;
	}
	
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		mStrategy = (Strategy) getElement().getAdapter(Strategy.class);
	}

	@Override
	public boolean performOk() {
		StrategyManager.getCurrent().changeDisplayName(mStrategy, mNameText.getText());		
		return true;
	}

}
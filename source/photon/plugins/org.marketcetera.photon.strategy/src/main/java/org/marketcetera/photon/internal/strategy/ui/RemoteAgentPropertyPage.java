package org.marketcetera.photon.internal.strategy.ui;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.photon.internal.strategy.RemoteStrategyAgent;
import org.marketcetera.photon.internal.strategy.StrategyManager;
import org.marketcetera.photon.internal.strategy.StrategyValidation;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Property page for configuring a {@link RemoteStrategyAgent}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class RemoteAgentPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private RemoteStrategyAgent mRemoteAgent;
	private Text mURI;
	private Text mUsername;
	private Text mPassword;

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		mURI =
				createText(composite, Messages.REMOTE_AGENT_PROPERTY_PAGE_URL_LABEL,
						Messages.REMOTE_AGENT_PROPERTY_PAGE_URL_TOOLTIP, ObjectUtils
								.toString(mRemoteAgent.getURI()), false);

		Group group = new Group(composite, SWT.NONE);
		group.setText(Messages.REMOTE_AGENT_PROPERTY_PAGE_CREDENTIALS_LABEL.getText());
		GridDataFactory.defaultsFor(group).span(2, 1).applyTo(group);

		mUsername =
				createText(group, Messages.REMOTE_AGENT_PROPERTY_PAGE_USERNAME_LABEL,
						Messages.REMOTE_AGENT_PROPERTY_PAGE_USERNAME_LABEL, mRemoteAgent
								.getUsername(), false);

		mPassword =
				createText(group, Messages.REMOTE_AGENT_PROPERTY_PAGE_PASSWORD_LABEL,
						Messages.REMOTE_AGENT_PROPERTY_PAGE_PASSWORD_LABEL, mRemoteAgent
								.getPassword(), true);

		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(group);

		Label description = new Label(composite, SWT.WRAP);
		description.setText(Messages.REMOTE_AGENT_PROPERTY_PAGE_CREDENTIALS_DESCRIPTION.getText());
		GridDataFactory.defaultsFor(description).span(2, 1).applyTo(description);

		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		return composite;
	}

	private Text createText(Composite parent, I18NMessage0P labelText, I18NMessage0P labelTooltip,
			String initialText, boolean password) {
		Font font = parent.getFont();

		Label label = new Label(parent, SWT.NONE);
		label.setFont(font);
		label.setText(StrategyUI.formatLabel(labelText));
		label.setToolTipText(labelTooltip.getText());

		int style = SWT.BORDER;
		if (password) {
			style |= SWT.PASSWORD;
		}
		Text text = new Text(parent, style);
		text.setFont(font);
		if (initialText != null) {
			text.setText(initialText);
		}
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		return text;
	}

	private void validate() {
		String url = mURI.getText();
		IStatus status =
				validateNotBlank(url, Messages.REMOTE_AGENT_PROPERTY_PAGE_URL_LABEL.getText());
		if (!status.isOK()) {
			handleStatus(status);
			return;
		}
		status = validateURL(url);
		if (!status.isOK()) {
			handleStatus(status);
			return;
		}

		setValid(true);
		setErrorMessage(null);
	}

	private void handleStatus(IStatus status) {
		setValid(false);
		setErrorMessage(status.getMessage());
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		mRemoteAgent = (RemoteStrategyAgent) getElement().getAdapter(RemoteStrategyAgent.class);
	}

	@Override
	public boolean performOk() {
		try {
			StrategyManager.getCurrent().updateAgent(mRemoteAgent, new URI(mURI.getText()),
					StringUtils.defaultIfEmpty(mUsername.getText(), null),
					StringUtils.defaultIfEmpty(mPassword.getText(), null));
			return true;
		} catch (URISyntaxException e) {
			// validation really should already have happened
			SLF4JLoggerProxy.error(this, e);
			ErrorDialog.openError(getShell(), null, null, ValidationStatus.error(
					getInvalidURLMessage(), e));
			return false;
		}
	}

	/**
	 * Validates that a string is not blank.
	 * 
	 * @param string
	 *            the string to validate
	 * @param field
	 *            the field name for the error message
	 * @return validation result
	 */
	private static IStatus validateNotBlank(String string, String field) {
		return StrategyValidation.validateNotBlank(field, string);
	}

	/**
	 * Validates that a url string is valid
	 * 
	 * @param url
	 *            the url
	 * @return validation result
	 */
	private static IStatus validateURL(String url) {
		try {
			new URI(url);
		} catch (URISyntaxException e) {
			return ValidationStatus.error(getInvalidURLMessage());
		}
		return ValidationStatus.ok();
	}

	private static String getInvalidURLMessage() {
		return Messages.REMOTE_AGENT_PROPERTY_PAGE_INVALID_URL
				.getText(Messages.REMOTE_AGENT_PROPERTY_PAGE_URL_LABEL.getText());
	}

}

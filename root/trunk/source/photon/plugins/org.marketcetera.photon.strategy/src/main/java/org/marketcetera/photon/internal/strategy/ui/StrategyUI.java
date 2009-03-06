package org.marketcetera.photon.internal.strategy.ui;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.photon.internal.strategy.Strategy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Factory class with static methods to create common {@link Strategy} UI
 * elements.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class StrategyUI {

	private static MessageFormat LABEL_PATTERN = new MessageFormat("{0}:"); //$NON-NLS-1$

	/**
	 * Creates a {@link Label} and read-only {@link Text} for a strategy's file.
	 * 
	 * @param parent
	 *            parent composite in which to create controls
	 * @return the created Text widget
	 */
	public static Text createFileText(Composite parent) {
		Font font = parent.getFont();
		Label label = new Label(parent, SWT.NONE);
		label.setFont(font);
		label.setText(formatLabel(Messages.STRATEGYUI_FILE_LABEL));
		label.setToolTipText(Messages.STRATEGYUI_FILE_TOOLTIP.getText());

		Text file = new Text(parent, SWT.READ_ONLY);
		file.setFont(font);

		return file;
	}

	/**
	 * Creates a {@link Label} and {@link Text} for a strategy's class name. The
	 * Text will be read-only according to the <code>readonly</code> parameter.
	 * 
	 * @param parent
	 *            parent composite in which to create controls
	 * @param readonly
	 *            true if created Text widget should be read-only
	 * @return the created Text widget
	 */
	public static Text createClassNameText(Composite parent, boolean readonly) {
		Font font = parent.getFont();
		Label label = new Label(parent, SWT.NONE);
		label.setFont(font);
		label.setText(formatLabel(Messages.STRATEGYUI_CLASS_LABEL));
		label.setToolTipText(Messages.STRATEGYUI_CLASS_TOOLTIP.getText());

		final Text className = new Text(parent, readonly ? SWT.READ_ONLY
				: SWT.BORDER);
		className.setFont(font);

		return className;
	}

	/**
	 * Creates a {@link Label} and {@link Text} for a strategy's display name.
	 * 
	 * @param parent
	 *            parent composite in which to create controls
	 * @return the created Text widget
	 */
	public static Text createDisplayNameText(Composite parent) {
		Font font = parent.getFont();
		Label label = new Label(parent, SWT.NONE);
		label.setFont(font);
		label.setText(formatLabel(Messages.STRATEGYUI_DISPLAY_NAME_LABEL));
		label
				.setToolTipText(Messages.STRATEGYUI_DISPLAY_NAME_TOOLTIP
						.getText());

		final Text name = new Text(parent, SWT.BORDER);
		name.setFont(font);

		return name;
	}

	/**
	 * Creates a check box {@link Button} for a strategy's routing.
	 * 
	 * @param parent
	 *            parent composite in which to create controls
	 * @return the created Button
	 */
	public static Button createRoutingCheckBox(Composite parent) {
		Font font = parent.getFont();
		Button routeToServer = new Button(parent, SWT.CHECK);
		routeToServer.setFont(font);
		routeToServer.setText(Messages.STRATEGYUI_ROUTE_TO_SERVER_LABEL.getText());
		routeToServer.setToolTipText(Messages.STRATEGYUI_ROUTE_TO_SERVER_TOOLTIP.getText());
		return routeToServer;
	}

	/**
	 * Formats a field description to be a label.
	 * 
	 * @param message
	 *            the localizable field description
	 * @return the label string
	 */
	public static String formatLabel(I18NMessage0P message) {
		return LABEL_PATTERN.format(new Object[] { message.getText() });
	}
}

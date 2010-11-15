package org.marketcetera.photon.commons.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Encapsulates localization of a UI label. This is a {@link ReflectiveMessages}
 * extension so it can be used in a Messages class, e.g.
 * 
 * <pre>
 * final class Messages {
 *     static LocalizedLabel ELEMENT;
 * 
 *     static {
 *         ReflectiveMessages.init(Messages.class);
 *     }
 * }
 * </pre>
 * 
 * The above {@link LocalizedLabel} will be initialized from two
 * {@link I18NMessage0P} message keys:
 * <ol>
 * <li>element.label - the raw label</li>
 * <li>element.tooltip - the tooltip for the label</li>
 * </ol>
 * Typically labels are decorated in the UI, e.g. suffixed with a colon ":".
 * This can be done with {@link #getFormattedLabel()}. The decoration format
 * pattern is an external string that can be configured as desired.
 * <p>
 * For convenience, the formatted label text and tooltip can be set on a
 * {@link Label} widget by {@link #initializeLabel(Label)}. A new Label can be
 * created with {@link #createLabel(Composite)}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class LocalizedLabel {

    private static final String LABEL_ENTRY_ID = "label"; //$NON-NLS-1$
    private static final String TOOLTIP_ENTRY_ID = "tooltip"; //$NON-NLS-1$

    private final I18NMessage0P mLabel;
    private final I18NMessage0P mTooltip;

    /**
     * Constructor. Typically LocalizedLabel objects are constructed
     * automatically for a Messages class.
     * 
     * @param label
     *            the raw label message
     * @param tooltip
     *            the tooltip message
     * @throws IllegalArgumentException
     *             if label or tooltip is null
     */
    public LocalizedLabel(I18NMessage0P label, I18NMessage0P tooltip) {
        if (label == null) {
            throw new IllegalArgumentException("label must not be null"); //$NON-NLS-1$
        }
        if (tooltip == null) {
            throw new IllegalArgumentException("tooltip must not be null"); //$NON-NLS-1$
        }
        mLabel = label;
        mTooltip = tooltip;
    }

    /**
     * Returns the raw unformatted label.
     * 
     * @return the raw label
     */
    public String getRawLabel() {
        return mLabel.getText();
    }

    /**
     * Returns the label with a colon attached as a suffix.
     * 
     * @return the formatted label
     */
    public String getFormattedLabel() {
        return formatLabel(getRawLabel());
    }

    /**
     * Returns the tooltip.
     * 
     * @return the tooltip
     */
    public String getTooltip() {
        return mTooltip.getText();
    }

    /**
     * Convenience method that sets the text and tooltip on the provided
     * {@link Label} widget.
     * 
     * @param widget
     *            the label widget
     */
    public void initializeLabel(Label widget) {
        widget.setText(getFormattedLabel());
        widget.setToolTipText(getTooltip());
    }

    /**
     * Convenience method that creates a {@link Label} widget and sets the text
     * and tooltip on it.
     * 
     * @param parent
     *            the parent composite for the label
     */
    public Label createLabel(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        initializeLabel(label);
        return label;
    }

    /**
     * Formats a field label for use in UI by applying a format string. This
     * typically adds a colon ":" to the label text.
     * 
     * @param message
     *            the field description
     * @return the label string
     */
    public static String formatLabel(String message) {
        return Messages.LOCALIZED_LABEL__FORMAT_PATTERN.getText(message);
    }
    
    /**
     * Returns the underlying message object for the label.
     * 
     * @return the label message
     */
    I18NMessage0P getLabelMessage() {
        return mLabel;
    }
    
    /**
     * Returns the underlying message object for the tooltip.
     * 
     * @return the tooltip message
     */
    I18NMessage0P getTooltipMessage() {
        return mTooltip;
    }

    /**
     * {@link ReflectiveMessages} extension method.
     * 
     * @param fieldName
     *            the name of the field being initialized
     * @param logger
     *            the logger to assist in {@link I18NMessage} construction
     * @return the LocalizedLabel to assign to the field
     */
    static LocalizedLabel initReflectiveMessages(String fieldName,
            I18NLoggerProxy logger) {
        String messageId = fieldName.toLowerCase();
        return new LocalizedLabel(new I18NMessage0P(logger, messageId,
                LABEL_ENTRY_ID), new I18NMessage0P(logger, messageId,
                TOOLTIP_ENTRY_ID));
    }
}

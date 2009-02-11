package org.marketcetera.photon.notification;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A popup that displays an {@link INotification}.
 * 
 * This class may be subclassed to override
 * <ul>
 * <li>{@link #getImage(INotification)} - the images displayed for the
 * notifications</li>
 * <li>{@link #getLabelFont()} - the font used for the heading labels</li>
 * <li>{@link #fitLineToWidth(GC, String, int)} - the algorithm for truncating
 * the subject text to fit on a single line</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
@SuppressWarnings("restriction")
public class DesktopNotificationPopup extends AbstractNotificationPopup {

	/**
	 * time in milliseconds before the popup closes automatically
	 */
	private static final int CLOSE_DELAY = 4000;

	/**
	 * used for truncating long text
	 */
	private static final String ELLIPSIS = Messages.POPUP_ELLIPSIS.getText();

	/**
	 * the notification
	 */
	private final INotification mNotification;

	/**
	 * Constructor.
	 * 
	 * Subclasses should call this constructor, but may customize/override
	 * configuration by calling {@link #setFadingEnabled(boolean)} and
	 * {@link #setDelayClose(long)}.
	 * 
	 * @param display
	 *            the display used by {@link AbstractNotificationPopup}
	 * @param notification
	 *            the notification to display, cannot be null
	 * @throws AssertionFailedException
	 *             if notification is null
	 */
	public DesktopNotificationPopup(Display display, INotification notification) {
		super(display);
		Assert.isNotNull(notification);
		this.mNotification = notification;
		setFadingEnabled(true);
		setDelayClose(CLOSE_DELAY);
	}

	@Override
	protected final void createContentArea(Composite parent) {
		createHeading(parent);
		createLabel(parent, mNotification.getBody(), parent.getFont(), SWT.WRAP);
	}

	/**
	 * Creates the heading area of the popup.
	 * 
	 * @param parent
	 *            the parent
	 */
	private void createHeading(Composite parent) {
		Composite composite = new Composite(parent, SWT.NO_FOCUS);
		composite.setBackground(parent.getBackground());
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);

		// layout depends on whether there is an image
		Image image = getImage(mNotification);
		GridLayoutFactory layout = GridLayoutFactory.swtDefaults().spacing(15,
				5);
		if (image != null) {
			layout.numColumns(3).applyTo(composite);
			Label imageLabel = new Label(composite, SWT.NO_FOCUS);
			imageLabel.setBackground(parent.getBackground());
			GridDataFactory.defaultsFor(imageLabel).span(1, 3).applyTo(
					imageLabel);
			imageLabel.setImage(image);
		} else {
			layout.numColumns(2).applyTo(composite);
		}

		createLabel(composite, Messages.POPUP_SUBJECT_LABEL.getText(),
				getLabelFont(), SWT.RIGHT);

		// Special label that truncates text with "..." as necessary
		final Label subject = new Label(composite, SWT.NO_FOCUS);
		subject.setBackground(parent.getBackground());
		subject.setFont(parent.getFont());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subject);
		subject.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				subject.setText(fitLineToWidth(e.gc,
						mNotification.getSubject(), e.width));
				// The text only needs to be calculated once.
				subject.removePaintListener(this);
			}
		});

		createLabel(composite, Messages.POPUP_PRIORITY_LABEL.getText(),
				getLabelFont(), SWT.RIGHT);
		createLabel(composite, getSeverityLabel(mNotification.getSeverity()),
				parent.getFont(), SWT.NONE);
		createLabel(composite, Messages.POPUP_TIMESTAMP_LABEL.getText(),
				getLabelFont(), SWT.RIGHT);
		createLabel(composite, mNotification.getDate().toString(), parent
				.getFont(), SWT.NONE);
	}

	/**
	 * Helper method to create a label.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param text
	 *            the label text
	 * @param font
	 *            the label font
	 * @param style
	 *            the label style
	 * @return a label configured with the provided parameters
	 */
	private Label createLabel(Composite parent, String text, Font font,
			int style) {
		Label label = new Label(parent, SWT.NO_FOCUS | style);
		label.setBackground(parent.getBackground());
		label.setFont(font);
		GridDataFactory.defaultsFor(label).applyTo(label);
		label.setText(text);
		return label;
	}

	/**
	 * Helper method to provide label text for a given {@link Severity}.
	 * 
	 * @param severity
	 *            the severity
	 * @return label for the severity
	 */
	private String getSeverityLabel(Severity severity) {
		switch (severity) {
		case HIGH:
			return Messages.POPUP_SEVERITY_LABEL_HIGH.getText();
		case MEDIUM:
			return Messages.POPUP_SEVERITY_LABEL_MEDIUM.getText();
		case LOW:
			return Messages.POPUP_SEVERITY_LABEL_LOW.getText();
		default:
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Provides the label font. Default is to the bold version of the JFace
	 * default font.
	 * 
	 * Subclasses can override to provide a different font.
	 * 
	 * @return the font to use for heading labels
	 */
	protected Font getLabelFont() {
		return JFaceResources.getFontRegistry().getBold(
				JFaceResources.DEFAULT_FONT);
	}

	/**
	 * Provides the image to use for the given notification. Default is based on
	 * severity:
	 * <ul>
	 * <li>HIGH - SWT.ICON_ERROR</li>
	 * <li>MEDIUM - SWT.ICON_WARNING</li>
	 * <li>LOW - SWT.ICON_INFORMATION</li>
	 * </ul>
	 * 
	 * Subclass can return null if no image is desired.
	 * 
	 * @return the image to display or null for no image
	 */
	protected Image getImage(INotification notification) {
		switch (notification.getSeverity()) {
		case HIGH:
			return getSWTImage(SWT.ICON_ERROR);
		case MEDIUM:
			return getSWTImage(SWT.ICON_WARNING);
		default:
			return getSWTImage(SWT.ICON_INFORMATION);
		}
	}

	/**
	 * Strategy for truncating text to a specified width using ellipsis. The
	 * default strategy tries to break at a space if possible.
	 * 
	 * Subclass implementations must return a String that fits inside the given
	 * width when rendered using the provided GC.
	 * 
	 * @param gc
	 *            the graphics context
	 * @param text
	 *            the text to fit
	 * @param width
	 *            the maximum width
	 * @return a new string, shortened with ellipsis if necessary
	 */
	protected String fitLineToWidth(GC gc, String text, int width) {
		int pixels = 0;
		int dotextent = gc.stringExtent(ELLIPSIS).x;
		if (dotextent >= width) {
			// No point truncating if the ellipsis is greater than the provided
			// width
			return ""; //$NON-NLS-1$
		}
		StringCharacterIterator iter = new StringCharacterIterator(text);
		pixels += gc.getAdvanceWidth(iter.current());
		char c;
		while ((c = iter.next()) != CharacterIterator.DONE) {
			pixels += gc.getAdvanceWidth(c);
			if (pixels > width) {
				pixels += dotextent;
				while ((pixels -= gc.getAdvanceWidth(c)) > width)
					c = iter.previous();
				int index = iter.getIndex();
				while (c != ' ' && c != CharacterIterator.DONE)
					c = iter.previous();
				if (c != CharacterIterator.DONE)
					index = iter.getIndex();
				return text.substring(0, index) + ELLIPSIS;
			}
		}
		return text;
	}

	/**
	 * Helper method to get an <code>Image</code> from the provided SWT image
	 * constant.
	 * 
	 * @param imageID
	 *            the SWT image constant
	 * @return image the image
	 */
	private Image getSWTImage(final int imageID) {
		return getShell().getDisplay().getSystemImage(imageID);
	}

	@Override
	public final int open() {
		// Simply call the superclass implementation. This method was overridden
		// purely to hide the discouraged access warning from clients.
		return super.open();
	}

}

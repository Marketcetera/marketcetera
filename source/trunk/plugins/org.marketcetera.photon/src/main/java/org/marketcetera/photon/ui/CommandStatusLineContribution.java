package org.marketcetera.photon.ui;

import jfun.parsec.Parser;
import jfun.parsec.ParserException;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commands.MessageCommand;
import org.marketcetera.photon.commands.ShowOrderInTicketCommand;
import org.marketcetera.photon.parser.CommandParser;

/**
 * The CommandStatusLineContribution represents the UI component that
 * allows users to enter text-based commands.  It consists of a one-line
 * text area.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class CommandStatusLineContribution extends ContributionItem {

	public static final String ID = "org.marketcetera.photon.CommandStatusLineContribution";

	public final static int DEFAULT_CHAR_WIDTH = 40;

	private Text textArea;

//	private Logger mInternalDebugLogger = Application.getMainConsoleLogger();

	private String text = ""; //$NON-NLS-1$

	private int widthHint = -1;

	private int heightHint = -1;

	private String tooltip;

	private CommandParser commandParser;

	/**
	 * Create a CommandStatusLineContribution with the specified ID,
	 * and default character width
	 * 
	 * @param id the id for the status line contribution
	 */
	public CommandStatusLineContribution(String id) {
		this(id, DEFAULT_CHAR_WIDTH);
	}

	/**
	 * Create a CommandStatusLineContribution with the specified ID
	 * and character width.
	 * 
	 * @param id
	 * @param charWidth
	 */
	public CommandStatusLineContribution(String id, int charWidth) {
		super(id);
		this.widthHint = charWidth;
		commandParser = new CommandParser();
	}

	/**
	 * Sets up the user interface components for this status line contribution.
	 * Attempts to figure out the width of the requested character width, and
	 * adjust the text area accordingly.
	 * 
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Composite)
	 */
	public void fill(Composite parent) {
		Label sep = new Label(parent, SWT.SEPARATOR);
		Label command = new Label(parent, SWT.NONE);
		command.setText(Messages.CommandStatusLineContribution_CommandLabel);
		textArea = new Text(parent, SWT.NONE);

		GC gc = new GC(parent);
		gc.setFont(parent.getFont());
		FontMetrics fm = gc.getFontMetrics();
		Point extent = gc.textExtent(text);
		if (widthHint > 0) {
			widthHint = fm.getAverageCharWidth() * widthHint;
		} else {
			widthHint = extent.x;
		}
		heightHint = (int) (fm.getHeight() * .75);
		gc.dispose();

		StatusLineLayoutData statusLineLayoutData = new StatusLineLayoutData();
		statusLineLayoutData.widthHint = widthHint;
		statusLineLayoutData.heightHint = heightHint;
		textArea.setLayoutData(statusLineLayoutData);
		textArea.setText(text);
		textArea.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}
		});
		if (tooltip != null) {
			textArea.setToolTipText(tooltip);
		}

		statusLineLayoutData = new StatusLineLayoutData();
		statusLineLayoutData.heightHint = heightHint;
		sep.setLayoutData(statusLineLayoutData);
	}

	/**
	 * The callback for a key release event. Checks to see if the key that was
	 * released was "Enter". If so, (@link #parseAndFireCommandEvent(String,
	 * org.marketcetera.photon.actions.CommandEvent.Destination)} is called,
	 * routing the command to the OrderManager. If the user types Control-t, the
	 * command is sent to the order ticket, to be displayed for further editing.
	 * 
	 * @param e
	 *            the key released event
	 */
	protected void handleKeyReleased(KeyEvent e) {
		Text theText = (Text) e.widget;
		String theInputString = theText.getText();
		if (theInputString.length() > 0){
			try {
				IPhotonCommand command = null;
				if ('\r' == e.character) {
					theText.setText("");
					command = commandParser.parseCommand(theInputString);
				} else if (e.keyCode == 't' && ((e.stateMask & SWT.CONTROL) != 0)) {
					theText.setText("");
					command = commandParser.parseCommand(theInputString);
					command = new ShowOrderInTicketCommand(((MessageCommand)command).getMessage());
				}
				if (command != null){
					command.execute();
				}
			} catch (ParserException e1) {
				PhotonPlugin.getMainConsoleLogger().error(theInputString+": "+e1.getMessage() );
			}
		}
	}


	/**
	 * Gets the text currently in the command entry text area.
	 * @return the command entry text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text of the command entry text area
	 * @param text the new text to use
	 */
	public void setText(String text) {
		if (text == null)
			throw new NullPointerException();

		this.text = text;

		if (textArea != null && !textArea.isDisposed())
			textArea.setText(this.text);

		// if (this.text.length() == 0) {
		// if (isVisible()) {
		// setVisible(false);
		// IContributionManager contributionManager = getParent();
		//
		// if (contributionManager != null)
		// contributionManager.update(true);
		// }
		// } else {
		// if (!isVisible()) {
		// setVisible(true);
		// IContributionManager contributionManager = getParent();
		//
		// if (contributionManager != null)
		// contributionManager.update(true);
		// }
		// }
	}

	/**
	 * Sets the tooltip of the status line contribution.
	 * @param tooltip the new tooltip to use
	 */
	public void setTooltip(String tooltip) {
		if (tooltip == null)
			throw new NullPointerException();

		this.tooltip = tooltip;

		if (textArea != null && !textArea.isDisposed()) {
			textArea.setToolTipText(this.tooltip);
		}
	}

	// public void setImage(Image image) {
	// if (image == null)
	// throw new NullPointerException();
	//
	// this.image = image;
	//
	// if (textArea != null && !textArea.isDisposed())
	// textArea.setImage(this.image);
	//
	// if (!isVisible()) {
	// setVisible(true);
	// IContributionManager contributionManager = getParent();
	//
	// if (contributionManager != null)
	// contributionManager.update(true);
	// }
	// }

	/**
	 * The method to set the focus of the application to this status line contribution. Calls 
	 * {@link Text#selectAll()}  followed by {@link Text#setFocus()}.
	 * @return true if the focus was successfully set to this status line contribution
	 */
	public boolean setFocus() {
		textArea.selectAll();
		return textArea.setFocus();
	}



	/**
	 * Sets the {@link IDFactory} for the {@link Parser} member.
	 * @see org.marketcetera.photon.parser.Parser#init(org.marketcetera.core.IDFactory)
	 */
	public void setIDFactory(IDFactory factory) {
		commandParser.setIDFactory(factory);
	}

}

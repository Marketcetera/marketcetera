package org.marketcetera.photon;

import java.util.LinkedList;
import java.util.List;

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
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.photon.parser.Parser;
import org.marketcetera.photon.parser.ParserException;

import quickfix.Message;

public class CommandStatusLineContribution extends ContributionItem {

	public static final String ID = "org.marketcetera.photon.CommandStatusLineContribution";

	public final static int DEFAULT_CHAR_WIDTH = 40;

	private Text textArea;

//	private Logger mInternalDebugLogger = Application.getMainConsoleLogger();

	private String text = ""; //$NON-NLS-1$

	private int widthHint = -1;

	private int heightHint = -1;

	private String tooltip;

	private List<ICommandListener> commandListeners = new LinkedList<ICommandListener>();

	private Parser commandParser;

	public CommandStatusLineContribution(String id) {
		this(id, DEFAULT_CHAR_WIDTH);
	}

	public CommandStatusLineContribution(String id, int charWidth) {
		super(id);
		this.widthHint = charWidth;
		commandParser = new Parser();
	}

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

	protected void handleKeyReleased(KeyEvent e) {
		Text theText = (Text) e.widget;
		String theInputString = theText.getText();
		try {
			if ('\r' == e.character) {
				theText.setText("");
				parseAndFireCommandEvent(theInputString,
						CommandEvent.Destination.BROKER);
			} else if (e.keyCode == 't' && ((e.stateMask & SWT.CONTROL) != 0)) {
				theText.setText("");
				parseAndFireCommandEvent(theInputString,
						CommandEvent.Destination.EDITOR);
			}
		} catch (NoMoreIDsException e1) {
			Application.getMainConsoleLogger().error("Ran out of ID's parsing command '"+theInputString +"'");
		} catch (ParserException e1) {
			Application.getMainConsoleLogger().error(theInputString+": "+e1.getMessage() );
		}
	}

	private void parseAndFireCommandEvent(String theInputString,
			CommandEvent.Destination dest) throws NoMoreIDsException,
			ParserException {
		commandParser.setInput(theInputString);
		Parser.Command aCommand;
		aCommand = commandParser.command();

		for (Object messageObj : aCommand.mResults) {
			Message message = (Message) messageObj;
			fireCommandEvent(message, dest);
		}
	}

	public String getText() {
		return text;
	}

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

	public boolean setFocus() {
		textArea.selectAll();
		return textArea.setFocus();
	}

	public void addCommandListener(ICommandListener listener) {
		commandListeners.add(listener);
	}

	public boolean removeCommandListener(ICommandListener listener) {
		return commandListeners.remove(listener);
	}

	public void fireCommandEvent(Message aMessage, CommandEvent.Destination dest) {
		for (ICommandListener listener : commandListeners) {
			CommandEvent evt = new CommandEvent(aMessage, dest);
			listener.commandIssued(evt);
		}
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.parser.Parser#init(org.marketcetera.core.IDFactory)
	 */
	public void setIDFactory(IDFactory factory) {
		commandParser.init(factory);
	}

}

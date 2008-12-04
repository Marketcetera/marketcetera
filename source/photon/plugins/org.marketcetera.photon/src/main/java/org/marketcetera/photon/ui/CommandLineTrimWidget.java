package org.marketcetera.photon.ui;

import jfun.parsec.ParserException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.swt.IFocusService;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commands.MessageCommand;
import org.marketcetera.photon.commands.SendOrderToOrderManagerCommand;
import org.marketcetera.photon.commands.ShowOrderInTicketCommand;
import org.marketcetera.photon.parser.CommandParser;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;


/**
 * Bare-bones new command entry area widget for the status line.
 * 
 * @author andrei@lissovski.org
 */
public class CommandLineTrimWidget
    extends Workaround253082ContributionItem
    implements Messages
{

	private Text textArea;

	private String text = ""; //$NON-NLS-1$
	private int charWidth = -1;

	private double heightFactor = 1;

	private String tooltip;

	private CommandParser commandParser;
	public final static int DEFAULT_CHAR_WIDTH = 25;
	public static final String ID = "org.marketcetera.photon.ui.commandLineTrimWidget"; //$NON-NLS-1$

	
	public CommandLineTrimWidget() {
		this(DEFAULT_CHAR_WIDTH);
	}
	
	public CommandLineTrimWidget(int charWidth) {
		super();
		this.charWidth = charWidth;
		commandParser = new CommandParser();
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		commandParser.setMessageFactory(plugin.getMessageFactory());
		commandParser.setDataDictionary(plugin.getFIXDataDictionary().getDictionary());
		commandParser.setBrokerValidator(BrokerManager.getCurrent());
	}

	@Override
	protected Control createControl(Composite parent) {
		super.createControl(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout());
		composite.setData(this);
		
		Label command = new Label(composite, SWT.NONE);
		command.setText(Messages.CommandStatusLineContribution_CommandLabel.getText());
		textArea = new Text(composite, SWT.BORDER);

		IFocusService focusService = (IFocusService) getWorkbenchWindow().getService(IFocusService.class);
		focusService.addFocusTracker(textArea, ID);
		
		Point sizeHint = EclipseUtils.getTextAreaSize(composite, text, charWidth, heightFactor);

		RowData rowData = new RowData();
		rowData.width = sizeHint.x;
		rowData.height = sizeHint.y;
		textArea.setLayoutData(rowData);
		textArea.setText(text);
		textArea.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}
		});
		if (tooltip != null) {
			textArea.setToolTipText(tooltip);
		}
		return composite;
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
					theText.setText(""); //$NON-NLS-1$
					command = commandParser.parseCommand(theInputString);
				} else if (e.keyCode == 't' && ((e.stateMask & SWT.CONTROL) != 0)) {
					theText.setText(""); //$NON-NLS-1$
					command = commandParser.parseCommand(theInputString);
					Message message = ((MessageCommand)command).getMessage();
					if (FIXMessageUtil.isOrderSingle(message)){
						command = new ShowOrderInTicketCommand(message, ((SendOrderToOrderManagerCommand)command).getBroker());
					}
				}
				if (command != null){
					command.execute();
				}
			} catch (ParserException e1) {
				PhotonPlugin.getMainConsoleLogger().error(PARSE_EXCEPTION.getText(theInputString),
				                                          e1);
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

	/**
	 * The method to set the focus of the application to this status line contribution. Calls 
	 * {@link Text#selectAll()}  followed by {@link Text#setFocus()}.
	 * @return true if the focus was successfully set to this status line contribution
	 */
	public boolean setFocus() {
		textArea.selectAll();
		return textArea.setFocus();
	}


}

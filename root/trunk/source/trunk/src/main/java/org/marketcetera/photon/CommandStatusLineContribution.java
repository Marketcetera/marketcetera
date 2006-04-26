package org.marketcetera.photon;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionManager;
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
import org.marketcetera.photon.actions.CommandEvent;
import org.marketcetera.photon.actions.ICommandListener;
import org.marketcetera.photon.actions.OrderCommandEvent;

public class CommandStatusLineContribution extends ContributionItem {

	public final static int DEFAULT_CHAR_WIDTH = 40;

	private Text textArea;

	private Logger mInternalDebugLogger = Application.getDebugConsoleLogger();
	
	private String text = ""; //$NON-NLS-1$

	private int widthHint = -1;

	private int heightHint = -1;

	private String tooltip;

	private List<ICommandListener> commandListeners = new LinkedList<ICommandListener>();

	public CommandStatusLineContribution() {
		this(null);
	}

	public CommandStatusLineContribution(String id) {
		this(id, DEFAULT_CHAR_WIDTH);
	}

	public CommandStatusLineContribution(String id, int charWidth) {
		super(id);
		this.widthHint = charWidth;
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
        });		if (tooltip != null) {
			textArea.setToolTipText(tooltip);
		}

		statusLineLayoutData = new StatusLineLayoutData();
		statusLineLayoutData.heightHint = heightHint;
		sep.setLayoutData(statusLineLayoutData);
	}

	protected void handleKeyReleased(KeyEvent e) {
        if ('\r' == e.character) {
            Text theText = (Text) e.widget;
            String theInputString = theText.getText();
            theText.setText("");
            fireCommandEvent(new OrderCommandEvent(theInputString));
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

//		if (this.text.length() == 0) {
//			if (isVisible()) {
//				setVisible(false);
//				IContributionManager contributionManager = getParent();
//
//				if (contributionManager != null)
//					contributionManager.update(true);
//			}
//		} else {
//			if (!isVisible()) {
//				setVisible(true);
//				IContributionManager contributionManager = getParent();
//
//				if (contributionManager != null)
//					contributionManager.update(true);
//			}
//		}
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

	public void fireCommandEvent(CommandEvent event) {
		mInternalDebugLogger.info("Command issued: "+event.getStringValue());
		for (ICommandListener listener : commandListeners) {
			listener.commandIssued(event);
		}
	}

}

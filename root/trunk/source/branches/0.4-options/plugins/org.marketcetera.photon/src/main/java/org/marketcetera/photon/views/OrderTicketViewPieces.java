package org.marketcetera.photon.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.ui.validation.ControlDecoration;

/**
 * A UI section of the order ticket views such as StockOrderTicket.
 */
public class OrderTicketViewPieces {

	private static final String CONTROL_DECORATOR_KEY = "CONTROL_DECORATOR_KEY";

	private Composite defaultParent;

	private FormToolkit formToolkit;

	private Combo sideCombo = null;

	private Text quantityText = null;

	private Text symbolText = null;

	private Text priceText = null;

	private Combo tifCombo = null;

	private Image errorImage;

	private Image warningImage;

	private List<Control> decoratedInputControls = new LinkedList<Control>();

	public OrderTicketViewPieces(Composite defaultParent,
			FormToolkit formToolkit) {
		this.defaultParent = defaultParent;
		this.formToolkit = formToolkit;

		init();
	}

	private void init() {
		FieldDecoration deco = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		errorImage = deco.getImage();
		deco = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_WARNING);
		warningImage = deco.getImage();
	}

	private FormToolkit getFormToolkit() {
		return formToolkit;
	}

	public void addInputControlErrorDecoration(Control control) {
		ControlDecoration cd = new ControlDecoration(control, SWT.LEFT
				| SWT.BOTTOM);
		cd.setImage(errorImage);
		cd.hide();
		control.setData(CONTROL_DECORATOR_KEY, cd);
		decoratedInputControls.add(control);
	}

	public void createSideInput() {

		sideCombo = new Combo(defaultParent, SWT.BORDER);
		sideCombo.add(SideImage.BUY.getImage());
		sideCombo.add(SideImage.SELL.getImage());
		sideCombo.add(SideImage.SELL_SHORT.getImage());
		sideCombo.add(SideImage.SELL_SHORT_EXEMPT.getImage());

		// Force Side to be uppercase
		sideCombo.addVerifyListener( new VerifyListener() {
			public void verifyText(VerifyEvent event) {
				if( event.widget == sideCombo ) {
					if( event.text != null ) {
						event.text = event.text.toUpperCase();
					}
				}
			}
		});

		addInputControlErrorDecoration(sideCombo);
	}

	public void createQuantityInput() {
		quantityText = getFormToolkit().createText(defaultParent, null,
				SWT.SINGLE | SWT.BORDER);

		Point sizeHint = EclipseUtils.getTextAreaSize(quantityText, null, 10,
				1.0);

		GridData quantityTextGridData = new GridData();
		// quantityTextGridData.heightHint = sizeHint.y;
		quantityTextGridData.widthHint = sizeHint.x;
		quantityTextGridData.horizontalAlignment = GridData.FILL;
		quantityText.setLayoutData(quantityTextGridData);

		quantityText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});
		addInputControlErrorDecoration(quantityText);
	}

	public void createSymbolInput() {
		GridData symbolTextGridData = new GridData();
		symbolTextGridData.horizontalAlignment = GridData.FILL;
		symbolTextGridData.grabExcessHorizontalSpace = true;
		symbolTextGridData.verticalAlignment = GridData.CENTER;

		symbolText = getFormToolkit().createText(defaultParent, null,
				SWT.SINGLE | SWT.BORDER);
		symbolText.setLayoutData(symbolTextGridData);
		addInputControlErrorDecoration(symbolText);
	}

	public void createPriceInput() {
		priceText = getFormToolkit().createText(defaultParent, null,
				SWT.SINGLE | SWT.BORDER);

		Point sizeHint = EclipseUtils.getTextAreaSize(priceText, null, 10, 1.0);

		GridData quantityTextGridData = new GridData();
		// quantityTextGridData.heightHint = sizeHint.y;
		quantityTextGridData.widthHint = sizeHint.x;
		priceText.setLayoutData(quantityTextGridData);

		priceText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});
		addInputControlErrorDecoration(priceText);
	}

	public void createTifInput() {

		tifCombo = new Combo(defaultParent, SWT.BORDER);
		tifCombo.add(TimeInForceImage.DAY.getImage());
		tifCombo.add(TimeInForceImage.OPG.getImage());
		tifCombo.add(TimeInForceImage.CLO.getImage());
		tifCombo.add(TimeInForceImage.FOK.getImage());
		tifCombo.add(TimeInForceImage.GTC.getImage());
		tifCombo.add(TimeInForceImage.IOC.getImage());

		addInputControlErrorDecoration(tifCombo);
	}

	public Combo getSideCombo() {
		return sideCombo;
	}

	public Text getSymbolText() {
		return symbolText;
	}

	public Text getPriceText() {
		return priceText;
	}

	public Text getQuantityText() {
		return quantityText;
	}

	public Combo getTifCombo() {
		return tifCombo;
	}

	/**
	 * Update the state of the label and sendButton based on the error message
	 * and severity.
	 */
	public void showErrorMessage(String errorMessage, int severity,
			Label errorMessageLabel, Label errorIconLabel, Button sendButton) {
		if (errorMessage == null) {
			errorMessageLabel.setText("");
			errorIconLabel.setImage(null);
		} else {
			errorMessageLabel.setText(errorMessage);
			if (severity == IStatus.OK) {
				errorIconLabel.setImage(null);
			} else {
				if (severity == IStatus.ERROR)
					errorIconLabel.setImage(errorImage);
				else
					errorIconLabel.setImage(warningImage);
			}
		}

		// todo: Remove the below code if it remains unused.
		// Currently the OrderTicketControllerHelper is managing sendButton
		// state.
		//
		// if (severity == IStatus.ERROR) {
		// sendButton.setEnabled(false);
		// } else {
		// sendButton.setEnabled(true);
		// }
	}

	public void clearErrors() {
		for (Control aControl : decoratedInputControls) {
			Object cd;
			if (((cd = aControl.getData(CONTROL_DECORATOR_KEY)) != null)
					&& cd instanceof ControlDecoration) {
				ControlDecoration controlDecoration = ((ControlDecoration) cd);
				controlDecoration.hide();
			}

			aControl.setBackground(null);
		}
	}

	public void showErrorForControl(Control aControl, int severity,
			String message) {
		Object cd;
		if (((cd = aControl.getData(CONTROL_DECORATOR_KEY)) != null)
				&& cd instanceof ControlDecoration) {
			ControlDecoration controlDecoration = ((ControlDecoration) cd);
			if (severity == IStatus.OK) {
				controlDecoration.hide();
			} else {
				if (severity == IStatus.ERROR) {
					controlDecoration.setImage(errorImage);
				} else {
					controlDecoration.setImage(warningImage);
				}
				if (message != null) {
					controlDecoration.setDescriptionText(message);
				}
				controlDecoration.show();
			}
		}
	}

}

package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport;

import static org.marketcetera.photon.Messages.ADD_EXECUTION_REPORT_MXBOX_TITLE_ERROR;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.BrokerIDField;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.CustomFixField;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.CustomNoneFixField;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportContainer;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportField;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportFixFields;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportNoneFixField;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers.ExecutionReportFieldContentProvider;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers.ExecutionReportFieldLabelProvider;
import org.marketcetera.trade.FIXMessageWrapper;
import quickfix.Message;

/**
 * Creates Add Execution Report dialog
 * 
 * @author milan
 *
 */
public class AddExecutionReportDialog extends ReportDialog
{
	/** Execution report viewer*/
	private ExecutionReportViewer fExecutionReportViewer;

	
	/** Add execution report field */
	private Button fAddButton;
	
	/** Parent composite*/
	private Composite fValueComposite;

	/** Predefined fields for an execution report*/
	private ComboViewer fFieldCombo;
	
	/** Value\s for the selected field */
	private Control fValuesControl;
	
	/** Execution report data */
	private ExecutionReportContainer fExecutionReportFields = new ExecutionReportContainer();

	
	public AddExecutionReportDialog(Shell parentShell)
	{
		super(parentShell, Messages.ADD_EXECUTION_REPORT_DIALOG_DESCRIPTION.getText());
	}

	protected Control createDialogArea(Composite parent)
	{
		Composite parentComposite = createComposite(parent, new GridData(SWT.FILL, SWT.FILL, true, true));

		// Execution Report panel
		createExecutionReportPane(parentComposite);
		
		return parent;
	}

	private void createExecutionReportPane(Composite parent)
	{
		// Create table viewer
		createExecutionReportViewer(parent);
		
		// Create combos and buttons
		createExecutionReportControls(parent);
	}

	private void createExecutionReportViewer(Composite parent)
	{
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = SIZE;
		Composite viewerComposite = createComposite(parent, gridData);

		fExecutionReportViewer = new ExecutionReportViewer(this);
		fExecutionReportViewer.createViewer(viewerComposite, fExecutionReportFields);
	}

	/**
	 * Create dialog controls
	 * 
	 * @param parent
	 */
	private void createExecutionReportControls(Composite parent)
	{
		// Buttons container
		Composite buttonsComposite = createComposite(parent, new GridData(GridData.FILL_HORIZONTAL));
		buttonsComposite.setLayout(new GridLayout(2, false));
		
		fValueComposite = createComposite(buttonsComposite, new GridData(GridData.FILL_HORIZONTAL));
		fValueComposite.setLayout(new GridLayout(2, true));

		// Create field combo box
		createFieldComboViewer(fValueComposite);

		fValuesControl = new Text(fValueComposite, SWT.BORDER);
		fValuesControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
		fAddButton = new Button(buttonsComposite, SWT.PUSH);
		fAddButton.setText(Messages.ADD_EXECUTION_REPORT_DIALOG_BUTTON_ADD.getText());
		fAddButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				setAddButtonClicked(event);
			}
		});
	}

	/**
	 * Creates a viewer with a ComboBox as it's child
	 * component.
	 * 
	 * @param composite
	 */
	private void createFieldComboViewer(Composite composite)
	{
		fFieldCombo = new ComboViewer(composite, SWT.NONE);
		fFieldCombo.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fFieldCombo.setContentProvider(new ExecutionReportFieldContentProvider());
		fFieldCombo.setLabelProvider(new ExecutionReportFieldLabelProvider());
		
		final ExecutionReportFixFields existingFields = new ExecutionReportFixFields();
		fFieldCombo.setInput(existingFields);
		fFieldCombo.addSelectionChangedListener(new ISelectionChangedListener() 
		{
	        @Override
	        public void selectionChanged(SelectionChangedEvent event) 
	        {
	        	selectionChangedListener(event, existingFields);
	        }
	    });
		
		fFieldCombo.getCombo().addVerifyListener(new VerifyListener() 
		{
			@Override
			public void verifyText(VerifyEvent e) 
			{
				selectionTypedListener(e);
			}
		});
	}
	
	private void selectionChangedListener(SelectionChangedEvent event, ExecutionReportFixFields existingFields)
	{
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        ExecutionReportField reportField = (ExecutionReportField) selection.getFirstElement();

        String[] values = reportField.getValues();
        if(values == ExecutionReportField.NULL_VALUE)
        {
        	if(fValuesControl == null || fValuesControl.isDisposed())
        	{
        		fValuesControl = new Text(fValueComposite, SWT.BORDER);
        		fValuesControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        		((Text) fValuesControl).setText("");
        		fValueComposite.layout();
        		return;
        	}
        	if(fValuesControl instanceof Text)
        	{
        		((Text) fValuesControl).setText("");
        		return;
        	}
        	else
        	{
        		fValuesControl.dispose();
        		fValuesControl = new Text(fValueComposite, SWT.BORDER);
        		fValuesControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        		((Text) fValuesControl).setText("");
        		fValueComposite.layout();
        		return;        		
        	}
        }
        else{
        	if(fValuesControl == null || fValuesControl.isDisposed())
        	{
        		fValuesControl = new Combo(fValueComposite, SWT.READ_ONLY);
        		fValuesControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        		((Combo) fValuesControl).setItems(values);
        		fValueComposite.layout();
        		return;
        	}
        	if(fValuesControl instanceof Combo)
        	{
        		((Combo) fValuesControl).setItems(values);
        		return;
        	}
        	else
        	{
        		fValuesControl.dispose();
        		fValuesControl = new Combo(fValueComposite, SWT.READ_ONLY);
        		fValuesControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        		((Combo) fValuesControl).setItems(values);
        		fValueComposite.layout();
        		return;
        		
        	}
        }
	}
	
	/**
	 * Monitor the typing event and empty the values combo
	 * 
	 * @param event of type <code>VerifyEvent</code>
	 */
	private void selectionTypedListener(VerifyEvent event)
	{
		// Skip special keys
		if (event.keyCode != 0)
		{
        	if(fValuesControl instanceof Text)
        	{
        		((Text) fValuesControl).setText("");
        		return;
        	}
        	else
        	{
        		fValuesControl.dispose();
        		fValuesControl = new Text(fValueComposite, SWT.BORDER);
        		fValuesControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        		((Text) fValuesControl).setText("");
        		fValueComposite.layout();
        		return;        		
        	}
		}
		
	}
	
	private void setAddButtonClicked(SelectionEvent event)
	{
		Object selectedField = getComboData(fFieldCombo.getCombo());
		
		if(selectedField == null)
			return;

		if(selectedField instanceof ExecutionReportField)
		{
			ExecutionReportField reportField = (ExecutionReportField) selectedField;
			
			String selectedValue;
			
			if(fValuesControl instanceof Combo)
			{
				selectedValue = getComboValue((Combo) fValuesControl);
			}
			else
			{
				selectedValue = ((Text) fValuesControl).getText();
			}
						
			if(selectedValue == null)
			{
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
				messageBox.setText(ADD_EXECUTION_REPORT_MXBOX_TITLE_ERROR.getText());
				messageBox.setMessage(reportField.getValidateMessage());
				messageBox.open();
				return;
			}

			// Add execution report field
			reportField.setSelectedValue(selectedValue);
			
			//Field validation on add button pressed
			if(!reportField.validateValue())
			{
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
				messageBox.setText(ADD_EXECUTION_REPORT_MXBOX_TITLE_ERROR.getText());
				messageBox.setMessage(reportField.getValidateMessage());
				messageBox.open();
				return;
			}

			fExecutionReportFields.addExecutionReportField(reportField);

			// Refresh data model
			fExecutionReportViewer.update();
			
			// Clear combo boxes
			fFieldCombo.getCombo().deselectAll();
			if(fValuesControl instanceof Combo)
				((Combo) fValuesControl).deselectAll();
			else
				((Text) fValuesControl).setText("");
		}
	}
	
	@Override
	protected void okPressed() 
	{
		Message executionReport = fExecutionReportFields.createExecutionReport();
		/*BrokerID, BrokerOrderID, StrategyTag*/
		String BrokerID = null;
		ExecutionReportNoneFixField[] noneFixFields = fExecutionReportFields.getNoneFixFields();
		for(int i = 0; i < noneFixFields.length; i++){
			if(noneFixFields[i].getFieldName().equals(BrokerIDField.BROKER_ID_FIELD_NAME)){
				BrokerID = noneFixFields[i].getFieldValue();
			}
		}
		/*Missing brokerID validation*/
		if(BrokerID == null)
		{
			MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
			messageBox.setText(ADD_EXECUTION_REPORT_MXBOX_TITLE_ERROR.getText());
			messageBox.setMessage("Please add BrokerID field.");
			messageBox.open();
			return;
		}
		try 
		{
			ClientManager.getInstance().addReport(new FIXMessageWrapper(executionReport), new org.marketcetera.trade.BrokerID(BrokerID));
		} catch (ConnectionException e) 
		{
			MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
			messageBox.setText(ADD_EXECUTION_REPORT_MXBOX_TITLE_ERROR.getText());
			messageBox.setMessage(org.apache.commons.lang.exception.ExceptionUtils.getRootCauseMessage(e));
			messageBox.open();
			return;
		} 
		catch (ClientInitException e) 
		{
			PhotonPlugin.LOGGER.error("Client Init", e);
		}			
		super.okPressed();
	}
	
	public void removeFromExecutionReport(ExecutionReportField[] executionReportPairs)
	{
		// Remove from model
		fExecutionReportFields.removeExecutionReportFields(executionReportPairs);
		
		// Refresh data model
		fExecutionReportViewer.update();
	}
	
	/**
	 * Check selected combo element, skip selected if 
	 * element has been typed-in. Skip empty values
	 * 
	 * @param combo of type <code>Combo</code>
	 * @return selected or typed-in element of type <code>String</code>
	 */
	private Object getComboData(Combo combo)
	{
		int selectionIndex = combo.getSelectionIndex();
		
		if(selectionIndex == -1)
		{
			String fieldName = combo.getText();
			
			if(!fieldName.equals(EMPTY_VALUE))
			{
				try
				{
					Integer.parseInt(fieldName);
					return new CustomFixField(fieldName);
				}
				catch(NumberFormatException nfe)
				{
					return new CustomNoneFixField(fieldName);					
				}
			}
		} 
		else
		{
			return fFieldCombo.getElementAt(selectionIndex);
		}
		
		return null;
	}

	/**
	 * Check selected combo element, skip selected if 
	 * element has been typed-in. Skip empty values
	 * 
	 * @param combo of type <code>Combo</code>
	 * @return selected or typed-in element of type <code>String</code>
	 */
	private String getComboValue(Combo combo)
	{
		int selectionIndex = combo.getSelectionIndex();
		String selectedField = null;
		
		if(selectionIndex == -1)
		{
			selectedField = combo.getText();
			if(selectedField.equals(EMPTY_VALUE))
				return null;
		} 
		else
		{
			selectedField = combo.getItem(selectionIndex);
		}
		
		return selectedField;
	}


}	

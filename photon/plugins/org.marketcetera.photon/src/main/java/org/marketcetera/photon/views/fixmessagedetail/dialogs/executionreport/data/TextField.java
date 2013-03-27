package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;
import org.marketcetera.photon.Messages;

/**
 * Execution report text
 * 
 * @author Milos Djuric
 *
 */
public class TextField extends ExecutionReportField {

	private int FIELD;
	
	public TextField(int field)
	{
		FIELD = field;
	}
	
	@Override
	public int getField()
	{
		return FIELD;
	}

	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_TEXT.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() 
	{
		return fSelectedValue;
	}

}

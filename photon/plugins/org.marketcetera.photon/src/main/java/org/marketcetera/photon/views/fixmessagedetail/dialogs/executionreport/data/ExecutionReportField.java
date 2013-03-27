package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;


/**
 * Execution report field class representing a single 
 * row in the execution report table
 * 
 * @author milan
 *
 */
public abstract class ExecutionReportField
{
	protected String fSelectedValue;
	
	public static final String[] NULL_VALUE = null;
	
	public abstract int getField();
	
	public abstract String getFieldName();
	
	public abstract String[] getValues();
	
	abstract public Object getFieldValue();
	
	public void setSelectedValue(String selectedValue)
	{
		fSelectedValue = selectedValue;
	}
	
	public String getSelectedValue()
	{
		return fSelectedValue;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ExecutionReportField)
		{
			ExecutionReportField reportField = (ExecutionReportField) o;
			
			return getFieldName().equals(reportField.getFieldName());
		}
		
		return false;
	}
}

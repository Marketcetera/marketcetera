package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

/**
 * Custom execution report field. Both field name
 * and value are user-defined.
 * 
 * @author milan
 *
 */
public class CustomField extends ExecutionReportField 
{
	/** Custom fix field value */
	private final int FIELD = 1005;
	
	/** Custom field name */
	private final String fFieldName;
	
	public CustomField(String fieldName)
	{
		fFieldName = fieldName;
	}
	
	@Override
	public int getField() 
	{
		return FIELD;
	}
	
	@Override
	public String getFieldName() 
	{
		return fFieldName;
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() {
		return new org.marketcetera.quickfix.CustomField<String>(FIELD, fSelectedValue);
	}
}

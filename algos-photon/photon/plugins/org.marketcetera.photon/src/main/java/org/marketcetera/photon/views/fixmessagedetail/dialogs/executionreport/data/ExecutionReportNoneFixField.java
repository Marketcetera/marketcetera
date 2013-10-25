package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

public abstract class ExecutionReportNoneFixField extends ExecutionReportField {

	public String getFieldValue()
	{
		return fValue;
	}
	
	public boolean isFixField()
	{
		return false;
	}
}

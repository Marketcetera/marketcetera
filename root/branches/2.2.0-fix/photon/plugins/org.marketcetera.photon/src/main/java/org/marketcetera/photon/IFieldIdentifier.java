package org.marketcetera.photon;

public interface IFieldIdentifier {

	public Integer getFieldID();
	public Integer getGroupID();
	public Integer getGroupDiscriminatorID();
	public Object getGroupDiscriminatorValue();
}

package org.marketcetera.photon.model;

import org.eclipse.core.runtime.PlatformObject;
import org.marketcetera.core.InternalID;

public abstract class PositionProgress extends PlatformObject {
	
	public abstract String getName();

	public abstract Portfolio getParent();
	
	public abstract double getProgress();
	
}

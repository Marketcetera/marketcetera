package org.marketcetera.photon;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Control;

public class SWTDisposer {
	List<Control> controlList = new LinkedList<Control>();
	List<Resource> resourceList = new LinkedList<Resource>();
	
	public void addControl(Control toDispose){
		controlList.add(toDispose);
	}
	public void addResource(Resource toDispose){
		
	}
	public void disposeAll(){
		for (Control control : controlList) {
			control.dispose();
		}
		for (Resource resource : resourceList) {
			resource.dispose();
		}
	}
}

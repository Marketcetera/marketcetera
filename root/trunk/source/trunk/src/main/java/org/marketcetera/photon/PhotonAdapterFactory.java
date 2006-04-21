package org.marketcetera.photon;

import java.math.BigDecimal;
import java.math.MathContext;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.photon.model.Portfolio;
import org.marketcetera.photon.model.PositionEntry;

public class PhotonAdapterFactory implements IAdapterFactory {

	private IWorkbenchAdapter portfolioAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return ((Portfolio) o).getParent();
		}

		public String getLabel(Object o) {
			Portfolio portfolio = ((Portfolio) o);
			return portfolio.getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					Application.PLUGIN_ID, IImageKeys.PORTFOLIO);
		}

		public Object[] getChildren(Object o) {
			return ((Portfolio) o).getEntries();
		}
	};

	private IWorkbenchAdapter positionAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return ((PositionEntry) o).getParent();
		}

		public String getLabel(Object o) {
			PositionEntry entry = ((PositionEntry) o);
			BigDecimal progressBigDecimal = new BigDecimal(entry.getProgress());
			progressBigDecimal.round(new MathContext(2));
			progressBigDecimal = progressBigDecimal.multiply(new BigDecimal(100));
			return entry.getName() + " (" +progressBigDecimal+  "%)";
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return AbstractUIPlugin.imageDescriptorFromPlugin(
					Application.PLUGIN_ID, IImageKeys.EQUITY);
		}

		public Object[] getChildren(Object o) {
			return new Object[0];
		}
	};


	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof Portfolio)
			return portfolioAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof PositionEntry)
			return positionAdapter;
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}

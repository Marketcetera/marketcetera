package org.marketcetera.photon;

import java.math.BigDecimal;
import java.math.MathContext;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.Security;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.model.Portfolio;
import org.marketcetera.photon.model.PositionEntry;
import org.marketcetera.photon.views.FilterGroup;
import org.marketcetera.photon.views.FilterItem;
import org.marketcetera.photon.views.FiltersView;

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
			return PhotonPlugin.getImageDescriptor(IImageKeys.PORTFOLIO);
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
			progressBigDecimal = progressBigDecimal
					.multiply(new BigDecimal(100));
			return entry.getName() + " (" + progressBigDecimal + "%)";
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return PhotonPlugin.getImageDescriptor(IImageKeys.EQUITY);
		}

		public Object[] getChildren(Object o) {
			return new Object[0];
		}
	};

	private IWorkbenchAdapter messageHistoryAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return null;
		}

		public String getLabel(Object o) {
			return null;
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			return ((FIXMessageHistory) o).getHistory();
		}
	};

	private IWorkbenchAdapter messageAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return null;
		}

		public String getLabel(Object o) {
			return "Message";
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			return null;
		}
	};

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof Portfolio)
			return portfolioAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof PositionEntry)
			return positionAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof FIXMessageHistory)
			return messageHistoryAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof MessageHolder)
			return messageAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof FilterGroup)
			return groupAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof AccountID)
			return accountAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof FilterItem)
			return filterAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof Security)
			return securityAdapter;

		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

	// ///////////////////
	// Filter stuff

	private IWorkbenchAdapter accountAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return FiltersView.getRoot().getChildren()[0];
		}

		public String getLabel(Object o) {
			AccountID accountID = (AccountID) o;
			String nick = accountID.getAccountNickname();
			if (nick != null && !nick.equals("")) {
				nick = " (" + nick + ")";
			} else {
				nick = "";
			}
			return accountID.toString() + nick;
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return PhotonPlugin.getImageDescriptor(IImageKeys.ACCOUNT);
		}

		public Object[] getChildren(Object o) {
			return new Object[0];
		}
	};

	private IWorkbenchAdapter securityAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return FiltersView.getRoot().getChildren()[0];
		}

		public String getLabel(Object o) {
			Security aSecurity = (Security) o;
			return aSecurity.getSymbol();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return PhotonPlugin.getImageDescriptor(IImageKeys.EQUITY);
		}

		public Object[] getChildren(Object o) {
			return new Object[0];
		}
	};

	private IWorkbenchAdapter filterAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return FiltersView.getRoot().getChildren()[1];
		}

		public String getLabel(Object o) {
			FilterItem item = (FilterItem) o;
			return item.getName();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		public Object[] getChildren(Object o) {
			return new Object[0];
		}
	};

    private IWorkbenchAdapter groupAdapter = new IWorkbenchAdapter() {
        public Object getParent(Object o) {
            return ((FilterGroup)o).getParent();
        }

        public String getLabel(Object o) {
            return ((FilterGroup)o).getLabel();
        }


        public ImageDescriptor getImageDescriptor(Object object) {
        	return ((FilterGroup)object).getImageDescriptor();
        }

        public Object[] getChildren(Object o) {
            return ((FilterGroup)o).getChildren();
        }
    };

}

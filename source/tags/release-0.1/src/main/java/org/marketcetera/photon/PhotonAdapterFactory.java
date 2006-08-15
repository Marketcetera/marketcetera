package org.marketcetera.photon;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.model.MessageHolder;

public class PhotonAdapterFactory implements IAdapterFactory {


	protected IWorkbenchAdapter messageAdapter = new IWorkbenchAdapter() {
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
			return new Object[0];
		}
	};

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof MessageHolder)
			return messageAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof AccountID)
			return accountAdapter;
		if (adapterType == IWorkbenchAdapter.class
				&& adaptableObject instanceof MSymbol)
			return symbolAdapter;

		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

	// ///////////////////
	// Filter stuff

	private IWorkbenchAdapter accountAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return null;
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

	private IWorkbenchAdapter symbolAdapter = new IWorkbenchAdapter() {
		public Object getParent(Object o) {
			return null;
		}

		public String getLabel(Object o) {
			MSymbol aSymbol = (MSymbol) o;
			return aSymbol.toString();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return PhotonPlugin.getImageDescriptor(IImageKeys.EQUITY);
		}

		public Object[] getChildren(Object o) {
			return new Object[0];
		}
	};



}

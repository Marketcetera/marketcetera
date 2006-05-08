package org.marketcetera.photon.views;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.Security;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IFiltersListener;
import org.marketcetera.photon.IOrderActionListener;
import org.marketcetera.photon.PhotonAdapterFactory;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.OrdStatus;
import quickfix.field.Symbol;

@ClassVersion("$Id$")
public class FiltersView extends ViewPart implements IOrderActionListener {

    public static final String ID = "org.marketcetera.photon.views.FiltersView";

    public Set<AccountID> mExistingAccounts = new HashSet<AccountID>();
    public Set<Security> mExistingSymbols = new HashSet<Security>();

    private TreeViewer mTreeViewer;
    private static FilterGroup sRoot = new FilterGroup(null, "root");
    FilterGroup mAccountFilterGroup;
    FilterGroup mStatusFilterGroup;
    FilterGroup mSecurityFilterGroup;
	private IAdapterFactory adapterFactory = new PhotonAdapterFactory();

    public FiltersView() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void createPartControl(Composite parent) {
		Platform.getAdapterManager().registerAdapters(adapterFactory, FilterGroup.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, FilterItem.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, Security.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory, AccountID.class);

    	
        mTreeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
                                             | SWT.V_SCROLL);
        initializeFilters();
        getSite().setSelectionProvider(mTreeViewer);
        mTreeViewer.setLabelProvider(new WorkbenchLabelProvider());
        mTreeViewer.setContentProvider(new BaseWorkbenchContentProvider());
        mTreeViewer.setInput(sRoot);
        sRoot.addFiltersListener(
                new IFiltersListener() {
                    public void filtersChanged(FilterGroup fg, Object obj) {
                        refresh();
                    };
                });
        Application.getOrderManager().addOrderActionListener(this);
        

    }

    private void initializeFilters()
    {
        mAccountFilterGroup = new FilterGroup(sRoot, "Accounts");
        mSecurityFilterGroup = new FilterGroup(sRoot, "Securities");

        mStatusFilterGroup = new FilterGroup(sRoot, "Status");
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.NEW, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.NEW)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.PARTIALLY_FILLED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.PARTIALLY_FILLED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.FILLED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.FILLED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.DONE_FOR_DAY, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.DONE_FOR_DAY)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.CANCELED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.CANCELED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.REPLACED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.REPLACED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.PENDING_CANCEL, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.PENDING_CANCEL)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.STOPPED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.STOPPED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.REJECTED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.REJECTED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.SUSPENDED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.SUSPENDED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.PENDING_NEW, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.PENDING_NEW)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.CALCULATED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.CALCULATED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.EXPIRED, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.EXPIRED)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.ACCEPTED_FOR_BIDDING, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.ACCEPTED_FOR_BIDDING)));
        mStatusFilterGroup.addChild(new FIXFilterItem<Character>(OrdStatus.FIELD, OrdStatus.PENDING_REPLACE, FIXDataDictionaryManager.getHumanFieldValue(OrdStatus.FIELD, ""+OrdStatus.PENDING_REPLACE)));

        sRoot.addChild(mAccountFilterGroup);
        sRoot.addChild(mSecurityFilterGroup);
        sRoot.addChild(mStatusFilterGroup);
    }

    public void addAccount(AccountID id){
        if (!mExistingAccounts.contains(id)){
        mAccountFilterGroup.addChild(id);
            mExistingAccounts.add(id);
            refresh();
        }
    }

    public void addSecurity(Security sec){
        if (!mExistingSymbols.contains(sec)){
        mSecurityFilterGroup.addChild(sec);
            mExistingSymbols.add(sec);
            refresh();
        }
    }

    private void refresh() {
        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                mTreeViewer.refresh();
            }
        });
    }

    public void setFocus() {
        mTreeViewer.getControl().setFocus();
    }

    public static FilterGroup getRoot() {
        return sRoot;
    }

    public void orderActionTaken(Message message) {
    	try {
			String accountString = message.getString(Account.FIELD);
			mAccountFilterGroup.addChild(new FIXFilterItem<String>(Account.FIELD, accountString, accountString));
    	} catch (FieldNotFound e) {
			// do nothing
		}
    	try {
			String symbolString = message.getString(Symbol.FIELD);
			mSecurityFilterGroup.addChild(new FIXFilterItem<String>(Symbol.FIELD, symbolString, symbolString));
		} catch (FieldNotFound e) {
			// do nothing
		}
    }

}

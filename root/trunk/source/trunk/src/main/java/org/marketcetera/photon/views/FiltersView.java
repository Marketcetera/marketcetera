package org.marketcetera.photon.views;


import java.util.HashSet;
import java.util.Set;

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
import org.marketcetera.photon.TextUtils;
import org.marketcetera.photon.model.FilterGroup;
import org.marketcetera.photon.model.FilterItem;
import org.marketcetera.symbology.Exchange;

import quickfix.field.OrdStatus;

@ClassVersion("$Id$")
public class FiltersView extends ViewPart implements IOrderActionListener {

    public static final String ID = "org.marketcetera.photon.views.filters";

    public Set<AccountID> mExistingAccounts = new HashSet<AccountID>();
    public Set<Security> mExistingSymbols = new HashSet<Security>();

    private TreeViewer mTreeViewer;
    private static FilterGroup sRoot = new FilterGroup(null, "root");
    FilterGroup mAccountFilterGroup;
    FilterGroup mStatusFilterGroup;
    FilterGroup mSecurityFilterGroup;

    public FiltersView() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void createPartControl(Composite parent) {
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

    }

    private void initializeFilters()
    {
        mAccountFilterGroup = new FilterGroup(sRoot, "Accounts");
        mSecurityFilterGroup = new FilterGroup(sRoot, "Securities");

        mStatusFilterGroup = new FilterGroup(sRoot, "Status");
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.NEW, TextUtils.getOrdStatusName(OrdStatus.NEW)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.PARTIALLY_FILLED, TextUtils.getOrdStatusName(OrdStatus.PARTIALLY_FILLED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.FILLED, TextUtils.getOrdStatusName(OrdStatus.FILLED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.DONE_FOR_DAY, TextUtils.getOrdStatusName(OrdStatus.DONE_FOR_DAY)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.CANCELED, TextUtils.getOrdStatusName(OrdStatus.CANCELED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.REPLACED, TextUtils.getOrdStatusName(OrdStatus.REPLACED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.PENDING_CANCEL, TextUtils.getOrdStatusName(OrdStatus.PENDING_CANCEL)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.STOPPED, TextUtils.getOrdStatusName(OrdStatus.STOPPED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.REJECTED, TextUtils.getOrdStatusName(OrdStatus.REJECTED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.SUSPENDED, TextUtils.getOrdStatusName(OrdStatus.SUSPENDED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.PENDING_NEW, TextUtils.getOrdStatusName(OrdStatus.PENDING_NEW)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.CALCULATED, TextUtils.getOrdStatusName(OrdStatus.CALCULATED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.EXPIRED, TextUtils.getOrdStatusName(OrdStatus.EXPIRED)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.ACCEPTED_FOR_BIDDING, TextUtils.getOrdStatusName(OrdStatus.ACCEPTED_FOR_BIDDING)));
        mStatusFilterGroup.addChild(new FilterItem<Character>(OrdStatus.PENDING_REPLACE, TextUtils.getOrdStatusName(OrdStatus.PENDING_REPLACE)));

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

    public void orderActionTaken(String symbol, Exchange exchange, String currency, AccountID id) {
        if (symbol!= null) addSecurity(new Security(symbol, null));
        if (id != null) addAccount(id);
    }

}

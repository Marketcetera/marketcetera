package org.marketcetera.photon.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.IOrderActionListener;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.Symbol;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swt.EventListViewer;

@ClassVersion("$Id$")
public class FiltersView extends ViewPart implements IOrderActionListener {

    public static final String ID = "org.marketcetera.photon.views.FiltersView";


	private ThreadedMatcherEditor<MessageHolder> threadedMatcherEditor;
	private CompositeMatcherEditor<MessageHolder> topMatcherEditor;


	
	private EventList<MatcherEditor> symbolMatchers = new BasicEventList<MatcherEditor>();
	private EventListViewer symbolViewer;
    private List symbolSWTList;
	private CompositeMatcherEditor<MessageHolder> accountMatcherEditor;

	
	
	private EventList<MatcherEditor> accountMatchers = new BasicEventList<MatcherEditor>();
    private List accountSWTList;
	private EventListViewer accountViewer;
	private CompositeMatcherEditor<MessageHolder> symbolMatcherEditor;


	private FIXCheckboxMatcherEditor ordStatusMatcherEditor;
	private FIXCheckboxMatcherEditor msgTypeMatcherEditor;



	
    public FiltersView() {
        super();

        ordStatusMatcherEditor = new FIXCheckboxMatcherEditor();
		msgTypeMatcherEditor = new FIXCheckboxMatcherEditor();

    }

    public void createPartControl(Composite parent) {
    	FillLayout marginFillLayout = new FillLayout(SWT.VERTICAL);
    	marginFillLayout.marginHeight = 5;
    	marginFillLayout.marginWidth = 5;
    	
    	parent.setLayout(marginFillLayout);
    	
    	Group accountGroup = new Group(parent, SWT.NONE);
    	accountGroup.setLayout(marginFillLayout);
    	accountGroup.setText("Account");
    	accountSWTList = new List(accountGroup, SWT.MULTI);
    	accountViewer = new EventListViewer(accountMatchers, accountSWTList);
    	
    	Group symbolGroup = new Group(parent, SWT.NONE);
    	symbolGroup.setLayout(marginFillLayout);
    	symbolGroup.setText("Symbol");
    	symbolSWTList = new List(symbolGroup, SWT.MULTI);
    	symbolViewer = new EventListViewer(symbolMatchers, symbolSWTList);
    	
    	
    	
        GridLayout marginGridLayout = new GridLayout();
        marginGridLayout.numColumns = 2;
        marginGridLayout.marginHeight = 5;
        marginGridLayout.marginWidth = 5;

        Group ordStatusGroup = new Group(parent, SWT.NONE);
    	ordStatusGroup.setText("Order Status");
        ordStatusGroup.setLayout(marginGridLayout);
    	
        initializeOrdStatusButtons(ordStatusGroup, ordStatusMatcherEditor);

    	Group msgTypeGroup = new Group(parent, SWT.NONE);
    	msgTypeGroup.setText("Message Type");
    	msgTypeGroup.setLayout(marginGridLayout);
  
        initializeMsgTypeButtons(msgTypeGroup, msgTypeMatcherEditor);
        
        
       createMatcherEditors();
       initContent();

    }
    
	private void initContent() {
        accountMatchers.add(new FIXMatcherEditor<String>(Account.FIELD, null, "<NO ACCOUNT>"));
        accountMatchers.add(new FIXMatcherEditor<String>(Account.FIELD, "FOO", "FOO"));
        accountMatchers.add(new FIXMatcherEditor<String>(Account.FIELD, "QWER", "QWER"));

        symbolMatchers.add(new FIXMatcherEditor<String>(Symbol.FIELD, null, "<NO SYMBOL>"));
        ordStatusMatcherEditor.getMatcherEditors().add(new FIXMatcherEditor<Character>(OrdStatus.FIELD, null, "Missing OrdStatus"));
	}
	
	private void createMatcherEditors() {
		topMatcherEditor = new CompositeMatcherEditor<MessageHolder>();
        threadedMatcherEditor = new ThreadedMatcherEditor<MessageHolder>(topMatcherEditor);

        accountMatcherEditor = new CompositeMatcherEditor<MessageHolder>(accountViewer.getSelected());
		accountMatcherEditor.setMode(CompositeMatcherEditor.OR);
        topMatcherEditor.getMatcherEditors().add(accountMatcherEditor);
        
        symbolMatcherEditor = new CompositeMatcherEditor<MessageHolder>(symbolViewer.getSelected());
        symbolMatcherEditor.setMode(CompositeMatcherEditor.OR);
        topMatcherEditor.getMatcherEditors().add(symbolMatcherEditor);

        topMatcherEditor.getMatcherEditors().add(ordStatusMatcherEditor);
        topMatcherEditor.getMatcherEditors().add(msgTypeMatcherEditor);
	}

    private void initializeOrdStatusButtons(Group ordStatusGroup, FIXCheckboxMatcherEditor matcherStore)
    {
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.NEW);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.PARTIALLY_FILLED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.FILLED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.DONE_FOR_DAY);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.CANCELED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.REPLACED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.PENDING_CANCEL);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.STOPPED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.REJECTED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.SUSPENDED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.PENDING_NEW);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.CALCULATED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.EXPIRED);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.ACCEPTED_FOR_BIDDING);
		createFIXCheckbox(ordStatusGroup, matcherStore, OrdStatus.FIELD, OrdStatus.PENDING_REPLACE);
    }

    private void initializeMsgTypeButtons(Group msgTypeGroup, FIXCheckboxMatcherEditor matcherStore)
    {
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.DONT_KNOW_TRADE);
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.EXECUTION_REPORT);
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.ORDER_SINGLE);
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.ORDER_STATUS_REQUEST);
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.ORDER_CANCEL_REJECT);
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.ORDER_CANCEL_REQUEST);
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.ORDER_CANCEL_REPLACE_REQUEST	);
		createFIXCheckbox(msgTypeGroup, matcherStore, MsgType.FIELD, MsgType.HEARTBEAT);
    }

	private <T> void createFIXCheckbox(Group ordStatusGroup, FIXCheckboxMatcherEditor matcherStore,
			int fieldTag, T fieldValue) 
	{
		Button aButton;
		aButton = new Button(ordStatusGroup, SWT.CHECK);
		aButton.setText(FIXDataDictionaryManager.getHumanFieldValue(fieldTag, ""+fieldValue));
    	aButton.setSelection(true);
    	FIXMatcherEditor<T> matcherEditor = new FIXMatcherEditor<T>(fieldTag, fieldValue, "");
		aButton.setData(matcherEditor);
    	aButton.addSelectionListener(matcherStore);
    	matcherStore.getMatcherEditors().add(matcherEditor);
	}

    public void addAccount(AccountID id){
        String acctString = id.toString();
        addAccount(acctString);
    }
    public void addAccount(String acctString){
		if (accountSWTList.indexOf(acctString) < 0){
        	accountSWTList.add(acctString);
        }
    }

    public void addSymbol(MSymbol symbol){
        String symbolString = symbol.toString();
		addSymbol(symbolString);
    }

	private void addSymbol(String symbolString) {
		if (symbolSWTList.indexOf(symbolString) < 0){
			symbolSWTList.add(symbolString);
        }
	}

    private void refresh() {
        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
            }
        });
    }

    public void orderActionTaken(Message message) {
    	try {
			String accountString = message.getString(Account.FIELD);
			addAccount(accountString);
    	} catch (FieldNotFound e) {
			// do nothing
		}
    	try {
			String symbolString = message.getString(Symbol.FIELD);
			addSymbol(symbolString);
		} catch (FieldNotFound e) {
			// do nothing
		}
    }

	@Override
	public void setFocus() {
		accountSWTList.setFocus();
	}

	public ThreadedMatcherEditor<MessageHolder> getMatcherEditor() {
		return threadedMatcherEditor;
	}


}

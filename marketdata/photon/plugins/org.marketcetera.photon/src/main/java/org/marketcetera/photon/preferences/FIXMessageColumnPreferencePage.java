package org.marketcetera.photon.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.AveragePriceView;
import org.marketcetera.photon.views.FIXMessagesView;
import org.marketcetera.photon.views.FillsView;
import org.marketcetera.photon.views.OpenOrdersView;
import org.marketcetera.quickfix.FIXMessageUtil;

/**
 * 
 * @author caroline.leung@softwaregoodness.com
 *
 */
public class FIXMessageColumnPreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage, Messages
{

	public static final String COLUMN_FILTER_TEXT = "column.filter.text"; //$NON-NLS-1$

	public static final String FIX_MESSAGE_DETAIL_PREFERENCE = "fix.message.detail"; //$NON-NLS-1$

	public static final String ID = "org.marketcetera.photon.preferences.FIXMessageColumnPreferencePage"; //$NON-NLS-1$
	private static final int INVALID_FIELD_ID = -1;

	private Combo subPageCombo;

	private Text columnFilterText;

	private Text customFixFieldIDText;

	private Button customFixFieldInputButton;

	private SelectionListener selectionListener;
	private MouseListener mouseListener;

	private ModifyListener modifyListener;
	private FIXMessageColumnChooserEditor fixMsgFieldsChooser;
	
	private Button clearFilterButton;

	public FIXMessageColumnPreferencePage() {
		super(FLAT);
		setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors() {
		createMsgTypesCombo(getFieldEditorParent());
		createColumnFilterText(getFieldEditorParent());

		String subPageID = FIXMessageColumnPrefsSubPageType.getIDFromName(subPageCombo.getText());
		
		fixMsgFieldsChooser = new FIXMessageColumnChooserEditor(FIX_MESSAGE_DETAIL_PREFERENCE,
				                                                FIX_MESSAGE_DETAIL_PREFERENCE_LABEL.getText(),
				                                                getFieldEditorParent(),
				                                                subPageID);
		addField(fixMsgFieldsChooser);
		
		createCustomFixFieldIDText(getFieldEditorParent());
		
	}

	private void createMsgTypesCombo(Composite parent) {
		parent.setLayout(new FormLayout());

		Label viewFixMsgTypeLabel = new Label(parent, SWT.NONE);
		viewFixMsgTypeLabel.setText(VIEW_LABEL.getText());

		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0);
		labelFormData.top = new FormAttachment(0);
		viewFixMsgTypeLabel.setLayoutData(labelFormData);

		subPageCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		String[] msgTypes = getSubPageTypes();
		subPageCombo.setItems(msgTypes);
		subPageCombo.setText(msgTypes[0]);

		FormData comboFormData = new FormData();
		comboFormData.left = new FormAttachment(viewFixMsgTypeLabel, 10);
		comboFormData.top = new FormAttachment(0);
		subPageCombo.setLayoutData(comboFormData);
		
		subPageCombo.addSelectionListener(getSelectionListener());
	}

	private void createColumnFilterText(Composite parent) {
		parent.setLayout(new FormLayout());

		Label availableColumnsLabel = new Label(parent, SWT.NONE);
		availableColumnsLabel.setText(AVAILABLE_COLUMNS_LABEL.getText());

		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0);
		labelFormData.top = new FormAttachment(subPageCombo, 20);
		availableColumnsLabel.setLayoutData(labelFormData);

		Label columnFilterLabel = new Label(parent, SWT.NONE);
		columnFilterLabel.setText(COLUMN_FILTER_LABEL.getText());

		FormData columnFilterLabelFormData = new FormData();
		columnFilterLabelFormData.left = new FormAttachment(0);
		columnFilterLabelFormData.top = new FormAttachment(
				availableColumnsLabel, 10);
		columnFilterLabel.setLayoutData(columnFilterLabelFormData);

		columnFilterText = new Text(parent, SWT.BORDER);

		FormData filterFormData = new FormData();
		filterFormData.left = new FormAttachment(columnFilterLabel);
		filterFormData.top = new FormAttachment(availableColumnsLabel, 10);
		filterFormData.width = EclipseUtils.getTextAreaSize(columnFilterText,
		                                                    ACCOUNT_TYPE_LABEL.getText(),
		                                                    0,
		                                                    1.0).x;
		columnFilterText.setLayoutData(filterFormData);
		
		{
			clearFilterButton = new Button(parent, SWT.PUSH);
			FormData data = new FormData();
			data.left = new FormAttachment(columnFilterText, 5);
			data.top = new FormAttachment(availableColumnsLabel, 8); 
			clearFilterButton.setLayoutData(data);
			clearFilterButton.setText(CLEAR_LABEL.getText());
			clearFilterButton.pack();
			clearFilterButton.setEnabled(false);	
		}
		columnFilterText.addModifyListener(getModifyListener());
		addFilterListeners();
	}
	
	private void addFilterListeners() {
		clearFilterButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				columnFilterText.setText(""); //$NON-NLS-1$
				fixMsgFieldsChooser.resetFilter();
			}
		});		
		columnFilterText.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				fixMsgFieldsChooser.applyFilter(columnFilterText.getText());
			}
		});
	}

	private void createCustomFixFieldIDText(Composite parent) {
		parent.setLayout(new FormLayout());

		Label customFixFieldIDLabel = new Label(parent, SWT.NONE);
		customFixFieldIDLabel.setText(CUSTOM_FIX_FIELD_ID_LABEL.getText());

		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0);
		customFixFieldIDLabel.setLayoutData(labelFormData);

		customFixFieldIDText = new Text(parent, SWT.BORDER);

		FormData fieldIDFormData = new FormData();
		fieldIDFormData.left = new FormAttachment(0);
		fieldIDFormData.top = new FormAttachment(customFixFieldIDLabel, 2);
		fieldIDFormData.bottom = new FormAttachment(100);
		fieldIDFormData.width = EclipseUtils.getTextAreaSize(
				customFixFieldIDText, "1000000", 0, 1.0).x; //$NON-NLS-1$
		customFixFieldIDText.setLayoutData(fieldIDFormData);
		customFixFieldIDText.addModifyListener(getModifyListener());

		customFixFieldInputButton = new Button(parent, SWT.PUSH);
		customFixFieldInputButton.setText("Add Custom"); //$NON-NLS-1$

		FormData buttonFormData = new FormData();
		buttonFormData.left = new FormAttachment(
				customFixFieldIDText, 5);
		buttonFormData.top = new FormAttachment(
				customFixFieldIDLabel, 2);
		buttonFormData.bottom = new FormAttachment(100);
		customFixFieldInputButton.setLayoutData(buttonFormData);
		customFixFieldInputButton.addMouseListener(getMouseListener());
		customFixFieldInputButton.setEnabled(false);
	}
	
	private SelectionListener getSelectionListener() {
		if (selectionListener == null) {
			createSelectionListener();
		}
		return selectionListener;
	}
	
	private void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == subPageCombo) {
					String subPageID = FIXMessageColumnPrefsSubPageType.getIDFromName(subPageCombo.getText());
					fixMsgFieldsChooser.changeSubPage(subPageID);
				} 
			}
		};
	}

	private ModifyListener getModifyListener() {
		if (modifyListener == null) {
			createModifyListener();
		}
		return modifyListener;
	}
	
	private void createModifyListener() {
		modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Widget widget = event.widget;
				if (widget == customFixFieldIDText) {
					boolean hasText = hasText(customFixFieldIDText);
					customFixFieldInputButton.setEnabled(hasText);
				} else if (widget == columnFilterText) {
					boolean hasText = hasText(columnFilterText);					
					clearFilterButton.setEnabled(hasText);					
				}
			}
		};
	}
	
	private boolean hasText(Text textBox) {
		String text = textBox.getText();
		boolean hasText = (text != null) && (!text.trim().equals("")); 	 //$NON-NLS-1$
		return hasText;
	}

	private MouseListener getMouseListener() {
		if (mouseListener == null) {
			createMouseListener();
		}
		return mouseListener;
	}
	
	private void createMouseListener() {
		mouseListener = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				Widget widget = event.widget;
				if (widget == customFixFieldInputButton) {
					createCustomFixField();
				}
			}
		};
	}
		
	private void createCustomFixField() {
		String idAsText = customFixFieldIDText.getText();
		if (idAsText != null && idAsText.length() > 0) {
			int fieldID = INVALID_FIELD_ID;
			try {
				fieldID = Integer.parseInt(idAsText);
			} catch (NumberFormatException e) {
				PhotonPlugin.getMainConsoleLogger().warn(CUSTOM_FIELD_ID_INVALID.getText(idAsText));
				return;
			}
			if (fieldID < 0) {
				PhotonPlugin.getMainConsoleLogger().warn(CUSTOM_FIELD_ID_NEGATIVE.getText(idAsText));
			} else if (FIXMessageUtil.isValidField(fieldID)) {
				String fixField = PhotonPlugin.getDefault().getFIXDataDictionary().getHumanFieldName(fieldID);
				PhotonPlugin.getMainConsoleLogger().warn(CUSTOM_FIELD_CONFLICT.getText(idAsText,
				                                                                       fixField));
				return;
			} else {
				fixMsgFieldsChooser.addCustomFieldToAvailableFieldsList(fieldID);
			}
		}
		customFixFieldIDText.setText(""); //$NON-NLS-1$
	}
	@Override
	public boolean performOk() {
		try {
			super.performOk(); 
			((ScopedPreferenceStore) getPreferenceStore()).save(); 
		} catch (IOException e) {
			// TODO: do something
		}
		return super.performOk();
	}

	private String[] getSubPageTypes() {
		FIXMessageColumnPrefsSubPageType[] subPageIDs = FIXMessageColumnPrefsSubPageType.values();
		List<String> typeNames = new ArrayList<String>();
		for (FIXMessageColumnPrefsSubPageType type : subPageIDs)
		{
			typeNames.add(type.toString());			
		}
		String[] typeArray = new String[typeNames.size()];
		return typeNames.toArray(typeArray);
	}
	
	
	public enum FIXMessageColumnPrefsSubPageType
	{
		AVERAGE_PRICE(AVERAGE_PRICE_LABEL.getText(),
		              AveragePriceView.ID),
		FILLS(FILLS_LABEL.getText(),
		      FillsView.ID),
		FIX_MESSAGES(FIX_MESSAGES_LABEL.getText(),
		             FIXMessagesView.ID),
		OPEN_ORDERS(OPEN_ORDERS_LABEL.getText(),
		            OpenOrdersView.ID);
		
		private String name;
		private String id;

		FIXMessageColumnPrefsSubPageType(String name, String id){
			this.name = name;
			this.id = id;
		}

		public String toString() {
			return name;
		}
		
		public String getName() {
			return name;
		}
		
		public String getID() {
			return id;
		}
		
		public static String getIDFromName(String name) {
			FIXMessageColumnPrefsSubPageType[] prefTypes = FIXMessageColumnPrefsSubPageType.values();
			for(FIXMessageColumnPrefsSubPageType prefType : prefTypes) {
				if(prefType.getName() != null && prefType.getName().equals(name)) {
					return prefType.getID();
				}
			}
			return null;
		}
	}
}

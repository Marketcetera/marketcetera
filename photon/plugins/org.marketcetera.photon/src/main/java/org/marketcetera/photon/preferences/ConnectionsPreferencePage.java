package org.marketcetera.photon.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;

/**
 * Connection Preferences.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ConnectionsPreferencePage
        extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    /**
     * Create a new ConnectionsPreferencePage instance.
     */
    public ConnectionsPreferencePage()
    {
        super(GRID);
        setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
    }
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench inWorkbench) {}
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
     */
    @Override
    public boolean performOk()
    {
        dareJmsUrlEditor.setStringValue(dareJmsUrlEditor.getStringValue().trim());
        dareWebServiceHostEditor.setStringValue(dareWebServiceHostEditor.getStringValue().trim());
        nexusWebServiceHostEditor.setStringValue(nexusWebServiceHostEditor.getStringValue().trim());
        return super.performOk();
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    @Override
    protected void createFieldEditors()
    {
        Group dareGroup = new Group(getFieldEditorParent(),SWT.NONE);
        GridDataFactory.fillDefaults().span(2,1).grab(true,false).applyTo(dareGroup);
        GridLayoutFactory.swtDefaults().applyTo(dareGroup);
        dareGroup.setText(Messages.CONNECTION_PREFERENCES_SERVER_LABEL.getText());
        Composite dareComposite = new Composite(dareGroup,SWT.NONE);
        GridDataFactory.fillDefaults().grab(true,false).applyTo(dareComposite);
        dareJmsUrlEditor = new UrlFieldEditor(PhotonPreferences.JMS_URL,
                                          Messages.CONNECTION_PREFERENCES_JMS_URL_LABEL.getText(),
                                          dareComposite);
        addField(dareJmsUrlEditor);
        dareWebServiceHostEditor = new UrlFieldEditor(PhotonPreferences.WEB_SERVICE_HOST,
                                                      Messages.CONNECTION_PREFERENCES_WEB_SERVICE_HOST_LABEL.getText(),
                                                      dareComposite);
        addField(dareWebServiceHostEditor);
        dareWebServicePortEditor = new IntegerFieldEditor(PhotonPreferences.WEB_SERVICE_PORT,
                                                          Messages.CONNECTION_PREFERENCES_WEB_SERVICE_PORT_LABEL.getText(),
                                                          dareComposite);
        addField(dareWebServicePortEditor);
        // begin
        Group nexusGroup = new Group(getFieldEditorParent(),SWT.NONE);
        GridDataFactory.fillDefaults().span(2,1).grab(true,false).applyTo(nexusGroup);
        GridLayoutFactory.swtDefaults().applyTo(nexusGroup);
        nexusGroup.setText(Messages.CONNECTION_PREFERENCES_NEXUS_SERVER_LABEL.getText());
        Composite nexusComposite = new Composite(nexusGroup,SWT.NONE);
        GridDataFactory.fillDefaults().grab(true,false).applyTo(nexusComposite);
        nexusWebServiceHostEditor = new UrlFieldEditor(PhotonPreferences.NEXUS_WEB_SERVICE_HOST,
                                                       Messages.CONNECTION_PREFERENCES_NEXUS_WEB_SERVICE_HOST_LABEL.getText(),
                                                       nexusComposite);
        addField(nexusWebServiceHostEditor);
        nexusWebServicePortEditor = new IntegerFieldEditor(PhotonPreferences.NEXUS_WEB_SERVICE_PORT,
                                                           Messages.CONNECTION_PREFERENCES_NEXUS_WEB_SERVICE_PORT_LABEL.getText(),
                                                           nexusComposite);
        addField(nexusWebServicePortEditor);
        // end
        orderIDPrefixEditor = new StringFieldEditor(PhotonPreferences.ORDER_ID_PREFIX,
                                                    Messages.ORDER_ID_PREFIX_LABEL.getText(),
                                                    getFieldEditorParent());
        addField(orderIDPrefixEditor);
    }
    /**
     * 
     */
    public static final String ID = "org.marketcetera.photon.preferences.connections"; //$NON-NLS-1$
    /**
     * 
     */
    private UrlFieldEditor dareJmsUrlEditor;
    /**
     * 
     */
    private StringFieldEditor orderIDPrefixEditor;
    /**
     * 
     */
    private UrlFieldEditor dareWebServiceHostEditor;
    /**
     * 
     */
    private IntegerFieldEditor dareWebServicePortEditor;
    /**
     * 
     */
    private UrlFieldEditor nexusWebServiceHostEditor;
    /**
     * 
     */
    private IntegerFieldEditor nexusWebServicePortEditor;
}

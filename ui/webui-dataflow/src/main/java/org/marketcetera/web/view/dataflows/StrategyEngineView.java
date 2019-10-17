package org.marketcetera.web.view.dataflows;

import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.service.dataflow.DataFlowClientService;
import org.marketcetera.web.service.dataflow.DataFlowClientServiceInstance;
import org.marketcetera.web.view.AbstractGridView;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.marketcetera.web.view.PagedDataContainer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/* $License$ */

/**
 * Provides a view of Strategy Engines.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyEngineView
        extends AbstractGridView<DecoratedStrategyEngine>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return "Data Flows";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getGrid().addSelectionListener(inEvent -> {
            DecoratedStrategyEngine selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                // TODO permission check before adding action to dropdown
                getActionSelect().setReadOnly(false);
                if(selectedObject.isConnected()) {
                    getActionSelect().addItems(ACTION_DISCONNECT,
                                               ACTION_DATAFLOWS);
                } else {
                    getActionSelect().addItem(ACTION_CONNECT);
                }
                getActionSelect().addItems(ACTION_EDIT,
                                           ACTION_DELETE);
            }
        });
    }
    /**
     * Create a new StrategyEngineView instance.
     *
     * @param inViewProperties
     */
    StrategyEngineView(Properties inViewProperties)
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        try {
            DecoratedStrategyEngine selectedItem = getSelectedItem();
            if(selectedItem == null || inEvent.getProperty().getValue() == null) {
                return;
            }
            String action = String.valueOf(inEvent.getProperty().getValue());
            SLF4JLoggerProxy.info(this,
                                  "{}: {} {} '{}'",
                                  String.valueOf(VaadinSession.getCurrent().getAttribute(SessionUser.class)),
                                  getViewName(),
                                  action,
                                  selectedItem.getName());
            switch(action) {
                case ACTION_EDIT:
                    createOrEdit(selectedItem,
                                 false);
                    break;
                case ACTION_CONNECT:
                    DataFlowClientServiceInstance serviceInstance = DataFlowClientService.getInstance().getServiceInstance(selectedItem);
                    if(!serviceInstance.connect()) {
                        Notification.show("Connection Error",
                                          "Unable to connect to " + selectedItem,
                                          Type.ERROR_MESSAGE);
                    }
                    break;
                case ACTION_DELETE:
                    Collection<DecoratedStrategyEngine> existingStrategyEngines = DataFlowClientService.getInstance().getStrategyEngines();
                    existingStrategyEngines.remove(selectedItem);
                    DataFlowClientService.getInstance().setStrategyEngines(existingStrategyEngines);
                    // intentionally fall through to disconnect
                case ACTION_DISCONNECT:
                    serviceInstance = DataFlowClientService.getInstance().getServiceInstance(selectedItem);
                    serviceInstance.disconnect();
                    break;
                case ACTION_DATAFLOWS:
                    ModuleView moduleView = new ModuleView(selectedItem);
                    webMessageService.post(new NewWindowEvent() {
                        @Override
                        public String getWindowTitle()
                        {
                            return moduleView.getViewSubjectName();
                        }
                        @Override
                        public String toString()
                        {
                            return "NewModuleViewEvent: " + selectedItem.getName();
                        }
                        @Override
                        public ContentViewFactory getViewFactory()
                        {
                            return new ContentViewFactory() {
                                @Override
                                public ContentView create(Properties inViewProperties)
                                {
                                    return moduleView;
                                }
                            };
                        }
                    });
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported action: " + action);
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onCreateNew(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    protected void onCreateNew(ClickEvent inEvent)
    {
        // create a new SE object
        DecoratedStrategyEngine newStrategyEngine = new DecoratedStrategyEngine();
        createOrEdit(newStrategyEngine,
                     true);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("name",
                             "connected",
                             "hostname",
                             "port",
                             "url");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Data Flow";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createDataContainer()
     */
    @Override
    protected PagedDataContainer<DecoratedStrategyEngine> createDataContainer()
    {
        return new StrategyEnginePagedDataContainer(this);
    }
    /**
     * Create or edit the given strategy engine.
     *
     * @param inStrategyEngine a <code>DecoratedStrategyEngine</code> value
     * @param inIsNew a <code>boolean</code> value
     */
    private void createOrEdit(DecoratedStrategyEngine inStrategyEngine,
                              boolean inIsNew)
    {
        final Window formWindow = new Window((inIsNew?"Create ":"Edit ")+getViewSubjectName());
        VerticalLayout content = new VerticalLayout();
        content.setMargin(true);
        formWindow.setContent(content);
        formWindow.center();
        formWindow.setModal(true);
        formWindow.setSizeUndefined();
        formWindow.setClosable(false);
        formWindow.setDraggable(false);
        formWindow.setResizable(false);
        final FormLayout fieldLayout = new FormLayout();
        fieldLayout.setMargin(true);
        fieldLayout.setSizeFull();
        final TextField nameField = new TextField("Name");
        nameField.setNullRepresentation("");
        nameField.setDescription("Unique name value");
        nameField.setRequired(true);
        nameField.setRequiredError("Name is required");
        nameField.setValue(inStrategyEngine.getName());
        nameField.addValidator(inValue -> {
            if(inValue == null) {
                throw new InvalidValueException("Name is required");
            }
        });
        nameField.addValueChangeListener(inEvent -> {
            nameField.setValidationVisible(true);
        });
        nameField.setValidationVisible(false);
        final TextField hostnameField = new TextField("Hostname");
        final TextField portField = new TextField("Port");
        final Button testConnectionButton = new Button("Test Connection");
        final Label testConnectionLabel = new Label();
        testConnectionLabel.setVisible(false);
        hostnameField.setNullRepresentation("");
        hostnameField.setDescription("Strategy Engine hostname");
        hostnameField.setRequired(true);
        hostnameField.setValue(inStrategyEngine.getHostname());
        hostnameField.setRequiredError("Hostname is required");
        hostnameField.addValidator(inValue -> {
            if(inValue == null) {
                throw new InvalidValueException("Hostname is required");
            }
            if(!inIsNew) {
                return;
            }
        });
        hostnameField.addValueChangeListener(inEvent -> {
            hostnameField.setValidationVisible(true);
            if(!hostnameField.isEmpty()) {
                testConnectionButton.setReadOnly(false);
            }
        });
        hostnameField.setValidationVisible(false);
        if(hostnameField.isEmpty()) {
            testConnectionButton.setReadOnly(true);
        } else {
            testConnectionButton.setReadOnly(false);
        }
        portField.setReadOnly(false);
        portField.setDescription("Strategy Engine port");
        portField.setValue(inStrategyEngine.getPort()==0?"":String.valueOf(inStrategyEngine.getPort()));
        portField.setRequired(true);
        portField.setRequiredError("Port is required");
        portField.setValidationVisible(false);
        portField.addValidator(inValue -> {
            try {
                Integer.parseInt(String.valueOf(inValue));
            } catch (Exception e) {
                throw new InvalidValueException("Port must be between 1 and 65535");
            }
        });
        portField.addValueChangeListener(inEvent -> {
            portField.setValidationVisible(true);
        });
        testConnectionButton.addClickListener(inEvent -> {
            testConnectionLabel.setVisible(true);
            try(Socket s = new Socket(hostnameField.getValue(),
                                      Integer.parseInt(portField.getValue()))) {
                testConnectionLabel.setValue("Test connection success");
                testConnectionLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
            } catch (Exception e) {
                testConnectionLabel.setValue("Test connection failed: " + ExceptionUtils.getRootCauseMessage(e));
                testConnectionLabel.setStyleName(ValoTheme.LABEL_FAILURE);
            }
        });
        fieldLayout.addComponents(nameField,
                                  hostnameField,
                                  portField,
                                  testConnectionButton,
                                  testConnectionLabel);
        fieldLayout.setComponentAlignment(testConnectionButton,
                                          Alignment.BOTTOM_RIGHT);
        content.addComponent(fieldLayout);
        CssLayout buttonLayout = new CssLayout();
        content.addComponent(buttonLayout);
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        buttonLayout.addComponents(okButton,
                                   cancelButton);
        cancelButton.addClickListener(inEvent -> {
            formWindow.close();
        });
        okButton.addClickListener(inEvent -> {
            Iterator<Component> components = fieldLayout.iterator();
            while(components.hasNext()) {
                Component component = components.next();
                if(component instanceof Validatable) {
                    Validatable validatableComponent = (Validatable)component;
                    if(!validatableComponent.isValid()) {
                        return;
                    }
                }
            }
            formWindow.close();
            try {
                inStrategyEngine.setName(nameField.getValue());
                inStrategyEngine.setHostname(hostnameField.getValue());
                inStrategyEngine.setPort(Integer.parseInt(portField.getValue()));
                Collection<DecoratedStrategyEngine> existingStrategyEngines = DataFlowClientService.getInstance().getStrategyEngines();
                if(!inIsNew) {
                    existingStrategyEngines.remove(inStrategyEngine);
                }
                existingStrategyEngines.add(inStrategyEngine);
                DataFlowClientService.getInstance().setStrategyEngines(existingStrategyEngines);
                getGrid().deselectAll();
            } catch (Exception e) {
                String message = ExceptionUtils.getRootCauseMessage(e);
                SLF4JLoggerProxy.error(StrategyEngineView.this,
                                       e,
                                       "Error editing or creating object: {}",
                                       inStrategyEngine);
                Notification.show((inIsNew?"Create ":"Edit ") + getViewSubjectName() + " Error",
                                  "Error occurred storing object: " + message,
                                  Type.ERROR_MESSAGE);
            }
        });
        UI.getCurrent().addWindow(formWindow);
    }
    /**
     * Get the webMessageService value.
     *
     * @return a <code>WebMessageService</code> value
     */
    WebMessageService getWebMessageService()
    {
        return webMessageService;
    }
    /**
     * Sets the webMessageService value.
     *
     * @param inWebMessageService a <code>WebMessageService</code> value
     */
    void setWebMessageService(WebMessageService inWebMessageService)
    {
        webMessageService = inWebMessageService;
    }
    /**
     * provides access to web message services
     */
    protected WebMessageService webMessageService;
    /**
     * action examine data flows
     */
    private final String ACTION_DATAFLOWS = "Examine Data Flows";
    /**
     * action edit value
     */
    private final String ACTION_EDIT = "Edit";
    /**
     * action connect value
     */
    private final String ACTION_CONNECT = "Connect";
    /**
     * action disconnect value
     */
    private final String ACTION_DISCONNECT = "Disconnect";
    /**
     * action delete value
     */
    private final String ACTION_DELETE = "Delete";
    /**
     * global name of this view
     */
    public static final String NAME = "DataFlowView";
    private static final long serialVersionUID = 1185152523219310958L;
}

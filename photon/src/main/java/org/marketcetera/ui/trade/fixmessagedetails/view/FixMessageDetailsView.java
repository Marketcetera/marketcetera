package org.marketcetera.ui.trade.fixmessagedetails.view;

import java.util.Iterator;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import quickfix.InvalidMessage;

/* $License$ */

/**
 * Displays a detailed view of a FIX message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FixMessageDetailsView
        extends AbstractContentView
        implements ContentView
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        mainLayout = new VBox();
        initializeTable();
        mainLayout.getChildren().add(fixMessageGrid);
        updateRows(fixMessage);
    }
    private void updateRows(quickfix.Message fixMessage)
    {
        fixMessageGrid.getItems().clear();
        try {
            FIXVersion fixVersion = FIXVersion.getFIXVersion(fixMessage);
            quickfix.DataDictionary dataDictionary = FIXMessageUtil.getDataDictionary(fixVersion);
            Iterator<quickfix.Field<?>> fieldIterator = fixMessage.getHeader().iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageGrid.getItems().add(new DisplayFixMessageValue(field.getTag(),
                                                                         dataDictionary.getFieldName(field.getTag()),
                                                                         dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                                                         String.valueOf(field.getObject())));
            }
            fieldIterator = fixMessage.iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageGrid.getItems().add(new DisplayFixMessageValue(field.getTag(),
                                                                         dataDictionary.getFieldName(field.getTag()),
                                                                         dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                                                         String.valueOf(field.getObject())));
            }
            fieldIterator = fixMessage.getTrailer().iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageGrid.getItems().add(new DisplayFixMessageValue(field.getTag(),
                                                                         dataDictionary.getFieldName(field.getTag()),
                                                                         dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                                                         String.valueOf(field.getObject())));
            }
        } catch (Exception e) {
            webMessageService.post(new NotificationEvent("Display FIX Message Details",
                                                         "Unable to display FIX message details: " + PlatformServices.getMessage(e),
                                                         AlertType.WARNING));
        }
    }
    private void initializeTable()
    {
        fixMessageGrid = new TableView<>();
        initializeColumns(fixMessageGrid);
        fixMessageGrid.setPlaceholder(new Label("no fields to display"));
    }
    private void initializeColumns(TableView<DisplayFixMessageValue> inTableView)
    {
        TableColumn<DisplayFixMessageValue,Integer> tagColumn = new TableColumn<>("Tag"); 
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
        tagColumn.setSortable(false);
        TableColumn<DisplayFixMessageValue,String> nameColumn = new TableColumn<>("Name"); 
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setSortable(false);
        TableColumn<DisplayFixMessageValue,String> typeColumn = new TableColumn<>("Type"); 
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setSortable(false);
        TableColumn<DisplayFixMessageValue,String> valueColumn = new TableColumn<>("Value"); 
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setSortable(false);
        inTableView.getColumns().add(tagColumn);
        inTableView.getColumns().add(nameColumn);
        inTableView.getColumns().add(typeColumn);
        inTableView.getColumns().add(valueColumn);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getMainLayout()
     */
    @Override
    public Region getMainLayout()
    {
        return mainLayout;
    }
    /**
     * Create a new FixMessageDetailsView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public FixMessageDetailsView(Region inParent,
                                 NewWindowEvent inEvent,
                                 Properties inViewProperties)
    {
        super(inParent,
              inEvent,
              inViewProperties);
        String rawFixMessage = inViewProperties.getProperty(quickfix.Message.class.getCanonicalName());
        try {
            fixMessage = new quickfix.Message(rawFixMessage);
        } catch (InvalidMessage e) {
            throw new RuntimeException(e);
        }
    }
    public static class DisplayFixMessageValue
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DisplayFixMessageValue [tag=").append(tag).append(", name=").append(name).append(", type=")
                    .append(type).append(", value=").append(value).append("]");
            return builder.toString();
        }
        /**
         * Create a new DisplayFixMessageValue instance.
         *
         * @param inTag
         * @param inName
         * @param inType
         * @param inValue
         */
        private DisplayFixMessageValue(int inTag,
                                       String inName,
                                       String inType,
                                       String inValue)
        {
            tag = inTag;
            name = inName;
            type = inType;
            value = inValue;
        }
        /**
         * Get the tag value.
         *
         * @return an <code>int</code> value
         */
        public int getTag()
        {
            return tag;
        }
        /**
         * Get the name value.
         *
         * @return a <code>String</code> value
         */
        public String getName()
        {
            return name;
        }
        /**
         * Get the type value.
         *
         * @return a <code>String</code> value
         */
        public String getType()
        {
            return type;
        }
        /**
         * Get the value value.
         *
         * @return a <code>String</code> value
         */
        public String getValue()
        {
            return value;
        }
        private final int tag;
        private final String name;
        private final String type;
        private final String value;
    }
    private VBox mainLayout;
    private TableView<DisplayFixMessageValue> fixMessageGrid;
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
    /**
     * FIX message to display
     */
    private final quickfix.Message fixMessage;
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Message Details View";

 
 // private static final long serialVersionUID = 8926640586123984644L;
//    /* (non-Javadoc)
//     * @see com.vaadin.ui.AbstractComponent#attach()
//     */
//    @Override
//    public void attach()
//    {
//        setSizeFull();
//        fixMessageGrid = new Grid();
//        fixMessageGrid.addColumn("Tag",
//                                 String.class).setSortable(false);
//        fixMessageGrid.addColumn("Name",
//                                 String.class).setSortable(false);;
//        fixMessageGrid.addColumn("Type",
//                                 String.class).setSortable(false);;
//        fixMessageGrid.addColumn("Value",
//                                 String.class).setSortable(false);;
//        try {
//            FIXVersion fixVersion = FIXVersion.getFIXVersion(fixMessage);
//            quickfix.DataDictionary dataDictionary = FIXMessageUtil.getDataDictionary(fixVersion);
//            Iterator<quickfix.Field<?>> fieldIterator = fixMessage.getHeader().iterator();
//            while(fieldIterator.hasNext()) {
//                quickfix.Field<?> field = fieldIterator.next();
//                fixMessageGrid.addRow(String.valueOf(field.getTag()),
//                                      dataDictionary.getFieldName(field.getTag()),
//                                      dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
//                                      String.valueOf(field.getObject()));
//            }
//            fieldIterator = fixMessage.iterator();
//            while(fieldIterator.hasNext()) {
//                quickfix.Field<?> field = fieldIterator.next();
//                fixMessageGrid.addRow(String.valueOf(field.getTag()),
//                                          dataDictionary.getFieldName(field.getTag()),
//                                          dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
//                                          String.valueOf(field.getObject()));
//            }
//            fieldIterator = fixMessage.getTrailer().iterator();
//            while(fieldIterator.hasNext()) {
//                quickfix.Field<?> field = fieldIterator.next();
//                fixMessageGrid.addRow(String.valueOf(field.getTag()),
//                                      dataDictionary.getFieldName(field.getTag()),
//                                      dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
//                                      String.valueOf(field.getObject()));
//            }
//        } catch (FieldNotFound e) {
//            throw new RuntimeException(e);
//        }
//        fixMessageGrid.setSizeFull();
//        fixMessageGrid.setId(getClass().getCanonicalName() + ".fixMessageGrid");
//        styleService.addStyle(fixMessageGrid);
//        addComponent(fixMessageGrid);
//        setId(getClass().getCanonicalName() + ".contentLayout");
//        styleService.addStyle(this);
//    }
//    /* (non-Javadoc)
//     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
//     */
//    @Override
//    public void enter(ViewChangeEvent inEvent)
//    {
//    }
//    /* (non-Javadoc)
//     * @see org.marketcetera.web.view.ContentView#getViewName()
//     */
//    @Override
//    public String getViewName()
//    {
//        return NAME;
//    }
//    /**
//     * Create a new FixMessageDetailsView instance.
//     *
//     * @param inParentWindow a <code>Window</code> value
//     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
//     * @param inViewProperties a <code>Properties</code> value
//     */
//    public FixMessageDetailsView(Window inParent,
//                                 NewWindowEvent inEvent,
//                                 Properties inViewProperties)
//    {
//        super(inParent,
//              inEvent,
//              inViewProperties);
//        String rawFixMessage = inViewProperties.getProperty(quickfix.Message.class.getCanonicalName());
//        try {
//            fixMessage = new quickfix.Message(rawFixMessage);
//        } catch (InvalidMessage e) {
//            throw new RuntimeException(e);
//        }
//    }
//    /**
//     * grid component to display the FIX message fields
//     */
//    private Grid fixMessageGrid;
//    /**
//     * FIX message to display
//     */
//    private final quickfix.Message fixMessage;
}

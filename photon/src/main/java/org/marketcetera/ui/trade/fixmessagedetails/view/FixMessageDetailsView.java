package org.marketcetera.ui.trade.fixmessagedetails.view;

import java.util.Iterator;
import java.util.Properties;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
        mainLayout = new VBox();
        initializeTable();
        mainLayout.getChildren().add(fixMessageTable);
        updateRows(fixMessage);
    }
    /**
     * Update the display rows with the given message.
     *
     * @param inFixMessage
     */
    private void updateRows(quickfix.Message inFixMessage)
    {
        fixMessageTable.getItems().clear();
        try {
            FIXVersion fixVersion = FIXVersion.getFIXVersion(inFixMessage);
            quickfix.DataDictionary dataDictionary = FIXMessageUtil.getDataDictionary(fixVersion);
            Iterator<quickfix.Field<?>> fieldIterator = inFixMessage.getHeader().iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageTable.getItems().add(new DisplayFixMessageValue(field.getTag(),
                                                                         dataDictionary.getFieldName(field.getTag()),
                                                                         dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                                                         String.valueOf(field.getObject())));
            }
            fieldIterator = inFixMessage.iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageTable.getItems().add(new DisplayFixMessageValue(field.getTag(),
                                                                         dataDictionary.getFieldName(field.getTag()),
                                                                         dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                                                         String.valueOf(field.getObject())));
            }
            fieldIterator = inFixMessage.getTrailer().iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageTable.getItems().add(new DisplayFixMessageValue(field.getTag(),
                                                                         dataDictionary.getFieldName(field.getTag()),
                                                                         dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                                                         String.valueOf(field.getObject())));
            }
        } catch (Exception e) {
            uiMessageService.post(new NotificationEvent("Display FIX Message Details",
                                                        "Unable to display FIX message details: " + PlatformServices.getMessage(e),
                                                        AlertType.WARNING));
        }
    }
    /**
     * Initialize the FIX message view table.
     */
    private void initializeTable()
    {
        fixMessageTable = new TableView<>();
        initializeColumns();
        fixMessageTable.setPlaceholder(new Label("no fields to display"));
        fixMessageTable.prefWidthProperty().bind(getParentWindow().widthProperty());
        fixMessageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mainLayout.prefHeightProperty().bind(getParentWindow().heightProperty());
    }
    /**
     * Initialize the table columns
     */
    private void initializeColumns()
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
        fixMessageTable.getColumns().add(tagColumn);
        fixMessageTable.getColumns().add(nameColumn);
        fixMessageTable.getColumns().add(typeColumn);
        fixMessageTable.getColumns().add(valueColumn);
    }
    /**
     * Provides a display version of a FIX message.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class DisplayFixMessageValue
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DisplayFixMessageValue [tag=").append(tagProperty.get()).append(", name=").append(nameProperty.get()).append(", type=")
                    .append(typeProperty.get()).append(", value=").append(valueProperty.get()).append("]");
            return builder.toString();
        }
        /**
         * Create a new DisplayFixMessageValue instance.
         *
         * @param inTag an <code>int</code> value
         * @param inName a <cod>String</code> value
         * @param inType a <code>String</code> value
         * @param inValue a <code>String</code> value
         */
        private DisplayFixMessageValue(int inTag,
                                       String inName,
                                       String inType,
                                       String inValue)
        {
            tagProperty.set(inTag);
            nameProperty.set(inName);
            typeProperty.set(inType);
            valueProperty.set(inValue);
        }
        /**
         * Get the tag property.
         *
         * @return a <code>ReadOnlyIntegerProperty</code> value
         */
        public ReadOnlyIntegerProperty tagProperty()
        {
            return tagProperty;
        }
        /**
         * Get the name property.
         *
         * @return a <code>ReadOnlyStringProperty</code> value
         */
        public ReadOnlyStringProperty nameProperty()
        {
            return nameProperty;
        }
        /**
         * Get the type property.
         *
         * @return a <code>ReadOnlyStringProperty</code> value
         */
        public ReadOnlyStringProperty typeProperty()
        {
            return typeProperty;
        }
        /**
         * Get the value property.
         *
         * @return a <code>ReadOnlyStringProperty</code> value
         */
        public ReadOnlyStringProperty valueProperty()
        {
            return valueProperty;
        }
        /**
         * tag property
         */
        private final IntegerProperty tagProperty = new SimpleIntegerProperty();
        /**
         * name property
         */
        private final StringProperty nameProperty = new SimpleStringProperty();
        /**
         * type property
         */
        private final StringProperty typeProperty = new SimpleStringProperty();
        /**
         * value property
         */
        private final StringProperty valueProperty = new SimpleStringProperty();
    }
    /**
     * main view layout
     */
    private VBox mainLayout;
    /**
     * FIX message fields table
     */
    private TableView<DisplayFixMessageValue> fixMessageTable;
    /**
     * FIX message to display
     */
    private final quickfix.Message fixMessage;
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Message Details View";
}

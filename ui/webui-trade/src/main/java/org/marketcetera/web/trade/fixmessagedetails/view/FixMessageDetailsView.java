package org.marketcetera.web.trade.fixmessagedetails.view;

import java.util.Iterator;
import java.util.Properties;

import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.web.view.AbstractContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Window;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;

/* $License$ */

/**
 * Displays a detailed view of a FIX message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FixMessageDetailsView
        extends AbstractContentView
{
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        setSizeFull();
        fixMessageGrid = new Grid();
        fixMessageGrid.addColumn("Tag",
                                 String.class).setSortable(false);
        fixMessageGrid.addColumn("Name",
                                 String.class).setSortable(false);;
        fixMessageGrid.addColumn("Type",
                                 String.class).setSortable(false);;
        fixMessageGrid.addColumn("Value",
                                 String.class).setSortable(false);;
        try {
            FIXVersion fixVersion = FIXVersion.getFIXVersion(fixMessage);
            quickfix.DataDictionary dataDictionary = FIXMessageUtil.getDataDictionary(fixVersion);
            Iterator<quickfix.Field<?>> fieldIterator = fixMessage.getHeader().iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageGrid.addRow(String.valueOf(field.getTag()),
                                      dataDictionary.getFieldName(field.getTag()),
                                      dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                      String.valueOf(field.getObject()));
            }
            fieldIterator = fixMessage.iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageGrid.addRow(String.valueOf(field.getTag()),
                                          dataDictionary.getFieldName(field.getTag()),
                                          dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                          String.valueOf(field.getObject()));
            }
            fieldIterator = fixMessage.getTrailer().iterator();
            while(fieldIterator.hasNext()) {
                quickfix.Field<?> field = fieldIterator.next();
                fixMessageGrid.addRow(String.valueOf(field.getTag()),
                                      dataDictionary.getFieldName(field.getTag()),
                                      dataDictionary.getFieldType(field.getTag()).getJavaType().getSimpleName(),
                                      String.valueOf(field.getObject()));
            }
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
        fixMessageGrid.setSizeFull();
        fixMessageGrid.setId(getClass().getCanonicalName() + ".fixMessageGrid");
        styleService.addStyle(fixMessageGrid);
        addComponent(fixMessageGrid);
        setId(getClass().getCanonicalName() + ".contentLayout");
        styleService.addStyle(this);
    }
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new FixMessageDetailsView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public FixMessageDetailsView(Window inParent,
                                 Properties inViewProperties)
    {
        super(inParent,
              inViewProperties);
        String rawFixMessage = inViewProperties.getProperty(quickfix.Message.class.getCanonicalName());
        try {
            fixMessage = new quickfix.Message(rawFixMessage);
        } catch (InvalidMessage e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * grid component to display the FIX message fields
     */
    private Grid fixMessageGrid;
    /**
     * FIX message to display
     */
    private final quickfix.Message fixMessage;
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Message Details View";
    private static final long serialVersionUID = 8926640586123984644L;
}

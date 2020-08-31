package org.marketcetera.web.fields;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.trade.TradeClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Lists;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.TextField;

/* $License$ */

/**
 * Provides a symbol field that resolves to an underlying instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResolvingInstrumentField
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        // symbol text
        symbolTextField = new TextField();
        symbolTextField.setCaption("Symbol");
        symbolTextField.setId(getClass().getCanonicalName() + ".symbolTextField");
        symbolTextField.addValidator(inValue -> {
            String symbol = StringUtils.trimToNull(String.valueOf(inValue));
            if(symbol == null) {
                resolvedInstrument = null;
                return;
            }
            resolvedInstrument = serviceManager.getService(TradeClientService.class).resolveSymbol(symbol);
            if(resolvedInstrument == null) {
                throw new InvalidValueException("Cannot resolve symbol");
            }
            symbolTextField.setDescription(resolvedInstrument.toString());
        });
        symbolTextField.addValueChangeListener(newValueEvent -> {
            synchronized(valueChangeListeners) {
                try {
                    valueChangeListeners.forEach(valueChangeListener->valueChangeListener.valueChange(newValueEvent));
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(ResolvingInstrumentField.this,
                                          e);
                }
            }
        });
        symbolTextField.setNullSettingAllowed(true);
    }
    /**
     * Get the symbolTextField value.
     *
     * @return a <code>TextField</code> value
     */
    public TextField getSymbolTextField()
    {
        return symbolTextField;
    }
    /**
     * Add the given value change listener.
     *
     * @param inListener a <code>ValueChangeListener</code> value
     */
    public void addValueChangeListener(ValueChangeListener inListener)
    {
        synchronized(valueChangeListeners) {
            valueChangeListeners.add(inListener);
        }
    }
    /**
     * Remove the given value change listener.
     *
     * @param inListener a <code>ValueChangeListener</code> value
     */
    public void removeValueChangeListener(ValueChangeListener inListener)
    {
        synchronized(valueChangeListeners) {
            valueChangeListeners.remove(inListener);
        }
    }
    /**
     * Get the resolvedInstrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getResolvedInstrument()
    {
        return resolvedInstrument;
    }
    /**
     * holds value change listeners
     */
    private final Collection<ValueChangeListener> valueChangeListeners = Lists.newArrayList();
    /**
     * instrument resolved from the symbol, may be <code>null</code>
     */
    private Instrument resolvedInstrument;
    /**
     * holds the symbol which gets resolved to an instrument
     */
    private TextField symbolTextField;
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
}

package org.marketcetera.web.service;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;

/* $License$ */

/**
 * Provides style resolution services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class StyleService
{
    /**
     * Validate and start the service.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
    }
    /**
     * Apply styles, if specified, to the given component with the given component id.
     *
     * @param inComponent a <code>HasStyle</code> value
     */
    public void addStyle(Component inComponent)
    {
        HasStyle componentToStyle;
        if(inComponent instanceof HasStyle) {
            componentToStyle = (HasStyle)inComponent;
        } else {
            return;
        }
        String componentId = inComponent.getId().orElse(null);
        if(componentId == null) {
            SLF4JLoggerProxy.trace(this,
                                   "Component {} has no id property",
                                   componentId);
            return;
        }
        SLF4JLoggerProxy.trace(this,
                               "Applying styles to {}",
                               componentId);
        // apply styles in order from least to most specific
        applyStyle(componentId.indexOf('.'),
                   componentId,
                   componentToStyle);
    }
    /**
     * Apply the style with the given index and key to the given component.
     *
     * @param inIndex an <code>int</code> value
     * @param inComponentId a <code>String</code> value
     * @param inComponent a <code>HasStyle</code> value
     */
    private void applyStyle(int inIndex,
                            String inComponentId,
                            HasStyle inComponent)
    {
        if(inComponentId == null) {
            return;
        }
        String key;
        if(inIndex == -1) {
            key = inComponentId;
        } else {
            key = inComponentId.substring(0,
                                  inIndex);
        }
        String stylesToApply = styleProperties.get(key);
        SLF4JLoggerProxy.trace(this,
                               "Applying styles {} to {} from key {}",
                               stylesToApply,
                               inComponentId,
                               key);
        if(stylesToApply != null) {
            inComponent.addClassName(stylesToApply);
        }
        if(inComponentId.equals(key)) {
            return;
        }
        int indexOfDot = inComponentId.indexOf('.',
                                       inIndex+1);
        applyStyle(indexOfDot,
                   inComponentId,
                   inComponent);
    }
    /**
     * map of styles specified in configuration
     */
    @Value("#{${metc.styles}}")
    private Map<String,String> styleProperties = Maps.newHashMap();
}

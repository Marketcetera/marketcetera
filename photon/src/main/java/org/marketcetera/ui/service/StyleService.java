package org.marketcetera.ui.service;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
     *
     *
     * @param inContentView
     */
    public void addStyle(ContentView inContentView)
    {
        addStyle(inContentView.getScene());
    }
    /**
     *
     *
     * @param inContentView
     */
    public void addStyle(Scene scene)
    {
        Parent root = scene.getRoot();
        for(Node node : root.getChildrenUnmodifiable()) {
            addStyle(node);
        }
    }
    public void addStyleToAll(Node...inComponents)
    {
        for(Node node : inComponents) {
            addStyle(node);
        }
    }
    public void addStyle(Node inComponent)
    {
        String componentId = inComponent.getId();
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
                   inComponent);
    }
    private void applyStyle(int inIndex,
                            String inKey,
                            Node inComponent)
    {
        if(inKey == null) {
            return;
        }
        String key;
        if(inIndex == -1) {
            key = inKey;
        } else {
            key = inKey.substring(0,
                                  inIndex);
        }
        String stylesToApply = styleProperties.get(key);
        SLF4JLoggerProxy.trace(this,
                               "Applying styles {} to {} from key {}",
                               stylesToApply,
                               inComponent.getId(),
                               key);
        if(stylesToApply != null) {
            inComponent.setStyle(stylesToApply);
        }
        if(inKey.equals(key)) {
            return;
        }
        int indexOfDot = inKey.indexOf('.',
                                       inIndex+1);
        applyStyle(indexOfDot,
                   inKey,
                   inComponent);
    }
    /**
     * map of styles specified in configuration
     */
    @Value("#{${metc.styles}}")
    private Map<String,String> styleProperties = Maps.newHashMap();
}

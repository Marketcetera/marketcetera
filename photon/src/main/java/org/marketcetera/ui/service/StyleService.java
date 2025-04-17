package org.marketcetera.ui.service;

import java.util.Map;

import jakarta.annotation.PostConstruct;

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
     * Add styles to the given view.
     *
     * @param inContentView a <code>ContentView</code> value
     */
    public void addStyle(ContentView inContentView)
    {
//        addStyle(inContentView.getScene());
    }
    /**
     * Add styles to the given scene.
     *
     * @param inScene a <code>Scene</code> value
     */
    public void addStyle(Scene inScene)
    {
        Parent root = inScene.getRoot();
        for(Node node : root.getChildrenUnmodifiable()) {
            addStyle(node);
        }
    }
    /**
     * Add styles to the given nodes.
     *
     * @param inNodes a <code>Node[]</code> value
     */
    public void addStyleToAll(Node...inNodes)
    {
        for(Node node : inNodes) {
            addStyle(node);
        }
    }
    /**
     * Add style to the given node.
     *
     * @param inComponent a <code>Node</code> value
     */
    public void addStyle(Node inComponent)
    {
//        String componentId = inComponent.getId();
//        if(componentId == null) {
//            SLF4JLoggerProxy.trace(this,
//                                   "Component {} has no id property",
//                                   inComponent.getClass().getSimpleName());
//            return;
//        }
//        SLF4JLoggerProxy.trace(this,
//                               "Applying styles to {}",
//                               componentId);
//        // apply styles in order from least to most specific
//        applyStyle(componentId.indexOf('.'),
//                   componentId,
//                   inComponent);
    }
    /**
     * Apply style to the node with the given key.
     *
     * @param inIndex an <code>int</code> value
     * @param inKey a <code>String</code> value
     * @param inComponent a <code>Node</code> value
     */
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

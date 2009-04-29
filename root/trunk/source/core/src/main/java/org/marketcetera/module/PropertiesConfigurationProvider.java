package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.log.I18NBoundMessage1P;

import java.util.Properties;
import java.util.Map;
import java.util.WeakHashMap;
import java.io.InputStream;
import java.io.IOException;

/* $License$ */
/**
 * Provides a properties file based configuration. This provider looks for
 * a unique properties file per module provider and uses that properties
 * file to resolve the default attribute values for the module factory
 * and the module instances from that provider.
 * <p>
 * The property file name is constructed from the module provider name.
 * <code>provider-type_provider-name.properties</code>. The properties files
 * are loaded via the classloader. However, the recommended location for
 * the properties file is <code>&lt;application-home&gt;/modules/conf</code>
 * <p>
 * Within the property file, the attribute values are encoded as
 * <code>instance-name.attribute-name=attribute-value</code>.
 * Default attribute values, that apply to all instances can be specified as
 * <code>.attribute-name=attribute-value</code.
 * <p>
 * For factory attributes, "<code>instance-name.</code>" is excluded from
 * the property name, ie. the attribute value is specified as
 * <code>attribute-name=attribute-value</code>
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class PropertiesConfigurationProvider
        implements ModuleConfigurationProvider {
    public PropertiesConfigurationProvider(ClassLoader inLoader) {
        mLoader = inLoader;
    }

    @Override
    public String getDefaultFor(ModuleURN inURN, String inAttribute)
            throws ModuleException {
        SLF4JLoggerProxy.debug(this,
                "Looking default value of attribute {} for {}",  //$NON-NLS-1$
                inAttribute, inURN);
        String propertiesFile = new StringBuilder().
                append(inURN.providerType()).
                append("_").                        //$NON-NLS-1$
                append(inURN.providerName()).
                append(".properties").toString();   //$NON-NLS-1$
        Properties p;
        synchronized(this) {
            p = mCache.get(propertiesFile);
            // if the property is not found in the cache
            // try loading it.
            if(p == null) {
                InputStream is = mLoader.
                        getResourceAsStream(propertiesFile);
                if(is != null) {
                    p = new Properties();
                    try {
                        p.load(is);
                        // Only add properties to the cache
                        // if it was loaded successfully.
                        mCache.put(propertiesFile, p);
                    } catch (IOException e) {
                        Messages.LOG_ERROR_READ_DEFAULT_CONFIG.warn(this, e,
                                propertiesFile, inURN, inAttribute);
                        throw new ModuleException(e, new I18NBoundMessage1P(
                                Messages.ERROR_READ_PROPERTIES,propertiesFile));
                    }
                }
            }
        }
        if(p != null && !p.isEmpty()) {
            if(inURN.instanceURN()) {
                // find the instance specific value
                String value = p.getProperty(inURN.instanceName() +
                        "." + inAttribute);   //$NON-NLS-1$
                if(value != null) {
                    // if the instance specific value is found
                    // return it
                    return value;
                } else {
                    // return property value for all instances
                    return p.getProperty("." + inAttribute);  //$NON-NLS-1$
                }
            } else {
                return p.getProperty(inAttribute);
            }
        }
        return null;
    }

    @Override
    public void refresh() {
        mCache.clear();
    }

    private final Map<String,Properties> mCache =
            new WeakHashMap<String,Properties>();
    private final ClassLoader mLoader;
}

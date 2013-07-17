package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.marketcetera.client.ClientModuleFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

/* $License$ */
/**
 * Tests {@link AgentConfigurationProvider}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class AgentConfigurationProviderTest extends PropertiesConfigurationProviderTest {
    @Test
    public void receiverProperties() throws Exception {
        AgentConfigurationProvider provider = (AgentConfigurationProvider) createProvider();
        //verify initial value
        assertNull(provider.getReceiverProperties());
        Properties props = createLoaderProperties(".");
        mLoader.addResource(ReceiverFactory.INSTANCE_URN, props);

        //Verify various return values before setting the receiver properties
        assertAttribs(provider, ReceiverFactory.INSTANCE_URN, "Value1Prop", "Value2Prop", null);
        //Set properties to an empty map and make sure it makes no difference
        provider.setReceiverProperties(new HashMap<String, String>());
        assertEquals(new HashMap<String, String>(), provider.getReceiverProperties());
        assertAttribs(provider, ReceiverFactory.INSTANCE_URN, "Value1Prop", "Value2Prop", null);
        //Set properties to a non-empty map
        Map<String, String> map = createPropertiesAttrib();
        provider.setReceiverProperties(map);
        //Verify various return values
        assertAttribs(provider, ReceiverFactory.INSTANCE_URN, "Value1SA", "Value2Prop", "Value3SA");

        //Verify that the returned properties instance is a clone of the supplied one
        assertNotSame(map, provider.getReceiverProperties());
        assertEquals(map, provider.getReceiverProperties());
        //Modifying the map after setting it doesn't change the set map
        Map<String, String> setMap = provider.getReceiverProperties();
        String propertyX = "PropertyX";
        map.put(propertyX, "new Value");
        assertEquals(setMap, provider.getReceiverProperties());
        assertFalse(provider.getReceiverProperties().containsKey(propertyX));
        //Modifying the map after getting it doesn't change the set map
        provider.getReceiverProperties().put(propertyX, "new Value");
        assertEquals(setMap, provider.getReceiverProperties());
        assertFalse(provider.getReceiverProperties().containsKey(propertyX));
        //verify that we can set properties to null
        provider.setReceiverProperties(null);
        assertNull(provider.getReceiverProperties());
    }

    @Test
    public void clientProperties() throws Exception {
        AgentConfigurationProvider provider = (AgentConfigurationProvider) createProvider();
        //verify initial value
        assertNull(provider.getClientProperties());
        Properties props = createLoaderProperties("");
        ModuleURN clientFactoryURN = ClientModuleFactory.INSTANCE_URN.parent();
        mLoader.addResource(clientFactoryURN, props);

        //Verify various return values before setting the receiver properties
        assertAttribs(provider, clientFactoryURN, "Value1Prop", "Value2Prop", null);
        //Set properties to an empty map and make sure it makes no difference
        provider.setClientProperties(new HashMap<String, String>());
        assertEquals(new HashMap<String, String>(), provider.getClientProperties());
        assertAttribs(provider, clientFactoryURN, "Value1Prop", "Value2Prop", null);
        //Set properties to a non-empty map
        Map<String, String> map = createPropertiesAttrib();
        provider.setClientProperties(map);
        //Verify various return values
        assertAttribs(provider, clientFactoryURN, "Value1SA", "Value2Prop", "Value3SA");

        //Verify that the returned properties instance is a clone of the supplied one
        assertNotSame(map, provider.getClientProperties());
        assertEquals(map, provider.getClientProperties());
        //Modifying the map after setting it doesn't change the set map
        Map<String, String> setMap = provider.getClientProperties();
        String propertyX = "PropertyX";
        map.put(propertyX, "new Value");
        assertEquals(setMap, provider.getClientProperties());
        assertFalse(provider.getClientProperties().containsKey(propertyX));
        //Modifying the map after getting it doesn't change the set map
        provider.getClientProperties().put(propertyX, "new Value");
        assertEquals(setMap, provider.getClientProperties());
        assertFalse(provider.getClientProperties().containsKey(propertyX));
        //verify that we can set properties to null
        provider.setClientProperties(null);
        assertNull(provider.getClientProperties());
    }

    @Override
    protected ModuleConfigurationProvider createProvider(ClassLoader inLoader) {
        return new AgentConfigurationProvider(inLoader);
    }

    /**
     * Creates the properties map for setting as either client or receiver
     * properties.
     *
     * @return the properties map.
     */
    private static Map<String, String> createPropertiesAttrib() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Property1", "Value1SA");
        map.put("Property3", "Value3SA");
        return map;
    }

    /**
     * Cretes the properties for the dynamic classloader to return for
     * either client or receiver modules.
     *
     * @param inPrefix the prefix to prepend to the property key.
     *
     * @return the properties instance.
     */
    private static Properties createLoaderProperties(String inPrefix) {
        Properties props = new Properties();
        props.setProperty(inPrefix + "Property1", "Value1Prop");
        props.setProperty(inPrefix + "Property2", "Value2Prop");
        return props;
    }

    /**
     * Verifes the return value of {@link ModuleConfigurationProvider#getDefaultFor(ModuleURN, String)}.
     *
     * @param provider the provider to query.
     * @param inURN the URN to supply
     * @param inValue1 expected first value
     * @param inValue2 expected second value
     * @param inValue3 expected third value
     *
     * @throws ModuleException if there were unexpected failures
     */
    private static void assertAttribs(ModuleConfigurationProvider provider,
                                      ModuleURN inURN,
                                      String inValue1,
                                      String inValue2,
                                      String inValue3) throws ModuleException {
        assertEquals(null, provider.getDefaultFor(inURN, "Unknown"));
        assertEquals(inValue1, provider.getDefaultFor(inURN, "Property1"));
        assertEquals(inValue2, provider.getDefaultFor(inURN, "Property2"));
        assertEquals(inValue3, provider.getDefaultFor(inURN, "Property3"));
    }
}

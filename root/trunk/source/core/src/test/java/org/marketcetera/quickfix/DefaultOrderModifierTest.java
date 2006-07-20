package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

/**
 * @author Graham Miller
 * @version $Id$
 */
public class DefaultOrderModifierTest extends TestCase {
    public DefaultOrderModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(DefaultOrderModifierTest.class);
    }

    public void testModifyOrder() throws Exception {
        String testValue = "A value";
        DefaultOrderModifier modifier = new DefaultOrderModifier();
        modifier.addDefaultField(111, testValue, DefaultOrderModifier.MessageFieldType.MESSAGE);

        Message aMessage = FIXMessageUtil.createNewMessage();

        modifier.modifyOrder(aMessage);
        StringField outField = new StringField(111);
        assertEquals(testValue, aMessage.getField(outField).getValue());
        final Message outerMessage = aMessage;
        new ExpectedTestFailure(FieldNotFound.class, null) {
                protected void execute() throws Throwable
                {
                    outerMessage.getField(new StringField(112));
                }
            }.run();
    }
    
    public void testModifyOrderValueExists() throws Exception {
        String replacementValue = "Replacement value";
        DefaultOrderModifier modifier = new DefaultOrderModifier();
        modifier.addDefaultField(111, replacementValue, DefaultOrderModifier.MessageFieldType.MESSAGE);

        String originalValue = "Original value";
        Message aMessage = FIXMessageUtil.createNewMessage();
        aMessage.setField(new StringField(111, originalValue));

        modifier.modifyOrder(aMessage);
        StringField outField = new StringField(111);
        assertEquals(originalValue, aMessage.getField(outField).getValue());

    }
}

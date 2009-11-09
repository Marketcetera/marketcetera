package org.marketcetera.event.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.event.LogEventLevel.DEBUG;
import static org.marketcetera.event.LogEventLevel.ERROR;
import static org.marketcetera.event.LogEventLevel.INFO;
import static org.marketcetera.event.LogEventLevel.WARN;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEventLevel;
import org.marketcetera.event.TestMessages;
import org.marketcetera.util.log.I18NBoundMessageNP;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageNP;

/**
 * Tests {@link LogEventBuilder} and {@link LinkEventImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LogEventTest
        extends AbstractEventBuilderTestBase<LogEvent, LogEventBuilder>
        implements TestMessages
{
    /**
     * Run before each test.
     */
    @Before
    public void setup()
    {
        level = DEBUG;
    }
    /**
     * Tests {@link LogEventBuilder#withException(Throwable)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withException()
            throws Exception
    {
        level = DEBUG;
        doWithExceptionTest();
        level = INFO;
        doWithExceptionTest();
        level = WARN;
        doWithExceptionTest();
        level = ERROR;
        doWithExceptionTest();
    }
    /**
     * Tests variants of <code>withMessage</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMessage()
            throws Exception
    {
        LogEventBuilder builder = setDefaults(getBuilder());
        builder.withMessage(null);
        assertNull(builder.getMessage());
        setDefaults(builder).withMessage(MESSAGE_0P);
        assertEquals(MESSAGE_0P,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[0],
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_1P,
                                         1);
        assertEquals(MESSAGE_1P,
                     builder.getMessage());
        verify(builder);
        assertTrue(Arrays.equals(new Serializable[] { 1 },
                                 builder.getParameters()));
        setDefaults(builder).withMessage(MESSAGE_1P,
                                         null);
        assertEquals(MESSAGE_1P,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { null },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_2P,
                                         1,
                                         2);
        assertEquals(MESSAGE_2P,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { 1, 2 },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_3P,
                                         1,
                                         2,
                                         3);
        assertEquals(MESSAGE_3P,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { 1, 2, 3 },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_4P,
                                         1,
                                         2,
                                         3,
                                         4);
        assertEquals(MESSAGE_4P,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { 1, 2, 3, 4 },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_5P,
                                         1,
                                         2,
                                         3,
                                         4,
                                         5);
        assertEquals(MESSAGE_5P,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { 1, 2, 3, 4, 5 },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_6P,
                                         1,
                                         2,
                                         3,
                                         4,
                                         5,
                                         6);
        assertEquals(MESSAGE_6P,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { 1, 2, 3, 4, 5, 6 },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_NP,
                                         (Serializable[])null);
        assertEquals(MESSAGE_NP,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_NP,
                                         new Serializable[0]);
        assertEquals(MESSAGE_NP,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { },
                                 builder.getParameters()));
        verify(builder);
        setDefaults(builder).withMessage(MESSAGE_NP,
                                         1,
                                         2,
                                         3,
                                         4,
                                         5,
                                         6,
                                         7);
        assertEquals(MESSAGE_NP,
                     builder.getMessage());
        assertTrue(Arrays.equals(new Serializable[] { 1, 2, 3, 4, 5, 6, 7 },
                                 builder.getParameters()));
        verify(builder);
    }
    /**
     * Tests the ability to serialize and deserialize a log message.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void serialization()
            throws Exception
    {
        LogEventBuilder builder = setDefaults(getBuilder());
        LogEvent beforeEvent = builder.create();
        assertTrue(beforeEvent instanceof Serializable);
        String beforeMessage = beforeEvent.getMessage();
        assertNotNull(beforeMessage);
        ByteArrayOutputStream outboundObjectData = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(outboundObjectData);
        serializer.writeObject(beforeEvent);
        serializer.close();
        ByteArrayInputStream inboundObjectData = new ByteArrayInputStream(outboundObjectData.toByteArray()); 
        ObjectInputStream deserializer = new ObjectInputStream(inboundObjectData);
        LogEvent afterEvent = (LogEvent)deserializer.readObject();
        assertEquals(beforeMessage,
                     afterEvent.getMessage());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#getBuilder()
     */
    @Override
    protected LogEventBuilder getBuilder()
    {
        switch(level) {
            case DEBUG :
                return LogEventBuilder.debug();
            case INFO :
                return LogEventBuilder.info();
            case WARN :
                return LogEventBuilder.warn();
            case ERROR :
                return LogEventBuilder.error();
            default :
                throw new UnsupportedOperationException();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#setDefaults(org.marketcetera.event.impl.AbstractEventBuilderImpl)
     */
    @Override
    protected LogEventBuilder setDefaults(LogEventBuilder inBuilder)
            throws Exception
    {
        inBuilder = super.setDefaults(inBuilder);
        inBuilder.withException(new NullPointerException());
        inBuilder.withMessage(MESSAGE_0P);
        return inBuilder;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderTestBase#verify(org.marketcetera.event.impl.AbstractEventBuilderImpl)
     */
    @Override
    protected LogEvent verify(LogEventBuilder inBuilder)
            throws Exception
    {
        LogEvent event = super.verify(inBuilder);
        assertEquals(inBuilder.getException(),
                     event.getException());
        assertEquals(level,
                     event.getLevel());
        event.getMessage();
        I18NMessageNP npBuilderMessage = translateMessage(inBuilder.getMessage());
        I18NBoundMessageNP resolvedBuilderMessage = new I18NBoundMessageNP(npBuilderMessage,
                                                                           inBuilder.getParameters());
        assertEquals(resolvedBuilderMessage.getText(),
                     event.getMessage());
        return event;
    }
    /**
     * Executes a single iteration of the exception test.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void doWithExceptionTest()
            throws Exception
    {
        LogEventBuilder builder = getBuilder();
        setDefaults(builder);
        builder.withException(null);
        assertNull(builder.getException());
        NullPointerException npe = new NullPointerException();
        builder.withException(npe);
        assertEquals(npe,
                     builder.getException());
        verify(builder);
    }
    /**
     * Translates the given <code>I18NMessage</code> to a specific
     * subclass of <code>I18NMessage</code> that takes a variable number of parameters.
     *
     * @param inMessage an <code>I18NMessage</code> value
     * @return an <code>I18NMessageNP</code> value
     */
    private I18NMessageNP translateMessage(I18NMessage inMessage)
    {
        return new I18NMessageNP(inMessage.getLoggerProxy(),
                                 inMessage.getMessageId(),
                                 inMessage.getEntryId());
    }
    /**
     * determines what type of builder should be created
     */
    private LogEventLevel level = DEBUG;
}

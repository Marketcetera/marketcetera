package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.Messages.INVALID_ID;
import static org.marketcetera.marketdata.Messages.INVALID_REQUEST_TYPE;
import static org.marketcetera.marketdata.Messages.INVALID_STRING_VALUE;
import static org.marketcetera.marketdata.Messages.LINE_SEPARATOR_NOT_ALLOWED;
import static org.marketcetera.marketdata.Messages.MISSING_REQUEST_TYPE;

import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.util.test.UnicodeData;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class DataRequestTest
{
    private MockDataRequest mockRequest;
    @BeforeClass
    public static void doOnce()
    {
        MockDataTypeMissingNewRequestFromString.doRegister();
        MockDataRequestInaccessibleNewRequestFromString.doRegister();
        MockDataRequestNewRequestFromStringWrongSignature.doRegister();
        MockDataRequestNewRequestFromStringWrongReturnType.doRegister();
        MockDataRequestNewRequestFromStringNotStatic.doRegister();
        MockDataRequest.doRegister();
        MockDataRequestWithHashCodeAndEquals.doRegister();
    }
    @Before
    public void doEachTime()
        throws Exception
    {
        mockRequest = (MockDataRequest)DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(null,
                                                                                                                   MockDataRequest.TYPE,
                                                                                                                   "-1",
                                                                                                                   "false",
                                                                                                                   "booya"));
    }
    @Test
    public void dataRequestFromString()
        throws Exception
    {
        final String request = null;
        new ExpectedTestFailure(NullPointerException.class)
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringBadFormat()
        throws Exception
    {
        final String request = "this string is not in the format of a properties object";
        new ExpectedTestFailure(IllegalArgumentException.class)
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringUnregisteredType()
        throws Exception
    {
        final String request = constructStringRepresentationOfDataRequest(null,
                                                                          "unregisteredType",
                                                                          null,
                                                                          null,
                                                                          null);
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_REQUEST_TYPE.getText("unregisteredType"))
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringMissingRequestType()
        throws Exception
    {
        final String request = constructStringRepresentationOfDataRequest(null,
                                                                          null,
                                                                          null,
                                                                          null,
                                                                          null);
        new ExpectedTestFailure(IllegalArgumentException.class,
                                MISSING_REQUEST_TYPE.getText())
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringTypeMissing()
        throws Exception
    {
        final String request = constructStringRepresentationOfDataRequest(null,
                                                                          MockDataTypeMissingNewRequestFromString.TYPE,
                                                                          null,
                                                                          null,
                                                                          null);
        new ExpectedTestFailure(IllegalArgumentException.class)
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringInaccessibleMethod()
        throws Exception
    {
        final String request = constructStringRepresentationOfDataRequest(null,
                                                                          MockDataRequestInaccessibleNewRequestFromString.TYPE,
                                                                          null,
                                                                          null,
                                                                          null);
        new ExpectedTestFailure(IllegalArgumentException.class)
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringWrongSignature()
        throws Exception
    {
        final String request = constructStringRepresentationOfDataRequest(null,
                                                                          MockDataRequestNewRequestFromStringWrongSignature.TYPE,
                                                                          null,
                                                                          null,
                                                                          null);
        new ExpectedTestFailure(IllegalArgumentException.class)
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringWrongReturnType()
        throws Exception
    {
        final String request = constructStringRepresentationOfDataRequest(null,
                                                                          MockDataRequestNewRequestFromStringWrongReturnType.TYPE,
                                                                          null,
                                                                          null,
                                                                          null);
        new ExpectedTestFailure(IllegalArgumentException.class)
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void fromStringNotStatic()
        throws Exception
    {
        final String request = constructStringRepresentationOfDataRequest(null,
                                                                          MockDataRequestNewRequestFromStringNotStatic.TYPE,
                                                                          null,
                                                                          null,
                                                                          null);
        new ExpectedTestFailure(IllegalArgumentException.class)
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(request);
            }
        }.run();
    }
    @Test
    public void testValidType()
        throws Exception
    {
        String requestString = constructStringRepresentationOfDataRequest(null,
                                                                          MockDataRequest.TYPE,
                                                                          "100",
                                                                          "true",
                                                                          "some stuff");
        DataRequest result = DataRequest.newRequestFromString(requestString);
        assertTrue(result instanceof MockDataRequest);
        assertEquals(100,
                     ((MockDataRequest)result).var1);
        assertTrue(((MockDataRequest)result).var2);
        assertEquals("some stuff",
                     ((MockDataRequest)result).var3);
    }
    @Test
    public void testEquivalence()
        throws Exception
    {
        DataRequest request2 = DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(null,
                                                                                                           MockDataRequest.TYPE,
                                                                                                           Integer.toString(mockRequest.var1),
                                                                                                           Boolean.toString(mockRequest.var2),
                                                                                                           mockRequest.var3));
        assertFalse(mockRequest.equivalent(null));
        assertTrue(mockRequest.equivalent(mockRequest));
        assertFalse(mockRequest.getId() == request2.getId());
        assertTrue(mockRequest.equivalent(request2));
    }
    @Test
    public void hashCodeAndEquals()
        throws Exception
    {
        assertFalse(mockRequest.equals(null));
        assertFalse(mockRequest.hashCode() == 0);
        assertFalse(mockRequest.equals(this));
        assertFalse(mockRequest.hashCode() == this.hashCode());
        assertEquals(mockRequest,
                     mockRequest);
        assertEquals(mockRequest.hashCode(),
                     mockRequest.hashCode());
        // request should be a different data request with the same id
        DataRequest request2 = DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(Long.toString(mockRequest.getId()),
                                                                                                           MockDataRequest.TYPE,
                                                                                                           Integer.toString(mockRequest.var1),
                                                                                                           Boolean.toString(mockRequest.var2),
                                                                                                           mockRequest.var3));
        assertEquals(mockRequest.getId(),
                     request2.getId());
        assertEquals(mockRequest,
                     request2);
        assertEquals(mockRequest.hashCode(),
                     request2.hashCode());
        DataRequest request3 = DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(null,
                                                                                                           MockDataRequest.TYPE,
                                                                                                           Integer.toString(mockRequest.var1),
                                                                                                           Boolean.toString(mockRequest.var2),
                                                                                                           mockRequest.var3));
        assertFalse(mockRequest.getId() == request3.getId());
        assertFalse(mockRequest.equals(request3));
        assertFalse(mockRequest.hashCode() == request3.hashCode());
        MockDataRequestWithHashCodeAndEquals request4 = (MockDataRequestWithHashCodeAndEquals) DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(Long.toString(mockRequest.getId()),
                                                                                                                                MockDataRequestWithHashCodeAndEquals.TYPE,
                                                                                                                                Integer.toString(mockRequest.var1),
                                                                                                                                Boolean.toString(mockRequest.var2),
                                                                                                                                mockRequest.var3));
        assertEquals(mockRequest.getId(),
                     request4.getId());
        assertFalse(mockRequest.equals(request4));
        assertFalse(request4.equals(mockRequest));
        assertFalse(mockRequest.hashCode() == request4.hashCode());
        DataRequest request5 = DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(Long.toString(request4.getId()),
                                                                                                           MockDataRequestWithHashCodeAndEquals.TYPE,
                                                                                                           Integer.toString(request4.var1),
                                                                                                           Boolean.toString(request4.var2),
                                                                                                           request4.var3));
        assertEquals(request4.getId(),
                     request5.getId());
        assertEquals(request4,
                     request5);
        assertEquals(request4.hashCode(),
                     request5.hashCode());
    }
    @Test
    public void toStringRoundTrip()
        throws Exception
    {
        DataRequest request1 = DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(null,
                                                                                                           MockDataRequestWithHashCodeAndEquals.TYPE,
                                                                                                           null,
                                                                                                           null,
                                                                                                           null));
        String mockRequestString = request1.toString();
        MockDataRequestWithHashCodeAndEquals newRequest = (MockDataRequestWithHashCodeAndEquals)DataRequest.newRequestFromString(mockRequestString);
        assertEquals(request1,
                     newRequest);
    }
    @Test
    public void nullConstructorArgument()
    {
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_REQUEST_TYPE.getText("null"))
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                new DataRequest(null) {};
            }
        }.run();
    }
    @Test
    public void populatePropertiesWithObjectAttributes()
        throws Exception
    {
        Properties values = new Properties();
        mockRequest.populatePropertiesWithObjectAttributes(values);
        assertEquals(mockRequest.getId(),
                     Long.parseLong(values.getProperty(DataRequest.ID_KEY)));
        assertEquals(mockRequest.getTypeIdentifier(),
                     values.getProperty(DataRequest.TYPE_KEY));
        // MockDataRequest does not properly set its attributes in the properties
        assertNull(values.getProperty(MockDataRequest.VAR1_KEY));
        assertNull(values.getProperty(MockDataRequest.VAR2_KEY));
        assertNull(values.getProperty(MockDataRequest.VAR3_KEY));
        // create a different type of MockDataRequest that does set its attributes properly
        MockDataRequestWithHashCodeAndEquals request1 = (MockDataRequestWithHashCodeAndEquals) DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(null,
                                                                                                                                                                           MockDataRequestWithHashCodeAndEquals.TYPE,
                                                                                                                                                                           "-1",
                                                                                                                                                                           "true",
                                                                                                                                                                           "some value"));
        values.clear();
        assertFalse(mockRequest.getId() == request1.getId());
        assertFalse(mockRequest.getTypeIdentifier().equals(request1.getTypeIdentifier()));
        request1.populatePropertiesWithObjectAttributes(values);
        assertEquals(request1.getId(),
                     Long.parseLong(values.getProperty(DataRequest.ID_KEY)));
        assertEquals(request1.getTypeIdentifier(),
                     values.getProperty(DataRequest.TYPE_KEY));
        assertEquals("-1",
                     values.getProperty(MockDataRequestWithHashCodeAndEquals.VAR1_KEY));
        assertEquals("true",
                     values.getProperty(MockDataRequestWithHashCodeAndEquals.VAR2_KEY));
        assertEquals("some value",
                     values.getProperty(MockDataRequestWithHashCodeAndEquals.VAR3_KEY));
    }
    @Test
    public void stringValueValidation()
        throws Exception
    {
        final String var3Value = "stuff" + DataRequest.KEY_VALUE_DELIMITER + "other stuff"; 
        MockDataRequestWithHashCodeAndEquals request1 = (MockDataRequestWithHashCodeAndEquals) DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(null,
                                                                                                                                                                           MockDataRequestWithHashCodeAndEquals.TYPE,
                                                                                                                                                                           "-1",
                                                                                                                                                                           "false",
                                                                                                                                                                           var3Value));
        assertEquals("stuff",
                     request1.var3);
        final String var3Value2 = "stuff" + DataRequest.LINE_SEPARATOR + "other stuff"; 
        new ExpectedTestFailure(IllegalArgumentException.class,
                                LINE_SEPARATOR_NOT_ALLOWED.getText())
        {
            @Override
            protected void execute()
                    throws Throwable
            {
                DataRequest.newRequestFromString(constructStringRepresentationOfDataRequest(null,
                                                                                            MockDataRequestWithHashCodeAndEquals.TYPE,
                                                                                            "-1",
                                                                                            "false",
                                                                                            var3Value2));
            }
        }.run();
    }
    @Test
    public void validateAndSetRequestDefaultsIfNecessary()
        throws Exception
    {
        Properties output = new Properties();
        assertFalse(output.containsKey(DataRequest.ID_KEY));
        assertNull(output.getProperty(DataRequest.ID_KEY));
        DataRequest.validateAndSetRequestDefaultsIfNecessary(output);
        assertTrue(output.containsKey(DataRequest.ID_KEY));
        long id = Long.parseLong(output.getProperty(DataRequest.ID_KEY));
        assertTrue(id > 0);
        DataRequest.validateAndSetRequestDefaultsIfNecessary(output);
        assertEquals(id,
                     Long.parseLong(output.getProperty(DataRequest.ID_KEY)));
    }
    @Test
    public void propertiesToString()
        throws Exception
    {
        Properties output = new Properties();
        output.setProperty(DataRequest.TYPE_KEY,
                           MockDataRequestWithHashCodeAndEquals.TYPE);
        output.setProperty(DataRequest.ID_KEY,
                           Long.toString(System.nanoTime()));
        output.setProperty(MockDataRequestWithHashCodeAndEquals.VAR3_KEY,
                           UnicodeData.HELLO_GR);
        String request1String = DataRequest.propertiesToString(output);
        // see if we can construct a request from the properties string
        MockDataRequestWithHashCodeAndEquals request2 = (MockDataRequestWithHashCodeAndEquals)DataRequest.newRequestFromString(request1String);
        assertEquals(output.getProperty(MockDataRequestWithHashCodeAndEquals.VAR3_KEY),
                     request2.var3);
    }
    @Test
    public void validateId()
        throws Exception
    {
        final Properties values = new Properties();
        // set the type which will be common to all tests
        values.setProperty(DataRequest.TYPE_KEY,
                           MockDataRequest.TYPE);
        // id non-numeric
        values.setProperty(DataRequest.ID_KEY,
                           "this isn't a number");
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_ID.getText("this isn't a number")) {
            @Override
            protected void execute()
                throws Throwable
            {
                new DataRequest(values) {};
            }
        }.run();
        // id less than zero
        values.setProperty(DataRequest.ID_KEY,
                           "-1");
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_ID.getText("-1")) {
            @Override
            protected void execute()
                throws Throwable
            {
                new DataRequest(values) {};
            }
        }.run();
        // id valid
        long validID = System.nanoTime();
        values.setProperty(DataRequest.ID_KEY,
                           Long.toString(validID));
        DataRequest request = new DataRequest(values) {};
        assertEquals(validID,
                     request.getId());
    }
    @Test
    public void validateStringValue()
        throws Exception
    {
        final String[] value = new String[1];
        value[0] = null;
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                throws Throwable
            {
                DataRequest.validateStringValue(value[0]);
            }
        }.run();
        value[0] = "some stuff " + DataRequest.KEY_VALUE_DELIMITER + "other stuff";
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_STRING_VALUE.getText(value[0])) {
            @Override
            protected void execute()
                throws Throwable
            {
                DataRequest.validateStringValue(value[0]);
            }
        }.run();
        value[0] = "some stuff " + DataRequest.LINE_SEPARATOR + "other stuff";
        new ExpectedTestFailure(IllegalArgumentException.class,
                                INVALID_STRING_VALUE.getText(value[0])) {
            @Override
            protected void execute()
                throws Throwable
            {
                DataRequest.validateStringValue(value[0]);
            }
        }.run();
        value[0] = "some stuff & other stuff";
        assertEquals(value[0],
                     DataRequest.validateStringValue(value[0]));
    }
    public static String constructStringRepresentationOfDataRequest(String inId,
                                                                    String inType,
                                                                    String inVar1,
                                                                    String inVar2,
                                                                    String inVar3)
    {
        StringBuilder requestAsString = new StringBuilder();
        if(inType != null) {
            requestAsString.append("type=").append(inType).append(":");
        }
        if(inId != null) {
            requestAsString.append("id=").append(inId).append(":");
        }
        if(inVar1 != null) {
            requestAsString.append(MockDataRequest.VAR1_KEY).append("=").append(inVar1).append(":");
        }
        if(inVar2 != null) {
            requestAsString.append(MockDataRequest.VAR2_KEY).append("=").append(inVar2).append(":");
        }
        if(inVar3 != null) {
            requestAsString.append(MockDataRequest.VAR3_KEY).append("=").append(inVar3).append(":");
        }
        return requestAsString.toString();
    }
    public static class MockDataTypeMissingNewRequestFromString
        extends DataRequest
    {
        public static final String TYPE = "mockType1";
        public static void doRegister()
        {
            DataRequest.registerType(TYPE,
                                     MockDataTypeMissingNewRequestFromString.class);
        }
        /**
         * Create a new MockDataRequestUnregisteredType instance.
         *
         * @param inId
         */
        public MockDataTypeMissingNewRequestFromString(Properties inProperties)
        {
            super(inProperties);
        }
    }
    public static class MockDataRequestInaccessibleNewRequestFromString
        extends DataRequest
    {
        public static final String TYPE = "mockType2";
        public static void doRegister()
        {
            DataRequest.registerType(TYPE,
                                     MockDataRequestNewRequestFromStringWrongSignature.class);
        }
        /**
         * Create a new MockDataRequestUnregisteredType instance.
         *
         * @param inId
         */
        public MockDataRequestInaccessibleNewRequestFromString(Properties inProperties)
        {
            super(inProperties);
        }
        @SuppressWarnings("unused")
        private static MockDataRequestInaccessibleNewRequestFromString newRequestFromString(Properties inRequest)
        {
            return new MockDataRequestInaccessibleNewRequestFromString(inRequest);
        }
    }
    public static class MockDataRequestNewRequestFromStringWrongSignature
        extends DataRequest
    {
        public static final String TYPE = "mockType3";
        public static void doRegister()
        {
            DataRequest.registerType(TYPE,
                                     MockDataRequestNewRequestFromStringWrongSignature.class);
        }
        /**
         * Create a new MockDataRequestUnregisteredType instance.
         *
         * @param inId
         */
        public MockDataRequestNewRequestFromStringWrongSignature(Properties inProperties)
        {
            super(inProperties);
        }
        protected static MockDataRequestNewRequestFromStringWrongSignature newRequestFromString(MockDataRequestNewRequestFromStringWrongSignature inWrongType)
        {
            return new MockDataRequestNewRequestFromStringWrongSignature(new Properties());
        }
    }
    public static class MockDataRequestNewRequestFromStringWrongReturnType
        extends DataRequest
    {
        public static final String TYPE = "mockType4";
        public static void doRegister()
        {
            DataRequest.registerType(TYPE,
                                     MockDataRequestNewRequestFromStringWrongReturnType.class);
        }
        /**
         * Create a new MockDataRequestUnregisteredType instance.
         *
         * @param inId
         */
        public MockDataRequestNewRequestFromStringWrongReturnType(Properties inRequest)
        {
            super(inRequest);
        }
        protected static String newRequestFromString(MockDataRequestNewRequestFromStringWrongReturnType inWrongType)
        {
            return "this won't help";
        }
    }
    public static class MockDataRequestNewRequestFromStringNotStatic
        extends DataRequest
    {
        public static final String TYPE = "mockType5";
        public static void doRegister()
        {
            DataRequest.registerType(TYPE,
                                     MockDataRequestNewRequestFromStringNotStatic.class);
        }
        /**
         * Create a new MockDataRequestUnregisteredTypex instance.
         *
         * @param inId
         */
        public MockDataRequestNewRequestFromStringNotStatic(Properties inRequest)
        {
            super(inRequest);
        }
        protected MockDataRequestNewRequestFromStringNotStatic newRequestFromString(Properties inRequest)
        {
            return new MockDataRequestNewRequestFromStringNotStatic(inRequest);
        }
    }
    public static class MockDataRequest
        extends DataRequest
    {
        public static final String TYPE = "mockType6";
        public static final String VAR1_KEY = "var1";
        public static final String VAR2_KEY = "var2";
        public static final String VAR3_KEY = "var3";
        public int var1;
        public boolean var2;
        public String var3;
        public static void doRegister()
        {
            DataRequest.registerType(TYPE,
                                     MockDataRequest.class);
        }
        /**
         * Create a new MockDataRequestUnregisteredTypex instance.
         *
         * @param inId
         */
        public MockDataRequest(Properties inProperties,
                               int inVar1,
                               boolean inVar2,
                               String inVar3)
        {
            super(inProperties);
            var1 = inVar1;
            var2 = inVar2;
            var3 = inVar3;
        }
        protected static MockDataRequest newRequestFromString(Properties inRequest)
        {
            return new MockDataRequest(inRequest,
                                       Integer.parseInt(inRequest.getProperty(VAR1_KEY)),
                                       Boolean.parseBoolean(inRequest.getProperty(VAR2_KEY)),
                                       inRequest.getProperty(VAR3_KEY));
        }
    }
    public static class MockDataRequestWithHashCodeAndEquals
        extends DataRequest
    {
        public static final String TYPE = "mockType7";
        public static final String VAR1_KEY = "var1";
        public static final String VAR2_KEY = "var2";
        public static final String VAR3_KEY = "var3";
        public int var1;
        public boolean var2;
        public String var3;
        public static void doRegister()
        {
            DataRequest.registerType(TYPE,
                                     MockDataRequestWithHashCodeAndEquals.class);
        }
        /**
         * Create a new MockDataRequestUnregisteredTypex instance.
         *
         * @param inId
         */
        public MockDataRequestWithHashCodeAndEquals(Properties inRequest,
                                                    int inVar1,
                                                    boolean inVar2,
                                                    String inVar3)
        {
            super(inRequest);
            var1 = inVar1;
            var2 = inVar2;
            var3 = validateStringValue(inVar3);
        }
        protected static MockDataRequestWithHashCodeAndEquals newRequestFromString(Properties inRequest)
        {
            return new MockDataRequestWithHashCodeAndEquals(inRequest,
                                                            Integer.parseInt(inRequest.getProperty(VAR1_KEY)),
                                                            Boolean.parseBoolean(inRequest.getProperty(VAR2_KEY)),
                                                            inRequest.getProperty(VAR3_KEY));
        }
        protected static void validateAndSetRequestDefaultsIfNecessary(Properties inProperties)
        {
            DataRequest.validateAndSetRequestDefaultsIfNecessary(inProperties);
            if(!inProperties.containsKey(VAR1_KEY)) {
                inProperties.setProperty(VAR1_KEY,
                                         Integer.toString(((int)(System.nanoTime() % 1000))));
            }
            if(!inProperties.containsKey(VAR2_KEY)) {
                inProperties.setProperty(VAR2_KEY,
                                         Boolean.toString(System.nanoTime() % 2 == 0));
            }
            if(!inProperties.containsKey(VAR3_KEY)) {
                inProperties.setProperty(VAR3_KEY,
                                         Long.toString(System.currentTimeMillis()));
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.DataRequest#addAttributesToProperties(java.util.Properties)
         */
        @Override
        protected void addCurrentAttributesValues(Properties inProperties)
        {
            inProperties.setProperty(VAR1_KEY,
                                     Integer.toString(var1));
            inProperties.setProperty(VAR2_KEY,
                                     Boolean.toString(var2));
            inProperties.setProperty(VAR3_KEY,
                                     var3);
        }
        @Override
        protected int doHashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + var1;
            result = prime * result + (var2 ? 1231 : 1237);
            result = prime * result + ((var3 == null) ? 0 : var3.hashCode());
            return result;
        }
    }
}

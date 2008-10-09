/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.commons.i18n;

import org.apache.commons.i18n.bundles.ErrorBundle;

import java.util.Locale;

/**
 * @author Mattias Jiderhamn
 */
public class LocalizedExceptionTest extends MockProviderTestBase {
    public void testLocalizedErrorWithCause() {
        Throwable cause = new Exception("foo");
        ErrorBundle errorBundle = new ErrorBundle("errorMessageId");
        LocalizedException le = new LocalizedException(errorBundle, cause);
        assertEquals("Cause", cause, le.getCause());
        assertEquals("Error bundle", errorBundle, le.getErrorMessage());
        assertEquals("Error message", cause.getMessage(), le.getMessage());

        addMockProvider(); // Add mock provider

        LocalizedException le2 = new LocalizedException(errorBundle, cause);
        assertEquals("Cause", cause, le2.getCause());
        assertEquals("Error bundle", errorBundle, le2.getErrorMessage());
        assertEquals("Error message", getMockString("errorMessageId", ErrorBundle.SUMMARY, Locale.getDefault()),
                le2.getMessage());
    }


    public void testLocalizedErrorWithoutCause() {
        ErrorBundle errorBundle = new ErrorBundle("errorMessageId");
        LocalizedException le = new LocalizedException(errorBundle);
        assertNull("Cause", le.getCause());
        assertEquals("Error bundle", errorBundle, le.getErrorMessage());
        assertEquals("Error message",
                "Message bundle with key errorMessageId does not contain an entry with key summary", le.getMessage());

        addMockProvider(); // Add mock provider

        LocalizedException le2 = new LocalizedException(errorBundle);
        assertNull("Cause", le.getCause());
        assertEquals("Error bundle", errorBundle, le2.getErrorMessage());
        assertEquals("Error message", getMockString("errorMessageId", ErrorBundle.SUMMARY, Locale.getDefault()),
                le2.getMessage());
    }
}

/*
 *
 * ====================================================================
 *
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
package org.apache.i18n.examples;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.i18n.LocalizedException;
import org.apache.commons.i18n.XMLMessageProvider;
import org.apache.commons.i18n.MessageManager;
import org.apache.commons.i18n.bundles.ErrorBundle;

/**
 * @author Daniel Florey
 *  
 */
public class LocalizedExceptionExample {
    private static final Logger logger = Logger
            .getLogger(LocalizedExceptionExample.class.getName());

    public static void main(String[] args) {
        // Install the file providing the required messages for this example
        MessageManager.addMessageProvider("org.apache.commons-i18n.examples",
                new XMLMessageProvider(Thread.currentThread().getContextClassLoader().getResourceAsStream(
                        "exampleMessages.xml")));

        // Simulate the locale of the current user in a multi-user environment
        // such as a web application
        Locale currentUsersLocale = Locale.GERMAN;

        // This is the real part dealing with localized exceptions
        try {
            someMethodThrowingAnException();
        } catch (LocalizedException exception) {
            // Retrieve the detailed localized error message
            ErrorBundle errorMessage = exception.getErrorMessage();

            // Print the summary of this error to the log with level SEVERE
            // using the VM default locale:
            logger.log(Level.SEVERE, errorMessage.getSummary(Locale
                    .getDefault()));

            // Print the details of this error to the log with level FINE
            // using the VM default locale:
            logger
                    .log(Level.FINE, errorMessage.getDetails(Locale
                            .getDefault()));

            // Provide the title of this error to the user in a highly visible
            // way
            // using the current users locale:
            System.out.println("#### "
                    + errorMessage.getTitle(currentUsersLocale) + " ####");

            // Provide the text of this error to the user
            // using the current users locale:
            System.out.println(errorMessage.getText(currentUsersLocale));
        }
    }

    /**
     * @throws LocalizedException
     *             is thrown just to show the capabilities of
     *             LocalizedExceptions
     */
    private static void someMethodThrowingAnException()
            throws LocalizedException {
        String userCausingTheException = "Daniel";
        throw new LocalizedException(new ErrorBundle("theCauseOfThisException",
                new String[]{userCausingTheException}));
    }
}
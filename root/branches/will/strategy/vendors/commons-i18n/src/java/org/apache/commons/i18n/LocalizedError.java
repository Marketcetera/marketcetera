/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.i18n.bundles.ErrorBundle;

/**
 * The <code>LocalizedError</code> class is the base class for all errors
 * that provide locaized error informations. This class should be subclassed
 * in order to provide specific errors capable of localization support.
 *
 */
public class LocalizedError extends Error {
    private ErrorBundle errorMessage;

    /**
     * @param errorMessage The error message contains a detailed localized description of this error
     * @param throwable The <code>Throwable</code> that caused this error
     */
    public LocalizedError(ErrorBundle errorMessage, Throwable throwable) {
        super(errorMessage.getSummary(Locale.getDefault(), throwable.getMessage()), throwable);
        this.errorMessage = errorMessage;
    }

    /**
     * @param errorMessage The error message contains a detailed localized description of this error
     */
    public LocalizedError(ErrorBundle errorMessage) {
        super(errorMessage.getSummary(
                Locale.getDefault(), 
                        MessageFormat.format(
                                I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.MESSAGE_ENTRY_NOT_FOUND),
                                new String[] { errorMessage.getId(), ErrorBundle.SUMMARY }))); 
        this.errorMessage = errorMessage;
    }


    /**
     * @return the detailed error message that describes this error
     */
    public ErrorBundle getErrorMessage() {
        return errorMessage;
    }
}

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//i18n/src/java/org/apache/commons/i18n/MessageNotFoundException.java,v 1.1 2004/10/04 13:41:09 dflorey Exp $
 * $Revision$
 * $Date: 2006-11-29 01:00:46 -0800 (Wed, 29 Nov 2006) $
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
package org.apache.commons.i18n;

import java.lang.RuntimeException;

/**
 * The <code>MessageNotFoundException</code> indicates that a particular message
 * could not be found by using the given message id.
 *
 */
public class MessageNotFoundException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message indicating that a particular message
     * could not be found.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public MessageNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message indicating that a particular message and cause
     * indicating that a particular message could not be found.
     * <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public MessageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

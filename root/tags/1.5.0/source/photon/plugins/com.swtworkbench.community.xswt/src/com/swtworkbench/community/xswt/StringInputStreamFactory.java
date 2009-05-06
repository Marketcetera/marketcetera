/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     David Orme - Initial implementation
 */
package com.swtworkbench.community.xswt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * StringInputStreamFactory.  A class that converts a java.lang.String into a 
 * java.io.InputStream.
 *
 * @author djo
 */
public class StringInputStreamFactory {
	public static InputStream construct(final String rawMaterial) {
		try {
			return new ByteArrayInputStream(rawMaterial.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("utf-8 should always be available",e);
		}
	}

}

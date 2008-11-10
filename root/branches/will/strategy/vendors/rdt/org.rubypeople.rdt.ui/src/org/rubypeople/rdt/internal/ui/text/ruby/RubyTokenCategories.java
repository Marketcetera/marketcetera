/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package org.rubypeople.rdt.internal.ui.text.ruby;

import java.lang.reflect.Field;

/**
 * @author Kevin Lindsey
 */
public class RubyTokenCategories
{
	/**
	 * RubyTokenCategories
	 */
	protected RubyTokenCategories()
	{
	}

	/**
	 * UNKNOWN
	 */
	public static final int UNKNOWN = -1;
	
	/**
	 * ERROR
	 */
	public static final int ERROR = 0;

	/**
	 * WHITESPACE
	 */
	public static final int WHITESPACE = 1;

	/**
	 * IDENTIFIER
	 */
	public static final int IDENTIFIER = 2;

	/**
	 * KEYWORD
	 */
	public static final int KEYWORD = 3;

	/**
	 * PUNCTUATOR
	 */
	public static final int PUNCTUATOR = 4;

	/**
	 * LITERAL
	 */
	public static final int LITERAL = 5;

	/**
	 * COMMENT
	 */
	public static final int COMMENT = 6;

	/**
	 * MAX_VALUE
	 */
	public static final int MAX_VALUE = COMMENT;

	/**
	 * getNames
	 *
	 * @return String[]
	 */
	public static String[] getNames()
	{
		String[] result = new String[MAX_VALUE + 1];
		
		for (int i = 0; i <= MAX_VALUE; i++)
		{
			result[i] = getName(i);
		}
		
		return result;
	}
	
	/**
	 * Get the name associated with the specified token category
	 * 
	 * @param category
	 *            The token category
	 * @return The name associated with this token category
	 */
	public static String getName(int category)
	{
		switch (category)
		{
			case ERROR:
				return "ERROR"; //$NON-NLS-1$

			case IDENTIFIER:
				return "IDENTIFIER"; //$NON-NLS-1$
				
			case WHITESPACE:
				return "WHITESPACE"; //$NON-NLS-1$

			case KEYWORD:
				return "KEYWORD"; //$NON-NLS-1$

			case PUNCTUATOR:
				return "PUNCTUATOR"; //$NON-NLS-1$

			case LITERAL:
				return "LITERAL"; //$NON-NLS-1$

			case COMMENT:
				return "COMMENT"; //$NON-NLS-1$

			default:
				return "<unknown>"; //$NON-NLS-1$
		}
	}

	/**
	 * getIntValue
	 * 
	 * @param name
	 * @return int
	 */
	public static int getIntValue(String name)
	{
		Class c = RubyTokenCategories.class;
		int result = -1;

		try
		{
			Field f = c.getField(name);

			result = f.getInt(c);
		}
		// fail silently
		catch (SecurityException e)
		{
		}
		catch (NoSuchFieldException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}

		return result;
	}
}
